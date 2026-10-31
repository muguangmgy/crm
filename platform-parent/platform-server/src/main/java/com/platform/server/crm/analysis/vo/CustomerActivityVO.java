package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "客户活跃度分析")
public class CustomerActivityVO {

    @Schema(description = "活跃度等级：HIGH/MEDIUM/LOW/INACTIVE/NEVER")
    private String activityLevel;

    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "占比")
    private BigDecimal percentage;

    @Schema(description = "平均跟进次数")
    private Integer avgFollowCount;

    @Schema(description = "平均最后跟进距今天数")
    private Integer avgDaysSinceLastFollow;
}