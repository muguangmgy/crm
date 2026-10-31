package com.platform.server.crm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "标签保存请求")
public class TagSaveDTO {

    @Schema(description = "标签ID（修改时必填）")
    private Long id;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagName;

    @Schema(description = "标签颜色", example = "#FF5733")
    private String tagColor;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;
}