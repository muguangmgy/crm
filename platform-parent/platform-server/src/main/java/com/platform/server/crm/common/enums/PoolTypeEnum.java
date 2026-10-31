package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户池类型枚举
 */
@Getter
@AllArgsConstructor
public enum PoolTypeEnum {

    PRIVATE(1, "私有池"),
    PUBLIC(2, "公海池");

    private final Integer code;
    private final String name;

    public static PoolTypeEnum getByCode(Integer code) {
        if (code == null) return null;
        for (PoolTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}