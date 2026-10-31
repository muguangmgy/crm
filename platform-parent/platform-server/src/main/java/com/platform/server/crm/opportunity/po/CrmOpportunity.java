package com.platform.server.crm.opportunity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机实体
 */
@Data
@TableName("crm_opportunity")
public class CrmOpportunity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联客户 */
    private Long customerId;

    /** 主要联系人 */
    private Long contactId;

    /** 商机名称 */
    private String oppName;

    /** 商机编号（自动生成） */
    private String oppNo;

    /** 当前阶段ID */
    private Long stageId;

    /** 当前阶段名称（冗余） */
    private String stageName;

    /** 成交概率（继承阶段配置） */
    private Integer probability;

    /** 预期金额 */
    private BigDecimal expectedAmount;

    /** 实际成交金额 */
    private BigDecimal actualAmount;

    /** 预期成交日期 */
    private LocalDate expectedDate;

    /** 实际成交日期 */
    private LocalDate closeDate;

    /** 商机来源 */
    private String source;

    /** 状态：1进行中 2已成交 3已失败 */
    private Integer status;

    /** 负责人 */
    private Long ownerId;

    /** 所属部门ID */
    private Long deptId;

    /** 中标报价单ID */
    private Long winnerQuoteId;

    /** 商机描述 */
    private String description;

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