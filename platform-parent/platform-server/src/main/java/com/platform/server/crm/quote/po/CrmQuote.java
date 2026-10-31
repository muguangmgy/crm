package com.platform.server.crm.quote.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报价单实体
 */
@Data
@TableName("crm_quote")
public class CrmQuote {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联商机 */
    private Long opportunityId;

    /** 关联客户 */
    private Long customerId;

    /** 报价单编号（自动生成） */
    private String quoteNo;

    /** 报价单名称 */
    private String quoteName;

    /** 报价总金额 */
    private BigDecimal totalAmount;

    /** 整体折扣率（%） */
    private BigDecimal discountRate;

    /** 最终金额 */
    private BigDecimal finalAmount;

    /** 状态：1草稿 2已发送 3已接受 4已拒绝 */
    private Integer status;

    /** 报价有效期 */
    private LocalDate expireDate;

    /** 发送时间 */
    private LocalDateTime sendTime;

    /** 接受时间 */
    private LocalDateTime acceptTime;

    /** 备注 */
    private String remark;

    /** 负责人 */
    private Long ownerId;

    /** 所属部门ID */
    private Long deptId;

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