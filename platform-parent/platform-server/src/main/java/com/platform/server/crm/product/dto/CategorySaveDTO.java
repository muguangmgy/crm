package com.platform.server.crm.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "分类保存请求")
public class CategorySaveDTO {

    @Schema(description = "分类ID（修改时必填）")
    private Long id;

    @Schema(description = "父分类ID（0为顶级）", example = "0")
    private Long parentId;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电子产品")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称最多50字符")
    private String categoryName;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;
}