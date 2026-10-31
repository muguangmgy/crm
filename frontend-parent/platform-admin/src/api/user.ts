import { http } from './request'
import type { UserVO, UserQueryDTO, UserSaveDTO } from '@/types/user'
import type { PageVO, Status } from '@/types/api'

export const userApi = {
  getList(params?: UserQueryDTO): Promise<PageVO<UserVO>> {
    return http.get<PageVO<UserVO>>('/system/user/list', params)
  },

  getById(id: number): Promise<UserVO> {
    return http.get<UserVO>(`/system/user/${id}`)
  },

  create(data: UserSaveDTO): Promise<number> {
    return http.post<number>('/system/user', data)
  },

  update(data: UserSaveDTO): Promise<void> {
    return http.put<void>('/system/user', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/user/${id}`)
  },

  resetPassword(id: number, password: string): Promise<void> {
    return http.put<void>('/system/user/password', { id, password })
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/user/status', { id, status })
  },

  getOptions(): Promise<UserVO[]> {
    return http.get<UserVO[]>('/system/user/options')
  }
}