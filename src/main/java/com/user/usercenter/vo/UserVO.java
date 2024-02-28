package com.user.usercenter.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.user.usercenter.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO  implements Serializable  {

    private static final long serialVersionUID = -6002344475106061199L;


    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 姓名
     */
    private String username;


    /**
     * 登录账号
     */
    private String userAccount;


    /**
     * 用户状态(0 启用,1 禁用)
     */
    private Integer userStatus;

}
