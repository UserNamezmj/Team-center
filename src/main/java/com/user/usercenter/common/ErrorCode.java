package com.user.usercenter.common;

public enum ErrorCode {

    UNKNOWN_ERROR(4001, "未知错误"),
    INVALID_ERROR(4002, "无效输入"),
    DATABASE_ERROR(4003, "数据查询错误"),
    ACCOUNT_NOT_POWER(4001, "无权限"),
    ACCOUNT_NOT_ROLE(4003, "无修改权限");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
