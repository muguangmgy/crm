import { http } from '@/api/request'
import type { ContractVO, ContractQueryDTO, ContractSaveDTO } from '@/types/crm/contract'
import type { PageVO } from '@/types/api'

export const contractApi = {
  /** 合同列表 */
  getList(params?: ContractQueryDTO): Promise<PageVO<ContractVO>> {
    return http.get<PageVO<ContractVO>>('/crm/contract/list', params)
  },

  /** 合同下拉选项（发票/收款关联选择，仅租户隔离） */
  getOptions(status: number = 3): Promise<ContractVO[]> {
    return http.get<ContractVO[]>('/crm/contract/options', { status })
  },

  /** 合同详情 */
  getById(id: number): Promise<ContractVO> {
    return http.get<ContractVO>(`/crm/contract/${id}`)
  },

  /** 新增合同 */
  create(data: ContractSaveDTO): Promise<number> {
    return http.post<number>('/crm/contract', data)
  },

  /** 修改合同 */
  update(data: ContractSaveDTO): Promise<void> {
    return http.put<void>('/crm/contract', data)
  },

  /** 删除合同 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/contract/${id}`)
  },

  /** 提交审批 */
  submit(id: number): Promise<void> {
    return http.post<void>(`/crm/contract/${id}/submit`)
  },

  /** 审批通过 */
  approve(id: number): Promise<void> {
    return http.post<void>(`/crm/contract/${id}/approve`)
  },

  /** 驳回 */
  reject(id: number): Promise<void> {
    return http.post<void>(`/crm/contract/${id}/reject`)
  },

  /** 终止合同 */
  terminate(id: number): Promise<void> {
    return http.post<void>(`/crm/contract/${id}/terminate`)
  }
}