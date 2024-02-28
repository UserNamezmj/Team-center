package com.user.usercenter.aspect;

import com.user.usercenter.interfaceConfig.DistributedLock;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 分布式锁切面类
 * @author 只有影子
 */
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private RedissonClient redissonClient;
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String redisKey = getRedisKey(joinPoint, distributedLock);
        log.info("拼接后的redisKey为：" + redisKey);
        RLock lock = redissonClient.getLock(redisKey);
        if (!lock.tryLock()) {
            // 可以使用自己的异常类，演示用RuntimeException
            throw new RuntimeException(distributedLock.errorDesc());
        }
        // 执行被切面的方法
        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 动态解密参数，拼接redisKey
     * @param joinPoint
     * @param distributedLock  注解
     * @return
     */
    private String getRedisKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EvaluationContext context = new MethodBasedEvaluationContext(TypedValue.NULL, method, joinPoint.getArgs(), PARAMETER_NAME_DISCOVERER);
        StringBuilder redisKey = new StringBuilder();
        // 拼接redis前缀
        if (StringUtil.isNotBlank(distributedLock.prefix())) {
            redisKey.append(distributedLock.prefix()).append(":");
        } else {
            // 获取类名
            String className = joinPoint.getTarget().getClass().getSimpleName();
            // 获取方法名
            String methodName = joinPoint.getSignature().getName();
            redisKey.append(className).append(":").append(methodName).append(":");
        }

        ExpressionParser parser = new SpelExpressionParser();
        for (String key : distributedLock.keys()) {
            // keys是个SpEL表达式
            Expression expression = parser.parseExpression(key);
            Object value = expression.getValue(context);
            redisKey.append(ObjectUtils.nullSafeToString(value));
        }
        return redisKey.toString();
    }
}