import { http } from '@/api/request'
import type {
  PaymentPlanVO,
  PaymentPlanQueryDTO,
  PaymentPlanSaveDTO,
  PaymentRecordVO,
  PaymentRecordQueryDTO,
  PaymentRecordSaveDTO
} from '@/types/crm/payment'
import type { PageVO } from '@/types/api'

export const paymentPlanApi = {
  /** 收款计划列表 */
  getList(params?: PaymentPlanQueryDTO): Promise<PageVO<PaymentPlanVO>> {
    return http.get<PageVO<PaymentPlanVO>>('/crm/payment/plan/list', params)
  },

  /** 收款计划详情 */
  getById(id: number): Promise<PaymentPlanVO> {
    return http.get<PaymentPlanVO>(`/crm/payment/plan/${id}`)
  },

  /** 根据合同ID查询收款计划 */
  getByContractId(contractId: number): Promise<PaymentPlanVO[]> {
    return http.get<PaymentPlanVO[]>(`/crm/payment/plan/contract/${contractId}`)
  },

  /** 新增收款计划 */
  create(data: PaymentPlanSaveDTO): Promise<number> {
    return http.post<number>('/crm/payment/plan', data)
  },

  /** 修改收款计划 */
  update(data: PaymentPlanSaveDTO): Promise<number> {
    return http.put<number>('/crm/payment/plan', data)
  },

  /** 删除收款计划 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/payment/plan/${id}`)
  }
}

export const paymentRecordApi = {
  /** 收款记录列表 */
  getList(params?: PaymentRecordQueryDTO): Promise<PageVO<PaymentRecordVO>> {
    return http.get<PageVO<PaymentRecordVO>>('/crm/payment/record/list', params)
  },

  /** 收款记录详情 */
  getById(id: number): Promise<PaymentRecordVO> {
    return http.get<PaymentRecordVO>(`/crm/payment/record/${id}`)
  },

  /** 根据合同ID查询收款记录 */
  getByContractId(contractId: number): Promise<PaymentRecordVO[]> {
    return http.get<PaymentRecordVO[]>(`/crm/payment/record/contract/${contractId}`)
  },

  /** 新增收款记录 */
  create(data: PaymentRecordSaveDTO): Promise<number> {
    return http.post<number>('/crm/payment/record', data)
  },

  /** 修改收款记录 */
  update(data: PaymentRecordSaveDTO): Promise<number> {
    return http.put<number>('/crm/payment/record', data)
  },

  /** 删除收款记录 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/payment/record/${id}`)
  },

  /** 收款确认 */
  confirm(id: number): Promise<void> {
    return http.post<void>(`/crm/payment/record/${id}/confirm`)
  },

  /** 取消确认 */
  unconfirm(id: number): Promise<void> {
    return http.post<void>(`/crm/payment/record/${id}/unconfirm`)
  }
}