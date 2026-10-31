import { http } from '@/api/request'
import type { QuoteVO, QuoteQueryDTO, QuoteSaveDTO } from '@/types/crm/quote'
import type { PageVO } from '@/types/api'

export const quoteApi = {
  /** 报价单列表 */
  getList(params?: QuoteQueryDTO): Promise<PageVO<QuoteVO>> {
    return http.get<PageVO<QuoteVO>>('/crm/quote/list', params)
  },

  /** 报价单详情 */
  getById(id: number): Promise<QuoteVO> {
    return http.get<QuoteVO>(`/crm/quote/${id}`)
  },

  /** 新增报价单 */
  create(data: QuoteSaveDTO): Promise<number> {
    return http.post<number>('/crm/quote', data)
  },

  /** 修改报价单 */
  update(data: QuoteSaveDTO): Promise<void> {
    return http.put<void>('/crm/quote', data)
  },

  /** 删除报价单 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/quote/${id}`)
  },

  /** 发送报价单 */
  send(id: number): Promise<void> {
    return http.post<void>(`/crm/quote/send/${id}`)
  },

  /** 报价中标 */
  accept(id: number): Promise<void> {
    return http.post<void>(`/crm/quote/accept/${id}`)
  },

  /** 报价拒绝 */
  reject(id: number): Promise<void> {
    return http.post<void>(`/crm/quote/reject/${id}`)
  }
}