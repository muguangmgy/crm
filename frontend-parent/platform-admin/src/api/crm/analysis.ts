import { http } from '@/api/request'
import type {
  AnalysisQueryDTO,
  DashboardVO,
  WarningsVO,
  FunnelVO,
  TrendVO,
  SalesPerformanceVO,
  TeamPerformanceVO,
  CustomerDistributionVO,
  CustomerValueVO,
  CustomerActivityVO,
  ChurnWarningVO,
  ConversionVO,
  PredictionVO,
  ContractStatusItemVO,
  AgingItemVO,
  PaymentProgressItemVO,
  RankingVO
} from '@/types/crm/analysis'

/** 去掉空字符串/undefined，避免 LocalDate 等类型绑定失败 */
function cleanParams<T extends Record<string, unknown>>(params?: T): Partial<T> | undefined {
  if (!params) return undefined
  const result: Record<string, unknown> = {}
  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== undefined && value !== null) {
      result[key] = value
    }
  })
  return result as Partial<T>
}

export const analysisApi = {
  // ==================== 仪表盘 ====================

  getDashboard(): Promise<DashboardVO> {
    return http.get<DashboardVO>('/crm/analysis/dashboard')
  },

  getFunnel(params?: AnalysisQueryDTO): Promise<FunnelVO[]> {
    return http.get<FunnelVO[]>('/crm/analysis/opportunity/funnel', cleanParams(params))
  },

  getTrend(params?: AnalysisQueryDTO): Promise<TrendVO[]> {
    return http.get<TrendVO[]>('/crm/analysis/sales/trend', cleanParams(params))
  },

  getWarnings(params?: AnalysisQueryDTO): Promise<WarningsVO> {
    return http.get<WarningsVO>('/crm/analysis/warnings', cleanParams(params))
  },

  getCustomerDistribution(params?: AnalysisQueryDTO & { dimension: 'industry' | 'region' | 'level' }): Promise<CustomerDistributionVO[]> {
    return http.get<CustomerDistributionVO[]>('/crm/analysis/customer/distribution', cleanParams(params))
  },

  // ==================== 销售分析 ====================

  getSalesPerformance(params?: AnalysisQueryDTO): Promise<SalesPerformanceVO[]> {
    return http.get<SalesPerformanceVO[]>('/crm/analysis/sales/performance', cleanParams(params))
  },

  getTeamPerformance(params?: AnalysisQueryDTO): Promise<TeamPerformanceVO[]> {
    return http.get<TeamPerformanceVO[]>('/crm/analysis/sales/team', cleanParams(params))
  },

  getSalesRanking(params?: AnalysisQueryDTO): Promise<RankingVO[]> {
    return http.get<RankingVO[]>('/crm/analysis/sales/ranking', cleanParams(params))
  },

  // ==================== 客户分析 ====================

  getCustomerValue(params?: AnalysisQueryDTO): Promise<CustomerValueVO[]> {
    return http.get<CustomerValueVO[]>('/crm/analysis/customer/value', cleanParams(params))
  },

  getCustomerActivity(params?: AnalysisQueryDTO): Promise<CustomerActivityVO[]> {
    return http.get<CustomerActivityVO[]>('/crm/analysis/customer/activity', cleanParams(params))
  },

  getChurnWarning(params?: AnalysisQueryDTO & { page?: number; size?: number }): Promise<{ list: ChurnWarningVO[]; total: number }> {
    return http.get<{ list: ChurnWarningVO[]; total: number }>('/crm/analysis/customer/churn-warning', cleanParams(params))
  },

  // ==================== 商机分析 ====================

  getConversion(params?: AnalysisQueryDTO): Promise<ConversionVO[]> {
    return http.get<ConversionVO[]>('/crm/analysis/opportunity/conversion', cleanParams(params))
  },

  getPrediction(params?: AnalysisQueryDTO): Promise<PredictionVO> {
    return http.get<PredictionVO>('/crm/analysis/opportunity/prediction', cleanParams(params))
  },

  // ==================== 合同分析 ====================

  /** 合同状态分布列表 */
  getContractExecution(params?: AnalysisQueryDTO): Promise<ContractStatusItemVO[]> {
    return http.get<ContractStatusItemVO[]>('/crm/analysis/contract/execution', cleanParams(params))
  },

  /** 应收账款账龄列表 */
  getReceivable(params?: AnalysisQueryDTO): Promise<AgingItemVO[]> {
    return http.get<AgingItemVO[]>('/crm/analysis/contract/receivable', cleanParams(params))
  },

  /** 收款进度（按合同）列表 */
  getPaymentProgress(params?: AnalysisQueryDTO): Promise<PaymentProgressItemVO[]> {
    return http.get<PaymentProgressItemVO[]>('/crm/analysis/payment/progress', cleanParams(params))
  }
}
