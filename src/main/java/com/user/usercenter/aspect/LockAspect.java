package com.user.usercenter.aspect;


import com.user.usercenter.contant.MessageConstant;
import com.user.usercenter.exception.BaseException;
import com.user.usercenter.interfaceConfig.RedisAPILimit;
import com.user.usercenter.utils.RedissonLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class LockAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Around("@annotation(redisAPILimit)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RedisAPILimit redisAPILimit) throws Throwable {
        if (redisAPILimit == null) {
            return proceedingJoinPoint.proceed();
        }
        String apiKey = redisAPILimit.apiKey();
        RLock lock = redissonClient.getLock(apiKey);
        if (lock.tryLock(3000, 5000, TimeUnit.MILLISECONDS)) {
            try {
                log.info("线程{} 获取锁成功", Thread.currentThread().getName());
                return proceedingJoinPoint.proceed();
            } finally {
                lock.unlock();
                log.info("线程{} 释放锁", Thread.currentThread().getName());
            }
        } else {
            log.info("线程{} 获取锁失败", Thread.currentThread().getName());
//                return new APIResult(-9, "请求超时");
        }
        return proceedingJoinPoint.proceed();
    }
}
