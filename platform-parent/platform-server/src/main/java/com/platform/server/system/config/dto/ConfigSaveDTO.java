package com.platform.server.system.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "业务参数保存请求")
public class ConfigSaveDTO {

    @Schema(description = "参数ID（修改时必填）")
    private Long id;

    @Schema(description = "租户ID（平台用户创建时可指定，0表示全局）", example = "0")
    private Long tenantId;

    @Schema(description = "分组编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "basic")
    @NotBlank(message = "分组编码不能为空")
    @Size(max = 50, message = "分组编码最多50字符")
    private String groupCode;

    @Schema(description = "参数键", requiredMode = Schema.RequiredMode.REQUIRED, example = "site_name")
    @NotBlank(message = "参数键不能为空")
    @Size(max = 100, message = "参数键最多100字符")
    private String configKey;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "站点名称")
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称最多100字符")
    private String configName;

    @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理平台")
    @NotBlank(message = "参数值不能为空")
    @Size(max = 1000, message = "参数值最多1000字符")
    private String configValue;

    @Schema(description = "值类型：1-string 2-number 3-json", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "值类型不能为空")
    private Integer valueType;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "站点显示名称")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}