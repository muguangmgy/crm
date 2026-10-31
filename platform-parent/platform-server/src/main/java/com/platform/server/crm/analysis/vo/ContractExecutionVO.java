package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "合同执行分析")
public class ContractExecutionVO {

    @Schema(description = "状态：DRAFT/APPROVING/EFFECTIVE/EXPIRED/TERMINATED")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "合同数量")
    private Integer contractCount;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "占比")
    private BigDecimal percentage;

    @Schema(description = "即将到期数量（30天内）")
    private Integer expiringCount;

    @Schema(description = "已到期未终止数量")
    private Integer expiredNotTerminatedCount;
}