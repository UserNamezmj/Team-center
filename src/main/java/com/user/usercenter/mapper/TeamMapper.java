package com.user.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.usercenter.model.Team;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zmj
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-12-20 15:20:21
* @Entity generator.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {


    @Select("select * from team")
    List<Team> selectTeamList();
}




