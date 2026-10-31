import { http } from './request'
import type { MenuVO, MenuQueryDTO, MenuSaveDTO } from '@/types/menu'
import type { PageVO } from '@/types/api'

export const menuApi = {
  getList(params?: MenuQueryDTO): Promise<PageVO<MenuVO>> {
    return http.get<PageVO<MenuVO>>('/system/menu/list', params)
  },

  getTree(): Promise<MenuVO[]> {
    return http.get<MenuVO[]>('/system/menu/tree')
  },

  getUserMenus(roleId?: number): Promise<MenuVO[]> {
    const params = roleId ? { roleId } : {}
    return http.get<MenuVO[]>('/system/menu/userMenus', params)
  },

  getById(id: number): Promise<MenuVO> {
    return http.get<MenuVO>(`/system/menu/${id}`)
  },

  create(data: MenuSaveDTO): Promise<number> {
    return http.post<number>('/system/menu', data)
  },

  update(data: MenuSaveDTO): Promise<void> {
    return http.put<void>('/system/menu', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/menu/${id}`)
  }
}