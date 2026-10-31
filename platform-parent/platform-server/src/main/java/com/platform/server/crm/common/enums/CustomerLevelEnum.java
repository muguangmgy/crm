package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户等级枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerLevelEnum {

    NORMAL(1, "普通"),
    IMPORTANT(2, "重要"),
    VIP(3, "VIP");

    private final Integer code;
    private final String name;

    public static CustomerLevelEnum getByCode(Integer code) {
        if (code == null) return null;
        for (CustomerLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}