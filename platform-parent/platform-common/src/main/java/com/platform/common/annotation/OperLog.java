package com.platform.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /**
     * 模块名称
     * 如 "用户管理"、"角色管理"
     */
    String module();

    /**
     * 操作类型
     * 如 "新增"、"修改"、"删除"、"查询"
     */
    String operation();

    /**
     * 操作对象类型（可选）
     * 如 "用户"、"角色"、"菜单"
     */
    String targetType() default "";

    /**
     * 操作对象ID的SpEL表达式（可选）
     * 如 "#id"、"#user.id"、"#result.data"
     * 支持从方法参数或返回结果中提取
     */
    String targetIdExpr() default "";
}