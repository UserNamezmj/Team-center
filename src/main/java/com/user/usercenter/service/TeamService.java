package com.user.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.usercenter.model.Team;
import com.user.usercenter.model.request.JoinRequest;
import com.user.usercenter.result.PageResult;
import com.user.usercenter.vo.TeamQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zmj
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-12-20 15:20:21
*/
public interface TeamService extends IService<Team> {


    /**
     *
     * 判断team是否为空*/
    Boolean isTeam(Team team);

    boolean AddTeam(Team team, HttpServletRequest request);

    PageResult SelectQueryTeamList(TeamQuery teamQuery);

    boolean UpdateTeam(Team team,HttpServletRequest request);

    boolean AddTeamList(JoinRequest joinRequest, HttpServletRequest request);


    boolean OutTeam(Long teamId, HttpServletRequest request);

    boolean DissolveTeam(Long teamId, HttpServletRequest request);

    PageResult getisTeam(List<TeamQuery> records, HttpServletRequest request);


//    List<Team> listTeamQuery(TeamQuery teamQuery);
}
