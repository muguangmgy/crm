package com.platform.server.crm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跟进方式枚举
 */
@Getter
@AllArgsConstructor
public enum FollowTypeEnum {

    PHONE("电话", "电话沟通"),
    EMAIL("邮件", "邮件联系"),
    VISIT("拜访", "上门拜访"),
    WECHAT("微信", "微信沟通"),
    OTHER("其他", "其他方式");

    private final String code;
    private final String name;

    public static FollowTypeEnum getByCode(String code) {
        if (code == null) return null;
        for (FollowTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}