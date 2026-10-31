package com.platform.server.common.constants;

public class SecurityConstants {

    // JWT配置 (HMAC-SHA256需要至少32字节密钥)
    public static final String JWT_SECRET = "platform-platform-secret-key-2026-secure-minimum-32bytes";


    // 验证码配置
    public static final long CAPTCHA_EXPIRE_MINUTES = 5; // 验证码有效期
    public static final int CAPTCHA_MAX_COUNT_PER_CLIENT = 10; // 每个客户端最大验证码数量

    // 登录失败限制
    public static final int LOGIN_FAIL_MAX_COUNT = 10;
    public static final long LOGIN_FAIL_LOCK_MINUTES = 30;

    // HTTP Header
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // 默认租户ID
    public static final Long DEFAULT_TENANT_ID = 1L;

}