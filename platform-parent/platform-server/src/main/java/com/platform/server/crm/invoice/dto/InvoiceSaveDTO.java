package com.platform.server.crm.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "发票保存请求")
public class InvoiceSaveDTO {

    @Schema(description = "发票ID（修改时必填）")
    private Long id;

    @Schema(description = "关联合同ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @Schema(description = "发票类型：1普通发票 2增值税专用发票", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发票类型不能为空")
    private Integer invoiceType;

    @Schema(description = "发票金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    @NotNull(message = "发票金额不能为空")
    private BigDecimal invoiceAmount;

    @Schema(description = "开票日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-04-30")
    @NotNull(message = "开票日期不能为空")
    private LocalDate invoiceDate;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;

    @Schema(description = "发票文件URL")
    @Size(max = 200, message = "文件URL最多200字符")
    private String fileUrl;
}