package com.user.usercenter.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 通用类*
 */
@Data
public class BaseException<T> implements Serializable {
    private static final long serialVersionUID = -6455398077292897753L;

    private String message;

    private Integer code;

    private T data;


    public BaseException(String message, Integer code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public BaseException(String message, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public BaseException(Integer code, T data) {
        this("", code, data);
    }
}
