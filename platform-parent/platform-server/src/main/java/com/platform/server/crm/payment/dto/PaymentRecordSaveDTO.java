package com.platform.server.crm.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "收款记录保存请求")
public class PaymentRecordSaveDTO {

    @Schema(description = "记录ID（修改时必填）")
    private Long id;

    @Schema(description = "关联合同ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @Schema(description = "关联收款计划ID（可选）")
    private Long planId;

    @Schema(description = "实际收款金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @NotNull(message = "收款金额不能为空")
    private BigDecimal paymentAmount;

    @Schema(description = "实际收款日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-05-01")
    @NotNull(message = "收款日期不能为空")
    private LocalDate paymentDate;

    @Schema(description = "收款方式", example = "bank_transfer")
    private String paymentType;

    @Schema(description = "付款人名称", example = "张三")
    @Size(max = 100, message = "付款人名称最多100字符")
    private String payerName;

    @Schema(description = "付款账户", example = "6222****1234")
    @Size(max = 50, message = "付款账户最多50字符")
    private String payerAccount;

    @Schema(description = "收款账户", example = "公司主账户")
    @Size(max = 50, message = "收款账户最多50字符")
    private String ourAccount;

    @Schema(description = "凭证附件URL")
    @Size(max = 200, message = "凭证URL最多200字符")
    private String voucherUrl;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}