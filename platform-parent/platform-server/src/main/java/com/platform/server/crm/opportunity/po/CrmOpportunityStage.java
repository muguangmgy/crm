package com.platform.server.crm.opportunity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商机阶段配置实体
 */
@Data
@TableName("crm_opportunity_stage")
public class CrmOpportunityStage {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 阶段名称 */
    private String stageName;

    /** 阶段编码 */
    private String stageCode;

    /** 默认成交概率（0-100） */
    private Integer probability;

    /** 排序（决定流程顺序） */
    private Integer sort;

    /** 是否成交阶段：0否 1是 */
    private Integer isWin;

    /** 是否失败阶段：0否 1是 */
    private Integer isLost;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}