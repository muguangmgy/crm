package com.platform.server.crm.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "产品保存请求")
public class ProductSaveDTO {

    @Schema(description = "产品ID（修改时必填）")
    private Long id;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "产品A")
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称最多100字符")
    private String productName;

    @Schema(description = "产品编码", example = "PROD001")
    @Size(max = 50, message = "产品编码最多50字符")
    private String productCode;

    @Schema(description = "单位", example = "件")
    @Size(max = 20, message = "单位最多20字符")
    private String unit;

    @Schema(description = "规格", example = "100x200")
    @Size(max = 100, message = "规格最多100字符")
    private String spec;

    @Schema(description = "型号", example = "V1.0")
    @Size(max = 50, message = "型号最多50字符")
    private String model;

    @Schema(description = "标准价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "标准价格不能为空")
    private BigDecimal price;

    @Schema(description = "最低价格", example = "80.00")
    private BigDecimal minPrice;

    @Schema(description = "成本价格", example = "60.00")
    private BigDecimal costPrice;

    @Schema(description = "产品描述")
    @Size(max = 500, message = "产品描述最多500字符")
    private String description;

    @Schema(description = "产品图片URL")
    @Size(max = 200, message = "图片URL最多200字符")
    private String imageUrl;

    @Schema(description = "状态：0停售 1在售", example = "1")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;
}