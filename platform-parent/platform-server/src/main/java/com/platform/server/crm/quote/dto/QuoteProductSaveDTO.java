package com.platform.server.crm.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "报价单产品明细保存请求")
public class QuoteProductSaveDTO {

    @Schema(description = "明细ID（修改时必填，删除时传负数）")
    private Long id;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @Schema(description = "产品名称", example = "产品A")
    @Size(max = 100, message = "产品名称最多100字符")
    private String productName;

    @Schema(description = "单位", example = "件")
    @Size(max = 20, message = "单位最多20字符")
    private String unit;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @Schema(description = "行折扣（%）", example = "5.00")
    private BigDecimal discountRate;

    @Schema(description = "行总价", example = "950.00")
    private BigDecimal totalPrice;

    @Schema(description = "备注")
    @Size(max = 200, message = "备注最多200字符")
    private String remark;
}