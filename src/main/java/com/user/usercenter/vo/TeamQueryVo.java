package com.user.usercenter.vo;


import com.user.usercenter.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TeamQueryVo extends TeamQuery implements Serializable {

    private static final long serialVersionUID = 7350373026560231176L;


    /**
     * 创建人的用户信息 *
     */
    private UserVO userVo;

    /**
     * 队伍成员信息
     */
    private List<User> userList;

}
