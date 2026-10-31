package com.platform.common.constants;

/**
 * @ClassName: RedisConstants
 * @Description: redis 相关常量
 * @Author: waynevip@foxmail.com
 * @Date: 2026/04/21 17:35
 */
public class RedisConstants {

    /**
     * 空值占位符
     */
    public static final String NULL_PLACEHOLDER = "__NULL__";

    /**
     * 判断缓存值是否为空占位符
     *
     * @param value 缓存值
     * @return 是否为空占位符
     */
    public static boolean isNullPlaceholder(Object value) {
        return value instanceof String && NULL_PLACEHOLDER.equals(value);
    }


    // Redis Key前缀
    public static final String REDIS_ACCESS_TOKEN_KEY = "paltform:token:access:";
    public static final String REDIS_REFRESH_TOKEN_KEY = "paltform:token:refresh:";
    public static final String REDIS_TOKEN_REFRESH_LOCK_KEY = "paltform:token:refresh:lock:";
    public static final String REDIS_CAPTCHA_COUNT_KEY = "paltform:captcha:count:";
    public static final String REDIS_CAPTCHA_KEY = "paltform:captcha:";
    public static final String REDIS_LOGIN_FAIL_KEY = "paltform:login:fail:";

    /**
     * 用户当前角色 Redis Key 前缀
     */
    public static final String REDIS_USER_ROLE_KEY = "paltform:user:role:";
    /**
     * 角色权限 Redis Key 前缀
     */
    public static final String REDIS_ROLE_PERMISSIONS_KEY = "paltform:role:permissions:";
    /**
     * 角色权限缓存时间
     */
    public static final long ROLE_PERMISSIONS_EXPIRE_HOURS = 1;
    /**
     * 部门及子部门ID Redis Key 前缀
     */
    public static final String REDIS_DEPT_IDS_KEY = "paltform:dept:deptIds:";
    /**
     * 部门及子部门ID缓存时间
     */
    public static final long DEPT_IDS_EXPIRE_MILLERS = 30;
    /**
     * 用户所有角色权限 Redis Key 前缀
     */
    public static final String REDIS_USER_PERMISSIONS_KEY = "paltform:user:permissions:";

    // Redis注解相关Key前缀
    /**
     * 幂等key前缀
     */
    public static final String IDEMPOTENT_KEY_PREFIX = "platform:idempotent";
    /**
     * 防重复提交key前缀
     */
    public static final String REPEAT_LIMIT_KEY_PREFIX = "platform:repeat";
    /**
     * 布隆过滤器key前缀
     */
    public static final String BLOOM_FILTER_KEY_PREFIX = "platform:bloom:";
    /**
     * 缓存锁key前缀
     */
    public static final String CACHE_LOCK_KEY_PREFIX = "platform:lock:";

    /**
     * CRM 首页看板缓存前缀（按登录身份隔离）
     */
    public static final String CRM_DASHBOARD_CACHE_PREFIX = "crm:dashboard";
}
