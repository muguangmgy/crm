package com.platform.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 * 用于用户、角色、部门、租户等实体的启用/禁用状态
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /**
     * 禁用状态
     * 实体被禁用后不可正常使用（如用户无法登录、角色不生效）
     */
    DISABLED(0, "禁用"),

    /**
     * 启用状态
     * 实体正常可用
     */
    ENABLED(1, "启用");

    /**
     * 状态编码（对应数据库status字段）
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static StatusEnum getByCode(Integer code) {
        if (code == null) {
            return ENABLED;
        }
        for (StatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return ENABLED;
    }

    /**
     * 判断是否为启用状态
     */
    public static boolean isEnabled(Integer code) {
        return ENABLED.getCode() == (code == null ? 1 : code);
    }

    /**
     * 判断是否为禁用状态
     */
    public static boolean isDisabled(Integer code) {
        return DISABLED.getCode() == (code == null ? 0 : code);
    }
}
