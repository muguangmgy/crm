<template>
  <div class="page-container">
    <!-- 工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-form :inline="true" class="toolbar-form">
        <!-- 租户选择器（仅平台管理员可见） -->
        <el-form-item label="租户" v-if="userStore.isPlatformRole">
          <el-select v-model="queryParams.tenantId" placeholder="请选择租户" clearable @change="handleTenantChange">
            <el-option
                v-for="tenant in tenantOptions"
                :key="tenant.id"
                :label="tenant.tenantName"
                :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1"/>
            <el-option label="禁用" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
          <el-button type="success" v-permission="'system:dept:add'" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 部门树表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
          v-loading="loading"
          :data="deptTree"
          border
          stripe
          row-key="id"
          default-expand-all
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200"/>
        <el-table-column prop="deptPath" label="部门路径" min-width="180"/>
        <el-table-column prop="leaderName" label="负责人" min-width="120"/>
        <el-table-column prop="sort" label="排序" width="80"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
                v-if="userStore.hasPermission('system:dept:edit')"
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(row)"
            />
            <el-tag v-else :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180"/>
        <el-table-column label="操作" width="200" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'system:dept:edit'" @click="handleEdit(row)">
              <el-icon>
                <Edit/>
              </el-icon>
              编辑
            </el-button>
            <el-button type="success" link v-permission="'system:dept:add'" @click="handleAddChild(row)">
              <el-icon>
                <Plus/>
              </el-icon>
              新增
            </el-button>
            <el-button type="danger" link v-permission="'system:dept:delete'" @click="handleDelete(row)">
              <el-icon>
                <Delete/>
              </el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
      >
        <!-- 租户选择（平台用户可见，编辑或新增子部门时禁用） -->
        <el-form-item label="租户" prop="tenantId" v-if="userStore.isPlatformRole">
          <el-select
              v-model="formData.tenantId"
              placeholder="请选择租户"
              @change="handleDialogTenantChange"
              :disabled="!!formData.id || formData.parentId > 0"
          >
            <el-option
                v-for="tenant in tenantOptions"
                :key="tenant.id"
                :label="tenant.tenantName"
                :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
              v-model="formData.parentId"
              :data="dialogDeptTree"
              :props="{ label: 'deptName', value: 'id', children: 'children' }"
              placeholder="请选择上级部门（空则为顶级）"
              clearable
              check-strictly
              :disabled="(userStore.isPlatformRole && !formData.tenantId) || (formData.parentId > 0 && !formData.id)"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称"/>
        </el-form-item>
        <el-form-item label="负责人" prop="leaderId">
          <el-select v-model="formData.leaderId" placeholder="请选择负责人" clearable filterable>
            <el-option
                v-for="user in userOptions"
                :key="user.id"
                :label="user.userName + ' (' + user.userCode + ')'"
                :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {deptApi} from '@/api/dept'
import {tenantApi} from '@/api/tenant'
import {userApi} from '@/api/user'
import {useUserStore} from '@/stores/user'
import type {DeptQueryDTO, DeptSaveDTO, DeptVO} from '@/types/dept'
import type {TenantVO} from '@/types/tenant'
import type {UserVO} from '@/types/user'

const userStore = useUserStore()

const hasOperPermission = computed(() =>
    userStore.hasPermission('system:dept:edit') ||
    userStore.hasPermission('system:dept:add') ||
    userStore.hasPermission('system:dept:delete')
)

const loading = ref(false)
const submitLoading = ref(false)
const deptTree = ref<DeptVO[]>([])
const dialogDeptTree = ref<DeptVO[]>([])  // 弹窗中的上级部门选择树
const tenantOptions = ref<TenantVO[]>([])
const userOptions = ref<UserVO[]>([])

