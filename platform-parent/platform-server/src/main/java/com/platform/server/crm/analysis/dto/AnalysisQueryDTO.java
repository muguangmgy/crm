package com.platform.server.crm.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "分析查询条件")
public class AnalysisQueryDTO {

    @Schema(description = "租户ID（内部填充：ALL 数据权限时 TenantLine 会跳过，分析需显式隔离）", hidden = true)
    private Long tenantId;

    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-12-31")
    private LocalDate endDate;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "销售员ID", example = "1")
    private Long userId;

    @Schema(description = "数据权限部门ID列表（逗号分隔，内部使用）", hidden = true)
    private String scopeDeptIds;

    @Schema(description = "客户类型：1企业 2个人", example = "1")
    private Integer customerType;

    @Schema(description = "时间维度：MONTH/QUARTER/YEAR", example = "MONTH")
    private String timeDimension;

    @Schema(description = "行业", example = "IT")
    private String industry;

    @Schema(description = "省份", example = "北京")
    private String province;

    @Schema(description = "返回数量限制", example = "10")
    private Integer limit;

    @Schema(description = "偏移量（用于分页）", example = "0")
    private Integer offset;
}