package com.platform.common.constants;

/**
 * @ClassName: PlatformConstants
 * @Description: 平台常量
 * @Author: waynevip@foxmail.com
 * @Date: 2026/04/21 13:08
 */
public class PlatformConstants {

    //平台角色编码前缀
    public static final String PLATFORM_ROLE_CODE_PREFIX = "PLATFORM_";

    //租户角色编码前缀
    public static final String TENANT_ROLE_CODE_PREFIX = "TENANT_";

    //租户超级管理员角色编码
    public static final String TENANT_ADMIN_ROLE_CODE = "TENANT_ADMIN";

    /**
     * 登录类型：登录
     */
    public static final int LOGIN_TYPE_LOGIN = 1;
    /**
     * 登录类型：注销
     */
    public static final int LOGIN_TYPE_LOGOUT = 2;
    /**
     * 状态：成功
     */
    public static final int STATUS_SUCCESS = 1;
    /**
     * 状态：失败
     */
    public static final int STATUS_FAIL = 0;
}
