import {ref} from 'vue'
import {dictDataApi} from '@/api/dict-data'
import type {DictDataVO} from '@/types/dict-data'

/** 字典缓存 */
const dictCache = new Map<string, DictDataVO[]>()

/** 正在加载的字典 */
const loadingDicts = new Map<string, boolean>()

/**
 * 获取字典数据组合式函数
 * @param dictCode 字典编码
 */
export function useDict(dictCode: string) {
    const data = ref<DictDataVO[]>([])
    const loading = ref(false)

    // 从缓存获取
    if (dictCache.has(dictCode)) {
        data.value = dictCache.get(dictCode) || []
    } else if (!loadingDicts.get(dictCode)) {
        // 加载字典
        loadingDicts.set(dictCode, true)
        loading.value = true
        dictDataApi.getDictDataByCode(dictCode).then(res => {
            data.value = res
            dictCache.set(dictCode, res)
        }).finally(() => {
            loading.value = false
            loadingDicts.set(dictCode, false)
        })
    }

    return {data, loading}
}

/**
 * 批量获取多个字典
 * @param dictCodes 字典编码数组
 */
export function useDicts(dictCodes: string[]) {
    const dataMap = ref<Record<string, DictDataVO[]>>({})
    /** 全部字典请求结束（成功/失败都算），避免页面因空字典一直转圈 */
    const loaded = ref(false)

    const loadDicts = async () => {
        try {
            for (const code of dictCodes) {
                if (!dictCache.has(code)) {
                    try {
                        const res = await dictDataApi.getDictDataByCode(code)
                        const list = Array.isArray(res) ? res : []
                        dictCache.set(code, list)
                        dataMap.value[code] = list
                    } catch (e) {
                        console.error(`加载字典 ${code} 失败`, e)
                        dataMap.value[code] = []
                    }
                } else {
                    dataMap.value[code] = dictCache.get(code) || []
                }
            }
        } finally {
            loaded.value = true
        }
    }

    loadDicts()

    return {dataMap, loaded}
}

/**
 * 获取字典标签
 * @param dictCode 字典编码
 * @param value 字典值
 */
export function getDictLabel(dictCode: string, value: string | number): string {
    const data = dictCache.get(dictCode)
    if (!data) return String(value)
    const item = data.find(d => d.itemValue === String(value))
    return item?.itemLabel || String(value)
}

/**
 * 从字典数据数组获取标签（响应式友好）
 * @param dictData 字典数据数组
 * @param value 字典值
 */
export function getDictLabelFromData(dictData: DictDataVO[] | undefined, value: string | number): string {
    if (!dictData?.length) return String(value)
    const item = dictData.find(d => d.itemValue === String(value))
    return item?.itemLabel || String(value)
}

/**
 * 清除字典缓存
 */
export function clearDictCache() {
    dictCache.clear()
    loadingDicts.clear()
}