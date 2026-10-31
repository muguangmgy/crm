package com.platform.server.system.dictdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "字典项批量保存请求")
public class DictDataBatchSaveDTO {

    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "STATUS")
    @NotBlank(message = "字典类型编码不能为空")
    private String dictCode;

    @Schema(description = "租户ID", example = "0")
    private Long tenantId;

    @Schema(description = "字典项列表（新增和修改）")
    @Valid
    private List<DictDataItemDTO> items;

    @Schema(description = "删除的字典项ID列表", example = "[1, 2]")
    private List<Long> deleteIds;

    @Data
    @Schema(description = "字典项数据")
    public static class DictDataItemDTO {

        @Schema(description = "字典项ID（修改时必填）", example = "1")
        private Long id;

        @Schema(description = "字典项标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "启用")
        @NotBlank(message = "字典项标签不能为空")
        private String itemLabel;

        @Schema(description = "字典项值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotBlank(message = "字典项值不能为空")
        private String itemValue;

        @Schema(description = "排序号", example = "0")
        private Integer sort;

        @Schema(description = "状态：0禁用 1启用", example = "1")
        private Integer status;

        @Schema(description = "备注", example = "启用状态")
        private String remark;
    }
}