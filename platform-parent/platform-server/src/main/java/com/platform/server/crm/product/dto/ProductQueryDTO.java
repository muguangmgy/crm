package com.platform.server.crm.product.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "产品查询条件")
public class ProductQueryDTO extends PageQuery {

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "状态：0停售 1在售")
    private Integer status;

    @Schema(description = "单位")
    private String unit;
}