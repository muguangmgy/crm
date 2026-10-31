package com.platform.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围类型枚举
 * 用于控制用户可访问的数据范围
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

    /**
     * 全部数据权限
     * 平台超级管理员，无任何数据限制
     */
    ALL(1, "全部数据", 1),

    /**
     * 本租户数据权限
     * 租户管理员，只能访问本租户数据
     * SQL条件：WHERE tenant_id = ?
     */
    TENANT(2, "本租户数据", 2),

    /**
     * 部门及下级数据权限
     * 部门负责人，可访问本部门及所有下级部门数据
     * SQL条件：WHERE dept_id = ? OR dept_path LIKE '/?/%'
     */
    DEPT_AND_CHILD(3, "部门及下级数据", 3),

    /**
     * 本部门数据权限
     * 部门负责人，只能访问本部门数据
     * SQL条件：WHERE dept_id = ?
     */
    DEPT(4, "本部门数据", 4),
    /**
     * 仅本人数据权限
     * 普通用户，只能访问自己创建的数据
     * SQL条件：WHERE create_by = ?
     */
    SELF(5, "仅本人数据", 6),

    /**
     * 自定义数据权限
     * 通过sys_role_dept表关联指定部门列表
     * SQL条件：WHERE dept_id IN (关联部门列表)
     */
    CUSTOM(6, "自定义数据", 5);

    /**
     * 类型编码（对应数据库sys_role.data_scope字段）
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 权限优先级（数值越小权限越大）
     * 用于多角色合并时取最大权限
     */
    private final Integer priority;

    /**
     * 根据编码获取枚举
     */
    public static DataScopeType getByCode(Integer code) {
        if (code == null) {
            return ALL;
        }
        for (DataScopeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return ALL;
    }
}