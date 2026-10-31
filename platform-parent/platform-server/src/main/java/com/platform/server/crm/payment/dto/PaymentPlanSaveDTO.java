package com.platform.server.crm.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "收款计划保存请求")
public class PaymentPlanSaveDTO {

    @Schema(description = "计划ID（修改时必填）")
    private Long id;

    @Schema(description = "关联合同ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @Schema(description = "计划收款金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    @NotNull(message = "计划金额不能为空")
    private BigDecimal planAmount;

    @Schema(description = "计划收款日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-05-30")
    @NotNull(message = "计划日期不能为空")
    private LocalDate planDate;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}