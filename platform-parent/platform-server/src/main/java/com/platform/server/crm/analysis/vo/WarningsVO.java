package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "预警提醒汇总数据")
public class WarningsVO {

    @Schema(description = "即将到期合同列表")
    private List<DashboardVO.ExpiringContractVO> expiringContracts;

    @Schema(description = "逾期收款列表")
    private List<DashboardVO.OverduePaymentVO> overduePayments;

    @Schema(description = "流失预警客户列表")
    private List<ChurnWarningVO> churnCustomers;
}
