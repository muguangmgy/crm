package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "转化率分析")
public class ConversionRateVO {

    @Schema(description = "阶段ID")
    private Long stageId;

    @Schema(description = "阶段名称")
    private String stageName;

    @Schema(description = "阶段顺序")
    private Integer stageOrder;

    @Schema(description = "当前阶段商机数（存量快照近似）")
    private Integer enterCount;

    @Schema(description = "后续阶段商机数（已越过本阶段的近似）")
    private Integer exitCount;

    @Schema(description = "推进率：后续阶段数/(当前+后续)*100")
    private BigDecimal conversionRate;

    @Schema(description = "平均停留天数（距商机最近更新时间）")
    private Integer avgStayDays;
}