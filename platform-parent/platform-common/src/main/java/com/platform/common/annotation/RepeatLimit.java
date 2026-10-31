package com.platform.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交注解
 * 短时间内禁止相同请求重复调用
 * 使用Redis计数器实现
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatLimit {

    /**
     * SpEL表达式生成唯一key
     */
    String keyExpression();

    /**
     * 防重间隔时间（秒）
     * 默认5秒
     */
    long intervalSeconds() default 5;

    /**
     * 拦截时的提示消息
     */
    String message() default "请勿重复提交";
}