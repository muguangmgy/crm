<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <!-- 租户选择器（仅平台管理员可见） -->
        <el-form-item label="租户" v-if="userStore.isPlatformRole">
          <el-select v-model="queryParams.tenantId" placeholder="请选择租户" clearable @change="handleTenantChange">
            <el-option
                v-for="tenant in tenantOptions"
                :key="tenant.id"
                :label="tenant.name"
                :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable/>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryParams.roleCode" placeholder="请输入角色编码" clearable/>
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
          <el-button type="success" v-permission="'system:role:add'" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
          v-loading="loading"
          :data="tableData"
          border
          stripe
          style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="80"/>
        <el-table-column prop="roleName" label="角色名称" min-width="150"/>
        <el-table-column prop="roleCode" label="角色编码" min-width="150"/>
        <el-table-column prop="dataScope" label="数据范围" min-width="150">
          <template #default="{ row }">
            <span>{{ dataScopeMap[row.dataScope] || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
                v-if="userStore.hasPermission('system:role:edit')"
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
        <el-table-column label="操作" width="220" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'system:role:edit'" @click="handleEdit(row)">
              <el-icon>
                <Edit/>
              </el-icon>
              编辑
            </el-button>
            <el-button type="warning" link v-permission="'system:role:permission'" @click="handlePermission(row)">
              <el-icon>
                <Setting/>
              </el-icon>
              权限
            </el-button>
            <el-button type="danger" link v-permission="'system:role:delete'" @click="handleDelete(row)">
              <el-icon>
                <Delete/>
              </el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
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
        <!-- 租户选择（仅平台用户可见） -->
        <el-form-item label="租户" prop="tenantId" v-if="userStore.isPlatformRole">
          <el-select v-model="formData.tenantId" placeholder="请选择租户" clearable>
            <el-option
                v-for="tenant in tenantOptions"
                :key="tenant.id"
                :label="tenant.tenantName"
                :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称"/>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码">
            <template #prepend>
              <span class="role-code-prefix">{{ roleCodePrefix }}</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="formData.dataScope" placeholder="请选择数据范围">
            <el-option
                v-for="opt in dataScopeOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注"/>
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

    <!-- 权限分配弹窗 -->
    <el-dialog
        v-model="permissionVisible"
        title="分配权限"
        width="600px"
    >
      <el-tree
          ref="menuTreeRef"
          :data="menuTree"
          :props="{ label: 'menuName', children: 'children' }"
          show-checkbox
          node-key="id"
          default-expand-all
      />
      <template #footer>
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, nextTick, onMounted, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {roleApi} from '@/api/role'
import {menuApi} from '@/api/menu'
import {tenantApi} from '@/api/tenant'
import {useUserStore} from '@/stores/user'
import type {RoleQueryDTO, RoleSaveDTO, RoleVO} from '@/types/role'
import type {MenuVO} from '@/types/menu'
import type {TenantVO} from '@/types/tenant'
import type {PageVO} from '@/types/api'

const userStore = useUserStore()

const hasOperPermission = computed(() =>
    userStore.hasPermission('system:role:edit') ||
    userStore.hasPermission('system:role:permission') ||
    userStore.hasPermission('system:role:delete')
)

const dataScopeMap: Record<number, string> = {
  1: '全部数据',
  2: '本租户数据',
  3: '部门及下级',
  4: '本部门数据',
  5: '仅本人数据'
}

// 数据范围选项（根据用户角色过滤）
const dataScopeOptions = computed(() => {
  const allOptions = [
    {value: 1, label: '全部数据'},
    {value: 2, label: '本租户数据'},
    {value: 3, label: '部门及下级'},
    {value: 4, label: '本部门数据'},
    {value: 5, label: '仅本人数据'}
  ]
  // 非平台用户排除全部数据(1)，所有用户排除自定义(6)
  return allOptions.filter(opt => {
    if (!userStore.isPlatformRole && opt.value === 1) {
      return false
    }
    return true
  })
})

const loading = ref(false)
const submitLoading = ref(false)
const permissionLoading = ref(false)
const tableData = ref<RoleVO[]>([])
const total = ref(0)
const menuTree = ref<MenuVO[]>([])
const menuTreeRef = ref()

const queryParams = reactive<RoleQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  tenantId: undefined,
  roleName: '',
  roleCode: '',
  status: undefined
})

const tenantOptions = ref<TenantVO[]>([])

