import { http } from '@/api/request'
import type { CustomerVO, CustomerDetailVO, CustomerQueryDTO, CustomerSaveDTO, PoolTransferDTO, ContactVO, ContactSaveDTO, FollowVO, FollowSaveDTO } from '@/types/crm/customer'
import type { PageVO } from '@/types/api'
import type { TagVO } from '@/types/crm/customer'

export const customerApi = {
  /** 私有池客户列表 */
  getList(params?: CustomerQueryDTO): Promise<PageVO<CustomerVO>> {
    return http.get<PageVO<CustomerVO>>('/crm/customer/list', params)
  },

  /** 公海池客户列表 */
  getPool(params?: CustomerQueryDTO): Promise<PageVO<CustomerVO>> {
    return http.get<PageVO<CustomerVO>>('/crm/customer/pool', params)
  },

  /** 客户详情 */
  getById(id: number): Promise<CustomerDetailVO> {
    return http.get<CustomerDetailVO>(`/crm/customer/${id}`)
  },

  /** 新增客户 */
  create(data: CustomerSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer', data)
  },

  /** 修改客户 */
  update(data: CustomerSaveDTO): Promise<void> {
    return http.put<void>('/crm/customer', data)
  },

  /** 删除客户 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/${id}`)
  },

  /** 从公海池领取 */
  takeFromPool(id: number): Promise<void> {
    return http.post<void>(`/crm/customer/pool/take/${id}`)
  },

  /** 释放到公海池 */
  releaseToPool(id: number): Promise<void> {
    return http.post<void>(`/crm/customer/pool/release/${id}`)
  },

  /** 转移客户 */
  transfer(data: PoolTransferDTO): Promise<void> {
    return http.post<void>('/crm/customer/pool/transfer', data)
  },

  /** 获取客户标签 */
  getCustomerTags(customerId: number): Promise<TagVO[]> {
    return http.get<TagVO[]>(`/crm/customer/${customerId}/tags`)
  },

  /** 绑定客户标签 */
  bindTags(customerId: number, tagIds: number[]): Promise<void> {
    return http.post<void>(`/crm/customer/${customerId}/tags`, tagIds)
  },

  /** 获取所有标签 */
  getAllTags(): Promise<TagVO[]> {
    return http.get<TagVO[]>('/crm/customer/tags')
  },

  // ==================== 联系人 ====================

  /** 获取联系人列表 */
  getContacts(customerId: number): Promise<ContactVO[]> {
    return http.get<ContactVO[]>(`/crm/customer/contact/list/${customerId}`)
  },

  /** 保存联系人（新增或修改） */
  saveContact(data: ContactSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer/contact/save', data)
  },

  /** 删除联系人 */
  deleteContact(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/contact/${id}`)
  },

  // ==================== 跟进记录 ====================

  /** 获取跟进记录列表 */
  getFollowRecords(customerId: number): Promise<FollowVO[]> {
    return http.get<FollowVO[]>(`/crm/customer/follow/list/${customerId}`)
  },

  /** 保存跟进记录 */
  saveFollow(data: FollowSaveDTO): Promise<number> {
    return http.post<number>('/crm/customer/follow/save', data)
  },

  /** 删除跟进记录 */
  deleteFollow(id: number): Promise<void> {
    return http.delete<void>(`/crm/customer/follow/${id}`)
  }
}