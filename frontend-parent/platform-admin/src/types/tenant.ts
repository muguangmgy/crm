import type { Status } from './api'

/** 租户信息 */
export interface TenantVO {
  id: number
  tenantName: string
  tenantCode: string
  expireTime?: string
  contactName?: string
  contactPhone?: string
  status: Status
  createTime: string
}

/** 租户查询参数 */
export interface TenantQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantName?: string
  tenantCode?: string
  status?: Status
}

/** 租户保存参数 */
export interface TenantSaveDTO {
  id?: number
  tenantName: string
  tenantCode: string
  expireTime?: string
  contactName?: string
  contactPhone?: string
  status: Status
}