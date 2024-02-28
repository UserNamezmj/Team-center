package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.common.upload;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.contant.UserContant;
import com.user.usercenter.contant.UserIdContant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.model.User;
import com.user.usercenter.properties.AliOssProperties;
import com.user.usercenter.properties.AliOssUtil;
import com.user.usercenter.result.Result;
import com.user.usercenter.service.ChatService;
import com.user.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zmj
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-11-13 14:19:03
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AliOssUtil aliOssUtil;

    @Resource
    private ChatService chatService;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.效验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;

        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        //校验账户不能包含特殊字符
        String pattern = "^[a-zA-Z0-9_]+$";
        Pattern compile = Pattern.compile(pattern);
        boolean matches = compile.matcher(userAccount).matches();
        if (!matches) {
            return -1;

        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;

        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            return 1;
        }

        //2.对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        //3.向用户表插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean save = this.save(user);
        if (!save) {
            return -1;

        }
        log.info("user表的主键id {}", user.getId());

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.效验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;

        }
        if (userPassword.length() < 8) {
            return null;
        }
        //校验账户不能包含特殊字符
        String pattern = "^[a-zA-Z0-9_]+$";
        Pattern compile = Pattern.compile(pattern);
        boolean matches = compile.matcher(userAccount).matches();
        if (!matches) {
            return null;

        }
        //2.对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user1 = userMapper.selectOne(queryWrapper);
        if (user1 == null) {
            log.info("user login failed userAccount cannot match userPassword  {} {} {}", user1, userAccount, encryptPassword);
            throw new BaseException(MessageConstant.LOGIN_FAILED, 500, "登录账号不存在或密码错误");
        }
        //3.用户脱敏
        User cleanUser = new User();
        cleanUser.setId(user1.getId());
        cleanUser.setUsername(user1.getUsername());
        cleanUser.setUserAccount(user1.getUserAccount());
        cleanUser.setAvatarUrl(user1.getAvatarUrl());
        cleanUser.setGender(user1.getGender());
        cleanUser.setPhone(user1.getPhone());
        cleanUser.setEmail(user1.getEmail());
        cleanUser.setUserRole(user1.getUserRole());
        cleanUser.setTags(user1.getTags());
        cleanUser.setUserStatus(user1.getUserStatus());
        cleanUser.setCreateTime(user1.getCreateTime());
        cleanUser.setConnectionId(user1.getConnectionId());
        //4.记录用户登录状态
        log.info("request {}", request);
        Long userId = user1.getId();
        UserIdContant.USER_LOGIN_STATE = userId.toString();
        log.info(" id {}", UserIdContant.USER_LOGIN_STATE);
        request.getSession().setAttribute(UserIdContant.USER_LOGIN_STATE, cleanUser);
        return cleanUser;
    }

    /*
     * 用户脱敏*/
    @Override
    public User getSafeUser(User user) {
        user.setId(user.getId());
        user.setUsername(user.getUsername());
        user.setUserAccount(user.getUserAccount());
        user.setAvatarUrl(user.getAvatarUrl());
        user.setTags(user.getTags());
        user.setUserRole(user.getUserRole());
        user.setGender(user.getGender());
        user.setPhone(user.getPhone());
        user.setEmail(user.getEmail());
        user.setUserStatus(user.getUserStatus());
        user.setCreateTime(user.getCreateTime());
        return user;
    }


    /*
     *
     * 根据标签查询用户* */
    @Override
    public List<User> getUserListTags(List<String> TagsName) {
        if (CollectionUtils.isEmpty(TagsName)) {
            return null;
        }


        //第一种方式,遍及标签集合 查找匹配用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tagsName : TagsName) {
//           queryWrapper =  queryWrapper.like("tags",tagsName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList;

        //第二种方式 内存查找方法
        List<User> userList = userMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        Gson gson = new Gson();
        //在内存中判断是否有包含的标签
        List<User> userList1 = userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> stringSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : TagsName) {
                if (!(stringSet.contains(tagName))) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
        return userList1;
    }


    /*
     * 修改用户信息*/
    @Override
    public User userUpdate(User user) {
        User user2 = userMapper.selectById(user.getId());
        user.setConnectionId(user2.getConnectionId());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", user.getId());
        int length = userMapper.update(user, queryWrapper);
        if (length > 0 && length < 2) {
            log.info("用户更新成功{}", length);
        } else {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "存在多个数据");
        }
        User user1 = userMapper.selectById(user.getId());
        //文件同步更新
        Boolean aBoolean = chatService.updateChat(user);
        return user1;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserIdContant.USER_LOGIN_STATE);
        User user = (User) attribute;
        log.info("role {}", UserContant.POWER_USER_ROLE);
        return user != null || user.getUserRole() == UserContant.POWER_USER_ROLE;
    }


    /**
     * * 获取用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserIdContant.USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(attribute)) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "登录失效,请重新登录");
        }
        User user = (User) attribute;
        return user;
    }


    /**
     * 上传图片
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean upload(MultipartFile file, String name, HttpServletRequest request) throws Exception {
        User loginUser = this.getLoginUser(request);
        if (ObjectUtils.isEmpty(loginUser)) throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "请登录");
        //正则校验是否为图片类型
        if (!name.matches("^.+\\.(jpg|png)$")) {
            //图片类型 不匹配  程序应该终止
            return null;
        }
        //判断是否是一张图片
        InputStream inputStream = file.getInputStream();
        BufferedImage read = ImageIO.read(inputStream);
        int height = read.getHeight();
        int width = read.getWidth();
        if (height == 0 || width == 0) {
            return false;
        }
        //准备磁盘路径
//        String dirPath = "D:/image/";
//        //将这个文件路径封装为file对象
//        File filePath = new File(dirPath);
//        //目录是否存在
//        if (!filePath.exists()) {
//            //如果文件目录不存在,则创建目录
//            filePath.mkdirs(); //表示多级目录上传.
//        }
        //生成唯一标识
        String s = UUID.randomUUID().toString();
        //获取图片类型
        String fileType = name.substring(name.lastIndexOf("."));
        //生成新文件名称
        String newFilename = s + fileType;
        //上传图片阿里云oss对象存储
        Boolean result = uploadAli(file, newFilename, request);
        //封装图片全路径
        String path =  newFilename;
        //文件上传
        File file1 = new File(path);
        file.transferTo(file1);
        inputStream.close();
        //文件同步更新
        result = chatService.updateChat(loginUser);
        return result;
    }


    /**
     * 删除用户消息
     */
    @Override
    public boolean deleteInformation(Long id) {
        if (ObjectUtils.isEmpty(id) || id < 0) throw new BaseException(MessageConstant.PARAMS_ERROR, 400, "id 不存在");
        boolean remove = chatService.removeById(id);
        return remove;
    }

    private Boolean uploadAli(MultipartFile file, String newFilename, HttpServletRequest request) {
        try {
            String newFilePath = aliOssUtil.upload(file.getBytes(), newFilename);
            User loginUser = this.getLoginUser(request);
            User user = this.getById(loginUser.getId());
//            String path1 = "http://localhost:8080/api/image/" + newFilename;
            user.setAvatarUrl(newFilePath);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", loginUser.getId());
            boolean update = this.update(user, queryWrapper);
            if (!update) new BaseException(ErrorCode.UNKNOWN_ERROR, "修改失败");
            log.info(newFilePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}




