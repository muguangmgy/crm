<template>
  <div class="page-container" v-loading="!dictLoaded">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable/>
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="queryParams.customerType" placeholder="请选择类型" clearable>
            <el-option v-for="item in customerTypeDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="Number(item.itemValue)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="queryParams.level" placeholder="请选择等级" clearable>
            <el-option v-for="item in customerLevelDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="Number(item.itemValue)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industry" placeholder="请选择行业" clearable filterable>
            <el-option v-for="item in industryDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="item.itemValue"/>
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
          <el-button type="success" v-permission="'crm:customer:add'" @click="handleAdd">
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
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="customerNo" label="客户编号" width="150"/>
        <el-table-column prop="customerName" label="客户名称" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="handleDetail(row)">{{ row.customerName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="customerType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.customerType === 1 ? 'primary' : 'success'">
              {{ getDictLabelFromData(customerTypeDict, row.customerType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130"/>
        <el-table-column prop="industry" label="行业" width="100">
          <template #default="{ row }">
            <span v-if="row.customerType === 1 && row.industry">{{
                getDictLabelFromData(industryDict, row.industry) || row.industry
              }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="120"/>
        <el-table-column prop="level" label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)">{{ getDictLabelFromData(customerLevelDict, row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="240" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:customer:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link v-permission="'crm:customer:pool:release'" @click="handleRelease(row)">释放
            </el-button>
            <el-button type="info" link v-permission="'crm:customer:pool:transfer'" @click="handleTransfer(row)">转移
            </el-button>
            <el-button type="danger" link v-permission="'crm:customer:delete'" @click="handleDelete(row)">删除
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
          @size-change="loadData"
          @current-change="loadData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-select v-model="formData.customerType" placeholder="请选择类型">
                <el-option v-for="item in customerTypeDict" :key="item.itemValue" :label="item.itemLabel"
                           :value="Number(item.itemValue)"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="formData.customerName" placeholder="请输入客户名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="formData.customerType === 1">
          <el-col :span="12">
            <el-form-item label="行业">
              <el-select v-model="formData.industry" placeholder="请选择行业" filterable>
                <el-option v-for="item in industryDict" :key="item.itemValue" :label="item.itemLabel"
                           :value="item.itemValue"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业规模">
              <el-select v-model="formData.companySize" placeholder="请选择规模">
                <el-option v-for="item in companySizeDict" :key="item.itemValue" :label="item.itemLabel"
                           :value="item.itemValue"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="省份">
              <el-input v-model="formData.province" placeholder="请输入省份"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市">
              <el-input v-model="formData.city" placeholder="请输入城市"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址">
          <el-input v-model="formData.address" placeholder="请输入详细地址"/>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="formData.phone" placeholder="请输入电话"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="formData.email" placeholder="请输入邮箱"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户来源">
              <el-select v-model="formData.source" placeholder="请选择来源">
                <el-option v-for="item in customerSourceDict" :key="item.itemValue" :label="item.itemLabel"
                           :value="item.itemValue"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户等级">
              <el-select v-model="formData.level" placeholder="请选择等级">
                <el-option v-for="item in customerLevelDict" :key="item.itemValue" :label="item.itemLabel"
                           :value="Number(item.itemValue)"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签">
          <el-select v-model="formData.tagIds" multiple placeholder="请选择标签" style="width: 100%">
            <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.tagName" :value="tag.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 转移对话框 -->
    <el-dialog v-model="transferVisible" title="转移客户" width="400px">
      <el-form>
        <el-form-item label="新负责人">
          <el-select v-model="transferForm.newOwnerId" placeholder="请选择新负责人" style="width: 100%">
            <el-option v-for="user in userOptions" :key="user.id" :label="user.userName" :value="user.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTransferSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import type {FormInstance} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useUserStore} from '@/stores/user'
import {customerApi} from '@/api/crm/customer'
import {getDictLabelFromData, useDicts} from '@/composables/useDict'
import type {CustomerQueryDTO, CustomerSaveDTO, CustomerVO, TagVO} from '@/types/crm/customer'

const router = useRouter()
const userStore = useUserStore()

// 需要的字典列表
const requiredDicts = ['crm_customer_type', 'crm_customer_level', 'crm_company_size', 'crm_customer_source', 'crm_industry']

// 预加载字典
const {dataMap, loaded: dictLoaded} = useDicts(requiredDicts)

// 字典数据
const customerTypeDict = computed(() => dataMap.value['crm_customer_type'] || [])
const customerLevelDict = computed(() => dataMap.value['crm_customer_level'] || [])
const companySizeDict = computed(() => dataMap.value['crm_company_size'] || [])
const customerSourceDict = computed(() => dataMap.value['crm_customer_source'] || [])
const industryDict = computed(() => dataMap.value['crm_industry'] || [])

// 查询参数
const queryParams = reactive<CustomerQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  customerType: undefined,
  level: undefined,
  industry: ''
})

// 数据
const loading = ref(false)
const tableData = ref<CustomerVO[]>([])
const total = ref(0)
const tagOptions = ref<TagVO[]>([])
const userOptions = ref<{ id: number; userName: string }[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<CustomerSaveDTO>({
  customerType: 1,
  customerName: '',
  level: 1,
  status: 1,
  tagIds: []
})

// 转移对话框
const transferVisible = ref(false)
const transferForm = reactive({customerId: 0, newOwnerId: undefined as number | undefined})

// 权限
const hasOperPermission = computed(() =>
    userStore.hasPermission('crm:customer:edit') || userStore.hasPermission('crm:customer:delete')
)

// 表单验证规则
const formRules = {
  customerType: [{required: true, message: '请选择客户类型', trigger: 'change'}],
  customerName: [{required: true, message: '请输入客户名称', trigger: 'blur'}]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await customerApi.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 加载标签选项
const loadTags = async () => {
  try {
    tagOptions.value = await customerApi.getAllTags()
  } catch (e) {
    console.error(e)
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryParams.customerName = ''
  queryParams.customerType = undefined
  queryParams.level = undefined
  queryParams.industry = ''
  queryParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增客户'
  formData.id = undefined
  formData.customerType = 1
  formData.customerName = ''
  formData.industry = undefined
  formData.companySize = undefined
  formData.source = undefined
  formData.level = 1
  formData.status = 1
  formData.tagIds = []
  formData.remark = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: CustomerVO) => {
  dialogTitle.value = '编辑客户'
  // 通过详情API获取完整数据（包含tagIds）
  try {
    const detail = await customerApi.getById(row.id)
    formData.id = detail.customer.id
    formData.customerType = detail.customer.customerType
    formData.customerName = detail.customer.customerName
    formData.industry = detail.customer.industry
    formData.companySize = detail.customer.companySize
    formData.province = detail.customer.province
    formData.city = detail.customer.city
    formData.phone = detail.customer.phone
    formData.email = detail.customer.email
    formData.source = detail.customer.source
    formData.level = detail.customer.level
    formData.status = detail.customer.status
    formData.remark = detail.customer.remark
    formData.tagIds = detail.customer.tagIds || []
    dialogVisible.value = true
  } catch (e) {
    console.error(e)
    ElMessage.error('获取客户详情失败')
  }
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await customerApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await customerApi.create(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

// 详情
const handleDetail = (row: CustomerVO) => {
  router.push(`/crm/customer/detail/${row.id}`)
}

// 释放到公海池
const handleRelease = async (row: CustomerVO) => {
  await ElMessageBox.confirm('确定将此客户释放到公海池?', '提示')
  await customerApi.releaseToPool(row.id)
  ElMessage.success('释放成功')
  loadData()
}

// 转移
const handleTransfer = (row: CustomerVO) => {
  transferForm.customerId = row.id
  transferForm.newOwnerId = undefined
  transferVisible.value = true
}

// 转移提交
const handleTransferSubmit = async () => {
  if (!transferForm.newOwnerId) {
    ElMessage.warning('请选择新负责人')
    return
  }
  await customerApi.transfer({customerId: transferForm.customerId, newOwnerId: transferForm.newOwnerId})
  ElMessage.success('转移成功')
  transferVisible.value = false
  loadData()
}

// 删除
const handleDelete = async (row: CustomerVO) => {
  await ElMessageBox.confirm('确定删除此客户?', '提示')
  await customerApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// 等级标签样式
const getLevelTagType = (level: number) => {
  const types: Record<number, string> = {1: '', 2: 'warning', 3: 'danger'}
  return types[level] || ''
}

onMounted(() => {
  loadData()
  loadTags()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>