package com.platform.server.crm.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户归属权限注解
 * 用于标记需要校验客户归属的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerOwner {

    /**
     * 是否必须为负责人
     * true: 只有负责人可以操作
     * false: 私有池客户需要校验归属，公海池客户不做限制
     */
    boolean requireOwner() default true;
}