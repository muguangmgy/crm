import { http } from '@/api/request'
import type { LeadVO, LeadQueryDTO, LeadSaveDTO, LeadConvertDTO } from '@/types/crm/lead'
import type { PageVO } from '@/types/api'

export const leadApi = {
  /** 线索列表 */
  getList(params?: LeadQueryDTO): Promise<PageVO<LeadVO>> {
    return http.get<PageVO<LeadVO>>('/crm/lead/list', params)
  },

  /** 线索详情 */
  getById(id: number): Promise<LeadVO> {
    return http.get<LeadVO>(`/crm/lead/${id}`)
  },

  /** 新增线索 */
  create(data: LeadSaveDTO): Promise<number> {
    return http.post<number>('/crm/lead', data)
  },

  /** 修改线索 */
  update(data: LeadSaveDTO): Promise<void> {
    return http.put<void>('/crm/lead', data)
  },

  /** 删除线索 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/lead/${id}`)
  },

  /** 线索转商机 */
  convert(data: LeadConvertDTO): Promise<number> {
    return http.post<number>('/crm/lead/convert', data)
  }
}