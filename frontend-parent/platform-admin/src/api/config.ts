import { http } from './request'
import type { ConfigVO, ConfigQueryDTO, ConfigSaveDTO } from '@/types/config'
import type { PageVO, Status } from '@/types/api'

export const configApi = {
  getList(params?: ConfigQueryDTO): Promise<PageVO<ConfigVO>> {
    return http.get<PageVO<ConfigVO>>('/system/config/list', params)
  },

  getById(id: number): Promise<ConfigVO> {
    return http.get<ConfigVO>(`/system/config/${id}`)
  },

  create(data: ConfigSaveDTO): Promise<number> {
    return http.post<number>('/system/config', data)
  },

  update(data: ConfigSaveDTO): Promise<void> {
    return http.put<void>('/system/config', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/config/${id}`)
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/config/status', { id, status })
  },

  /** 根据参数键获取参数（租户优先） */
  getByKey(configKey: string): Promise<ConfigVO> {
    return http.get<ConfigVO>(`/system/config/by-key/${configKey}`)
  },

  /** 根据分组获取参数列表（租户优先） */
  getByGroup(groupCode: string): Promise<ConfigVO[]> {
    return http.get<ConfigVO[]>(`/system/config/by-group/${groupCode}`)
  }
}