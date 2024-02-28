package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.mapper.ChatMapper;
import com.user.usercenter.mapper.InformationMapper;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.Information;
import com.user.usercenter.model.User;
import com.user.usercenter.service.ChatService;
import com.user.usercenter.service.UserService;
import com.user.usercenter.vo.ChatVo;
import com.user.usercenter.vo.InformationQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author zmj
 * @description 针对表【chat(聊天列表)】的数据库操作Service实现
 * @createDate 2024-01-10 14:58:46
 */
@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private InformationMapper informationMapper;

    @Resource
    private ChatMapper chatMapper;


    /**
     * 查看消息内容
     */
    @Override
    public List<Chat> chatList(InformationQuery informationQuery) {
        Long userId = informationQuery.getUserId();
        Long anotherId = informationQuery.getAnotherId();
        QueryWrapper<Chat> chatQueryWrapper = new QueryWrapper<>();
        chatQueryWrapper.eq("userId", userId).and(i -> i.eq("anotherId", anotherId))
                .or().eq("userId", anotherId).and(i -> i.eq("anotherId", userId));
        List<Chat> chat = chatMapper.selectList(chatQueryWrapper);
        if (CollectionUtils.isEmpty(chat)) throw new BaseException(MessageConstant.NO_DATA, 4000, "数据为空");
        return chat;
    }

    /**
     * 查看关注用户
     */
    @Override
    public List<Chat> chatListUser(InformationQuery informationQuery) {
        Long userId = informationQuery.getUserId();
        QueryWrapper<Chat> chatQueryWrapper = new QueryWrapper<>();
        chatQueryWrapper.eq("userId", userId).or().eq("anotherId", userId);
        List<Chat> chat = chatMapper.selectList(chatQueryWrapper);
        // 根据name去重
        List<Chat> chatList = chat.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Chat::getUsername))), ArrayList::new)
        );
        return chatList;
    }


    /**
     * 头像同步更新
     */
    @Override
    public Boolean updateChat(User user) {
        //获取user实体类
        User user1 = userMapper.selectById(user.getId());
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", user.getId());
        //获取chatList
        List<Chat> chatList = chatMapper.selectList(queryWrapper);
        chatList = chatList.stream().map(chat -> {
            chat.setUsername(user1.getUsername());
            chat.setAvatarUrl(user1.getAvatarUrl());
            return chat;
        }).collect(Collectors.toList());
        //批量更新操作
        log.info("{}",chatList);
        boolean result = this.updateBatchById(chatList);
        return result;
    }
}




