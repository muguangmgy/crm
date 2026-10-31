package com.platform.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 布隆过滤器注解
 * 查询前检查数据是否存在，不存在直接返回，防止缓存穿透
 * 基于Redis Bitmap实现，启动时自动初始化
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BloomCheck {

    /**
     * 布隆过滤器名称
     * 如 "user-bloom"、"product-bloom"
     */
    String bloomName();

    /**
     * 初始化查询SQL
     * 如 "SELECT id FROM sys_user WHERE status = 1"
     */
    String initQuery();

    /**
     * SpEL表达式提取检查值
     * 如 "#userId"、"#productId"
     */
    String valueExpression();

    /**
     * 拦截时的提示消息
     */
    String message() default "数据不存在";

    /**
     * 预期元素数量
     * 略大于实际量：预估数据量 × 1.2~1.5，预留增长空间
     */
    long expectedItems() default 10000;

    /**
     * 误判率
     * 普通业务：0.01 (1%)
     * 关键业务：0.001 (0.1%)
     */
    double falseProbability() default 0.01;
}