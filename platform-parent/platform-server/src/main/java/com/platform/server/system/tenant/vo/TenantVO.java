package com.platform.server.system.tenant.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "租户信息响应")
public class TenantVO {

    @Schema(description = "租户ID", example = "1")
    private Long id;

    @Schema(description = "租户名称", example = "测试公司")
    private String tenantName;

    @Schema(description = "租户编码", example = "test_company")
    private String tenantCode;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "过期时间", example = "2025-12-31 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "联系人", example = "张三")
    private String contactName;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}