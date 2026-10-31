package com.platform.server.system.menu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 * 用于区分菜单、按钮等不同类型的资源
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    /**
     * 目录类型
     * 只有路由，不对应具体页面，用于组织菜单结构
     */
    DIRECTORY(1, "目录"),

    /**
     * 菜单类型
     * 对应具体页面，有路由和组件路径
     */
    MENU(2, "菜单"),

    /**
     * 按钮类型
     * 页面内的操作按钮，用于权限控制
     */
    BUTTON(3, "按钮");

    /**
     * 类型编码（对应数据库menu_type字段）
     */
    private final int code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static MenuTypeEnum getByCode(Integer code) {
        if (code == null) {
            return MENU;
        }
        for (MenuTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return MENU;
    }
}