package com.user.usercenter.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQuery implements Serializable {

    private long page;

    private long pageSize;


}