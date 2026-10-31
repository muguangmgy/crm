package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户类型枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerTypeEnum {

    ENTERPRISE(1, "企业"),
    PERSONAL(2, "个人");

    private final Integer code;
    private final String name;

    public static CustomerTypeEnum getByCode(Integer code) {
        if (code == null) return null;
        for (CustomerTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}