package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "业绩排名")
public class SalesRankingVO {

    @Schema(description = "排名")
    private Integer rankNum;

    @Schema(description = "销售员ID")
    private Long userId;

    @Schema(description = "销售员姓名")
    private String userName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "成交金额")
    private BigDecimal dealAmount;

    @Schema(description = "成交商机数")
    private Integer dealCount;

    @Schema(description = "收款金额")
    private BigDecimal receivedAmount;
}