package com.user.usercenter.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.user.usercenter.model.Chat;
import com.user.usercenter.model.Information;

/**
* @author zmj
* @description 针对表【information(消息主表)】的数据库操作Service
* @createDate 2024-01-10 15:01:06
*/
public interface InformationService extends IService<Information> {

    boolean saveInformation(Information information, Chat chat);
}
