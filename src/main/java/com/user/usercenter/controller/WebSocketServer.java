package com.user.usercenter.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.Information;
import com.user.usercenter.model.User;
import com.user.usercenter.service.ChatService;
import com.user.usercenter.service.InformationService;
import com.user.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/user/{userId}")
public class WebSocketServer {


    public static InformationService informationService;


    public static ChatService chatService;

    public static UserService userService;

    @Autowired
    public void setInformationService(InformationService informationService) {
        WebSocketServer.informationService = informationService;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        WebSocketServer.chatService = chatService;

    }

    @Autowired
    public void setUserService(UserService userService) {
        WebSocketServer.userService = userService;

    }

    // 这部分是日志打印
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    //会话id
    private Long chatId = null;

    private Integer isConnection = 1;


    // 在线连接数
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

//    // 采用Mysql去存储聊天数据
//    private MongoCollection<> collection;

    //当有用户建立连接到服务器的时候触发
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 建立连接的时候就将该用户的session存起来，方便后续的信息返回
        this.chatId = (long) Math.abs(UUID.randomUUID().hashCode());    // 聊天id
        sessionMap.put(userId, session);
        log.info("建立连接");
        log.info("新聊天用户={}，当前在线人数：{}", userId, sessionMap.size());
    }

    // 关闭时触发
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessionMap.remove(userId);
        log.info("有一连接关闭，移除聊天用户={}，当前在线人数为：{}", userId, sessionMap.size());
    }

    // 当用户发送消息时触发
    @OnMessage
    public void onMessage(String parmas, Session session, @PathParam("userId") String userId) {
        log.info("服务器收到聊天用户={}的消息：{}", userId, parmas);

        // 将收到前端的消息解析取出
        JSONObject object = JSONUtil.parseObj(parmas);
        String toUserId = object.getStr("anotherId");      // 发送给谁
        log.info("{} toUserId", toUserId);
        User user1 = insertavatarUrl(userId);
        String text = object.getStr("content");        // 文本信息
        String time = object.getStr("time");        // 发送时间
        String user = object.getStr("user");
//        String avatarUrl = object.getStr("avatarUrl");
//        String username = object.getStr("username");
        log.info("日志信息{}", chatId, toUserId, text, time);
        // 新建对象将要存储到数据库的内容封装到一起
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("id", userId);
        jsonObject.set("isConnection", isConnection);
        jsonObject.set("anotherId", toUserId);
        jsonObject.set("content", text);
        jsonObject.set("avatarUrl", user1.getAvatarUrl());
        jsonObject.set("username", user1.getUsername());
        jsonObject.set("time", time);
        jsonObject.set("user", user);
        // 如果toSession为空，则说明对面没在线，直接存到数据库
        // 若不为空，则说明对面在线，将数据存到数据库并且通过sendMessage实时发送给目标客户
        if (!ObjectUtils.isEmpty(sessionMap.get(toUserId))) {
            sendMessage(jsonObject.toString(), sessionMap.get(toUserId));
            log.info("发送给用户username={}，消息：{}", toUserId, jsonObject.toString());
            insertChatData(jsonObject, chatId);
        } else {
            log.info("用户{}不在线", toUserId);
            insertChatData(jsonObject, chatId);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    // 服务器发送消息给客户端
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务器给用户【{}】发送消息：{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务器发送消息给客户端失败");
        }
    }

    // 服务器发送信息给所有客户端（这步可拓展到群聊）
    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务器给全用户【{}】发送消息：{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务器发送消息给客户端失败");
        }
    }

    // 这是mysql存到数据库的操作
    private void insertChatData(JSONObject chatData, Long chatId) {
        log.info("{}  chatId{}", chatData, chatId);
        Information information = new Information();
        Chat chat = new Chat();
        Object from = chatData.get("id");
        Object to = chatData.get("anotherId");
        Object text = chatData.get("content");
        Object time = chatData.get("time");
        String s1 = from.toString();
        information.setUserId(Long.valueOf(s1));
        information.setTeamId(Long.valueOf("203"));
        chat.setUserId(Long.valueOf(s1));
        User user = userService.getById(Long.valueOf(s1));
        chat.setAvatarUrl(user.getAvatarUrl());
        chat.setUsername(user.getUsername());
        chat.setAnotherId(Long.valueOf(to.toString()));
        chat.setContent(text.toString());
        chat.setChatId(chatId);
        chat.setIsConnection(this.isConnection);
        chat.setTime(new Date());
        boolean result = informationService.saveInformation(information, chat);
    }

    // 这是mysql存到数据库的操作
    private User insertavatarUrl(String userId) {
        if (StringUtils.isEmpty(userId)) throw new BaseException(ErrorCode.UNKNOWN_ERROR, "数据为空");
        QueryWrapper<User> chatQueryWrapper = new QueryWrapper<>();
        chatQueryWrapper.eq("id", userId);
        User user = userService.getOne(chatQueryWrapper);
        if (ObjectUtils.isEmpty(user)) throw new BaseException(ErrorCode.UNKNOWN_ERROR, "接收人数据为空");
        return user;
    }

}
