package com.platform.server.crm.analysis.controller;

import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.analysis.dto.AnalysisQueryDTO;
import com.platform.server.crm.analysis.service.AnalysisService;
import com.platform.server.crm.analysis.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "数据分析", description = "CRM数据分析报表接口")
@RestController
@RequestMapping("/api/crm/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    // ========== 销售业绩接口 ==========

    @Operation(summary = "销售员业绩统计", description = "按销售员统计成交额、商机数、合同数")
    @ApiResponse(responseCode = "200", description = "成功返回销售员业绩列表")
    @PreAuthorize("hasAuthority('crm:analysis:sales')")
    @GetMapping("/sales/performance")
    public Result<List<SalesPerformanceVO>> getSalesPerformance(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getSalesPerformance(query));
    }

    @Operation(summary = "团队业绩统计", description = "按部门统计团队业绩")
    @ApiResponse(responseCode = "200", description = "成功返回团队业绩列表")
    @PreAuthorize("hasAuthority('crm:analysis:sales')")
    @GetMapping("/sales/team")
    public Result<List<TeamPerformanceVO>> getTeamPerformance(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getTeamPerformance(query));
    }

    @Operation(summary = "业绩趋势分析", description = "月度/季度/年度业绩趋势图表")
    @ApiResponse(responseCode = "200", description = "成功返回业绩趋势数据")
    @PreAuthorize("hasAuthority('crm:analysis:sales')")
    @GetMapping("/sales/trend")
    public Result<List<SalesTrendVO>> getSalesTrend(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getSalesTrend(query));
    }

    @Operation(summary = "业绩排名", description = "销售员业绩排名")
    @ApiResponse(responseCode = "200", description = "成功返回业绩排名列表")
    @PreAuthorize("hasAuthority('crm:analysis:sales')")
    @GetMapping("/sales/ranking")
    public Result<List<SalesRankingVO>> getSalesRanking(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getSalesRanking(query));
    }

    // ========== 客户分析接口 ==========

    @Operation(summary = "客户分布分析", description = "按行业统计客户分布")
    @ApiResponse(responseCode = "200", description = "成功返回客户分布数据")
    @PreAuthorize("hasAuthority('crm:analysis:customer')")
    @GetMapping("/customer/distribution")
    public Result<List<CustomerDistributionVO>> getCustomerDistribution(
            @Parameter(description = "分布维度：industry/region/level", example = "industry")
            @RequestParam(defaultValue = "industry") String dimension,
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        switch (dimension) {
            case "region":
                return Result.success(analysisService.getCustomerDistributionByRegion(query));
            case "level":
                return Result.success(analysisService.getCustomerDistributionByLevel(query));
            default:
                return Result.success(analysisService.getCustomerDistributionByIndustry(query));
        }
    }

    @Operation(summary = "客户价值分析", description = "客户贡献金额排序，识别高价值客户")
    @ApiResponse(responseCode = "200", description = "成功返回客户价值分析数据")
    @PreAuthorize("hasAuthority('crm:analysis:customer')")
    @GetMapping("/customer/value")
    public Result<List<CustomerValueVO>> getCustomerValue(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getCustomerValue(query));
    }

    @Operation(summary = "客户活跃度分析", description = "跟进频率、最近跟进时间分析")
    @ApiResponse(responseCode = "200", description = "成功返回客户活跃度数据")
    @PreAuthorize("hasAuthority('crm:analysis:customer')")
    @GetMapping("/customer/activity")
    public Result<List<CustomerActivityVO>> getCustomerActivity(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getCustomerActivity(query));
    }

    @Operation(summary = "流失预警列表", description = "无跟进超过阈值客户预警")
    @ApiResponse(responseCode = "200", description = "成功返回流失预警客户分页列表")
    @PreAuthorize("hasAuthority('crm:analysis:customer')")
    @GetMapping("/customer/churn-warning")
    public Result<PageVO<ChurnWarningVO>> getChurnWarning(
            @Parameter(description = "查询条件") AnalysisQueryDTO query,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(analysisService.getChurnWarningPage(query, page, size));
    }

    // ========== 商机分析接口 ==========

    @Operation(summary = "销售漏斗分析", description = "各阶段商机数量、金额分布")
    @ApiResponse(responseCode = "200", description = "成功返回销售漏斗数据")
    @PreAuthorize("hasAuthority('crm:analysis:opportunity')")
    @GetMapping("/opportunity/funnel")
    public Result<List<SalesFunnelVO>> getSalesFunnel(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getSalesFunnel(query));
    }

    @Operation(summary = "转化率分析", description = "各阶段转化率、整体转化率")
    @ApiResponse(responseCode = "200", description = "成功返回转化率数据")
    @PreAuthorize("hasAuthority('crm:analysis:opportunity')")
    @GetMapping("/opportunity/conversion")
    public Result<List<ConversionRateVO>> getConversionRate(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getConversionRate(query));
    }

    @Operation(summary = "成交预测", description = "基于商机金额×概率计算预测成交额")
    @ApiResponse(responseCode = "200", description = "成功返回成交预测数据")
    @PreAuthorize("hasAuthority('crm:analysis:opportunity')")
    @GetMapping("/opportunity/prediction")
    public Result<DealPredictionVO> getDealPrediction(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getDealPrediction(query));
    }

    // ========== 合同收款分析接口 ==========

    @Operation(summary = "合同执行分析", description = "合同状态分布、到期预警")
    @ApiResponse(responseCode = "200", description = "成功返回合同执行分析数据")
    @PreAuthorize("hasAuthority('crm:analysis:contract')")
    @GetMapping("/contract/execution")
    public Result<List<ContractExecutionVO>> getContractExecution(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getContractExecution(query));
    }

    @Operation(summary = "应收账款分析", description = "待收款金额、账龄分析")
    @ApiResponse(responseCode = "200", description = "成功返回应收账款分析数据")
    @PreAuthorize("hasAuthority('crm:analysis:contract')")
    @GetMapping("/contract/receivable")
    public Result<List<ReceivableAnalysisVO>> getReceivableAnalysis(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getReceivableAnalysis(query));
    }

    @Operation(summary = "收款进度分析", description = "合同收款率、逾期收款统计")
    @ApiResponse(responseCode = "200", description = "成功返回收款进度分析数据")
    @PreAuthorize("hasAuthority('crm:analysis:contract')")
    @GetMapping("/payment/progress")
    public Result<List<PaymentProgressVO>> getPaymentProgress(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getPaymentProgress(query));
    }

    // ========== 首页仪表盘接口 ==========

    @Operation(summary = "首页仪表盘", description = "首页仪表盘汇总数据")
    @ApiResponse(responseCode = "200", description = "成功返回仪表盘数据")
    @PreAuthorize("hasAuthority('crm:dashboard')")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        return Result.success(analysisService.getDashboard());
    }

    @Operation(summary = "预警提醒", description = "即将到期合同、逾期收款、流失预警客户汇总（支持部门/销售员筛选，limit 控制条数）")
    @ApiResponse(responseCode = "200", description = "成功返回预警提醒数据")
    @PreAuthorize("hasAuthority('crm:dashboard') or hasAuthority('crm:analysis:contract') or hasAuthority('crm:analysis:customer')")
    @GetMapping("/warnings")
    public Result<WarningsVO> getWarnings(
            @Parameter(description = "查询条件") AnalysisQueryDTO query) {
        return Result.success(analysisService.getWarnings(query));
    }
}