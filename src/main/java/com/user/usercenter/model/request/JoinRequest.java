package com.user.usercenter.model.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class JoinRequest implements Serializable {


    /**
    * 队伍id*/
    private long TeamId;

    /**
     * 密码* */
    private String Password;


}
