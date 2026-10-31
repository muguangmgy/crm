import { http } from './request'
import type { TenantVO, TenantQueryDTO, TenantSaveDTO } from '@/types/tenant'
import type { PageVO, Status } from '@/types/api'

export const tenantApi = {
  getList(params?: TenantQueryDTO): Promise<PageVO<TenantVO>> {
    return http.get<PageVO<TenantVO>>('/system/tenant/list', params)
  },

  getById(id: number): Promise<TenantVO> {
    return http.get<TenantVO>(`/system/tenant/${id}`)
  },

  create(data: TenantSaveDTO): Promise<number> {
    return http.post<number>('/system/tenant', data)
  },

  update(data: TenantSaveDTO): Promise<void> {
    return http.put<void>('/system/tenant', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/tenant/${id}`)
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/tenant/status', { id, status })
  },

  getOptions(): Promise<TenantVO[]> {
    return http.get<TenantVO[]>('/system/tenant/options')
  }
}