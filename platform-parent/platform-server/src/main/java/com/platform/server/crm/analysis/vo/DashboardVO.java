package com.platform.server.crm.analysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "首页仪表盘汇总数据")
public class DashboardVO {

    // ========== 顶部卡片数据 ==========

    @Schema(description = "本月新增客户数")
    private Integer newCustomerCount;

    @Schema(description = "本月成交金额")
    private BigDecimal dealAmount;

    @Schema(description = "本月收款金额")
    private BigDecimal receivedAmount;

    @Schema(description = "待跟进客户数")
    private Integer pendingFollowCount;

    // ========== 销售漏斗数据 ==========

    @Schema(description = "销售漏斗数据")
    private List<SalesFunnelVO> salesFunnel;

    // ========== 业绩趋势数据 ==========

    @Schema(description = "业绩趋势数据（近12个月）")
    private List<SalesTrendVO> salesTrend;

    // ========== 预警提醒 ==========

    @Schema(description = "即将到期合同数")
    private Integer expiringContractCount;

    @Schema(description = "逾期收款数")
    private Integer overduePaymentCount;

    @Schema(description = "流失预警客户数")
    private Integer churnWarningCount;

    @Schema(description = "即将到期合同列表（前5条）")
    private List<ExpiringContractVO> expiringContracts;

    @Schema(description = "逾期收款列表（前5条）")
    private List<OverduePaymentVO> overduePayments;

    // ========== 客户分布 ==========

    @Schema(description = "按行业分布数据")
    private List<CustomerDistributionVO> industryDistribution;

    @Schema(description = "按地区分布数据")
    private List<CustomerDistributionVO> regionDistribution;

    @Data
    @Schema(description = "即将到期合同")
    public static class ExpiringContractVO {

        @Schema(description = "合同ID")
        private Long contractId;

        @Schema(description = "合同编号")
        private String contractNo;

        @Schema(description = "客户名称")
        private String customerName;

        @Schema(description = "合同金额")
        private BigDecimal contractAmount;

        @Schema(description = "到期日期")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @Schema(description = "距到期天数")
        private Integer daysToExpire;

        @Schema(description = "负责人姓名")
        private String ownerName;
    }

    @Data
    @Schema(description = "逾期收款")
    public static class OverduePaymentVO {

        @Schema(description = "合同ID")
        private Long contractId;

        @Schema(description = "合同编号")
        private String contractNo;

        @Schema(description = "客户名称")
        private String customerName;

        @Schema(description = "逾期金额")
        private BigDecimal overdueAmount;

        @Schema(description = "计划收款日期")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate planDate;

        @Schema(description = "逾期天数")
        private Integer overdueDays;

        @Schema(description = "负责人姓名")
        private String ownerName;
    }
}