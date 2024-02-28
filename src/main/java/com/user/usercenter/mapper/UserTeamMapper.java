package com.user.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.usercenter.model.UserTeam;
import com.user.usercenter.model.request.JoinRequest;

/**
* @author zmj
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-12-20 15:23:33
* @Entity generator.domain.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {


    boolean AddTeamList(UserTeam userTeam);
}




