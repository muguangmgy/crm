package com.platform.server.crm.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "产品信息响应")
public class ProductVO {

    @Schema(description = "产品ID")
    private Long id;

    @Schema(description = "产品编号")
    private String productNo;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "规格")
    private String spec;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "标准价格")
    private BigDecimal price;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "成本价格")
    private BigDecimal costPrice;

    @Schema(description = "产品描述")
    private String description;

    @Schema(description = "产品图片URL")
    private String imageUrl;

    @Schema(description = "状态：0停售 1在售")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}