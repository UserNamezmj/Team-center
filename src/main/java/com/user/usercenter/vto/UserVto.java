package com.user.usercenter.vto;


import com.user.usercenter.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVto extends User implements Serializable {
    private static final long serialVersionUID = 5960259646191234027L;

    /**
     * 聊天内容
     */
    private String content;
}
