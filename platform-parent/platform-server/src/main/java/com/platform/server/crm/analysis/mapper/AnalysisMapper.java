package com.platform.server.crm.analysis.mapper;

import com.platform.common.annotation.DataScope;
import com.platform.server.crm.analysis.dto.AnalysisQueryDTO;
import com.platform.server.crm.analysis.vo.*;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnalysisMapper {

    // ========== 销售业绩 ==========

    @DataScope(deptAlias = "u", userAlias = "u")
    List<SalesPerformanceVO> selectSalesPerformance(AnalysisQueryDTO query);

    List<TeamPerformanceVO> selectTeamPerformance(AnalysisQueryDTO query);

    @DataScope(deptAlias = "o", userAlias = "o")
    List<SalesTrendVO> selectSalesTrend(AnalysisQueryDTO query);

    @DataScope(deptAlias = "u", userAlias = "u")
    List<SalesRankingVO> selectSalesRanking(AnalysisQueryDTO query);

    // ========== 客户分析 ==========

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<CustomerDistributionVO> selectCustomerDistributionByIndustry(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<CustomerDistributionVO> selectCustomerDistributionByRegion(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<CustomerDistributionVO> selectCustomerDistributionByLevel(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<CustomerValueVO> selectCustomerValue(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<CustomerActivityVO> selectCustomerActivity(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    List<ChurnWarningVO> selectChurnWarningList(AnalysisQueryDTO query);

    @DataScope(deptAlias = "cu", userAlias = "cu")
    Integer selectChurnWarningCount(AnalysisQueryDTO query);

    // ========== 商机分析 ==========

    @DataScope(deptAlias = "o", userAlias = "o")
    List<SalesFunnelVO> selectSalesFunnel(AnalysisQueryDTO query);

    @DataScope(deptAlias = "o", userAlias = "o")
    List<ConversionRateVO> selectConversionRate(AnalysisQueryDTO query);

    @DataScope(deptAlias = "o", userAlias = "o")
    DealPredictionVO selectDealPrediction(AnalysisQueryDTO query);

    // ========== 合同收款分析 ==========

    @DataScope(deptAlias = "c", userAlias = "c")
    List<ContractExecutionVO> selectContractExecution(AnalysisQueryDTO query);

    @DataScope(deptAlias = "c", userAlias = "c")
    List<ReceivableAnalysisVO> selectReceivableAnalysis(AnalysisQueryDTO query);

    @DataScope(deptAlias = "c", userAlias = "c")
    List<PaymentProgressVO> selectPaymentProgress(AnalysisQueryDTO query);

    // ========== 仪表盘 ==========

    DashboardSummaryVO selectDashboardSummary(@Param("startDate") String startDate,
                                              @Param("endDate") String endDate,
                                              @Param("days") Integer days,
                                              @Param("userId") Long userId,
                                              @Param("deptId") Long deptId,
                                              @Param("scopeDeptIds") String scopeDeptIds,
                                              @Param("tenantId") Long tenantId);

    List<DashboardVO.ExpiringContractVO> selectExpiringContracts(@Param("days") Integer days,
                                                                 @Param("limit") Integer limit,
                                                                 @Param("userId") Long userId,
                                                                 @Param("deptId") Long deptId,
                                                                 @Param("scopeDeptIds") String scopeDeptIds,
                                                                 @Param("tenantId") Long tenantId);

    List<DashboardVO.OverduePaymentVO> selectOverduePayments(@Param("limit") Integer limit,
                                                             @Param("userId") Long userId,
                                                             @Param("deptId") Long deptId,
                                                             @Param("scopeDeptIds") String scopeDeptIds,
                                                             @Param("tenantId") Long tenantId);

    // ========== 内部汇总 ==========

    @Data
    public static class DashboardSummaryVO {
        private Integer newCustomerCount;
        private java.math.BigDecimal dealAmount;
        private java.math.BigDecimal receivedAmount;
        private Integer pendingFollowCount;
        private Integer expiringContractCount;
        private Integer overduePaymentCount;
        private Integer churnWarningCount;
    }
}