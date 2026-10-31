package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "收款进度分析")
public class PaymentProgressVO {

    @Schema(description = "合同ID")
    private Long contractId;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "已收款金额")
    private BigDecimal receivedAmount;

    @Schema(description = "收款进度")
    private BigDecimal progressRate;

    @Schema(description = "待收款金额")
    private BigDecimal pendingAmount;

    @Schema(description = "逾期金额")
    private BigDecimal overdueAmount;

    @Schema(description = "下次收款日期")
    private String nextPaymentDate;

    @Schema(description = "收款状态：NORMAL/OVERDUE/COMPLETED")
    private String paymentStatus;
}