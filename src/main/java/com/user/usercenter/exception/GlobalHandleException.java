package com.user.usercenter.exception;


import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandleException {


    @ExceptionHandler(BaseException.class)
    public Result businessEcption(BaseException e) {

//        return  new Result.success();
        return Result.error(e.getDescription());

    }
}
