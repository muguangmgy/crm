package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "成交预测")
public class DealPredictionVO {

    @Schema(description = "预测类型", example = "管道预测")
    private String period;

    @Schema(description = "高概率管道期望金额合计（概率>=70%，未加权）")
    private BigDecimal highProbabilityAmount;

    @Schema(description = "中概率管道期望金额合计（概率30%-69%，未加权）")
    private BigDecimal mediumProbabilityAmount;

    @Schema(description = "低概率管道期望金额合计（概率&lt;30%，未加权）")
    private BigDecimal lowProbabilityAmount;

    @Schema(description = "加权预测成交额（期望金额×概率）")
    private BigDecimal predictedAmount;

    @Schema(description = "进行中商机数量")
    private Integer opportunityCount;

    @Schema(description = "时间范围内已成交金额")
    private BigDecimal closedAmount;
}