// 角色编码前缀：根据选中租户或当前用户租户编码动态计算
const roleCodePrefix = computed(() => {
  if (userStore.isPlatformRole) {
    // 平台用户：根据选中的租户获取租户编码
    if (formData.tenantId) {
      const tenant = tenantOptions.value.find(t => t.id === formData.tenantId)
      return tenant ? tenant.tenantCode + '_' : ''
    }
    return ''
  } else {
    // 租户用户：使用当前用户租户编码
    return userStore.userInfo?.tenantCode ? userStore.userInfo.tenantCode + '_' : ''
  }
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑角色' : '新增角色')
const formRef = ref<FormInstance>()

const formData = reactive<RoleSaveDTO>({
  id: undefined,
  tenantId: undefined,
  roleName: '',
  roleCode: '',
  dataScope: 1,
  remark: '',
  status: 1
})

const formRules = computed<FormRules>(() => ({
  tenantId: userStore.isPlatformRole
      ? [{required: true, message: '请选择租户', trigger: 'change'}]
      : [],
  roleName: [{required: true, message: '请输入角色名称', trigger: 'blur'}],
  roleCode: [{required: true, message: '请输入角色编码', trigger: 'blur'}],
  dataScope: [{required: true, message: '请选择数据范围', trigger: 'change'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
}))

const permissionVisible = ref(false)
const currentRoleId = ref(0)

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<RoleVO> = await roleApi.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

async function loadMenuTree() {
  try {
    const res = await menuApi.getTree()
    menuTree.value = res
  } catch {
    // 静默处理
  }
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

function handleTenantChange() {
  queryParams.pageNum = 1
  loadData()
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.roleName = ''
  queryParams.roleCode = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  loadData()
}

function handleCurrentChange(page: number) {
  queryParams.pageNum = page
  loadData()
}

function handleAdd() {
  formData.id = undefined
  formData.tenantId = userStore.isPlatformRole ? undefined : userStore.currentTenantId
  formData.roleName = ''
  formData.roleCode = ''  // 用户只输入后缀
  formData.dataScope = userStore.isPlatformRole ? 1 : 5
  formData.remark = ''
  formData.status = 1
  dialogVisible.value = true
}

function handleEdit(row: RoleVO) {
  formData.id = row.id
  formData.tenantId = row.tenantId
  formData.roleName = row.roleName
  // 提取后缀：去除"租户编码_"前缀部分
  const underscoreIndex = row.roleCode?.indexOf('_')
  formData.roleCode = underscoreIndex !== -1 ? row.roleCode.slice(underscoreIndex + 1) : row.roleCode
  formData.dataScope = row.dataScope || 1
  formData.remark = row.remark || ''
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await roleApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await roleApi.create(formData)
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

async function handleStatusChange(row: RoleVO) {
  try {
    await roleApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function handlePermission(row: RoleVO) {
  currentRoleId.value = row.id
  // 先清空之前的选择
  menuTreeRef.value?.setCheckedKeys([])
  // 打开弹窗
  permissionVisible.value = true
  // 等待 DOM 渲染完成
  await nextTick()
  // 获取并设置已授权的菜单
  try {
    const menuIds = await roleApi.getMenuIds(row.id)
    // 只设置叶子节点，让父节点自动计算半选/全选状态
    const leafIds = filterLeafIds(menuTree.value, menuIds)
    menuTreeRef.value?.setCheckedKeys(leafIds)
  } catch {
    menuTreeRef.value?.setCheckedKeys([])
  }
}

// 过滤出叶子节点ID（没有子节点的节点）
function filterLeafIds(tree: MenuVO[], checkedIds: number[]): number[] {
  const result: number[] = []

  function traverse(nodes: MenuVO[]) {
    for (const node of nodes) {
      if (checkedIds.includes(node.id)) {
        // 如果没有子节点或者是按钮类型，则是叶子节点
        if (!node.children?.length || node.menuType === 3) {
          result.push(node.id)
        } else {
          // 有子节点，继续遍历子节点
          traverse(node.children)
        }
      } else if (node.children?.length) {
        traverse(node.children)
      }
    }
  }

  traverse(tree)
  return result
}

async function handlePermissionSubmit() {
  // 获取完全选中的节点 + 半选节点（父节点部分选中状态）
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = [...checkedKeys, ...halfCheckedKeys] as number[]
  permissionLoading.value = true
  try {
    await roleApi.assignMenus(currentRoleId.value, menuIds)
    ElMessage.success('权限分配成功')
    permissionVisible.value = false
  } catch {
    // 静默处理
  } finally {
    permissionLoading.value = false
  }
}

async function handleDelete(row: RoleVO) {
  try {
    await ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await roleApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  loadData()
  loadMenuTree()
  loadTenantOptions()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}

.table-card {
  border-radius: 12px;
}

.role-code-prefix {
  font-weight: 600;
  color: #409eff;
}
</style>