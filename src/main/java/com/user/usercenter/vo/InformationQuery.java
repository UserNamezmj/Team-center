package com.user.usercenter.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class InformationQuery implements Serializable {

    private static final long serialVersionUID = 9181308711728240890L;
    /**
     * 聊天主表id
     */
    private Long informationId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 对方用户id
     */
    private Long anotherId;

}
