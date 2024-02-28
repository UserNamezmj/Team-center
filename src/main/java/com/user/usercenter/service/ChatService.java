package com.user.usercenter.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.User;
import com.user.usercenter.vo.InformationQuery;

import java.util.List;

/**
 * @author zmj
 * @description 针对表【chat(聊天列表)】的数据库操作Service
 * @createDate 2024-01-10 14:58:46
 */
public interface ChatService extends IService<Chat> {

    List<Chat> chatList(InformationQuery informationQuery);

    List<Chat> chatListUser(InformationQuery informationQuery);

    /*同步更新用户信息*/
    Boolean updateChat(User user);
}
