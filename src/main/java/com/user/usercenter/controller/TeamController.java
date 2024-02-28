package com.user.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.model.Team;
import com.user.usercenter.model.User;
import com.user.usercenter.model.UserTeam;
import com.user.usercenter.model.request.JoinRequest;
import com.user.usercenter.result.PageResult;
import com.user.usercenter.result.Result;
import com.user.usercenter.service.TeamService;
import com.user.usercenter.service.UserService;
import com.user.usercenter.service.UserTeamService;
import com.user.usercenter.utils.MatchTag;
import com.user.usercenter.vo.PageTeam;
import com.user.usercenter.vo.TeamQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(originPatterns = {"http://112.124.54.146","http://localhost:5173"}, allowCredentials = "true", allowedHeaders = {"*"})
public class TeamController {


    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserTeamService userTeamService;





    /*
     * 查询队伍接口*/
    @PostMapping("/list")
    public Result<PageResult> ListTeam(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(teamQuery)) {
            throw new BaseException(MessageConstant.NO_DATA, 400, "查询参数为空");
        }
        PageResult pageResult = teamService.SelectQueryTeamList(teamQuery);
        List<TeamQuery> records = pageResult.getRecords();
        //查询队伍列表
        List<Long> teamIdList = records.stream().map(TeamQuery::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamIdList)){
            pageResult.setTotal(0);
            pageResult.setRecords(teamIdList);
            return Result.success(pageResult);
        };
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
        pageResult.setRecords(records);
        return Result.success(pageResult);
    }

    /*
     * 分页查询*/
    @PostMapping
    public Result<PageResult> PageTeamList(@RequestBody PageTeam pageTeam) {
        if (ObjectUtils.isEmpty(pageTeam)) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR, 500, "参数不存在");

        }
        Page<Team> pageTeamPage = new Page<>(pageTeam.getPageSize(), pageTeam.getPageNum());

        Page<Team> page = teamService.page(pageTeamPage);

        PageResult pageResult = new PageResult();
        BeanUtils.copyProperties(page, pageResult);

        return Result.success(pageResult);
    }

    /*
     * 添加队伍接口 */
    @PostMapping("/add")
    public Result<Boolean> addTeam(@RequestBody Team team, HttpServletRequest request) {
        boolean result = false;
        if (teamService.isTeam(team)) {
            result = teamService.AddTeam(team, request);
        }
        return Result.success(result);
    }


    /*
     * 修改队伍接口*/
    @PostMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody Team team, HttpServletRequest request) {
        boolean result = false;
        if (teamService.isTeam(team)) {
            result = teamService.UpdateTeam(team, request);
        }
        return Result.success(result);
    }

    /*
     * 查询队伍详情*/
    @GetMapping("/list/{id}")
    public Result<Team> getByTeam(@PathVariable Long id) {
        Team team = new Team();
        if (id > 0) {
            Team teamById = teamService.getById(id);
            if (ObjectUtils.isEmpty(teamById)) {
                throw new BaseException(MessageConstant.NO_DATA, 40001, "根据 id 查找数据不存在");
            }
            BeanUtils.copyProperties(teamById, team);
        }
        return Result.success(team);
    }


    /**
     * 用户加入队伍
     */
    @PostMapping("/addTeam")
    public Result<Boolean> AddTeamList(@RequestBody JoinRequest joinRequest, HttpServletRequest request) {

        if (ObjectUtils.isEmpty(joinRequest)) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "参数为空");
        }
        boolean result = teamService.AddTeamList(joinRequest, request);
        return Result.success(result);
    }

    /**
     * 我加入的队伍
     */
    @PostMapping("/joinTeam")
    public Result<PageResult> joinTeamList(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0 || userId == null) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "id 不存在");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        Map<Long, List<UserTeam>> collect = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> arrayList = new ArrayList<>(collect.keySet());
        teamQuery.setIdList(arrayList);
        PageResult pageResult = teamService.SelectQueryTeamList(teamQuery);
        List records = pageResult.getRecords();
        PageResult pageResult1 = teamService.getisTeam(records, request);
        pageResult.setRecords(pageResult1.getRecords());
        return Result.success(pageResult);

    }

    /**
     * 推荐队伍TOP5
     */
    @GetMapping("/getTeamList")
    public Result<List<User>> listResult(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtils.isEmpty(loginUser)) throw new BaseException(ErrorCode.UNKNOWN_ERROR, "未登录");
        String tags = loginUser.getTags();
        //对象转化成JSON
        Gson gson = new Gson();
        List<String> listTag = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNotNull(User::getTags);
        queryWrapper.last(" limit 10");
        List<User> list = userService.list(queryWrapper);
        System.out.println(list.toString());
        TreeMap<Long, Integer> longListTreeMap = new TreeMap<>();
        list.forEach(item -> {
            String tags1 = item.getTags();
            List<String> listTags = gson.fromJson(tags1, new TypeToken<List<String>>() {
            }.getType());
            int i = MatchTag.minDistance(listTag, listTags);
            longListTreeMap.put(item.getId(), i);
        });
        //排序 降序
        List<User> userArrayList = new ArrayList<>();
        int num = 5;
        int index = 0;
        ArrayList<Map.Entry<Long, Integer>> entries = new ArrayList<>(longListTreeMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Long, Integer>>() {
            @Override
            public int compare(Map.Entry<Long, Integer> o1, Map.Entry<Long, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for (Map.Entry<Long, Integer> e : entries) {
            if (index > num) {
                break;
            }
//            System.out.println(e.getKey() + ":" + e.getValue());
            User user = userService.getById(e.getKey());
            userArrayList.add(user);
            index++;
        }
        return Result.success(userArrayList);
    }

    /**
     * 用户退出队伍*
     */
    @GetMapping("outTeam/{teamId}")
    public Boolean OutTeam(@PathVariable Long teamId, HttpServletRequest request) {
        if (teamId < 0 || teamId == null) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "id 不存在");
        }
        boolean result = teamService.OutTeam(teamId, request);
        return result;
    }


    /**
     * 用户解散队伍*
     */
    @GetMapping("dissolveTeam/{teamId}")
    public Boolean DissolveTeam(@PathVariable Long teamId, HttpServletRequest request) {
        if (teamId < 0 || teamId == null) {
            throw new BaseException(ErrorCode.UNKNOWN_ERROR, "id 不存在");
        }
        boolean result = teamService.DissolveTeam(teamId, request);
        return result;
    }

}
