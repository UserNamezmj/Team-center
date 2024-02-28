package com.user.usercenter.interfaceConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * @author 只有影子
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
  /**
   * 获取锁失败时，默认的错误描述
   */
  String errorDesc() default "任务正在处理中，请耐心等待";

  /**
   * SpEL表达式，用于获取锁的key
   * 示例：
   * "#name"则从方法参数中获取name的值作为key
   * "#user.id"则从方法参数中获取user对象中的id作为key
   */
  String[] keys() default {};

  /**
   * key的前缀，为空时取类名+方法名
   */
  String prefix() default "";
}