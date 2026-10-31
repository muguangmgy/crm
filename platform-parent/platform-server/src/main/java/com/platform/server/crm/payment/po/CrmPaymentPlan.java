package com.platform.server.crm.payment.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收款计划实体
 */
@Data
@TableName("crm_payment_plan")
public class CrmPaymentPlan {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联合同 */
    private Long contractId;

    /** 计划编号（自动生成） */
    private String planNo;

    /** 计划收款金额 */
    private BigDecimal planAmount;

    /** 计划收款日期 */
    private LocalDate planDate;

    /** 已收款金额 */
    private BigDecimal receivedAmount;

    /** 收款状态：0未收 1部分收款 2已收完 */
    private Integer receivedStatus;

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