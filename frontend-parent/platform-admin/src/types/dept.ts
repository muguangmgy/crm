import type { Status } from './api'

/** 部门信息 */
export interface DeptVO {
  id: number
  tenantId?: number
  parentId: number | null
  deptName: string
  deptPath?: string
  leaderId?: number
  leaderName?: string
  sort?: number
  status?: Status
  createTime?: string
  children?: DeptVO[]
}

/** 部门查询参数 */
export interface DeptQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  deptName?: string
  status?: Status
}

/** 部门保存参数 */
export interface DeptSaveDTO {
  id?: number
  tenantId?: number
  parentId: number | null
  deptName: string
  leaderId?: number
  sort: number
  status: Status
}