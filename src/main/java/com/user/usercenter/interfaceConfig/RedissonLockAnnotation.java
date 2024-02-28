package com.user.usercenter.interfaceConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * 添加到方法 ，修饰以@RequestBody JSON对象接收参数的方法
 */
@Target(ElementType.METHOD) //注解在方法
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLockAnnotation {

    /**
     * 指定组成分布式锁的key，以逗号分隔。
     * 如：keyParts="name,age",则分布式锁的key为这两个字段value的拼接
     * key=params.getString("name")+params.getString("age")
     */
    String keyParts();
}
