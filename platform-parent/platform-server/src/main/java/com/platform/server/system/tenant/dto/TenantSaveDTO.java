package com.platform.server.system.tenant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "租户保存请求")
public class TenantSaveDTO {

    @Schema(description = "租户ID（修改时必填）")
    private Long id;

    @Schema(description = "租户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "测试公司")
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 50, message = "租户名称最多50字符")
    private String tenantName;

    @Schema(description = "租户编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_company")
    @NotBlank(message = "租户编码不能为空")
    @Size(max = 30, message = "租户编码最多30字符")
    private String tenantCode;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "过期时间", example = "2025-12-31 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "联系人", example = "张三")
    @Size(max = 50, message = "联系人最多50字符")
    private String contactName;

    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 20, message = "联系电话最多20字符")
    private String contactPhone;
}