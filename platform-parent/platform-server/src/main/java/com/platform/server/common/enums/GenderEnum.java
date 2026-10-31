package com.platform.server.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 * 用于用户实体的性别字段
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * 未知/未设置
     */
    UNKNOWN(0, "未知"),

    /**
     * 男性
     */
    MALE(1, "男"),

    /**
     * 女性
     */
    FEMALE(2, "女");

    /**
     * 性别编码（对应数据库gender字段）
     */
    private final int code;

    /**
     * 性别描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static GenderEnum getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (GenderEnum gender : values()) {
            if (gender.getCode() == code) {
                return gender;
            }
        }
        return UNKNOWN;
    }
}