import type { Status } from './api'

/** 角色信息 */
export interface RoleVO {
  id: number
  tenantId?: number
  roleName: string
  roleCode: string
  dataScope?: number
  status: Status
  remark?: string
  createTime: string
}

/** 角色查询参数 */
export interface RoleQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  roleName?: string
  roleCode?: string
  status?: Status
}

/** 角色保存参数 */
export interface RoleSaveDTO {
  id?: number
  tenantId?: number
  roleName: string
  roleCode: string
  dataScope?: number
  status: Status
  remark?: string
}