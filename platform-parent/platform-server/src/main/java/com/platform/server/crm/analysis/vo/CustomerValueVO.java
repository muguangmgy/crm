package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "客户价值分析")
public class CustomerValueVO {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户类型：1企业 2个人")
    private Integer customerType;

    @Schema(description = "负责人姓名")
    private String ownerName;

    @Schema(description = "累计合同金额")
    private BigDecimal totalContractAmount;

    @Schema(description = "累计收款金额")
    private BigDecimal totalReceivedAmount;

    @Schema(description = "合同数量")
    private Integer contractCount;

    @Schema(description = "商机数量")
    private Integer opportunityCount;

    @Schema(description = "首次成交时间")
    private LocalDateTime firstDealTime;

    @Schema(description = "最近成交时间")
    private LocalDateTime lastDealTime;
}