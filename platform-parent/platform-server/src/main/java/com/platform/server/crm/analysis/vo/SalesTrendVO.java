package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "业绩趋势分析")
public class SalesTrendVO {

    @Schema(description = "时间周期（如：2024-01）")
    private String period;

    @Schema(description = "成交金额")
    private BigDecimal dealAmount;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "收款金额")
    private BigDecimal receivedAmount;

    @Schema(description = "新增客户数")
    private Integer newCustomerCount;

    @Schema(description = "成交商机数")
    private Integer dealCount;
}