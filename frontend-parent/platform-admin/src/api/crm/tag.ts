import { http } from '@/api/request'
import type { TagVO, TagSaveDTO } from '@/types/crm/customer'

export const tagApi = {
  /** 获取标签列表 */
  getList(): Promise<TagVO[]> {
    return http.get<TagVO[]>('/crm/customer/tag/list')
  },

  /** 保存标签 */
  save(data: TagSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer/tag/save', data)
  },

  /** 删除标签 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/tag/${id}`)
  }
}