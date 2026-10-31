import { http } from '@/api/request'
import type { FollowVO, FollowSaveDTO } from '@/types/crm/customer'

export const followApi = {
  /** 获取客户跟进记录列表 */
  getList(customerId: number): Promise<FollowVO[]> {
    return http.get<FollowVO[]>(`/crm/customer/follow/list/${customerId}`)
  },

  /** 保存跟进记录 */
  save(data: FollowSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer/follow/save', data)
  },

  /** 删除跟进记录 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/follow/${id}`)
  }
}