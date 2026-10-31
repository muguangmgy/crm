package com.platform.server.system.dicttype.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型实体
 */
@Data
@TableName("sys_dict_type")
public class SysDictType {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID，0表示全局共享 */
    private Long tenantId;

    /** 父字典类型ID（0为根节点） */
    private Long parentId;

    /** 字典编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 排序号 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 租户名称（JOIN查询结果，不持久化） */
    @TableField(exist = false)
    private String tenantName;
}