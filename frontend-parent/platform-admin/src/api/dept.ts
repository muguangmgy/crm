import { http } from './request'
import type { DeptVO, DeptQueryDTO, DeptSaveDTO } from '@/types/dept'
import type { PageVO, Status } from '@/types/api'

export const deptApi = {
  getList(params?: DeptQueryDTO): Promise<PageVO<DeptVO>> {
    return http.get<PageVO<DeptVO>>('/system/dept/list', params)
  },

  getTree(tenantId?: number): Promise<DeptVO[]> {
    return http.get<DeptVO[]>('/system/dept/tree', tenantId ? { tenantId } : undefined)
  },

  getById(id: number): Promise<DeptVO> {
    return http.get<DeptVO>(`/system/dept/${id}`)
  },

  create(data: DeptSaveDTO): Promise<number> {
    return http.post<number>('/system/dept', data)
  },

  update(data: DeptSaveDTO): Promise<void> {
    return http.put<void>('/system/dept', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/dept/${id}`)
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/dept/status', { id, status })
  }
}