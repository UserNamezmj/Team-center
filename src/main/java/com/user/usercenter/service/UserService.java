package com.user.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.usercenter.model.User;
import com.user.usercenter.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zmj
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-11-13 14:19:03
 */
public interface UserService extends IService<User> {


    long userRegister(String userAccount, String userPassword, String checkPassword);

    /*用户登录
     * */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /*
     * 用户脱敏*/
    User getSafeUser(User user);

    /*
     *
     * 根据标签查询用户* */
    List<User> getUserListTags(List<String> TagsName);


    User userUpdate(User user);


    /*
     * 是否是管理员*/
    boolean isAdmin(HttpServletRequest request);


    /*
     * 获取用户信息*/
    User getLoginUser(HttpServletRequest request);

    Boolean upload(MultipartFile file, String name, HttpServletRequest request) throws Exception;

    boolean deleteInformation(Long id);


    /**
     判断用户是否加入队伍*/

}
