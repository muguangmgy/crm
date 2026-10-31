package com.platform.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存防护注解
 * 防止缓存击穿和雪崩
 * 使用分布式锁 + 随机TTL实现
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSafe {

    /**
     * 缓存key前缀
     * 如 "user:detail"、"product:info"
     */
    String cacheKeyPrefix();

    /**
     * SpEL表达式生成key后缀
     * 如 "#userId"
     */
    String keyExpression();

    /**
     * 基础TTL（秒）
     * 默认1小时
     */
    long baseTtlSeconds() default 3600;

    /**
     * TTL随机偏移范围（秒）
     * 最终TTL = baseTtlSeconds + random(0, ttlOffsetSeconds)
     * 默认300秒偏移，防止雪崩
     */
    long ttlOffsetSeconds() default 300;

    /**
     * 获取锁等待时间（秒）
     * 默认3秒
     */
    long lockWaitSeconds() default 3;

    /**
     * 锁持有时间（秒）
     * 需大于数据库查询耗时
     * 默认10秒
     */
    long lockLeaseSeconds() default 10;
}