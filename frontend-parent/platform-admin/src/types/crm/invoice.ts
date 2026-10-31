/** 发票类型：1普通发票 2增值税专用发票 */
export type InvoiceType = 1 | 2

/** 发票状态：1已开票 2已作废 */
export type InvoiceStatus = 1 | 2

/** 发票VO */
export interface InvoiceVO {
  id: number
  invoiceNo?: string
  contractId: number
  contractName?: string
  contractNo?: string
  customerId?: number
  customerName?: string
  ownerId?: number
  ownerName?: string
  invoiceType: InvoiceType
  invoiceTypeName?: string
  invoiceAmount: number
  invoiceDate?: string
  invoiceStatus: InvoiceStatus
  invoiceStatusName?: string
  remark?: string
  fileUrl?: string
  createTime: string
  updateTime: string
}

/** 发票查询DTO */
export interface InvoiceQueryDTO {
  pageNum?: number
  pageSize?: number
  invoiceNo?: string
  contractId?: number
  invoiceType?: InvoiceType
  invoiceStatus?: InvoiceStatus
}

/** 发票保存DTO */
export interface InvoiceSaveDTO {
  id?: number
  contractId?: number
  invoiceType: InvoiceType
  invoiceAmount: number
  invoiceDate?: string
  remark?: string
  fileUrl?: string
}

/** 发票类型选项 */
export const invoiceTypeOptions = [
  { value: 1, label: '普通发票', tagType: 'info' },
  { value: 2, label: '增值税专用发票', tagType: 'success' }
]

/** 发票状态选项 */
export const invoiceStatusOptions = [
  { value: 1, label: '已开票', tagType: 'success' },
  { value: 2, label: '已作废', tagType: 'danger' }
]

/** 获取发票类型标签类型 */
export function getInvoiceTypeTagType(invoiceType: InvoiceType): string {
  const option = invoiceTypeOptions.find(opt => opt.value === invoiceType)
  return option?.tagType || 'info'
}

/** 获取发票类型标签名称 */
export function getInvoiceTypeLabel(invoiceType: InvoiceType): string {
  const option = invoiceTypeOptions.find(opt => opt.value === invoiceType)
  return option?.label || '未知'
}

/** 获取发票状态标签类型 */
export function getInvoiceStatusTagType(status: InvoiceStatus): string {
  const option = invoiceStatusOptions.find(opt => opt.value === status)
  return option?.tagType || 'info'
}

/** 获取发票状态标签名称 */
export function getInvoiceStatusLabel(status: InvoiceStatus): string {
  const option = invoiceStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}
