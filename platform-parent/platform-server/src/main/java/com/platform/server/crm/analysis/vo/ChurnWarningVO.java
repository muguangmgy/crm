package com.platform.server.crm.analysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "流失预警客户")
public class ChurnWarningVO {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户类型：1企业 2个人")
    private Integer customerType;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人姓名")
    private String ownerName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "最后跟进时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFollowTime;

    @Schema(description = "距今天数")
    private Integer daysSinceLastFollow;

    @Schema(description = "累计合同金额")
    private BigDecimal totalContractAmount;

    @Schema(description = "预警等级：HIGH/MEDIUM/LOW")
    private String warningLevel;
}