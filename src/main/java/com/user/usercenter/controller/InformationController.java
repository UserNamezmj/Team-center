package com.user.usercenter.controller;


import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.model.Information;
import com.user.usercenter.result.Result;
import com.user.usercenter.service.InformationService;
import com.user.usercenter.vo.InformationQuery;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/information")
@CrossOrigin(originPatterns = {"http://112.124.54.146","http://localhost:5173"}, allowCredentials = "true", allowedHeaders = {"*"})
public class InformationController {


    @Resource
    private InformationService informationService;


    /*查看消息内容*/
    @PostMapping("/list")
    public Result<List<Information>> informationList(@RequestBody InformationQuery informationQuery) {
        if (ObjectUtils.isEmpty(informationQuery)) throw new BaseException(MessageConstant.PARAMS_ERROR,4000,"参数为空");
        return null;
    }
}
