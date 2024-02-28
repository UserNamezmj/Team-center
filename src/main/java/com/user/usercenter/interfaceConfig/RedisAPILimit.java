package com.user.usercenter.interfaceConfig;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Documented
@Inherited
@Target(ElementType.METHOD)
@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisAPILimit {

    //限制数量
    int limit() default 5;
    //标识 时间段 5秒
    int sec() default 5;
    String apiKey() default "";



}
