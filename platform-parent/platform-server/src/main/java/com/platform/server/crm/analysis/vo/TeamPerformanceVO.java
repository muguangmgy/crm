package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "团队业绩统计")
public class TeamPerformanceVO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "团队人数")
    private Integer memberCount;

    @Schema(description = "成交商机数")
    private Integer dealCount;

    @Schema(description = "成交金额")
    private BigDecimal dealAmount;

    @Schema(description = "合同数")
    private Integer contractCount;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "收款金额")
    private BigDecimal receivedAmount;

    @Schema(description = "新增客户数")
    private Integer newCustomerCount;
}