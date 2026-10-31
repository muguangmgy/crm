package com.platform.server.crm.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "线索转商机请求")
public class LeadConvertDTO {

    @Schema(description = "线索ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "线索ID不能为空")
    private Long leadId;

    @Schema(description = "关联客户ID（可空，不填则自动创建）")
    private Long customerId;

    @Schema(description = "商机名称", example = "某公司采购商机")
    private String oppName;

    @Schema(description = "预期金额", example = "100000.00")
    private java.math.BigDecimal expectedAmount;

    @Schema(description = "预期成交日期", example = "2026-06-30")
    private java.time.LocalDate expectedDate;
}