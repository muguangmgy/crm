<template>
  <div class="page-container">
    <!-- 工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="toolbar-form">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-select v-model="queryParams.menuType" placeholder="请选择类型" clearable>
            <el-option label="目录" :value="1" />
            <el-option label="菜单" :value="2" />
            <el-option label="按钮" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" v-permission="'system:menu:add'" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 菜单树表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="menuTree"
        border
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="menu-table"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="240">
          <template #default="{ row }">
            <div class="menu-name-cell">
              <span class="expand-icon">
                <el-icon v-if="row.children && row.children.length > 0" @click="handleToggleExpand(row)">
                  <ArrowDown v-if="isExpanded(row)" />
                  <ArrowRight v-else />
                </el-icon>
              </span>
              <span class="icon-wrapper">
                <el-icon v-if="row.icon" class="menu-icon">
                  <component :is="row.icon" />
                </el-icon>
              </span>
              <span class="menu-name-text">{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 1" type="primary" effect="plain">目录</el-tag>
            <el-tag v-if="row.menuType === 2" type="success" effect="plain">菜单</el-tag>
            <el-tag v-if="row.menuType === 3" type="warning" effect="plain">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="180" />
        <el-table-column prop="component" label="组件路径" min-width="180" />
        <el-table-column prop="perms" label="权限标识" min-width="180" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">启用</el-tag>
            <el-tag v-if="row.status === 0" type="danger" size="small">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="200" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-permission="'system:menu:edit'" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="success" link size="small" v-permission="'system:menu:add'" @click="handleAddChild(row)" v-if="row.menuType !== 3">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button type="danger" link size="small" v-permission="'system:menu:delete'" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
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
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuTreeForSelect"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单（空则为顶级）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="formData.menuType !== 3">
          <el-input v-model="formData.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="formData.menuType === 2">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="formData.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="formData.menuType !== 3">
          <IconPicker v-model="formData.icon" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="可见" prop="visible" v-if="formData.menuType !== 3">
          <el-radio-group v-model="formData.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { menuApi } from '@/api/menu'
import IconPicker from '@/components/common/IconPicker.vue'
import { useUserStore } from '@/stores/user'
import type { MenuVO, MenuSaveDTO, MenuQueryDTO, MenuType } from '@/types/menu'

const userStore = useUserStore()

const hasOperPermission = computed(() =>
  userStore.hasPermission('system:menu:edit') ||
  userStore.hasPermission('system:menu:add') ||
  userStore.hasPermission('system:menu:delete')
)

const loading = ref(false)
const submitLoading = ref(false)
const menuTree = ref<MenuVO[]>([])
const tableRef = ref()
const expandedKeys = ref<Set<number>>(new Set())

// 检查行是否展开
function isExpanded(row: MenuVO): boolean {
  return expandedKeys.value.has(row.id)
}

// 切换展开/折叠
function handleToggleExpand(row: MenuVO) {
  if (isExpanded(row)) {
    expandedKeys.value.delete(row.id)
    tableRef.value?.toggleRowExpansion(row, false)
  } else {
    expandedKeys.value.add(row.id)
    tableRef.value?.toggleRowExpansion(row, true)
  }
}

// 初始化时展开所有
function initExpandAll(items: MenuVO[]) {
  for (const item of items) {
    if (item.children?.length) {
      expandedKeys.value.add(item.id)
      initExpandAll(item.children)
    }
  }
}

const queryParams = reactive<MenuQueryDTO>({
  pageNum: 1,
  pageSize: 1000,  // 获取所有菜单数据
  menuName: '',
  menuType: undefined,
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑菜单' : '新增菜单')
const formRef = ref<FormInstance>()

const menuTreeForSelect = computed(() => {
  return [
    { id: 0, menuName: '顶级菜单', children: [] },
    ...menuTree.value
  ]
})

const formData = reactive<MenuSaveDTO>({
  id: undefined,
  menuName: '',
  menuType: 1,
  parentId: null,
  path: '',
  component: '',
  perms: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: 1
})

const formRules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

// 将扁平列表构建为树形结构
function buildTree(items: MenuVO[], parentId: number | null): MenuVO[] {
  const tree: MenuVO[] = []
  for (const item of items) {
    if (item.parentId === parentId) {
      const children = buildTree(items, item.id)
      if (children.length > 0) {
        item.children = children
      }
      tree.push(item)
    }
  }
  tree.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  return tree
}

async function loadData() {
  loading.value = true
  try {
    const res = await menuApi.getList(queryParams)
    // 将扁平列表构建为树形结构
    menuTree.value = buildTree(res.list, 0)
    // 等待表格渲染完成后展开所有节点
    await nextTick()
    expandedKeys.value.clear()
    initExpandAll(menuTree.value)
    // 手动展开所有节点
    expandAllRows(menuTree.value)
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

// 手动展开所有行
function expandAllRows(items: MenuVO[]) {
  for (const item of items) {
    if (item.children?.length) {
      tableRef.value?.toggleRowExpansion(item, true)
      expandAllRows(item.children)
    }
  }
}

function handleSearch() {
  loadData()
}

function handleReset() {
  queryParams.menuName = ''
  queryParams.menuType = undefined
  queryParams.status = undefined
  loadData()
}

function handleAdd() {
  formData.id = undefined
  formData.menuName = ''
  formData.menuType = 1
  formData.parentId = null
  formData.path = ''
  formData.component = ''
  formData.perms = ''
  formData.icon = ''
  formData.sort = 0
  formData.visible = 1
  formData.status = 1
  dialogVisible.value = true
}

function handleAddChild(row: MenuVO) {
  formData.id = undefined
  formData.menuName = ''
  formData.menuType = row.menuType === 1 ? 2 : 3 as MenuType
  formData.parentId = row.id
  formData.path = ''
  formData.component = ''
  formData.perms = ''
  formData.icon = ''
  formData.sort = 0
  formData.visible = 1
  formData.status = 1
  dialogVisible.value = true
}

function handleEdit(row: MenuVO) {
  formData.id = row.id
  formData.menuName = row.menuName
  formData.menuType = row.menuType
  formData.parentId = row.parentId
  formData.path = row.path || ''
  formData.component = row.component || ''
  formData.perms = row.perms || ''
  formData.icon = row.icon || ''
  formData.sort = row.sort
  formData.visible = row.visible
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await menuApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await menuApi.create(formData)
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

async function handleDelete(row: MenuVO) {
  if (row.children?.length) {
    ElMessage.warning('存在子菜单，不能删除')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除菜单 ${row.menuName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await menuApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}

.table-card {
  border-radius: 12px;
}

.menu-table {
  // 完全隐藏Element Plus的展开图标和缩进占位
  :deep(.el-table__expand-icon) {
    display: none !important;
    width: 0 !important;
    padding: 0 !important;
  }

  :deep(.el-table__indent) {
    display: none !important;
    width: 0 !important;
    padding: 0 !important;
  }

  // 强制统一所有行高度
  :deep(.el-table__body tr) {
    height: 48px !important;
  }

  :deep(.el-table__body td) {
    height: 48px !important;
    padding: 0 10px !important;
  }

  :deep(.el-table__body .cell) {
    height: 48px !important;
    display: flex;
    align-items: center;
  }

  // 表头样式
  :deep(.el-table__header th) {
    height: 48px !important;
    padding: 0 10px !important;
    background-color: #e8e8e8 !important;
    font-weight: 600;
  }

  :deep(.el-table__header .cell) {
    height: 48px !important;
    display: flex;
    align-items: center;
  }

  // 根据层级给菜单名称单元格添加缩进
  :deep(.el-table__row) .menu-name-cell {
    padding-left: 0;
  }

  :deep(.el-table__row--level-1) {
    background-color: #fafafa;

    .menu-name-cell {
      padding-left: 20px;
    }
  }

  :deep(.el-table__row--level-2) {
    background-color: #f5f5f5;

    .menu-name-cell {
      padding-left: 40px;
    }
  }

  :deep(.el-table__row--level-3) {
    background-color: #f0f0f0;

    .menu-name-cell {
      padding-left: 60px;
    }
  }
}

.menu-name-cell {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;

  .expand-icon {
    width: 24px;
    min-width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    cursor: pointer;

    &:has(.el-icon) {
      border-radius: 4px;
      transition: background-color 0.2s;

      &:hover {
        background-color: #ecf5ff;
      }
    }

    .el-icon {
      font-size: 14px;
      color: #606266;
    }
  }

  .icon-wrapper {
    width: 20px;
    min-width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .menu-icon {
      color: #409eff;
      font-size: 16px;
    }
  }

  .menu-name-text {
    flex: 1;
    white-space: nowrap;
    min-width: 0;
  }
}
</style>