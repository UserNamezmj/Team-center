package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.common.TeamStats;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.interfaceConfig.RedisAPILimit;
import com.user.usercenter.mapper.TeamMapper;
import com.user.usercenter.mapper.UserTeamMapper;
import com.user.usercenter.model.Team;
import com.user.usercenter.model.User;
import com.user.usercenter.model.UserTeam;
import com.user.usercenter.model.request.JoinRequest;
import com.user.usercenter.result.PageResult;
import com.user.usercenter.service.TeamService;
import com.user.usercenter.service.UserService;
import com.user.usercenter.service.UserTeamService;
import com.user.usercenter.vo.PageTeam;
import com.user.usercenter.vo.TeamQuery;
import com.user.usercenter.vo.TeamQueryVo;
import com.user.usercenter.vo.UserVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.record.DVALRecord;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author zmj
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-12-20 15:20:21
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {


    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserTeamMapper userTeamMapper;


    @Override
    public Boolean isTeam(Team team) {
        if (ObjectUtils.isEmpty(team)) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR, 4000, "请求参数错误");
        }
        return true;
    }


    /*
     * 添加队伍 过期时间单位为秒s limit 限制次数 3* */
    @RedisAPILimit(apiKey = "System:zmj:AddTeam:lockAdd", limit = 1, sec = 5)
