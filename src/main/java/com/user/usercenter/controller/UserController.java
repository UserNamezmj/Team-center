package com.user.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.common.upload;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.contant.UserContant;
import com.user.usercenter.contant.UserIdContant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.model.User;
import com.user.usercenter.model.request.UserRegisterLogin;
import com.user.usercenter.model.request.UserRegisterRequest;
import com.user.usercenter.properties.AliOssUtil;
import com.user.usercenter.result.PageResult;
import com.user.usercenter.result.Result;
import com.user.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(originPatterns = {"http://112.124.54.146","http://localhost:5173"}, allowCredentials = "true", allowedHeaders = {"*"})
@Slf4j
public class UserController {


    @Resource
    private UserService userService;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /*
     * 用户注册功能*/
    @PostMapping("/register")
    public long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BaseException(MessageConstant.LOGIN_FAILED, 500, "请求参数错误");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }


    /**
     * 效验用户是否合法*
     */
    @GetMapping("/current/{userId}")
    public Result<User> getCurrentUser(@PathVariable Long userId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(userId.toString());
        log.info("attribute {}", attribute);
        User user = (User) attribute;
        if (user == null) {
            return null;
        }
        Long id = user.getId();
        User user1 = userService.getById(id);
        User safeUser = userService.getSafeUser(user1);
        return Result.success(safeUser);
    }

    /*
     * 用户登录功能*/
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserRegisterLogin registerLogin, HttpServletRequest requset) {
        if (registerLogin == null) {
            return null;
        }
        String userAccount = registerLogin.getUserAccount();
        String userPassword = registerLogin.getUserPassword();
        /*
         * 是否包含任何空值*/
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, requset);
        return Result.success(user);
    }


    /*
     * 用户退出登录功能*/
    @PostMapping("/outLogin")
    public long OutLogin(HttpServletRequest request) {
        request.getSession().removeAttribute(UserIdContant.USER_LOGIN_STATE);
        return -1;
    }


    /*
     * 用户查询接口*/
    @GetMapping("/search")
    public List<User> userList(String username, HttpServletRequest request) {
        //1.是否是管理员
        if (!userService.isAdmin(request)) {
            return new ArrayList<>();
        }
        //2.根据用户名查询接口
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> list = userService.list(queryWrapper);
        list.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 根据标签查询用户*
     */
    @GetMapping("/tags/userList")
    public Result<List<User>> userTagsList(@RequestParam(required = false) List<String> tagsList) {
        if (CollectionUtils.isEmpty(tagsList)) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "参数错误");
        }
        return Result.success(userService.getUserListTags(tagsList));

    }

    //    /**
//     * 测试连接数据
//     */
//    @GetMapping("/{userId}")
//    public Result userPageList(long userId) {
////        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
////        Page<User> userPage = new Page<>(pageNum, pageSize);
////        Page<User> page = userService.page(userPage, queryWrapper);
//        String arr = "消息接收到了";
//        log.info(" {}" , arr + userId);
//        return Result.success();
//    }
//    @GetMapping("/pageList")
//    public Result<PageResult> userPageList(@RequestParam long pageNum, long pageSize, HttpServletRequest request) {
//        //连接redis 开启缓存
//        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
//        //获取用户id
//        User loginUser = userService.getLoginUser(request);
//        log.info("{}", loginUser);
//        Long userId = loginUser.getId();
//        // 设置key格式
//        String key = String.format("Zmj:user:userPageList:%s", userId);
//        // 判断是否 缓存
//        if (!ObjectUtils.isEmpty(operations.get(key))) {
//            Object o = operations.get(key);
//            log.info("Result{}", o);
//            PageResult pageResult = (PageResult) o;
//            return Result.success(pageResult);
//        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        Page<User> userPage = new Page<>(1, 10);
//        Page<User> page = userService.page(userPage, queryWrapper);
//        PageResult pageResult = new PageResult();
//        pageResult.setTotal(page.getTotal());
//        pageResult.setRecords(page.getRecords());
//        //如果Redis没有缓存就存进去
//        try {
//            operations.set(key, pageResult, 10000, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            log.info("Error Redis key {}", e);
//        }
//        return Result.success(pageResult);
//    }
    @GetMapping("/pageList")
    public Result<PageResult> userPageList(@RequestParam long pageNum, long pageSize, HttpServletRequest request) {
        // 判断是否 缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userPage = new Page<>(1, 10);
        Page<User> page = userService.page(userPage, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        return Result.success(pageResult);
    }

    /**
     * 关注用户
     */
    @GetMapping("/connection/{userId}")
    public Result<Boolean> getConnectionId(@PathVariable Long userId, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(userId)) new BaseException(ErrorCode.UNKNOWN_ERROR, "id 不存在");
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtils.isEmpty(loginUser)) new BaseException(ErrorCode.UNKNOWN_ERROR, "无权限");
        User user = userService.getById(userId);
        user.setConnectionId(loginUser.getId());
        User user1 = new User();
        BeanUtils.copyProperties(user, user1);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        boolean save = userService.update(user1, queryWrapper);
        return Result.success(save);
    }


    /**
     * 用户修改*
     */
    @PutMapping("/update")
    public Result<User> userUpdate(@RequestBody User user, HttpServletRequest request) {
        if (!ObjectUtils.isEmpty(user) && user.getId() == null) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "参数错误");
        }
        if (!userService.isAdmin(request)) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "没有权限");
        }
        User user1 = userService.userUpdate(user);
        return Result.success(user1);
    }


    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public Result<Boolean> userUpload(MultipartFile file, HttpServletRequest request) throws Exception {
        //获取文件名称
        String name = file.getOriginalFilename();
        //上传图片结果
        Boolean result = userService.upload(file, name, request);
        return Result.success(result);
    }


    /**
     * 删除用户消息
     */
    @DeleteMapping("deleteInformation/{id}")
    public Result<Boolean> deleteInformation(@PathVariable Long id, HttpServletRequest request) {
        //判断用户是否登录
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtils.isEmpty(loginUser)) throw new BaseException(MessageConstant.USER_NOT_LOGIN, 401, "无权限");
       boolean result = userService.deleteInformation(id);
        return Result.success(result);
    }

    /*
     * 用户删除*/
    @PostMapping("/delete")
    public Boolean userDelete(@RequestBody Long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }


}
