package com.platform.server.crm.quote.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价单产品明细实体
 */
@Data
@TableName("crm_quote_product")
public class CrmQuoteProduct {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报价单ID */
    private Long quoteId;

    /** 产品ID */
    private Long productId;

    /** 产品名称（冗余） */
    private String productName;

    /** 单位 */
    private String unit;

    /** 数量 */
    private BigDecimal quantity;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 行折扣（%） */
    private BigDecimal discountRate;

    /** 行总价 */
    private BigDecimal totalPrice;

    /** 备注 */
    private String remark;
}