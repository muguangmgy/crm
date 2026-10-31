package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "应收账款分析")
public class ReceivableAnalysisVO {

    @Schema(description = "账龄区间")
    private String agingRange;

    @Schema(description = "账龄区间名称")
    private String agingRangeName;

    @Schema(description = "应收金额")
    private BigDecimal receivableAmount;

    @Schema(description = "合同数量")
    private Integer contractCount;

    @Schema(description = "占比")
    private BigDecimal percentage;

    @Schema(description = "风险等级：LOW/MEDIUM/HIGH")
    private String riskLevel;
}