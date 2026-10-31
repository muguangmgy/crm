package com.platform.server.system.dictdata.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典项实体
 */
@Data
@TableName("sys_dict_data")
public class SysDictData {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 字典编码 */
    private String dictCode;

    /** 字典值 */
    private String itemValue;

    /** 字典标签 */
    private String itemLabel;

    /** 排序号 */
    private Integer sort;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 备注 */
    private String remark;

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