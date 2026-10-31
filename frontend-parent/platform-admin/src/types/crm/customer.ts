import type { Status } from '@/types/api'

/** 客户VO */
export interface CustomerVO {
  id: number
  tenantId: number
  deptId?: number
  deptName?: string
  customerType: number  // 数字类型（后端为Integer）
  customerName: string
  customerNo?: string
  industry?: string     // 字符串，支持字典值
  companySize?: string  // 字符串，支持字典值
  province?: string
  city?: string
  address?: string
  website?: string
  gender?: number
  birthday?: string
  occupation?: string
  phone?: string
  email?: string
  source?: string       // 字符串，支持字典值
  level: number         // 数字类型（后端为Integer）
  status: Status
  poolType: number
  ownerId?: number
  ownerName?: string
  lockTime?: string
  nextFollowTime?: string
  remark?: string
  tagIds?: number[]
  tags?: TagVO[]
  createTime: string
  updateTime: string
}

/** 客户详情VO */
export interface CustomerDetailVO {
  customer: CustomerVO
  contacts: ContactVO[]
  followRecords: FollowVO[]
}

/** 客户查询DTO */
export interface CustomerQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  customerName?: string
  customerType?: number  // 数字类型
  poolType?: number
  level?: number         // 数字类型
  status?: Status
  industry?: string
  province?: string
  source?: string
  ownerId?: number
  deptId?: number
}

/** 客户保存DTO */
export interface CustomerSaveDTO {
  id?: number
  customerType: number   // 数字类型
  customerName: string
  industry?: string
  companySize?: string
  province?: string
  city?: string
  address?: string
  website?: string
  gender?: number
  birthday?: string
  occupation?: string
  phone?: string
  email?: string
  source?: string
  level?: number         // 数字类型
  status?: Status
  remark?: string
  tagIds?: number[]
}

/** 转移DTO */
export interface PoolTransferDTO {
  customerId: number
  newOwnerId: number
}

/** 联系人VO */
export interface ContactVO {
  id: number
  customerId: number
  contactName: string
  gender?: number
  position?: string
  department?: string
  isPrimary: number
  phone?: string
  mobile?: string
  email?: string
  wechat?: string
  remark?: string
  createTime: string
}

/** 联系人保存DTO */
export interface ContactSaveDTO {
  id?: number
  customerId: number
  contactName: string
  gender?: number
  position?: string
  department?: string
  isPrimary?: number
  phone?: string
  mobile?: string
  email?: string
  wechat?: string
  remark?: string
}

/** 标签VO */
export interface TagVO {
  id: number
  tagName: string
  tagColor?: string
  sort: number
  status: Status
  createTime: string
}

/** 标签保存DTO */
export interface TagSaveDTO {
  id?: number
  tagName: string
  tagColor?: string
  sort?: number
  status?: Status
}

/** 跟进记录VO */
export interface FollowVO {
  id: number
  customerId: number
  followType?: string    // 字符串，支持字典值
  followContent: string
  nextFollowTime?: string
  ownerId: number
  ownerName?: string
  createTime: string
}

/** 跟进记录保存DTO */
export interface FollowSaveDTO {
  id?: number
  customerId: number
  followType?: string
  followContent: string
  nextFollowTime?: string
}