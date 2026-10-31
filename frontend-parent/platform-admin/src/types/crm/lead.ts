import type { Status } from '@/types/api'

/** 线索状态：1新建 2跟进中 3已转商机 4已失效 */
export type LeadStatus = 1 | 2 | 3 | 4

/** 线索VO */
export interface LeadVO {
  id: number
  customerId?: number
  customerName?: string
  contactId?: number
  contactName?: string
  leadName: string
  source?: string
  status: LeadStatus
  statusName?: string
  phone?: string
  email?: string
  company?: string
  description?: string
  ownerId: number
  ownerName?: string
  convertedTime?: string
  convertedOppId?: number
  convertedOppName?: string
  createTime: string
  updateTime: string
}

/** 线索查询DTO */
export interface LeadQueryDTO {
  pageNum?: number
  pageSize?: number
  leadName?: string
  source?: string
  status?: LeadStatus
  ownerId?: number
  company?: string
  phone?: string
}

/** 线索保存DTO */
export interface LeadSaveDTO {
  id?: number
  customerId?: number
  contactId?: number
  leadName: string
  source?: string
  status?: LeadStatus
  phone?: string
  email?: string
  company?: string
  description?: string
  ownerId: number
}

/** 线索转商机DTO */
export interface LeadConvertDTO {
  leadId: number
  customerId?: number
  oppName?: string
  expectedAmount?: number
  expectedDate?: string
}

/** 线索状态选项 */
export const leadStatusOptions = [
  { value: 1, label: '新建', tagType: 'info' },
  { value: 2, label: '跟进中', tagType: 'warning' },
  { value: 3, label: '已转商机', tagType: 'success' },
  { value: 4, label: '已失效', tagType: 'danger' }
]

/** 获取线索状态标签类型 */
export function getLeadStatusTagType(status: LeadStatus): string {
  const option = leadStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取线索状态标签名称 */
export function getLeadStatusLabel(status: LeadStatus): string {
  const option = leadStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}