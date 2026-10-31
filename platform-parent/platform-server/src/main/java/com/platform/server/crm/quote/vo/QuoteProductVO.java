package com.platform.server.crm.quote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "报价单产品明细响应")
public class QuoteProductVO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "行折扣（%）")
    private BigDecimal discountRate;

    @Schema(description = "行总价")
    private BigDecimal totalPrice;

    @Schema(description = "备注")
    private String remark;
}