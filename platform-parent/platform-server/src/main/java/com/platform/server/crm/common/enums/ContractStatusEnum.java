package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合同状态枚举
 */
@Getter
@AllArgsConstructor
public enum ContractStatusEnum {

    DRAFT(1, "草稿"),
    APPROVAL(2, "审批中"),
    EFFECTIVE(3, "生效"),
    EXPIRED(4, "到期"),
    TERMINATED(5, "终止");

    private final Integer code;
    private final String name;

    public static String getNameByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ContractStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }

    public static ContractStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ContractStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}