/** 分析查询条件 */
export interface AnalysisQueryDTO {
  tenantId?: number
  startDate?: string
  endDate?: string
  deptId?: number
  userId?: number
  customerType?: number
  timeDimension?: 'MONTH' | 'QUARTER' | 'YEAR'
  industry?: string
  province?: string
  limit?: number
  offset?: number
}

/** 仪表盘汇总数据 */
export interface DashboardVO {
  newCustomerCount?: number
  dealAmount?: number
  receivedAmount?: number
  paymentAmount?: number
  pendingFollowCount?: number
  expiringContractCount?: number
  overduePaymentCount?: number
  churnWarningCount?: number
  salesFunnel?: FunnelVO[]
  salesTrend?: TrendVO[]
  industryDistribution?: CustomerDistributionVO[]
  regionDistribution?: CustomerDistributionVO[]
  expiringContracts?: ExpiringContractVO[]
  overduePayments?: OverduePaymentVO[]
}

/** 预警提醒汇总 */
export interface WarningsVO {
  expiringContracts?: ExpiringContractVO[]
  overduePayments?: OverduePaymentVO[]
  churnCustomers?: ChurnWarningVO[]
}

/** 销售漏斗数据 */
export interface FunnelVO {
  stageId: number
  stageName: string
  opportunityCount: number
  opportunityAmount?: number
  amount?: number
  probability?: number
  conversionRate?: number
}

/** 业绩趋势数据 */
export interface TrendVO {
  period: string
  dealAmount: number
  receivedAmount?: number
  paymentAmount?: number
  newCustomerCount?: number
  dealCount?: number
  contractAmount?: number
}

/** 销售员业绩统计 */
export interface SalesPerformanceVO {
  userId: number
  userName: string
  deptName?: string
  dealAmount: number
  dealCount?: number
  contractCount: number
  contractAmount?: number
  receivedAmount?: number
  newCustomerCount?: number
}

/** 团队业绩统计 */
export interface TeamPerformanceVO {
  deptId: number
  deptName: string
  dealAmount: number
  memberCount: number
  dealCount?: number
  contractCount: number
  contractAmount?: number
  receivedAmount?: number
  newCustomerCount?: number
}

/** 客户分布数据 */
export interface CustomerDistributionVO {
  dimension: string
  value: string
  customerCount: number
  percentage: number
}

/** 客户价值分析 */
export interface CustomerValueVO {
  customerId: number
  customerName: string
  customerType?: number
  ownerName?: string
  /** 累计合同金额（后端字段） */
  totalContractAmount?: number
  /** 兼容旧字段 */
  totalAmount?: number
  contractCount: number
  opportunityCount?: number
  /** 累计收款（后端字段） */
  totalReceivedAmount?: number
  receivedAmount?: number
  unreceivedAmount?: number
  firstDealTime?: string
  lastDealTime?: string
  lastDealDate?: string
}

/** 客户活跃度（后端按等级汇总） */
export interface CustomerActivityVO {
  /** HIGH / MEDIUM / LOW / INACTIVE / NEVER */
  activityLevel: string
  customerCount: number
  percentage?: number
  avgFollowCount?: number
  avgDaysSinceLastFollow?: number
}

/** 流失预警客户 */
export interface ChurnWarningVO {
  customerId: number
  customerName: string
  ownerName?: string
  noFollowDays?: number
  daysSinceLastFollow?: number
  lastFollowTime?: string
  contractAmount?: number
  totalContractAmount?: number
  warningLevel: number | string
}

/** 阶段推进分析（存量近似口径） */
export interface ConversionVO {
  stageId: number
  stageName: string
  /** 当前阶段商机数 */
  enterCount: number
  /** 后续阶段商机数 */
  exitCount: number
  /** 推进率 */
  conversionRate: number
  avgStayDays?: number
  overallRate?: number
}

/** 成交预测 */
export interface PredictionVO {
  /** 如：管道预测 */
  period?: string
  /** 高概率管道期望金额（未加权） */
  highProbabilityAmount?: number
  mediumProbabilityAmount?: number
  lowProbabilityAmount?: number
  /** 加权预测成交额 */
  predictedAmount: number
  opportunityCount?: number
  /** 时间范围内已成交金额 */
  closedAmount?: number
  opportunities?: OpportunityPredictionVO[]
}

/** 商机预测明细 */
export interface OpportunityPredictionVO {
  opportunityId: number
  opportunityName: string
  customerName: string
  expectedAmount: number
  probability: number
  predictedAmount: number
  expectedDate?: string
  ownerName?: string
}

/** 合同状态分布（后端 List 项） */
export interface ContractStatusItemVO {
  status: string | number
  statusName: string
  contractCount: number
  contractAmount?: number
  percentage?: number
  expiringCount?: number
  expiredNotTerminatedCount?: number
}

/** 即将/已到期合同 */
export interface ExpiringContractVO {
  contractId: number
  contractNo?: string
  contractName?: string
  customerName: string
  contractAmount: number
  receivedAmount?: number
  endDate: string
  remainingDays?: number
  daysToExpire?: number
  ownerName?: string
}

/** 账龄分布（后端 List 项） */
export interface AgingItemVO {
  agingRange: string
  agingRangeName?: string
  receivableAmount: number
  amount?: number
  contractCount?: number
  percentage: number
  riskLevel?: string
}

/** 逾期收款计划 */
export interface OverduePaymentVO {
  planId?: number
  contractId?: number
  contractNo?: string
  contractName?: string
  customerName: string
  planAmount?: number
  receivedAmount?: number
  overdueAmount?: number
  planDate: string
  overdueDays: number
  ownerName?: string
}

/** 收款进度（后端按合同 List 项） */
export interface PaymentProgressItemVO {
  contractId: number
  contractNo?: string
  customerName: string
  contractAmount: number
  receivedAmount: number
  progressRate?: number
  pendingAmount?: number
  overdueAmount?: number
  nextPaymentDate?: string
  paymentStatus?: string
}

/** 待收款计划 */
export interface PendingPaymentVO {
  planId?: number
  contractId?: number
  contractNo?: string
  contractName?: string
  customerName: string
  planAmount: number
  planDate: string
  remainingDays?: number
  ownerName?: string
}

/** 业绩排名（后端销售员排名） */
export interface RankingVO {
  rankNum: number
  userId?: number
  userName?: string
  deptName?: string
  targetId?: number
  targetName?: string
  dealAmount: number
  dealCount?: number
  receivedAmount?: number
  completionRate?: number
}
