import { http } from './request'
import type { DictTypeVO, DictTypeTreeVO, DictTypeQueryDTO, DictTypeSaveDTO } from '@/types/dict-type'
import type { PageVO, Status } from '@/types/api'

export const dictTypeApi = {
  getList(params?: DictTypeQueryDTO): Promise<PageVO<DictTypeVO>> {
    return http.get<PageVO<DictTypeVO>>('/system/dict-type/list', params)
  },

  getById(id: number): Promise<DictTypeVO> {
    return http.get<DictTypeVO>(`/system/dict-type/${id}`)
  },

  create(data: DictTypeSaveDTO): Promise<number> {
    return http.post<number>('/system/dict-type', data)
  },

  update(data: DictTypeSaveDTO): Promise<void> {
    return http.put<void>('/system/dict-type', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/dict-type/${id}`)
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/dict-type/status', { id, status })
  },

  getOptions(): Promise<DictTypeVO[]> {
    return http.get<DictTypeVO[]>('/system/dict-type/options')
  },

  getTree(): Promise<DictTypeTreeVO[]> {
    return http.get<DictTypeTreeVO[]>('/system/dict-type/tree')
  }
}