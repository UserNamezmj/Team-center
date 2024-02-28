package com.user.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.User;
import com.user.usercenter.result.Result;
import com.user.usercenter.service.ChatService;
import com.user.usercenter.service.UserService;
import com.user.usercenter.vo.InformationQuery;
import com.user.usercenter.vto.UserVto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/chat")
@CrossOrigin(originPatterns = {"http://112.124.54.146","http://localhost:5173"}, allowCredentials = "true", allowedHeaders = {"*"})
@Slf4j
public class ChatController {

    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @PostMapping("/list")
    public Result<List<Chat>> chatList(@RequestBody InformationQuery informationQuery) {
        if (ObjectUtils.isEmpty(informationQuery)) throw new BaseException(MessageConstant.PARAMS_ERROR, 4000, "参数为空");
        List<Chat> list = chatService.chatList(informationQuery);
        return Result.success(list);
    }

    @PostMapping("/chatUserList")
    public Result<List<Chat>> chatUser(@RequestBody InformationQuery informationQuery) {
        if (ObjectUtils.isEmpty(informationQuery)) throw new BaseException(MessageConstant.PARAMS_ERROR, 4000, "参数为空");
        List<Chat> list = chatService.chatListUser(informationQuery);
        return Result.success(list);
    }


    /**
     * 查询关注用户信息
     */
    @GetMapping("/chatUser/{userId}")
    public Result<List<UserVto>> chatUserList(@PathVariable Long userId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(userId.toString());
        if (ObjectUtils.isEmpty(attribute)) {
            throw new BaseException(ErrorCode.ACCOUNT_NOT_POWER, "用户没有登录");
        }
        User loginUser = (User) attribute;
        if (ObjectUtils.isEmpty(loginUser)) new BaseException(ErrorCode.UNKNOWN_ERROR, "无权限");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("connectionId").eq("connectionId", loginUser.getId());
        List<User> list = userService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) new BaseException(ErrorCode.UNKNOWN_ERROR, "数据为空");
        List<UserVto> userVtoList = new ArrayList<>();
        for (User user : list
        ) {
            UserVto userVto = new UserVto();
            BeanUtils.copyProperties(user, userVto);
            QueryWrapper<Chat> chatQueryWrapper = new QueryWrapper<>();
            chatQueryWrapper.eq("userId", user.getId()).orderByDesc("updateTime").last("limit 1");
            Chat chat = chatService.getOne(chatQueryWrapper);
            log.info("{}", chat);
            if (!ObjectUtils.isEmpty(chat)) {
                userVto.setContent(chat.getContent());
            } else {
                userVto.setContent("你好啊");
            }
            userVtoList.add(userVto);
        }
        return Result.success(userVtoList);
    }
}
