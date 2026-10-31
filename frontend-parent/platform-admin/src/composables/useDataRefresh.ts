import { watch } from 'vue'
import { useMenuStore } from '@/stores/menu'

/**
 * 监听角色切换，自动重新加载数据
 * @param loadData 数据加载函数
 * @param immediate 是否立即执行一次
 */
export function useDataRefresh(loadData: () => void, immediate = true) {
  const menuStore = useMenuStore()

  watch(
    () => menuStore.dataRefreshTrigger,
    () => {
      loadData()
    },
    { immediate }
  )
}