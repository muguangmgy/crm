package com.platform.server.crm.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "报价单保存请求")
public class QuoteSaveDTO {

    @Schema(description = "报价单ID（修改时必填）")
    private Long id;

    @Schema(description = "关联商机ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商机ID不能为空")
    private Long opportunityId;

    @Schema(description = "关联客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "报价单名称", example = "报价单001")
    @Size(max = 100, message = "报价单名称最多100字符")
    private String quoteName;

    @Schema(description = "报价总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    @NotNull(message = "报价总金额不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "整体折扣率（%）", example = "10.00")
    private BigDecimal discountRate;

    @Schema(description = "最终金额", example = "9000.00")
    private BigDecimal finalAmount;

    @Schema(description = "报价有效期", example = "2026-05-30")
    private LocalDate expireDate;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;

    @Schema(description = "负责人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "负责人不能为空")
    private Long ownerId;

    @Schema(description = "产品明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotEmpty(message = "产品明细不能为空")
    private List<QuoteProductSaveDTO> products;
}