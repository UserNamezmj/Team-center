package com.user.usercenter.common;

public enum upload {

    UPLOAD_JPG("jpg"),
    UPLOAD_PNG("png");

    private String name;

    //全参构造方法
    upload(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
