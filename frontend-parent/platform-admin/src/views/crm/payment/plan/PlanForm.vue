<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑收款计划' : '新增收款计划'"
    width="500px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="关联合同" prop="contractId">
        <el-select
          v-model="formData.contractId"
          placeholder="请选择合同"
          filterable
          @change="handleContractChange"
        >
          <el-option
            v-for="item in contractList"
            :key="item.id"
            :label="item.contractNo + ' - ' + item.contractName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="计划金额" prop="planAmount">
        <el-input-number
          v-model="formData.planAmount"
          :min="0"
          :precision="2"
          placeholder="请输入计划金额"
        />
        <span class="ml-2 text-gray-500">元</span>
      </el-form-item>

      <el-form-item label="计划日期" prop="planDate">
        <el-date-picker
          v-model="formData.planDate"
          type="date"
          placeholder="请选择计划收款日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

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
import { paymentPlanApi } from '@/api/crm/payment'
import type { PaymentPlanSaveDTO } from '@/types/crm/payment'
import type { ContractVO } from '@/types/crm/contract'

const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const contractList = ref<ContractVO[]>([])

// 表单数据
const formData = reactive<PaymentPlanSaveDTO>({
  contractId: undefined,
  planAmount: 0,
  planDate: '',
  remark: ''
})

// 表单验证规则
const rules = {
  contractId: [{ required: true, message: '请选择合同', trigger: 'change' }],
  planAmount: [{ required: true, message: '计划金额不能为空', trigger: 'blur' }],
  planDate: [{ required: true, message: '计划日期不能为空', trigger: 'change' }]
}

// 合同选择变化
const handleContractChange = (contractId: number) => {
  // 可以根据合同自动填充一些信息
}

// 打开弹窗
const open = async (id?: number, contracts?: ContractVO[]) => {
  // 设置合同列表
  if (contracts) {
    contractList.value = contracts
  }

  // 重置表单
  formData.id = undefined
  formData.contractId = undefined
  formData.planAmount = 0
  formData.planDate = ''
  formData.remark = ''

  isEdit.value = !!id

  // 如果是编辑，加载详情
  if (id) {
    try {
      const detail = await paymentPlanApi.getById(id)
      formData.id = detail.id
      formData.contractId = detail.contractId
      formData.planAmount = detail.planAmount
      formData.planDate = detail.planDate
      formData.remark = detail.remark || ''
    } catch (error) {
      ElMessage.error('加载收款计划详情失败')
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
      await paymentPlanApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await paymentPlanApi.create(formData)
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
.ml-2 {
  margin-left: 8px;
}

.text-gray-500 {
  color: #909399;
}
</style>