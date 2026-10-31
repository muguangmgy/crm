import { http } from './request'
import type { RoleVO, RoleQueryDTO, RoleSaveDTO } from '@/types/role'
import type { PageVO, Status } from '@/types/api'

export const roleApi = {
  getList(params?: RoleQueryDTO): Promise<PageVO<RoleVO>> {
    return http.get<PageVO<RoleVO>>('/system/role/list', params)
  },

  getById(id: number): Promise<RoleVO> {
    return http.get<RoleVO>(`/system/role/${id}`)
  },

  create(data: RoleSaveDTO): Promise<number> {
    return http.post<number>('/system/role', data)
  },

  update(data: RoleSaveDTO): Promise<void> {
    return http.put<void>('/system/role', data)
  },

  delete(id: number): Promise<void> {
    return http.delete<void>(`/system/role/${id}`)
  },

  updateStatus(id: number, status: Status): Promise<void> {
    return http.put<void>('/system/role/status', { id, status })
  },

  getOptions(): Promise<RoleVO[]> {
    return http.get<RoleVO[]>('/system/role/options')
  },

  /** 获取角色已分配的菜单ID列表 */
  getMenuIds(roleId: number): Promise<number[]> {
    return http.get<number[]>(`/system/role/${roleId}/menus`)
  },

  /** 分配角色菜单权限 */
  assignMenus(roleId: number, menuIds: number[]): Promise<void> {
    return http.post<void>(`/system/role/${roleId}/menus`, menuIds)
  }
}