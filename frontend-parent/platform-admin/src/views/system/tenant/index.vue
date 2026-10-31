<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="租户名称">
          <el-input v-model="queryParams.tenantName" placeholder="请输入租户名称" clearable />
        </el-form-item>
        <el-form-item label="租户编码">
          <el-input v-model="queryParams.tenantCode" placeholder="请输入租户编码" clearable />
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
          <el-button type="success" v-permission="'system:tenant:add'" @click="handleAdd">
            <el-icon><Plus /></el-icon>
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
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="tenantName" label="租户名称" min-width="150" />
        <el-table-column prop="tenantCode" label="租户编码" min-width="150" />
        <el-table-column prop="expireTime" label="到期时间" min-width="180" />
        <el-table-column prop="contactName" label="联系人" min-width="120" />
        <el-table-column prop="contactPhone" label="联系电话" min-width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-if="userStore.hasPermission('system:tenant:edit')"
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
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="150" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'system:tenant:edit'" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link v-permission="'system:tenant:delete'" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
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
        <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="formData.tenantName" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item label="租户编码" prop="tenantCode">
          <el-input v-model="formData.tenantCode" placeholder="请输入租户编码" />
        </el-form-item>
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker
            v-model="formData.expireTime"
            type="datetime"
            placeholder="请选择到期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="formData.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { tenantApi } from '@/api/tenant'
import { useUserStore } from '@/stores/user'
import type { TenantVO, TenantSaveDTO, TenantQueryDTO } from '@/types/tenant'
import type { PageVO } from '@/types/api'

const userStore = useUserStore()

const hasOperPermission = computed(() =>
  userStore.hasPermission('system:tenant:edit') || userStore.hasPermission('system:tenant:delete')
)

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<TenantVO[]>([])
const total = ref(0)

const queryParams = reactive<TenantQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  tenantName: '',
  tenantCode: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑租户' : '新增租户')
const formRef = ref<FormInstance>()

const formData = reactive<TenantSaveDTO>({
  id: undefined,
  tenantName: '',
  tenantCode: '',
  expireTime: '',
  contactName: '',
  contactPhone: '',
  status: 1
})

const formRules: FormRules = {
  tenantName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
  tenantCode: [{ required: true, message: '请输入租户编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<TenantVO> = await tenantApi.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.tenantName = ''
  queryParams.tenantCode = ''
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
  formData.tenantName = ''
  formData.tenantCode = ''
  formData.expireTime = ''
  formData.contactName = ''
  formData.contactPhone = ''
  formData.status = 1
  dialogVisible.value = true
}

function handleEdit(row: TenantVO) {
  formData.id = row.id
  formData.tenantName = row.tenantName
  formData.tenantCode = row.tenantCode
  formData.expireTime = row.expireTime || ''
  formData.contactName = row.contactName || ''
  formData.contactPhone = row.contactPhone || ''
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await tenantApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await tenantApi.create(formData)
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

async function handleStatusChange(row: TenantVO) {
  try {
    await tenantApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function handleDelete(row: TenantVO) {
  try {
    await ElMessageBox.confirm(`确定要删除租户 ${row.tenantName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await tenantApi.delete(row.id)
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
</style>