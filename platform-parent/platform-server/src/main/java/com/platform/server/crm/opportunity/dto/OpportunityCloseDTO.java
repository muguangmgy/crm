package com.platform.server.crm.opportunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商机成交/失败请求")
public class OpportunityCloseDTO {

    @Schema(description = "是否成交", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "成交/失败标识不能为空")
    private Boolean isWin;

    @Schema(description = "实际成交金额（成交时必填）", example = "95000.00")
    private BigDecimal actualAmount;

    @Schema(description = "中标报价单ID", example = "1")
    private Long winnerQuoteId;

    @Schema(description = "备注")
    private String remark;
}