import type {Status} from './api'

/** 登录类型：1登录 2注销 3刷新 */
export type LoginType = 1 | 2 | 3

/** 登录日志 */
export interface LoginLogVO {
    id: number
    userId: number
    tenantId?: number
    loginType: LoginType
    ipAddress: string
    deviceInfo?: string
    browser?: string
    os?: string
    loginTime: string
    status: Status
    message?: string
}

/** 登录日志查询参数 */
export interface LoginLogQueryDTO {
    pageNum?: number
    pageSize?: number
    userCode?: string
    loginType?: LoginType
    status?: Status
}

/** 操作日志 */
export interface OperLogVO {
    id: number
    tenantId?: number
    userCode: string
    module?: string
    operation?: string
    targetType?: string
    targetId?: string
    requestData?: string
    responseStatus?: number
    ipAddress: string
    operTime: string
    duration?: number
}

/** 操作日志查询参数 */
export interface OperLogQueryDTO {
    pageNum?: number
    pageSize?: number
    userId?: number
    module?: string
    operation?: string
    status?: Status
}