const queryParams = reactive<DeptQueryDTO>({
  pageNum: 1,
  pageSize: 1000,
  tenantId: undefined,
  deptName: undefined,
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑部门' : '新增部门')
const formRef = ref<FormInstance>()

const formData = reactive<DeptSaveDTO>({
  id: undefined,
  tenantId: undefined,
  parentId: null,
  deptName: '',
  leaderId: undefined,
  sort: 0,
  status: 1
})

const formRules = computed<FormRules>(() => ({
  tenantId: userStore.isPlatformRole
    ? [{ required: true, message: '请选择租户', trigger: 'change' }]
    : [],
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}))

async function loadData() {
  loading.value = true
  try {
    const res = await deptApi.getList(queryParams)
    // 将列表数据转换为树形结构
    deptTree.value = buildTree(res.list)
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

// 将列表转换为树形结构
function buildTree(list: DeptVO[]): DeptVO[] {
  if (!list.length) return []

  const allIds = new Set(list.map(item => item.id))
  const map = new Map<number, DeptVO[]>()

  // 按 parentId 分组
  list.forEach(item => {
    const parentId = item.parentId || 0
    if (!map.has(parentId)) {
      map.set(parentId, [])
    }
    map.get(parentId)!.push(item)
  })

  // 递归构建树
  const build = (parentId: number): DeptVO[] => {
    const children = map.get(parentId) || []
    children.forEach(child => {
      child.children = build(child.id)
    })
    return children
  }

  // 找出所有根节点：parentId=0 或父节点不在列表中
  const rootNodes: DeptVO[] = []
  list.forEach(item => {
    const parentId = item.parentId || 0
    // 如果 parentId=0 或父节点不在结果列表中，作为根节点
    if (parentId === 0 || !allIds.has(parentId)) {
      // 已经在 map.get(0) 中处理过的不要重复添加
      if (parentId === 0) {
        // 已经通过 build(0) 处理
      } else {
        // 父节点不存在的情况，需要单独处理
        const existingRoot = rootNodes.find(r => r.id === item.id)
        if (!existingRoot) {
          item.children = build(item.id)
          rootNodes.push(item)
        }
      }
    }
  })

  // 合并顶级部门和不存在的父节点的部门
  const topLevel = build(0)
  return [...topLevel, ...rootNodes]
}

async function loadTenantOptions() {
  if (!userStore.isPlatformRole) return
  try {
    const res = await tenantApi.getOptions()
    tenantOptions.value = res
  } catch {
    // 静默处理
  }
}

async function loadUserOptions() {
  try {
    const res = await userApi.getOptions()
    userOptions.value = res
  } catch {
    // 静默处理
  }
}

function handleTenantChange() {
  queryParams.pageNum = 1
  loadData()
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.deptName = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

async function handleAdd() {
  formData.id = undefined
  formData.tenantId = userStore.isPlatformRole ? undefined : userStore.userInfo?.tenantId
  formData.parentId = null
  formData.deptName = ''
  formData.leaderId = undefined
  formData.sort = 0
  formData.status = 1
  dialogDeptTree.value = []
  // 非平台用户先加载当前租户的部门树
  if (!userStore.isPlatformRole && userStore.userInfo?.tenantId) {
    await loadDialogDeptTree(userStore.userInfo.tenantId)
  }
  dialogVisible.value = true
}

async function handleAddChild(row: DeptVO) {
  formData.id = undefined
  formData.tenantId = row.tenantId  // 使用上级部门的租户ID
  formData.parentId = row.id
  formData.deptName = ''
  formData.leaderId = undefined
  formData.sort = 0
  formData.status = 1
  // 先加载部门树，再打开弹窗
  if (row.tenantId) {
    await loadDialogDeptTree(row.tenantId)
  }
  dialogVisible.value = true
}

async function handleEdit(row: DeptVO) {
  formData.id = row.id
  formData.tenantId = row.tenantId
  formData.parentId = row.parentId
  formData.deptName = row.deptName
  formData.leaderId = row.leaderId && row.leaderId > 0 ? row.leaderId : undefined
  formData.sort = row.sort
  formData.status = row.status
  // 先加载部门树，再打开弹窗
  if (row.tenantId) {
    await loadDialogDeptTree(row.tenantId)
  }
  dialogVisible.value = true
}

// 弹窗中租户变更，重新加载部门树
async function handleDialogTenantChange() {
  formData.parentId = null  // 清空上级部门选择
  if (formData.tenantId) {
    await loadDialogDeptTree(formData.tenantId)
  } else {
    dialogDeptTree.value = []
  }
}

// 加载弹窗部门树（根据租户ID）
async function loadDialogDeptTree(tenantId: number) {
  try {
    const res = await deptApi.getList({
      pageNum: 1,
      pageSize: 1000,
      tenantId: tenantId
    })
    const tree = buildTree(res.list)
    // 平台用户可选择顶级部门
    if (userStore.isPlatformRole) {
      dialogDeptTree.value = [
        { id: 0, deptName: '顶级部门', children: [] } as DeptVO,
        ...tree
      ]
    } else {
      dialogDeptTree.value = tree
    }
  } catch {
    dialogDeptTree.value = []
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await deptApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await deptApi.create(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 静默处理
  } finally {
    submitLoading.value = false
  }
}

async function handleStatusChange(row: DeptVO) {
  try {
    await deptApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function handleDelete(row: DeptVO) {
  if (row.children?.length) {
    ElMessage.warning('存在子部门，不能删除')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除部门 ${row.deptName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deptApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  loadData()
  loadTenantOptions()
  loadUserOptions()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}

.table-card {
  border-radius: 12px;
}
</style>