import type { Status } from '@/types/api'

/** 报价单状态：1草稿 2已发送 3已接受 4已拒绝 */
export type QuoteStatus = 1 | 2 | 3 | 4

/** 报价单VO */
export interface QuoteVO {
  id: number
  quoteNo?: string
  quoteName?: string
  opportunityId: number
  opportunityName?: string
  customerId: number
  customerName?: string
  totalAmount: number
  discountRate?: number
  finalAmount?: number
  status: QuoteStatus
  statusName?: string
  expireDate?: string
  sendTime?: string
  acceptTime?: string
  remark?: string
  ownerId: number
  ownerName?: string
  createTime: string
  updateTime: string
  products?: QuoteProductVO[]
}

/** 报价单产品明细VO */
export interface QuoteProductVO {
  id?: number
  productId: number
  productName?: string
  unit?: string
  quantity: number
  unitPrice: number
  discountRate?: number
  totalPrice?: number
  remark?: string
}

/** 报价单查询DTO */
export interface QuoteQueryDTO {
  pageNum?: number
  pageSize?: number
  quoteName?: string
  quoteNo?: string
  opportunityId?: number
  customerId?: number
  status?: QuoteStatus
  ownerId?: number
}

/** 报价单保存DTO */
export interface QuoteSaveDTO {
  id?: number
  opportunityId?: number
  customerId?: number
  quoteName?: string
  totalAmount: number
  discountRate?: number
  finalAmount?: number
  expireDate?: string
  remark?: string
  ownerId?: number
  products: QuoteProductSaveDTO[]
}

/** 报价单产品明细保存DTO */
export interface QuoteProductSaveDTO {
  id?: number
  productId?: number
  productName?: string
  unit?: string
  quantity: number
  unitPrice: number
  discountRate?: number
  totalPrice?: number
  remark?: string
  priceWarning?: boolean
}

/** 报价状态选项 */
export const quoteStatusOptions = [
  { value: 1, label: '草稿', tagType: 'info' },
  { value: 2, label: '已发送', tagType: 'warning' },
  { value: 3, label: '已接受', tagType: 'success' },
  { value: 4, label: '已拒绝', tagType: 'danger' }
]

/** 获取报价状态标签类型 */
export function getQuoteStatusTagType(status: QuoteStatus): string {
  const option = quoteStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取报价状态标签名称 */
export function getQuoteStatusLabel(status: QuoteStatus): string {
  const option = quoteStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}