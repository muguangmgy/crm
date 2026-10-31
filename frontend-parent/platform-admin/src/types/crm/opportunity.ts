import type { Status } from '@/types/api'

/** 商机状态：1进行中 2已成交 3已失败 */
export type OpportunityStatus = 1 | 2 | 3

/** 商机VO */
export interface OpportunityVO {
  id: number
  oppNo?: string
  oppName: string
  customerId: number
  customerName?: string
  contactId?: number
  contactName?: string
  stageId: number
  stageName?: string
  probability?: number
  expectedAmount?: number
  actualAmount?: number
  expectedDate?: string
  closeDate?: string
  source?: string
  status: OpportunityStatus
  ownerId: number
  ownerName?: string
  winnerQuoteId?: number
  /** 中标报价单编号 */
  winnerQuoteNo?: string
  /** 中标报价单名称 */
  winnerQuoteName?: string
  description?: string
  remark?: string
  createTime: string
  updateTime: string
}

/** 商机查询DTO */
export interface OpportunityQueryDTO {
  pageNum?: number
  pageSize?: number
  oppName?: string
  customerId?: number
  stageId?: number
  status?: OpportunityStatus
  ownerId?: number
  source?: string
}

/** 商机保存DTO */
export interface OpportunitySaveDTO {
  id?: number
  customerId?: number
  contactId?: number
  oppName: string
  stageId?: number
  expectedAmount?: number
  expectedDate?: string
  source?: string
  ownerId?: number
  description?: string
  remark?: string
}

/** 商机成交/失败DTO */
export interface OpportunityCloseDTO {
  isWin: boolean
  actualAmount?: number
  winnerQuoteId?: number
  remark?: string
}

/** 阶段VO */
export interface StageVO {
  id: number
  stageName: string
  stageCode?: string
  probability: number
  sort: number
  status: Status
  isWin?: number
  isLost?: number
  createTime: string
}

/** 阶段保存DTO */
export interface StageSaveDTO {
  id?: number
  stageName: string
  stageCode?: string
  probability?: number
  sort?: number
  isWin?: number
  isLost?: number
  status?: number
}

/** 商机状态选项 */
export const opportunityStatusOptions = [
  { value: 1, label: '进行中', tagType: 'primary' },
  { value: 2, label: '已成交', tagType: 'success' },
  { value: 3, label: '已失败', tagType: 'danger' }
]

/** 获取商机状态标签类型 */
export function getOpportunityStatusTagType(status: OpportunityStatus): string {
  const option = opportunityStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取商机状态标签名称 */
export function getOpportunityStatusLabel(status: OpportunityStatus): string {
  const option = opportunityStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}