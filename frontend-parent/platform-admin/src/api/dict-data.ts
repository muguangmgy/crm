import {http} from './request'
import type {DictDataBatchSaveDTO, DictDataQueryDTO, DictDataSaveDTO, DictDataVO} from '@/types/dict-data'
import type {PageVO, Status} from '@/types/api'

export const dictDataApi = {
    getList(params?: DictDataQueryDTO): Promise<PageVO<DictDataVO>> {
        return http.get<PageVO<DictDataVO>>('/system/dict-data/list', params)
    },

    getById(id: number): Promise<DictDataVO> {
        return http.get<DictDataVO>(`/system/dict-data/${id}`)
    },

    create(data: DictDataSaveDTO): Promise<number> {
        return http.post<number>('/system/dict-data', data)
    },

    update(data: DictDataSaveDTO): Promise<void> {
        return http.put<void>('/system/dict-data', data)
    },

    delete(id: number): Promise<void> {
        return http.delete<void>(`/system/dict-data/${id}`)
    },

    updateStatus(id: number, status: Status): Promise<void> {
        return http.put<void>('/system/dict-data/status', {id, status})
    },

    /** 根据字典编码和租户ID获取字典项 */
    getByCode(dictCode: string, tenantId?: number): Promise<DictDataVO[]> {
        return http.get<DictDataVO[]>(`/system/dict-data/by-code/${dictCode}`, {tenantId})
    },

    getDictDataByCode(dictCode: string): Promise<DictDataVO[]> {
        return http.get<DictDataVO[]>(`/system/dict-data/getDictDataByCode/${dictCode}`)
    },

    batchSave(data: DictDataBatchSaveDTO): Promise<void> {
        return http.post<void>('/system/dict-data/batch', data)
    }
}