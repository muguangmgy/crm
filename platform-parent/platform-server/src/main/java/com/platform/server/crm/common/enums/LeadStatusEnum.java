package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 线索状态枚举
 */
@Getter
@AllArgsConstructor
public enum LeadStatusEnum {

    NEW(1, "新建"),
    FOLLOWING(2, "跟进中"),
    CONVERTED(3, "已转商机"),
    INVALID(4, "已失效");

    private final Integer code;
    private final String name;

    public static LeadStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (LeadStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        LeadStatusEnum status = getByCode(code);
        return status != null ? status.getName() : "";
    }
}