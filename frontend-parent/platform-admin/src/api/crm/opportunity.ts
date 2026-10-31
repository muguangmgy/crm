import { http } from '@/api/request'
import type {
  OpportunityVO,
  OpportunityQueryDTO,
  OpportunitySaveDTO,
  OpportunityCloseDTO,
  StageVO,
  StageSaveDTO
} from '@/types/crm/opportunity'
import type { PageVO } from '@/types/api'

export const opportunityApi = {
  /** 商机列表 */
  getList(params?: OpportunityQueryDTO): Promise<PageVO<OpportunityVO>> {
    return http.get<PageVO<OpportunityVO>>('/crm/opportunity/list', params)
  },

  /** 商机详情 */
  getById(id: number): Promise<OpportunityVO> {
    return http.get<OpportunityVO>(`/crm/opportunity/${id}`)
  },

  /** 新增商机 */
  create(data: OpportunitySaveDTO): Promise<number> {
    return http.post<number>('/crm/opportunity', data)
  },

  /** 修改商机 */
  update(data: OpportunitySaveDTO): Promise<void> {
    return http.put<void>('/crm/opportunity', data)
  },

  /** 删除商机 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/opportunity/${id}`)
  },

  /** 阶段推进 */
  advance(id: number, nextStageId: number): Promise<void> {
    return http.post<void>(`/crm/opportunity/advance/${id}`, null, { nextStageId })
  },

  /** 成交/失败 */
  close(id: number, data: OpportunityCloseDTO): Promise<void> {
    return http.post<void>(`/crm/opportunity/close/${id}`, data)
  },

  /** 阶段列表（全部） */
  getStages(): Promise<StageVO[]> {
    return http.get<StageVO[]>('/crm/opportunity/stage/list')
  },

  /** 阶段列表（启用） */
  getActiveStages(): Promise<StageVO[]> {
    return http.get<StageVO[]>('/crm/opportunity/stage/active')
  },

  /** 新增阶段 */
  createStage(data: StageSaveDTO): Promise<number> {
    return http.post<number>('/crm/opportunity/stage', data)
  },

  /** 修改阶段 */
  updateStage(data: StageSaveDTO): Promise<void> {
    return http.put<void>('/crm/opportunity/stage', data)
  },

  /** 删除阶段 */
  deleteStage(id: number): Promise<void> {
    return http.delete<void>(`/crm/opportunity/stage/${id}`)
  }
}