package com.user.usercenter.contant;

import javax.annotation.Resource;
import java.util.UUID;

public interface UserContant {

    /*
     * 用户登录键*/
    String USER_LOGIN_STATE = UUID.randomUUID().toString();

    /*
    * 用户角色 0 普通用户 1 管理员*/

    Integer POWER_USER_ROLE = 1;
    Integer PUB_USER_ROLE = 0;
}
