<template>
  <el-dialog
    v-model="visible"
    title="线索转商机"
    width="500px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="线索名称">
        <el-input :value="leadInfo.leadName" disabled />
      </el-form-item>

      <el-form-item label="商机名称" prop="oppName">
        <el-input v-model="formData.oppName" placeholder="请输入商机名称" maxlength="100" />
      </el-form-item>

      <el-form-item label="关联客户">
        <el-select
          v-model="formData.customerId"
          placeholder="请选择客户（可空，不填则自动创建）"
          clearable
          filterable
        >
          <el-option
            v-for="item in customerList"
            :key="item.id"
            :label="item.customerName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="预期金额">
        <el-input-number
          v-model="formData.expectedAmount"
          :min="0"
          :precision="2"
          placeholder="请输入预期金额"
        />
      </el-form-item>

      <el-form-item label="预期成交日期">
        <el-date-picker
          v-model="formData.expectedDate"
          type="date"
          placeholder="请选择预期成交日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-alert
        type="info"
        :closable="false"
        show-icon
      >
        <template #title>
          转换后线索状态将变为"已转商机"，并生成一条新的商机记录
        </template>
      </el-alert>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确认转换</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { leadApi } from '@/api/crm/lead'
import { customerApi } from '@/api/crm/customer'
import type { LeadConvertDTO, LeadVO } from '@/types/crm/lead'
import type { CustomerVO } from '@/types/crm/customer'

const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const formRef = ref()

const leadInfo = ref<LeadVO>({
  id: 0,
  leadName: '',
  status: 1,
  ownerId: 0,
  ownerName: '',
  createTime: '',
  updateTime: ''
})

const customerList = ref<CustomerVO[]>([])

// 表单数据
const formData = reactive<LeadConvertDTO>({
  leadId: 0,
  customerId: undefined,
  oppName: '',
  expectedAmount: undefined,
  expectedDate: undefined
})

// 表单验证规则
const rules = {
  oppName: [{ required: true, message: '商机名称不能为空', trigger: 'blur' }]
}

// 加载客户列表
const loadCustomerList = async () => {
  try {
    const result = await customerApi.getList({ pageNum: 1, pageSize: 100, status: 1 })
    customerList.value = result.list
  } catch (error) {
    console.error('加载客户列表失败', error)
  }
}

// 打开弹窗
const open = async (lead: LeadVO) => {
  // 加载客户列表
  await loadCustomerList()

  // 设置线索信息
  leadInfo.value = lead

  // 重置表单
  formData.leadId = lead.id
  formData.customerId = lead.customerId
  formData.oppName = `${lead.leadName}-商机`
  formData.expectedAmount = undefined
  formData.expectedDate = undefined

  visible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    const oppId = await leadApi.convert(formData)
    ElMessage.success(`转换成功，商机ID: ${oppId}`)

    emit('success')
    visible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('转换失败')
    }
  } finally {
    submitting.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>