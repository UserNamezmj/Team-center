package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.mapper.UserTeamMapper;
import com.user.usercenter.model.UserTeam;
import com.user.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author zmj
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-12-20 15:23:33
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




