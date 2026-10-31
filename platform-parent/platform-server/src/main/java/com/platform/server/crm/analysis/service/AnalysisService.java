package com.platform.server.crm.analysis.service;

import com.platform.server.crm.analysis.dto.AnalysisQueryDTO;
import com.platform.server.crm.analysis.vo.*;
import com.platform.server.common.vo.PageVO;

import java.util.List;

public interface AnalysisService {

    // ========== 销售业绩 ==========

    List<SalesPerformanceVO> getSalesPerformance(AnalysisQueryDTO query);

    List<TeamPerformanceVO> getTeamPerformance(AnalysisQueryDTO query);

    List<SalesTrendVO> getSalesTrend(AnalysisQueryDTO query);

    List<SalesRankingVO> getSalesRanking(AnalysisQueryDTO query);

    // ========== 客户分析 ==========

    List<CustomerDistributionVO> getCustomerDistributionByIndustry(AnalysisQueryDTO query);

    List<CustomerDistributionVO> getCustomerDistributionByRegion(AnalysisQueryDTO query);

    List<CustomerDistributionVO> getCustomerDistributionByLevel(AnalysisQueryDTO query);

    List<CustomerValueVO> getCustomerValue(AnalysisQueryDTO query);

    List<CustomerActivityVO> getCustomerActivity(AnalysisQueryDTO query);

    PageVO<ChurnWarningVO> getChurnWarningPage(AnalysisQueryDTO query, Integer page, Integer size);

    // ========== 商机分析 ==========

    List<SalesFunnelVO> getSalesFunnel(AnalysisQueryDTO query);

    List<ConversionRateVO> getConversionRate(AnalysisQueryDTO query);

    DealPredictionVO getDealPrediction(AnalysisQueryDTO query);

    // ========== 合同收款分析 ==========

    List<ContractExecutionVO> getContractExecution(AnalysisQueryDTO query);

    List<ReceivableAnalysisVO> getReceivableAnalysis(AnalysisQueryDTO query);

    List<PaymentProgressVO> getPaymentProgress(AnalysisQueryDTO query);

    // ========== 仪表盘 ==========

    DashboardVO getDashboard();

    WarningsVO getWarnings(AnalysisQueryDTO query);
}