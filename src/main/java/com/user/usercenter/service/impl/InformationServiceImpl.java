package com.user.usercenter.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.controller.WebSocketServer;
import com.user.usercenter.mapper.InformationMapper;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.Information;
import com.user.usercenter.service.InformationService;
import com.user.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zmj
 * @description 针对表【information(消息主表)】的数据库操作Service实现
 * @createDate 2024-01-10 15:01:06
 */
@Service
@Slf4j
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information>
        implements InformationService {

//    @Autowired
//    private ChatService chatService;


    /*新增消息*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveInformation(Information information, Chat chat) {
        boolean save = false;
        save = this.save(information);
        log.info("{}", information.getId());
        chat.setInformationId(information.getId());
        save = WebSocketServer.chatService.save(chat);
        return save;
    }
}




