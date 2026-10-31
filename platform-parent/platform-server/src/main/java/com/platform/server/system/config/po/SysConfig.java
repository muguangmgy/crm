package com.platform.server.system.config.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务参数实体
 */
@Data
@TableName("sys_config")
public class SysConfig {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID，0表示全局共享 */
    private Long tenantId;

    /** 分组编码 */
    private String groupCode;

    /** 参数键 */
    private String configKey;

    /** 参数名称 */
    private String configName;

    /** 参数值 */
    private String configValue;

    /** 值类型：1-string 2-number 3-json */
    private Integer valueType;

    /** 备注 */
    private String remark;

    /** 状态：0禁用 1启用 */
    private Integer status;

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
}