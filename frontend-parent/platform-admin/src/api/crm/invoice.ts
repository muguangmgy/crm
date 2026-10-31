import { http } from '@/api/request'
import type { InvoiceVO, InvoiceQueryDTO, InvoiceSaveDTO } from '@/types/crm/invoice'
import type { PageVO } from '@/types/api'

export const invoiceApi = {
  /** 发票列表 */
  getList(params?: InvoiceQueryDTO): Promise<PageVO<InvoiceVO>> {
    return http.get<PageVO<InvoiceVO>>('/crm/invoice/list', params)
  },

  /** 发票详情 */
  getById(id: number): Promise<InvoiceVO> {
    return http.get<InvoiceVO>(`/crm/invoice/${id}`)
  },

  /** 新增发票 */
  create(data: InvoiceSaveDTO): Promise<number> {
    return http.post<number>('/crm/invoice', data)
  },

  /** 修改发票 */
  update(data: InvoiceSaveDTO): Promise<void> {
    return http.put<void>('/crm/invoice', data)
  },

  /** 删除发票 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/invoice/${id}`)
  },

  /** 作废发票 */
  cancel(id: number): Promise<void> {
    return http.post<void>(`/crm/invoice/${id}/cancel`)
  }
}