package com.user.usercenter.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.user.usercenter.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ChatVo extends User implements Serializable {

    private static final long serialVersionUID = 1851224839036671879L;

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

    /**
     * 是否在线(0 是,1 否)
     */
    private Integer isOnline;

    /**
     * 未读数量
     */
    private Integer unread;

    /**
     * 发送消息时间
     */
    private Date time;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否是最后一个消息(0 是,1 否)
     */
    private Integer isLatest;

    /**
     * 用户数据
     */
    private User user;

    /**
     * 接收人数据
     */
    private User anUser;
}
