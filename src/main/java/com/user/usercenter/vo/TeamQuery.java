package com.user.usercenter.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.user.usercenter.model.Team;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
 * team查询条件参数
 * * */
@Data
public class TeamQuery implements Serializable {


    private static final long serialVersionUID = 1L;


    /**
     * id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 根据名称和描述信息查询*
     */

    private String NameDescription;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 房间人数
     */
    private Integer RoomNum;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 用户id 集合
     */
    private List<Long> idList;

    /**
     * 用户是否加入队伍
     */
    private Boolean isTeam;

}
