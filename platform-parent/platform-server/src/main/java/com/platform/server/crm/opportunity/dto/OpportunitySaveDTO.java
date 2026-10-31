package com.platform.server.crm.opportunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "商机保存请求")
public class OpportunitySaveDTO {

    @Schema(description = "商机ID（修改时必填）")
    private Long id;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户不能为空")
    private Long customerId;

    @Schema(description = "主要联系人ID", example = "1")
    private Long contactId;

    @Schema(description = "商机名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "某某项目商机")
    @NotBlank(message = "商机名称不能为空")
    @Size(max = 100, message = "商机名称最多100字符")
    private String oppName;

    @Schema(description = "阶段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "阶段不能为空")
    private Long stageId;

    @Schema(description = "预期金额", example = "100000.00")
    private BigDecimal expectedAmount;

    @Schema(description = "预期成交日期", example = "2026-06-30")
    private LocalDate expectedDate;

    @Schema(description = "商机来源", example = "website")
    @Size(max = 50, message = "商机来源最多50字符")
    private String source;

    @Schema(description = "负责人ID（新增时可不传，默认当前登录用户）", example = "1")
    private Long ownerId;

    @Schema(description = "商机描述")
    @Size(max = 500, message = "商机描述最多500字符")
    private String description;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}