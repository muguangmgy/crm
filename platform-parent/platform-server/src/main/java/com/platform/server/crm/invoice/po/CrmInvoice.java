package com.platform.server.crm.invoice.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 发票实体
 */
@Data
@TableName("crm_invoice")
public class CrmInvoice {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联合同 */
    private Long contractId;

    /** 发票编号（自动生成） */
    private String invoiceNo;

    /** 发票类型：1普通发票 2增值税专用发票 */
    private Integer invoiceType;

    /** 发票金额 */
    private BigDecimal invoiceAmount;

    /** 开票日期 */
    private LocalDate invoiceDate;

    /** 发票状态：1已开票 2已作废 */
    private Integer invoiceStatus;

    /** 备注 */
    private String remark;

    /** 发票文件URL */
    private String fileUrl;

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