package com.user.usercenter.exception;


import com.user.usercenter.common.ErrorCode;

/**
 * 定义运行时异常
 *
 * @author zmj * *
 */
public class BaseException extends RuntimeException {

    private final int code;

    private final String description;

    public BaseException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public BaseException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BaseException(ErrorCode errCode, String description) {
        super(errCode.getMessage());
        this.code = errCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
