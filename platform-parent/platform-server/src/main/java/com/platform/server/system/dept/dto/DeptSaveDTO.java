package com.platform.server.system.dept.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "部门保存请求")
public class DeptSaveDTO {

    @Schema(description = "部门ID（修改时必填）")
    private Long id;

    @Schema(description = "租户ID（平台用户创建顶级部门时可选）", example = "1")
    private Long tenantId;

    @Schema(description = "父部门ID（0为顶级）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "请选择上级部门")
    private Long parentId;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称最多50字符")
    private String deptName;

    @Schema(description = "负责人ID", example = "1")
    private Long leaderId;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;
}