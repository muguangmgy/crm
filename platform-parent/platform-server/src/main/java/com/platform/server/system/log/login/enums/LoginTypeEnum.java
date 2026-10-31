package com.platform.server.system.log.login.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型枚举
 * 用于登录日志记录登录操作类型
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    /**
     * 用户登录
     * 用户通过用户名密码登录系统
     */
    LOGIN(1, "登录"),

    /**
     * 用户注销
     * 用户主动退出登录
     */
    LOGOUT(2, "注销"),

    /**
     * Token刷新
     * 使用Refresh Token刷新Access Token
     */
    REFRESH(3, "刷新");

    /**
     * 类型编码（对应数据库login_type字段）
     */
    private final int code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static LoginTypeEnum getByCode(Integer code) {
        if (code == null) {
            return LOGIN;
        }
        for (LoginTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return LOGIN;
    }
}