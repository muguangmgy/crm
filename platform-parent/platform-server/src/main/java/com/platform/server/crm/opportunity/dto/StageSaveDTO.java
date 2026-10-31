package com.platform.server.crm.opportunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "商机阶段保存请求")
public class StageSaveDTO {

    @Schema(description = "阶段ID（修改时必填）")
    private Long id;

    @Schema(description = "阶段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "初步接触")
    @NotBlank(message = "阶段名称不能为空")
    @Size(max = 50, message = "阶段名称最多50字符")
    private String stageName;

    @Schema(description = "阶段编码", example = "CONTACT")
    @Size(max = 50, message = "阶段编码最多50字符")
    private String stageCode;

    @Schema(description = "成交概率（0-100）", example = "10")
    @Min(value = 0, message = "成交概率最小为0")
    @Max(value = 100, message = "成交概率最大为100")
    private Integer probability;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "是否成交阶段：0否 1是", example = "0")
    private Integer isWin;

    @Schema(description = "是否失败阶段：0否 1是", example = "0")
    private Integer isLost;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;
}