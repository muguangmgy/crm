import type { Status } from '@/types/api'

/** 合同状态：1草稿 2审批中 3生效 4到期 5终止 */
export type ContractStatus = 1 | 2 | 3 | 4 | 5

/** 合同VO */
export interface ContractVO {
  id: number
  contractNo?: string
  contractName?: string
  customerId: number
  customerName?: string
  opportunityId?: number
  opportunityName?: string
  quoteId?: number
  quoteName?: string
  contractAmount: number
  /** 已收款金额 */
  receivedAmount?: number
  /** 已开票金额 */
  invoicedAmount?: number
  startDate?: string
  endDate?: string
  signDate?: string
  status: ContractStatus
  statusName?: string
  ownerId: number
  ownerName?: string
  remark?: string
  createTime: string
  updateTime: string
}

/** 合同查询DTO */
export interface ContractQueryDTO {
  pageNum?: number
  pageSize?: number
  contractName?: string
  contractNo?: string
  customerId?: number
  opportunityId?: number
  status?: ContractStatus
  ownerId?: number
  startDate?: string
  endDate?: string
}

/** 合同保存DTO */
export interface ContractSaveDTO {
  id?: number
  customerId?: number
  opportunityId?: number
  quoteId?: number
  contractName?: string
  contractAmount: number
  startDate?: string
  endDate?: string
  signDate?: string
  remark?: string
  ownerId?: number
}

/** 合同状态选项 */
export const contractStatusOptions = [
  { value: 1, label: '草稿', tagType: 'info' },
  { value: 2, label: '审批中', tagType: 'warning' },
  { value: 3, label: '生效', tagType: 'success' },
  { value: 4, label: '到期', tagType: '' },
  { value: 5, label: '终止', tagType: 'danger' }
]

/** 获取合同状态标签类型 */
export function getContractStatusTagType(status: ContractStatus): string {
  const option = contractStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取合同状态标签名称 */
export function getContractStatusLabel(status: ContractStatus): string {
  const option = contractStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}