import { http } from '@/api/request'
import type { ContactVO, ContactSaveDTO } from '@/types/crm/customer'

export const contactApi = {
  /** 获取客户联系人列表 */
  getList(customerId: number): Promise<ContactVO[]> {
    return http.get<ContactVO[]>(`/crm/customer/contact/list/${customerId}`)
  },

  /** 保存联系人 */
  save(data: ContactSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer/contact/save', data)
  },

  /** 删除联系人 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/contact/${id}`)
  },

  /** 设置主要联系人 */
  setPrimary(customerId: number, contactId: number): Promise<void> {
    return http.post<void>(`/crm/customer/contact/primary/${customerId}/${contactId}`)
  }
}