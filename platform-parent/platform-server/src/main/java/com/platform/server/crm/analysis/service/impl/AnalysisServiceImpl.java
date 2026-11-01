package com.platform.server.crm.analysis.service.impl;

import com.platform.common.annotation.CacheSafe;
import com.platform.common.constants.RedisConstants;
import com.platform.common.context.UserContext;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.DataScopeType;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.analysis.dto.AnalysisQueryDTO;
import com.platform.server.crm.analysis.mapper.AnalysisMapper;
import com.platform.server.crm.analysis.service.AnalysisService;
import com.platform.server.crm.analysis.vo.*;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisMapper analysisMapper;

    @Resource(name = "dashboardExecutor")
    private Executor dashboardExecutor;

    // ========== 销售业绩 ==========

    @Override
    public List<SalesPerformanceVO> getSalesPerformance(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        return analysisMapper.selectSalesPerformance(query);
    }

    @Override
    public List<TeamPerformanceVO> getTeamPerformance(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        // 部门范围由 applyDataScope / 前端筛选决定，不再强制落到当前用户部门
        return analysisMapper.selectTeamPerformance(query);
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        if (query.getTimeDimension() == null) {
            query.setTimeDimension("MONTH");
        }
        return analysisMapper.selectSalesTrend(query);
    }

    @Override
    public List<SalesRankingVO> getSalesRanking(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        return analysisMapper.selectSalesRanking(query);
    }

    // ========== 客户分析 ==========

    @Override
    public List<CustomerDistributionVO> getCustomerDistributionByIndustry(AnalysisQueryDTO query) {
        applyDataScope(query);
        return analysisMapper.selectCustomerDistributionByIndustry(query);
    }

    @Override
    public List<CustomerDistributionVO> getCustomerDistributionByRegion(AnalysisQueryDTO query) {
        applyDataScope(query);
        return analysisMapper.selectCustomerDistributionByRegion(query);
    }

    @Override
    public List<CustomerDistributionVO> getCustomerDistributionByLevel(AnalysisQueryDTO query) {
        applyDataScope(query);
        return analysisMapper.selectCustomerDistributionByLevel(query);
    }

    @Override
    public List<CustomerValueVO> getCustomerValue(AnalysisQueryDTO query) {
        applyDataScope(query);
        return analysisMapper.selectCustomerValue(query);
    }

    @Override
    public List<CustomerActivityVO> getCustomerActivity(AnalysisQueryDTO query) {
        applyDataScope(query);
        return analysisMapper.selectCustomerActivity(query);
    }

    @Override
    public PageVO<ChurnWarningVO> getChurnWarningPage(AnalysisQueryDTO query, Integer page, Integer size) {
        applyDataScope(query);
        query.setOffset((page - 1) * size);
        query.setLimit(size);
        List<ChurnWarningVO> list = analysisMapper.selectChurnWarningList(query);
        Integer total = analysisMapper.selectChurnWarningCount(query);
        return PageVO.of(list, total != null ? total.longValue() : 0L, page.longValue(), size.longValue());
    }

    // ========== 商机分析 ==========

    @Override
    public List<SalesFunnelVO> getSalesFunnel(AnalysisQueryDTO query) {
        // 日期由调用方传入；未传则看全量管道（仪表盘依赖此行为）
        applyDataScope(query);
        List<SalesFunnelVO> result = analysisMapper.selectSalesFunnel(query);
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public List<ConversionRateVO> getConversionRate(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        List<ConversionRateVO> result = analysisMapper.selectConversionRate(query);
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public DealPredictionVO getDealPrediction(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        DealPredictionVO result = analysisMapper.selectDealPrediction(query);
        if (result == null) {
            result = new DealPredictionVO();
            result.setPeriod("管道预测");
        }
        return result;
    }

    // ========== 合同收款分析 ==========

    @Override
    public List<ContractExecutionVO> getContractExecution(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        List<ContractExecutionVO> result = analysisMapper.selectContractExecution(query);
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public List<ReceivableAnalysisVO> getReceivableAnalysis(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        List<ReceivableAnalysisVO> result = analysisMapper.selectReceivableAnalysis(query);
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public List<PaymentProgressVO> getPaymentProgress(AnalysisQueryDTO query) {
        fillDefaultDateRange(query);
        applyDataScope(query);
        List<PaymentProgressVO> result = analysisMapper.selectPaymentProgress(query);
        return result != null ? result : Collections.emptyList();
    }

    // ========== 仪表盘 ==========

    /**
     * 看板汇总：漏斗/趋势/分布/逾期等指标线程池并行查询；
     * 子线程同步登录上下文保障数据权限；按租户+用户+角色+数据范围隔离 Redis 缓存。
     */
    @Override
    @CacheSafe(
            cacheKeyPrefix = RedisConstants.CRM_DASHBOARD_CACHE_PREFIX,
            keyExpression = "#tenantId:#userId:#roleId:#dataScope",
            baseTtlSeconds = 3600,
            ttlOffsetSeconds = 300,
            lockWaitSeconds = 5,
            lockLeaseSeconds = -1
    )
    public DashboardVO getDashboard() {
        UserContext userContext = UserContextHolder.getContext();
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        Integer days = 30;

        AnalysisQueryDTO scope = new AnalysisQueryDTO();
        applyDataScope(scope);
        Long scopeUserId = scope.getUserId();
        Long scopeDeptId = scope.getDeptId();
        String scopeDeptIds = scope.getScopeDeptIds();
        Long scopeTenantId = scope.getTenantId();

        YearMonth startMonth = currentMonth.minusMonths(11);
        LocalDate trendStart = startMonth.atDay(1);

        CompletableFuture<AnalysisMapper.DashboardSummaryVO> summaryFuture = supplyAsync(userContext, () ->
                analysisMapper.selectDashboardSummary(
                        startDate.toString(), endDate.toString(), days,
                        scopeUserId, scopeDeptId, scopeDeptIds, scopeTenantId));

        CompletableFuture<List<SalesFunnelVO>> funnelFuture = supplyAsync(userContext, () ->
                getSalesFunnel(new AnalysisQueryDTO()));

        CompletableFuture<List<SalesTrendVO>> trendFuture = supplyAsync(userContext, () -> {
            AnalysisQueryDTO trendQuery = new AnalysisQueryDTO();
            trendQuery.setStartDate(trendStart);
            trendQuery.setEndDate(endDate);
            trendQuery.setTimeDimension("MONTH");
            return getSalesTrend(trendQuery);
        });

        CompletableFuture<List<CustomerDistributionVO>> industryFuture = supplyAsync(userContext, () ->
                getCustomerDistributionByIndustry(new AnalysisQueryDTO()));

        CompletableFuture<List<CustomerDistributionVO>> regionFuture = supplyAsync(userContext, () ->
                getCustomerDistributionByRegion(new AnalysisQueryDTO()));

        CompletableFuture<List<DashboardVO.ExpiringContractVO>> expiringFuture = supplyAsync(userContext, () ->
                analysisMapper.selectExpiringContracts(days, 5, scopeUserId, scopeDeptId, scopeDeptIds, scopeTenantId));

        CompletableFuture<List<DashboardVO.OverduePaymentVO>> overdueFuture = supplyAsync(userContext, () ->
                analysisMapper.selectOverduePayments(5, scopeUserId, scopeDeptId, scopeDeptIds, scopeTenantId));

        try {
            CompletableFuture.allOf(
                    summaryFuture, funnelFuture, trendFuture,
                    industryFuture, regionFuture, expiringFuture, overdueFuture
            ).join();
        } catch (Exception e) {
            log.error("看板并行查询失败", e);
            throw e;
        }

        DashboardVO dashboard = new DashboardVO();
        AnalysisMapper.DashboardSummaryVO summary = summaryFuture.join();
        if (summary != null) {
            dashboard.setNewCustomerCount(summary.getNewCustomerCount());
            dashboard.setDealAmount(summary.getDealAmount());
            dashboard.setReceivedAmount(summary.getReceivedAmount());
            dashboard.setPendingFollowCount(summary.getPendingFollowCount());
            dashboard.setExpiringContractCount(summary.getExpiringContractCount());
            dashboard.setOverduePaymentCount(summary.getOverduePaymentCount());
            dashboard.setChurnWarningCount(summary.getChurnWarningCount());
        }
        dashboard.setSalesFunnel(funnelFuture.join());
        dashboard.setSalesTrend(trendFuture.join());
        dashboard.setIndustryDistribution(industryFuture.join());
        dashboard.setRegionDistribution(regionFuture.join());
        dashboard.setExpiringContracts(expiringFuture.join());
        dashboard.setOverduePayments(overdueFuture.join());
        return dashboard;
    }

    @Override
    public WarningsVO getWarnings(AnalysisQueryDTO query) {
        if (query == null) {
            query = new AnalysisQueryDTO();
        }
        Integer days = 30;
        applyDataScope(query);
        int limit = query.getLimit() != null && query.getLimit() > 0 ? query.getLimit() : 20;

        WarningsVO warnings = new WarningsVO();
        warnings.setExpiringContracts(analysisMapper.selectExpiringContracts(
                days, limit, query.getUserId(), query.getDeptId(), query.getScopeDeptIds(), query.getTenantId()));
        warnings.setOverduePayments(analysisMapper.selectOverduePayments(
                limit, query.getUserId(), query.getDeptId(), query.getScopeDeptIds(), query.getTenantId()));

        AnalysisQueryDTO churnQuery = new AnalysisQueryDTO();
        churnQuery.setTenantId(query.getTenantId());
        churnQuery.setUserId(query.getUserId());
        churnQuery.setDeptId(query.getDeptId());
        churnQuery.setScopeDeptIds(query.getScopeDeptIds());
        churnQuery.setCustomerType(query.getCustomerType());
        churnQuery.setIndustry(query.getIndustry());
        churnQuery.setProvince(query.getProvince());
        churnQuery.setOffset(0);
        churnQuery.setLimit(limit);
        warnings.setChurnCustomers(analysisMapper.selectChurnWarningList(churnQuery));

        return warnings;
    }

    // ========== 私有方法 ==========

    /**
     * 在看板线程池中执行任务，并同步主线程登录上下文到子线程。
     */
    private <T> CompletableFuture<T> supplyAsync(UserContext userContext, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserContextHolder.setContext(userContext);
                return supplier.get();
            } finally {
                UserContextHolder.clear();
            }
        }, dashboardExecutor);
    }

    private void fillDefaultDateRange(AnalysisQueryDTO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            YearMonth currentMonth = YearMonth.now();
            if (query.getStartDate() == null) {
                query.setStartDate(currentMonth.atDay(1));
            }
            if (query.getEndDate() == null) {
                query.setEndDate(currentMonth.atEndOfMonth());
            }
        }
    }

    /**
     * 分析 SQL 含多层子查询，不走数据权限拦截器；按当前角色 dataScope 写入查询条件。
     * ALL 权限下 TenantLine 会跳过，因此这里始终写入登录租户，避免跨租户串数（如漏斗阶段重复）。
     */
    private void applyDataScope(AnalysisQueryDTO query) {
        if (query == null) {
            return;
        }
        if (query.getTenantId() == null) {
            query.setTenantId(UserContextHolder.getTenantId());
        }
        DataScopeType type = DataScopeType.getByCode(UserContextHolder.getDataScope());
        switch (type) {
            case SELF -> {
                if (query.getUserId() == null) {
                    query.setUserId(UserContextHolder.getUserId());
                }
            }
            case DEPT -> {
                if (query.getDeptId() == null) {
                    query.setDeptId(UserContextHolder.getDeptId());
                }
            }
            case DEPT_AND_CHILD -> {
                if (!StringUtils.hasText(query.getScopeDeptIds())) {
                    query.setScopeDeptIds(UserContextHolder.getDeptIds());
                }
            }
            default -> {
                // ALL / TENANT：行级部门/本人条件不强制；租户已在上方写入
            }
        }
    }
}
