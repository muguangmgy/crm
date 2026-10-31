package com.platform.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性控制注解
 * 防止同一请求多次执行产生重复结果，默认返回第一次结果
 * 使用Redis SETNX实现
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * SpEL表达式生成唯一key
     * 如 "#order.orderNo:#order.userId"
     */
    String keyExpression();

    /**
     * 幂等有效期（秒）
     * 默认24小时
     */
    long expireSeconds() default 86400;

    /**
     * 拦截时的提示消息
     */
    String message() default "请求正在处理中，请勿重复提交";

    /**
     * 重复请求时是否返回缓存结果
     * true: 有效期内后续请求返回第一次执行的结果（不抛异常）
     * false: 后续请求抛异常拦截 该场景通常使用RepeatLimit
     */
    boolean returnCached() default true;

    /**
     * 等待第一次请求完成的最大时间（秒）
     * 仅当 returnCached=true 时生效
     * 默认5秒
     */
    int waitSeconds() default 5;
}