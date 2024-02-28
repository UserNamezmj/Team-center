package com.user.usercenter.result;


import com.user.usercenter.common.BaseException;
import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -1529831888819565801L;
    private String message;

    private Integer code;

    private T data;


    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 2000;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.code = 2000;
        result.data = object;
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result result = new Result();
        result.message = message;
        result.code = 5000;
        return result;
    }


}
