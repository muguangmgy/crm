import { http } from './request'
import type { LoginLogVO, LoginLogQueryDTO, OperLogVO, OperLogQueryDTO } from '@/types/log'
import type { PageVO } from '@/types/api'

export const logApi = {
  login: {
    getList(params?: LoginLogQueryDTO): Promise<PageVO<LoginLogVO>> {
      return http.get<PageVO<LoginLogVO>>('/system/log/login/list', params)
    },
    clean(keepDays: number): Promise<void> {
      return http.delete<void>(`/system/log/login/clean?keepDays=${keepDays}`)
    }
  },
  oper: {
    getList(params?: OperLogQueryDTO): Promise<PageVO<OperLogVO>> {
      return http.get<PageVO<OperLogVO>>('/system/log/oper/list', params)
    },
    clean(keepDays: number): Promise<void> {
      return http.delete<void>(`/system/log/oper/clean?keepDays=${keepDays}`)
    }
  }
}