//    @RedissonLockAnnotation(keyParts = "System:zmj:AddTeam:lockAdd")
//    @DistributedLock(keys = "#team.name")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean AddTeam(Team team, HttpServletRequest request) {
        TeamException(team);
        //判断是否登录
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN, 5000, "用户未登录");
        }
        team.setUserId(userId);
        //限制创建队伍数量
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId", userId);
        long count = this.count(teamQueryWrapper);
        log.info("count {}", count);
        if (count > 4) {
            throw new BaseException(MessageConstant.TEAM_MAXError, 400, "此用户创建队伍数量超过限制");
        }
        this.save(team);
        UserTeam userTeam = new UserTeam();
        BeanUtils.copyProperties(team, userTeam);
        userTeam.setTeamId(team.getId());
        boolean result = userTeamService.save(userTeam);
        return result;
    }


    /**
     * 分页查询队伍信息*
     */
    @Override
    public PageResult SelectQueryTeamList(TeamQuery teamQuery) {
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        //根据名称查询
        String name = teamQuery.getName();
        if (StringUtil.isNotBlank(name)) {
            teamQueryWrapper.like("name", name);
        }
        //根据描述信息
        String description = teamQuery.getDescription();
        if (StringUtil.isNotBlank(description)) {
            teamQueryWrapper.like("description", description);
        }
        //id查询用户已加入队伍的信息
        List<Long> idList = teamQuery.getIdList();
        if (CollectionUtils.isNotEmpty(idList)) {
            teamQueryWrapper.in("id", idList);
        }
        //根据名称和描述信息查询
        String nameDescription = teamQuery.getNameDescription();
        if (StringUtil.isNotBlank(nameDescription)) {
            teamQueryWrapper.and(qw -> qw.like("name", nameDescription).or().like("description", nameDescription));
        }
        //根据最大人数查询
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            teamQueryWrapper.eq("maxNum", maxNum);
        }
        //根据创建人来查询
        if (teamQuery.getUserId() != null && teamQuery.getUserId() > 0) {
            teamQueryWrapper.eq("userId", teamQuery.getUserId());
        }
        //根据状态选择
        Integer status = teamQuery.getStatus();
        int code2 = TeamStats.TEAM_PUBLICITY.getCode();
        int code = TeamStats.TEAM_PRIVACY.getCode();
        int code1 = TeamStats.TEAM_ENCIPHER.getCode();
        if (status != null && status != code ) {
            teamQueryWrapper.and(qw -> qw.eq("status", code1).or().eq("status", code2));
        }
        Date expireTime = teamQuery.getExpireTime();
        //只查询未过期的队伍
        if (ObjectUtils.isEmpty(expireTime)) {
            teamQueryWrapper.gt("expireTime", new Date());
        }else {
            teamQueryWrapper.gt("expireTime", expireTime);
        }
        ArrayList<TeamQueryVo> teamQueryArrayList = new ArrayList<>();
        PageTeam pageTeam = new PageTeam();
        Page<Team> page = new Page<>(pageTeam.getPageNum(), pageTeam.getPageSize());
        Page<Team> page1 = this.page(page, teamQueryWrapper);
        log.info("{}", page1.getRecords());
        for (Team team : page1.getRecords()
        ) {
            UserVO userVO = new UserVO();
            Long userId = team.getUserId();
            if (userId < 0) {
                throw new BaseException(MessageConstant.NO_DATA, 400, "id不存在");
            }
            User user = userService.getById(userId);
            BeanUtils.copyProperties(user, userVO);
            TeamQueryVo teamQueryVo = new TeamQueryVo();
            BeanUtils.copyProperties(team, teamQueryVo);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", team.getId());
            long count = userTeamService.count(userTeamQueryWrapper);
            teamQueryVo.setRoomNum((int) count);
            teamQueryVo.setUserVo(userVO);
            teamQueryArrayList.add(teamQueryVo);
        }

        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(teamQueryArrayList);
        return pageResult;
    }

    @Override
    public boolean UpdateTeam(Team team, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(team)) {
            throw new BaseException(MessageConstant.PARAMS_ERROR, 400, "参数为空");
        }
        //效验
        TeamException(team);
        // 只有管理员或者队伍的创建者可以修改
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN, 5000, "用户未登录");
        }
        if (team.getUserId() != loginUser.getId() && !userService.isAdmin(request)) {
            throw new BaseException(MessageConstant.PARAMS_ERROR, 400, "id 不存在 或 无数据");
        }
        //获取队伍id与数据库数据进行比较
        Long id = team.getId();
        Team team1 = this.getById(id);
        if (ObjectUtils.isEmpty(team1)) {
            throw new BaseException(MessageConstant.PARAMS_ERROR, 400, "id 不存在 或 无数据");
        }
        if (team.equals(team1)) {
            return true;
        }
        UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
        teamUpdateWrapper.eq("id",team.getId());
        boolean result = this.update(team, teamUpdateWrapper);
        return result;
    }


    /**
     * 用户加入队伍*
     */
    @Override
    public boolean AddTeamList(JoinRequest joinRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0 || userId == null) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "用户未登录");
        }
        Long teamId = joinRequest.getTeamId();
        //队伍id不存在
        if (teamId == null || teamId < 0) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "参数为空");
        }
        Team team = this.getById(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return false;
        }
        //禁止加入过期队伍
        if (team.getExpireTime().getTime() < new Date().getTime()) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "队伍有效时间已过期,请重新创建");
        }
        //禁止加入私有队伍
        TeamStats teamStatusEnum = TeamStats.getEnumByValue(team.getStatus());
        if (TeamStats.TEAM_PRIVACY.equals(teamStatusEnum)) {
            return false;
        }
        //加入加密队伍必须输入密码
        if (TeamStats.TEAM_ENCIPHER.equals(teamStatusEnum)) {
            if (StringUtil.isEmpty(joinRequest.getPassword()) || !joinRequest.getPassword().equals(team.getPassword())) {
                throw new BaseException(MessageConstant.UNKNOWN_ERROR, 4000, "密码错误");
            }
        }
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId", userId);
        long count = userTeamService.count(teamQueryWrapper);
        //人员最多加入五个队伍
        if (count > 5) {
            throw new BaseException(MessageConstant.TEAM_MAX, 400, "人员加入队伍总数超过五个");
        }
        //不能重复加入已加入的队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long count1 = userTeamService.count(userTeamQueryWrapper);
        if (count1 > 0) {
            throw new BaseException(MessageConstant.TEAM_MAX, 400, "不能重复加入自己的队伍");
        }
        //队伍人数已满不许加入
        QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
        userTeamQueryWrapper1.eq("teamId", teamId);
        long count2 = userTeamService.count(userTeamQueryWrapper1);
        if (count2 > 0 && count2 >= team.getMaxNum()) {
            throw new BaseException(MessageConstant.TEAM_MAX, 400, "队伍人数已满");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamMapper.AddTeamList(userTeam);
        return result;

    }


    /**
     * 用户退出队伍*
     */
    @Override
    public boolean OutTeam(Long teamId, HttpServletRequest request) {
        boolean result = false;
        //用户必须登录
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0 || userId == null) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "用户未登录");
        }
        //判断队伍是否存在
        Team team = this.getById(teamId);
        if (ObjectUtils.isEmpty(team)) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "队伍不存在");
        }
        //用户已加入队伍才可退出
        QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
        userTeamQueryWrapper1.eq("teamId", teamId);
        userTeamQueryWrapper1.eq("userId", userId);
        long count1 = userTeamService.count(userTeamQueryWrapper1);
        if (count1 <= 0) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "用户不存在,请创建新队伍");
        }
        //队伍还剩一人退出队伍就解散
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count > 0 && count <= 1) {
            this.removeById(teamId);
            result = userTeamService.removeById(teamId);
            if (result == false) {
                throw new BaseException(ErrorCode.UNKNOWN_ERROR, "移除队伍关联用户信息失败");
            }
        } else {
            //队伍大于一人,如果是队长退出 则顺位给第二个人.
            Long userId1 = team.getUserId();
            if (userId.equals(userId1)) {
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> list = userTeamService.list(userTeamQueryWrapper);
                UserTeam userTeam = list.get(1);
                Team team1 = new Team();
                team1.setUserId(userTeam.getUserId());
                team1.setId(userTeam.getTeamId());
                this.updateById(team1);
                userTeamService.removeById(team1.getId());
            } else {
                UserTeam one = userTeamService.getOne(userTeamQueryWrapper1);
                result = userTeamService.removeById(one.getId());
            }

        }
        return result;
    }


    /**
     * 解散队伍*
     */
    @Override
    public boolean DissolveTeam(Long teamId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0 || userId == null) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "用户未登录");
        }
        //判断是不是队长
        Team team = this.getById(teamId);
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_ROLE, "无权限解散队伍");
        }
        //解散队伍
        this.removeById(team.getId());
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean remove = userTeamService.remove(userTeamQueryWrapper);
        return remove;
    }

    @Override
    public PageResult getisTeam(List<TeamQuery> records, HttpServletRequest request) {
        //查询队伍列表
        List<Long> teamIdList = records.stream().map(TeamQuery::getId).collect(Collectors.toList());
        //查询队伍为空 直接返回空数据
        if (CollectionUtils.isEmpty(teamIdList)) throw new BaseException(MessageConstant.NO_DATA,2000,"你当前还没有加入房间");
        //判断用户是否加入队伍
        try {
            User loginUser = userService.getLoginUser(request);
            Long userId = loginUser.getId();
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("userId", userId);
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> list = userTeamService.list(userTeamQueryWrapper);
            //用户已加入队伍
            Set<Long> collect = list.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            records.forEach(item -> {
                boolean hasJoin = collect.contains(item.getId());
                item.setIsTeam(hasJoin);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        PageResult<Object> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        return pageResult;
    }


    /**
     * 用户加入的队伍
     */
//    @Override
//    public List<Team> listTeamQuery(TeamQuery teamQuery) {
//        return null;
//    }


    /**
     * 效验队伍字段类型以及属性值的限制*
     *
     * @param team
     */
    private static void TeamException(Team team) {
        //限制创建队伍人数
        if (team.getMaxNum() > 15) {
            throw new BaseException(MessageConstant.TEAM_MAX, 400, "最大不超过15");
        }
        //限制队伍标题长度
        String name = team.getName();
        if (name.length() > 15 || StringUtil.isEmpty(name)) {
            throw new BaseException(MessageConstant.FILEDS_LONG, 400, "字段长度最长不超过15");
        }
        //限制队伍过期时间要大于当前时间
        if (team.getExpireTime().getTime() < new Date().getTime()) {
            throw new BaseException(MessageConstant.TEAM_OVERTIME, 400, "队伍有效时间已过期,请重新创建");
        }
        //限制描述字段数量
        if (team.getDescription().length() > 15) {
            throw new BaseException(MessageConstant.FILEDS_LONG, 400, "字段长度最长超过15");
        }
        //默认队伍公开 私密 添加密码效验
        //  d. status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStats statusEnum = TeamStats.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "队伍状态不满足要求");
        }
        //  e. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStats.TEAM_ENCIPHER.equals(statusEnum)) {
            if (StringUtil.isEmpty(password) || password.length() > 32) {
                throw new BaseException(ErrorCode.UNKNOWN_ERROR, "密码设置不正确");
            }
        }

    }
}




