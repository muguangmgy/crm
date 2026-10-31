/** 收款状态：0未收 1部分收款 2已收款 */
export type ReceivedStatus = 0 | 1 | 2

/** 收款记录状态：1已确认 2待确认 */
export type PaymentRecordStatus = 1 | 2

/** 收款计划VO */
export interface PaymentPlanVO {
  id: number
  contractId: number
  contractNo?: string
  planNo?: string
  planAmount: number
  planDate: string
  receivedAmount?: number
  receivedStatus: ReceivedStatus
  receivedStatusName?: string
  remark?: string
  createTime: string
  updateTime: string
}

/** 收款计划查询DTO */
export interface PaymentPlanQueryDTO {
  pageNum?: number
  pageSize?: number
  contractId?: number
  planNo?: string
  receivedStatus?: ReceivedStatus
  planDateStart?: string
  planDateEnd?: string
}

/** 收款计划保存DTO */
export interface PaymentPlanSaveDTO {
  id?: number
  contractId?: number
  planAmount: number
  planDate: string
  remark?: string
}

/** 收款记录VO */
export interface PaymentRecordVO {
  id: number
  contractId: number
  contractNo?: string
  planId?: number
  planNo?: string
  paymentNo?: string
  paymentAmount: number
  paymentDate: string
  paymentType?: string
  paymentTypeName?: string
  payerName?: string
  payerAccount?: string
  ourAccount?: string
  status: PaymentRecordStatus
  statusName?: string
  voucherUrl?: string
  confirmTime?: string
  confirmBy?: number
  confirmByName?: string
  remark?: string
  createTime: string
  updateTime: string
}

/** 收款记录查询DTO */
export interface PaymentRecordQueryDTO {
  pageNum?: number
  pageSize?: number
  contractId?: number
  planId?: number
  paymentNo?: string
  status?: PaymentRecordStatus
  paymentDateStart?: string
  paymentDateEnd?: string
  paymentType?: string
}

/** 收款记录保存DTO */
export interface PaymentRecordSaveDTO {
  id?: number
  contractId?: number
  planId?: number
  paymentAmount: number
  paymentDate: string
  paymentType?: string
  payerName?: string
  payerAccount?: string
  ourAccount?: string
  voucherUrl?: string
  remark?: string
}

/** 收款计划状态选项 */
export const receivedStatusOptions = [
  { value: 0, label: '未收款', tagType: 'info' },
  { value: 1, label: '部分收款', tagType: 'warning' },
  { value: 2, label: '已收款', tagType: 'success' }
]

/** 收款记录状态选项 */
export const paymentRecordStatusOptions = [
  { value: 1, label: '已确认', tagType: 'success' },
  { value: 2, label: '待确认', tagType: 'warning' }
]

/** 收款方式选项 */
export const paymentTypeOptions = [
  { value: 'bank_transfer', label: '银行转账' },
  { value: 'cash', label: '现金' },
  { value: 'check', label: '支票' },
  { value: 'alipay', label: '支付宝' },
  { value: 'wechat', label: '微信支付' }
]

/** 获取收款状态标签类型 */
export function getReceivedStatusTagType(status: ReceivedStatus): string {
  const option = receivedStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取收款状态标签名称 */
export function getReceivedStatusLabel(status: ReceivedStatus): string {
  const option = receivedStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}

/** 获取收款记录状态标签类型 */
export function getPaymentRecordStatusTagType(status: PaymentRecordStatus): string {
  const option = paymentRecordStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取收款记录状态标签名称 */
export function getPaymentRecordStatusLabel(status: PaymentRecordStatus): string {
  const option = paymentRecordStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}

/** 获取收款方式名称 */
export function getPaymentTypeLabel(paymentType: string): string {
  const option = paymentTypeOptions.find(opt => opt.value === paymentType)
  return option?.label || paymentType || '-'
}