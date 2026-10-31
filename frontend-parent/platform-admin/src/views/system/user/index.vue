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
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userCode" placeholder="请输入用户名" clearable/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.userName" placeholder="请输入姓名" clearable/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable/>
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
          <el-button type="success" v-permission="'system:user:add'" @click="handleAdd">
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
        <el-table-column prop="userCode" label="用户名" min-width="120"/>
        <el-table-column prop="userName" label="姓名" min-width="120"/>
        <el-table-column prop="phone" label="手机号" min-width="130"/>
        <el-table-column prop="email" label="邮箱" min-width="180"/>
        <el-table-column prop="deptName" label="部门" min-width="120"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
                v-if="userStore.hasPermission('system:user:edit')"
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
        <el-table-column label="操作" width="250" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'system:user:edit'" @click="handleEdit(row)">
              <el-icon>
                <Edit/>
              </el-icon>
              编辑
            </el-button>
            <el-button type="warning" link v-permission="'system:user:edit'" @click="handleResetPwd(row)">
              <el-icon>
                <Key/>
              </el-icon>
              重置密码
            </el-button>
            <el-button type="danger" link v-permission="'system:user:delete'" @click="handleDelete(row)">
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
        width="600px"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
      >
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="formData.userName" placeholder="请输入姓名"/>
        </el-form-item>
        <el-form-item label="用户名" prop="userCode">
          <el-input v-model="formData.userCode" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!formData.id">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password/>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱"/>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
              v-model="formData.deptId"
              :data="deptTree"
              :props="{ label: 'deptName', value: 'id', children: 'children' }"
              placeholder="请选择部门"
              check-strictly
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" clearable>
            <el-option
                v-for="role in roleOptions"
                :key="role.id"
                :label="role.roleName"
                :value="role.id"
            />
          </el-select>
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

    <!-- 重置密码弹窗 -->
    <el-dialog
        v-model="resetPwdVisible"
        title="重置密码"
        width="400px"
    >
      <el-form :model="resetPwdForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="resetPwdForm.userCode" disabled/>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" show-password/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetPwdLoading" @click="handleResetPwdSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {userApi} from '@/api/user'
import {roleApi} from '@/api/role'
import {deptApi} from '@/api/dept'
import {tenantApi} from '@/api/tenant'
import {useUserStore} from '@/stores/user'
import type {UserQueryDTO, UserSaveDTO, UserVO} from '@/types/user'
import type {DeptVO} from '@/types/dept'
import type {RoleVO} from '@/types/role'
import type {OptionVO, PageVO} from '@/types/api'

const userStore = useUserStore()

const hasOperPermission = computed(() =>
    userStore.hasPermission('system:user:edit') || userStore.hasPermission('system:user:delete')
)

const loading = ref(false)
const submitLoading = ref(false)
const resetPwdLoading = ref(false)
const tableData = ref<UserVO[]>([])
const total = ref(0)
const deptTree = ref<DeptVO[]>([])
const roleOptions = ref<RoleVO[]>([])

const queryParams = reactive<UserQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  tenantId: undefined,
  userCode: '',
  userName: '',
  phone: '',
  status: undefined
})

const tenantOptions = ref<OptionVO[]>([])

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑用户' : '新增用户')
const formRef = ref<FormInstance>()

const formData = reactive<UserSaveDTO>({
  id: undefined,
  userCode: '',
  userName: '',
  password: '',
  phone: '',
  email: '',
  deptId: undefined,
  roleIds: [],
  status: 1
})

const formRules: FormRules = {
  userName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
  userCode: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}],
  deptId: [{required: true, message: '请选择部门', trigger: 'change'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
}

const resetPwdVisible = ref(false)
const resetPwdForm = reactive({
  id: 0,
  userCode: '',
  password: ''
})

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<UserVO> = await userApi.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

async function loadDeptTree() {
  try {
    const res = await deptApi.getTree()
    deptTree.value = res
  } catch {
    // 静默处理
  }
}

async function loadRoleOptions() {
  try {
    const res = await roleApi.getOptions()
    roleOptions.value = res
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
  queryParams.userCode = ''
  queryParams.userName = ''
  queryParams.phone = ''
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
  formData.userCode = ''
  formData.userName = ''
  formData.password = ''
  formData.phone = ''
  formData.email = ''
  formData.deptId = undefined
  formData.roleIds = []
  formData.status = 1
  dialogVisible.value = true
}

async function handleEdit(row: UserVO) {
  try {
    // 获取用户详情（包含角色）
    const user = await userApi.getById(row.id)
    formData.id = user.id
    formData.userCode = user.userCode
    formData.userName = user.userName
    formData.phone = user.phone || ''
    formData.email = user.email || ''
    formData.deptId = user.deptId
    formData.roleIds = user.roleIds || []
    formData.status = user.status
    dialogVisible.value = true
  } catch {
    // 静默处理
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await userApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await userApi.create(formData)
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

async function handleStatusChange(row: UserVO) {
  try {
    await userApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

function handleResetPwd(row: UserVO) {
  resetPwdForm.id = row.id
  resetPwdForm.userCode = row.userCode
  resetPwdForm.password = ''
  resetPwdVisible.value = true
}

async function handleResetPwdSubmit() {
  if (!resetPwdForm.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  resetPwdLoading.value = true
  try {
    await userApi.resetPassword(resetPwdForm.id, resetPwdForm.password)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch {
    // 静默处理
  } finally {
    resetPwdLoading.value = false
  }
}

async function handleDelete(row: UserVO) {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${row.userCode} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  loadData()
  loadDeptTree()
  loadRoleOptions()
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

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>