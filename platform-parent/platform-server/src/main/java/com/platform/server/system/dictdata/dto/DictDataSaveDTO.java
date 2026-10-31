package com.platform.server.system.dictdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "字典项保存请求")
public class DictDataSaveDTO {

    @Schema(description = "字典项ID（修改时必填）")
    private Long id;

    @Schema(description = "租户ID（平台用户创建时可指定）", example = "0")
    private Long tenantId;

    @Schema(description = "字典编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "STATUS")
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50, message = "字典编码最多50字符")
    private String dictCode;

    @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值最多100字符")
    private String itemValue;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "启用")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签最多100字符")
    private String itemLabel;

    @Schema(description = "排序号", example = "1")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "启用状态")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}