package com.user.usercenter.aspect;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.user.usercenter.interfaceConfig.RedissonLockAnnotation;
import com.user.usercenter.utils.RedissonLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁/防重复提交 aop
 */
@Aspect
@Component
@Slf4j
public class RedissonLockAop {

    /**
     * 切点，拦截被 @RedissonLockAnnotation 修饰的方法
     */
    @Pointcut("@annotation(com.user.usercenter.interfaceConfig.RedissonLockAnnotation)")
    public void redissonLockPoint() {
    }

    @Around("redissonLockPoint()")
    @ResponseBody
    public boolean checkLock(ProceedingJoinPoint pjp) throws Throwable {
        //当前线程名
        String threadName = Thread.currentThread().getName();
        log.info("线程{}------进入分布式锁aop------", threadName);
        //获取该注解的实例对象
        RedissonLockAnnotation annotation = ((MethodSignature) pjp.getSignature()).
                getMethod().getAnnotation(RedissonLockAnnotation.class);
        //生成分布式锁key的键名，以逗号分隔
        String key = annotation.keyParts();
        if (StringUtils.isEmpty(key)) {
            log.info("线程{} keyParts设置为空，不加锁", threadName);
            return (boolean) pjp.proceed();
        } else {
            log.info("线程{} 要加锁的key={}", threadName, key);
            //获取锁
            if (RedissonLockUtils.tryLock(key, 3000, 5000, TimeUnit.MILLISECONDS)) {
                try {
                    log.info("线程{} 获取锁成功", threadName);
                    return (boolean) pjp.proceed();
                } finally {
                    RedissonLockUtils.unlock(key);
                    log.info("线程{} 释放锁", threadName);
                }
            } else {
                log.info("线程{} 获取锁失败", threadName);
//                return new APIResult(-9, "请求超时");
            }
        }
        return true;

    }
}
