package com.platform.server.system.dicttype.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "字典类型保存请求")
public class DictTypeSaveDTO {

    @Schema(description = "字典类型ID（修改时必填）")
    private Long id;

    @Schema(description = "租户ID（平台用户创建时可指定，0表示全局）", example = "0")
    private Long tenantId;

    @Schema(description = "父字典类型ID（0为根节点）", example = "0")
    private Long parentId;

    @Schema(description = "字典编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "STATUS")
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50, message = "字典编码最多50字符")
    private String dictCode;

    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "状态字典")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称最多100字符")
    private String dictName;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "通用状态字典")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;

    @Schema(description = "排序号", example = "0")
    private Integer sort;
}