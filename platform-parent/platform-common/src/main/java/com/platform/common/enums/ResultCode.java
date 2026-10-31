package com.platform.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码
    PARAM_ERROR(1000, "参数错误"),
    LOGIN_FAILED(1001, "用户名或密码错误"),
    IP_NOT_ALLOWED(1002, "IP地址不在白名单中"),
    USER_DISABLED(1003, "账号已被禁用"),
    USER_NOT_FOUND(1004, "用户信息不存在"),
    TENANT_NOT_FOUND(1005, "租户信息不存在"),
    TENANT_EXPIRED(1006, "租户已过期"),
    TENANT_DISABLED(1007, "租户已禁用"),
    USER_ROLE_NOT_EXIST(1008, "用户未分配权限"),
    PASSWORD_ERROR(1010, "密码错误"),

    //只允许单点登录
    USER_ONLY_ONE_LOGIN(1006, "账号已在其他地点登录"),

    // Redis注解相关错误码
    /**
     * 幂等校验失败
     */
    IDEMPOTENT_ERROR(40001, "请求正在处理中"),
    /**
     * 重复提交
     */
    REPEAT_LIMIT_ERROR(40002, "请勿重复提交"),
    /**
     * 布隆过滤器校验失败
     */
    BLOOM_FILTER_ERROR(40003, "数据不存在"),
    /**
     * 缓存获取锁失败
     */
    CACHE_LOCK_ERROR(40004, "系统繁忙，请稍后重试");

    private final int code;
    private final String message;
}
