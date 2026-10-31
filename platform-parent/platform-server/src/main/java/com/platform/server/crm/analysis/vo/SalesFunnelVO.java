package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "销售漏斗分析")
public class SalesFunnelVO {

    @Schema(description = "阶段ID")
    private Long stageId;

    @Schema(description = "阶段名称")
    private String stageName;

    @Schema(description = "阶段顺序")
    private Integer stageOrder;

    @Schema(description = "商机数量")
    private Integer opportunityCount;

    @Schema(description = "商机金额")
    private BigDecimal opportunityAmount;

    @Schema(description = "占比")
    private BigDecimal percentage;

    @Schema(description = "预计成交概率")
    private BigDecimal probability;
}