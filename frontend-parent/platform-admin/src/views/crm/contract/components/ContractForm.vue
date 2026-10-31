<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑合同' : '新增合同'"
    width="700px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select
              v-model="formData.customerId"
              placeholder="请选择客户"
              filterable
              @change="handleCustomerChange"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.customerName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="关联商机">
            <el-select
              v-model="formData.opportunityId"
              placeholder="请选择商机"
              filterable
              clearable
              @change="handleOpportunityChange"
            >
              <el-option
                v-for="item in opportunityList"
                :key="item.id"
                :label="item.status === 2 ? `${item.oppName}（已成交）` : item.oppName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联报价单">
            <el-select
              v-model="formData.quoteId"
              placeholder="请选择报价单"
              filterable
              clearable
              @change="handleQuoteChange"
            >
              <el-option
                v-for="item in quoteList"
                :key="item.id"
                :label="item.quoteName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同名称" prop="contractName">
            <el-input v-model="formData.contractName" placeholder="请输入合同名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="合同金额" prop="contractAmount">
            <el-input-number
              v-model="formData.contractAmount"
              :min="0"
              :precision="2"
              placeholder="请输入合同金额"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="负责人" prop="ownerId">
            <el-input v-model="formData.ownerName" disabled placeholder="自动带出" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker
              v-model="formData.startDate"
              type="date"
              placeholder="请选择开始日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker
              v-model="formData.endDate"
              type="date"
              placeholder="请选择结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="签订日期">
            <el-date-picker
              v-model="formData.signDate"
              type="date"
              placeholder="请选择签订日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { contractApi } from '@/api/crm/contract'
import { opportunityApi } from '@/api/crm/opportunity'
import { quoteApi } from '@/api/crm/quote'
import type { ContractSaveDTO } from '@/types/crm/contract'
import type { CustomerVO } from '@/types/crm/customer'
import type { OpportunityVO } from '@/types/crm/opportunity'
import type { QuoteVO } from '@/types/crm/quote'

const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const customerList = ref<CustomerVO[]>([])
const opportunityList = ref<OpportunityVO[]>([])
const quoteList = ref<QuoteVO[]>([])

// 表单数据
const formData = reactive<ContractSaveDTO & { ownerName?: string }>({
  customerId: undefined,
  opportunityId: undefined,
  quoteId: undefined,
  contractName: '',
  contractAmount: 0,
  startDate: '',
  endDate: '',
  signDate: '',
  remark: '',
  ownerId: undefined,
  ownerName: ''
})

// 表单验证规则
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  contractAmount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  ownerId: [{ required: true, message: '负责人不能为空', trigger: 'change' }]
}

// 确保商机出现在下拉选项中（报价中标后商机可能已成交，或列表未命中时按 ID 补齐）
const ensureOpportunityOption = async (opportunityId: number) => {
  if (!opportunityId) return
  if (opportunityList.value.some(item => item.id === opportunityId)) return
  try {
    const opp = await opportunityApi.getById(opportunityId)
    if (opp && opp.status !== 3) {
      opportunityList.value = [...opportunityList.value, opp]
    }
  } catch (error) {
    console.error('加载关联商机失败', error)
  }
}

// 加载商机列表（根据客户ID）：含进行中、已成交（报价中标后商机会变为已成交）
const loadOpportunityList = async (customerId: number) => {
  if (!customerId) {
    opportunityList.value = []
    return
  }
  try {
    const result = await opportunityApi.getList({ pageNum: 1, pageSize: 100, customerId })
    // 排除已失败商机
    opportunityList.value = (result.list || []).filter(item => item.status !== 3)
  } catch (error) {
    console.error('加载商机列表失败', error)
    opportunityList.value = []
  }
}

// 加载报价单列表（根据客户ID）
const loadQuoteList = async (customerId: number) => {
  if (!customerId) {
    quoteList.value = []
    return
  }
  try {
    const result = await quoteApi.getList({ pageNum: 1, pageSize: 100, customerId, status: 3 })
    quoteList.value = result.list || []
  } catch (error) {
    console.error('加载报价单列表失败', error)
    quoteList.value = []
  }
}

// 客户选择变化
const handleCustomerChange = async (customerId: number) => {
  const customer = customerList.value.find(item => item.id === customerId)
  if (customer) {
    formData.ownerId = customer.ownerId
    formData.ownerName = customer.ownerName || ''
    // 清空之前的选择
    formData.opportunityId = undefined
    formData.quoteId = undefined
    // 加载该客户的商机和报价单
    await Promise.all([loadOpportunityList(customerId), loadQuoteList(customerId)])
    // 自动生成合同名称
    if (!formData.contractName && customer.customerName) {
      formData.contractName = `${customer.customerName}-合同`
    }
  }
}

// 商机选择变化
const handleOpportunityChange = (opportunityId: number) => {
  if (!opportunityId) return
  const opportunity = opportunityList.value.find(item => item.id === opportunityId)
  if (opportunity) {
    formData.ownerId = opportunity.ownerId
    formData.ownerName = opportunity.ownerName || ''
    // 如果没有合同名称，根据商机名称生成
    if (!formData.contractName) {
      formData.contractName = `${opportunity.oppName}-合同`
    }
  }
}

// 报价单选择变化
const handleQuoteChange = async (quoteId: number) => {
  if (!quoteId) return
  const quote = quoteList.value.find(item => item.id === quoteId)
  if (quote) {
    // 自动带入关联商机（列表中没有时按 ID 补齐，避免下拉显示「无数据」）
    if (quote.opportunityId) {
      await ensureOpportunityOption(quote.opportunityId)
      formData.opportunityId = quote.opportunityId
      handleOpportunityChange(quote.opportunityId)
    }
    // 自动带入报价金额
    formData.contractAmount = quote.finalAmount || quote.totalAmount
    // 如果没有合同名称，根据报价单名称生成
    if (!formData.contractName) {
      formData.contractName = `${quote.quoteName}-合同`
    }
  }
}

// 打开弹窗
const open = async (id?: number, customers?: CustomerVO[]) => {
  // 设置客户列表
  if (customers) {
    customerList.value = customers
  }

  // 重置表单
  formData.id = undefined
  formData.customerId = undefined
  formData.opportunityId = undefined
  formData.quoteId = undefined
  formData.contractName = ''
  formData.contractAmount = 0
  formData.startDate = ''
  formData.endDate = ''
  formData.signDate = ''
  formData.remark = ''
  formData.ownerId = undefined
  formData.ownerName = ''
  opportunityList.value = []
  quoteList.value = []

  isEdit.value = !!id

  // 如果是编辑，加载详情
  if (id) {
    try {
      const detail = await contractApi.getById(id)
      formData.id = detail.id
      formData.customerId = detail.customerId
      formData.opportunityId = detail.opportunityId
      formData.quoteId = detail.quoteId
      formData.contractName = detail.contractName || ''
      formData.contractAmount = detail.contractAmount
      formData.startDate = detail.startDate || ''
      formData.endDate = detail.endDate || ''
      formData.signDate = detail.signDate || ''
      formData.remark = detail.remark || ''
      formData.ownerId = detail.ownerId
      formData.ownerName = detail.ownerName || ''

      // 加载该客户的商机和报价单
      loadOpportunityList(detail.customerId)
      loadQuoteList(detail.customerId)
    } catch (error) {
      ElMessage.error('加载合同详情失败')
      return
    }
  }

  visible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    if (isEdit.value) {
      await contractApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await contractApi.create(formData)
      ElMessage.success('新增成功')
    }

    emit('success')
    visible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>

<style scoped lang="scss">
</style>