<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="租户" v-if="isPlatformRole">
          <el-select v-model="queryParams.tenantId" placeholder="请选择租户" clearable>
            <el-option label="全局" :value="0"/>
            <el-option v-for="item in tenantOptions" :key="item.id" :label="item.tenantName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="参数分组">
          <el-select v-model="queryParams.groupCode" placeholder="请选择分组" clearable>
            <el-option
                v-for="item in groupOptions"
                :key="item.itemValue"
                :label="item.itemLabel"
                :value="item.itemValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参数键">
          <el-input v-model="queryParams.configKey" placeholder="请输入参数键" clearable/>
        </el-form-item>
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable/>
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
          <el-button type="success" v-permission="'system:config:add'" @click="handleAdd">
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
      <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="80"/>
        <el-table-column prop="tenantId" label="租户" width="200" v-if="isPlatformRole">
          <template #default="{ row }">
            <el-tag v-if="row.tenantId === 0" type="info" size="small">全局</el-tag>
            <span v-else>{{ getTenantName(row.tenantId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="groupCode" label="分组" min-width="120">
          <template #default="{ row }">
            <span>{{ getGroupLabel(row.groupCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="configKey" label="参数键" min-width="150"/>
        <el-table-column prop="configName" label="参数名称" min-width="150"/>
        <el-table-column prop="configValue" label="参数值" min-width="100" show-overflow-tooltip/>
        <el-table-column prop="valueType" label="值类型" width="100">
          <template #default="{ row }">
            <el-tag :type="valueTypeTag[row.valueType as ValueType]">
              {{ valueTypeMap[row.valueType as ValueType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
                v-if="userStore.hasPermission('system:config:edit')"
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
        <el-table-column prop="createTime" label="创建时间" min-width="150"/>
        <el-table-column label="操作" width="180" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'system:config:edit'" @click="handleEdit(row)">
              <el-icon>
                <Edit/>
              </el-icon>
              编辑
            </el-button>
            <el-button type="danger" link v-permission="'system:config:delete'" @click="handleDelete(row)">
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="租户" prop="tenantId" v-if="isPlatformRole">
          <el-select v-model="formData.tenantId" placeholder="请选择租户">
            <el-option label="全局" :value="0"/>
            <el-option v-for="item in tenantOptions" :key="item.id" :label="item.tenantName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="参数分组" prop="groupCode">
          <el-select v-model="formData.groupCode" placeholder="请选择分组">
            <el-option
                v-for="item in groupOptions"
                :key="item.itemValue"
                :label="item.itemLabel"
                :value="item.itemValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参数键" prop="configKey">
          <el-input v-model="formData.configKey" placeholder="请输入参数键"/>
        </el-form-item>
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="formData.configName" placeholder="请输入参数名称"/>
        </el-form-item>
        <el-form-item label="值类型" prop="valueType">
          <el-radio-group v-model="formData.valueType">
            <el-radio :value="1">String</el-radio>
            <el-radio :value="2">Number</el-radio>
            <el-radio :value="3">JSON</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="参数值" prop="configValue">
          <el-input
              v-if="formData.valueType === 1"
              v-model="formData.configValue"
              placeholder="请输入参数值"
          />
          <el-input
              v-else-if="formData.valueType === 2"
              v-model="formData.configValue"
              placeholder="请输入数字，最多5位小数"
              @blur="handleNumberBlur"
          />
          <el-input
              v-else
              v-model="formData.configValue"
              type="textarea"
              :rows="4"
              placeholder="请输入JSON格式的参数值"
          />
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
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref, watch} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {configApi} from '@/api/config'
import {dictDataApi} from '@/api/dict-data'
import {tenantApi} from '@/api/tenant'
import {useUserStore} from '@/stores/user'
import type {ConfigGroupVO, ConfigQueryDTO, ConfigSaveDTO, ConfigVO, ValueType} from '@/types/config'
import type {TenantVO} from '@/types/tenant'
import type {PageVO} from '@/types/api'

const userStore = useUserStore()
const isPlatformRole = computed(() => userStore.isPlatformRole)

const hasOperPermission = computed(() =>
    userStore.hasPermission('system:config:edit') || userStore.hasPermission('system:config:delete')
)

const valueTypeMap: Record<ValueType, string> = {
  1: 'String',
  2: 'Number',
  3: 'JSON'
}

const valueTypeTag: Record<ValueType, string> = {
  1: '',
  2: 'success',
  3: 'warning'
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<ConfigVO[]>([])
const total = ref(0)
const groupOptions = ref<ConfigGroupVO[]>([])
const tenantOptions = ref<TenantVO[]>([])

const queryParams = reactive<ConfigQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  groupCode: '',
  configKey: '',
  configName: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑参数' : '新增参数')
const formRef = ref<FormInstance>()

const formData = reactive<ConfigSaveDTO>({
  id: undefined,
  tenantId: undefined,
  groupCode: '',
  configKey: '',
  configName: '',
  configValue: '',
  valueType: 1,
  status: 1,
  remark: ''
})

const formRules: FormRules = {
  groupCode: [{required: true, message: '请选择分组', trigger: 'change'}],
  configKey: [{required: true, message: '请输入参数键', trigger: 'blur'}],
  configName: [{required: true, message: '请输入参数名称', trigger: 'blur'}],
  configValue: [{required: true, message: '请输入参数值', trigger: 'blur'}],
  valueType: [{required: true, message: '请选择值类型', trigger: 'change'}]
}

// 值类型切换时清空参数值（仅新增时）
watch(() => formData.valueType, () => {
  if (!formData.id) {
    formData.configValue = ''
  }
})

function getGroupLabel(groupCode: string): string {
  const group = groupOptions.value.find(g => g.itemValue === groupCode)
  return group ? group.itemLabel : groupCode
}

function getTenantName(tenantId: number): string {
  const tenant = tenantOptions.value.find(t => t.id === tenantId)
  return tenant ? tenant.tenantName : String(tenantId)
}

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<ConfigVO> = await configApi.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

async function loadGroupOptions() {
  try {
    const res = await dictDataApi.getByCode('CONFIG_GROUP')
    groupOptions.value = res
  } catch {
    // 静默处理
  }
}

async function loadTenantOptions() {
  if (!isPlatformRole.value) return
  try {
    tenantOptions.value = await tenantApi.getOptions()
  } catch {
    // 静默处理
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.tenantId = undefined
  queryParams.groupCode = ''
  queryParams.configKey = ''
  queryParams.configName = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function handleNumberBlur() {
  if (!formData.configValue) return
  const num = parseFloat(formData.configValue)
  if (isNaN(num)) return

  // 验证最多5位小数
  const str = formData.configValue
  if (str.includes('.')) {
    const decimalPart = str.split('.')[1]
    if (decimalPart && decimalPart.length > 5) {
      ElMessage.warning('小数位最多5位')
      formData.configValue = num.toFixed(5)
      return
    }
  }

  // 整数不显示小数点，小数最多5位但去除末尾的0
  if (num % 1 === 0) {
    formData.configValue = String(num)
  } else {
    // 小数去除末尾多余的0，最多保留5位
    const fixed = num.toFixed(5)
    formData.configValue = parseFloat(fixed).toString()
  }
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
  formData.tenantId = 0  // 默认全局
  formData.groupCode = queryParams.groupCode || ''
  formData.configKey = ''
  formData.configName = ''
  formData.configValue = ''
  formData.valueType = 1
  formData.status = 1
  formData.remark = ''
  dialogVisible.value = true
}

function handleEdit(row: ConfigVO) {
  formData.id = row.id
  formData.tenantId = row.tenantId
  formData.groupCode = row.groupCode
  formData.configKey = row.configKey
  formData.configName = row.configName
  formData.configValue = row.configValue
  formData.valueType = row.valueType
  formData.status = row.status
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  // JSON 类型验证
  if (formData.valueType === 3) {
    try {
      JSON.parse(formData.configValue)
    } catch {
      ElMessage.error('JSON格式不正确')
      return
    }
  }

  // Number 类型验证
  if (formData.valueType === 2) {
    const num = parseFloat(formData.configValue)
    if (isNaN(num)) {
      ElMessage.error('请输入有效的数字')
      return
    }
    // 验证最多5位小数
    if (formData.configValue.includes('.')) {
      const decimalPart = formData.configValue.split('.')[1]
      if (decimalPart && decimalPart.length > 5) {
        ElMessage.error('小数位最多5位')
        return
      }
    }
  }

  submitLoading.value = true
  try {
    if (formData.id) {
      await configApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await configApi.create(formData)
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

async function handleStatusChange(row: ConfigVO) {
  try {
    await configApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function handleDelete(row: ConfigVO) {
  try {
    await ElMessageBox.confirm(`确定要删除参数 ${row.configName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await configApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  loadData()
  loadGroupOptions()
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
</style>