package com.platform.server.crm.payment.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收款记录实体
 */
@Data
@TableName("crm_payment_record")
public class CrmPaymentRecord {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联合同 */
    private Long contractId;

    /** 关联收款计划（可选） */
    private Long planId;

    /** 收款编号（自动生成） */
    private String paymentNo;

    /** 实际收款金额 */
    private BigDecimal paymentAmount;

    /** 实际收款日期 */
    private LocalDate paymentDate;

    /** 收款方式：银行转账/现金/支票等 */
    private String paymentType;

    /** 付款人名称 */
    private String payerName;

    /** 付款账户 */
    private String payerAccount;

    /** 收款账户 */
    private String ourAccount;

    /** 状态：1已确认 2待确认 */
    private Integer status;

    /** 凭证附件URL */
    private String voucherUrl;

    /** 确认时间 */
    private LocalDateTime confirmTime;

    /** 认人 */
    private Long confirmBy;

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