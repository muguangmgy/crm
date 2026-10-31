import { ref } from 'vue'
import { deptApi } from '@/api/dept'
import { userApi } from '@/api/user'
import type { DeptVO } from '@/types/dept'
import type { UserVO } from '@/types/user'

export interface AnalysisDeptOption {
  id: number
  deptName: string
}

export interface AnalysisUserOption {
  id: number
  userName: string
  deptId?: number
}

/** 扁平化部门树，供分析页下拉使用 */
const flattenDeptTree = (nodes: DeptVO[], result: AnalysisDeptOption[] = []): AnalysisDeptOption[] => {
  for (const node of nodes) {
    if (node.id != null) {
      result.push({ id: node.id, deptName: node.deptName })
    }
    if (node.children?.length) {
      flattenDeptTree(node.children, result)
    }
  }
  return result
}

/**
 * 分析页部门 / 销售员筛选选项
 */
export function useAnalysisFilters() {
  const deptOptions = ref<AnalysisDeptOption[]>([])
  const userOptions = ref<AnalysisUserOption[]>([])
  const filterLoading = ref(false)

  const loadFilterOptions = async () => {
    filterLoading.value = true
    try {
      const [deptTree, users] = await Promise.all([
        deptApi.getTree(),
        userApi.getOptions()
      ])
      deptOptions.value = flattenDeptTree(deptTree || [])
      userOptions.value = (users || []).map((u: UserVO) => ({
        id: u.id,
        userName: u.userName,
        deptId: u.deptId
      }))
    } catch (e) {
      console.error('加载分析筛选选项失败', e)
      deptOptions.value = []
      userOptions.value = []
    } finally {
      filterLoading.value = false
    }
  }

  /** 按部门过滤销售员选项 */
  const filterUsersByDept = (deptId?: number) => {
    if (!deptId) return userOptions.value
    return userOptions.value.filter(u => u.deptId === deptId)
  }

  return {
    deptOptions,
    userOptions,
    filterLoading,
    loadFilterOptions,
    filterUsersByDept
  }
}
