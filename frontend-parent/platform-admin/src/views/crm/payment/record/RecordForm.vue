<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑收款记录' : '新增收款记录'"
    width="600px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
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
                :label="`${item.contractNo || ''} - ${item.contractName || ''}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="关联收款计划">
            <el-select
              v-model="formData.planId"
              placeholder="请选择收款计划（可选）"
              clearable
              filterable
              @change="handlePlanChange"
            >
              <el-option
                v-for="item in planList"
                :key="item.id"
                :label="`${item.planNo} - 待收¥${getPlanRemain(item).toFixed(2)}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="收款金额" prop="paymentAmount">
            <el-input-number
              v-model="formData.paymentAmount"
              :min="0.01"
              :max="maxPaymentAmount > 0 ? maxPaymentAmount : undefined"
              :precision="2"
              placeholder="请输入收款金额"
            />
            <span class="ml-2 text-gray-500">元</span>
            <div v-if="formData.contractId" class="remain-tip">
              待收上限：¥{{ maxPaymentAmount.toFixed(2) }}
              <template v-if="formData.planId">（按所选计划）</template>
              <template v-else>（按合同）</template>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收款日期" prop="paymentDate">
            <el-date-picker
              v-model="formData.paymentDate"
              type="date"
              placeholder="请选择收款日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="收款方式">
            <el-select v-model="formData.paymentType" placeholder="请选择收款方式" clearable>
              <el-option
                v-for="item in paymentTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="付款人">
            <el-input v-model="formData.payerName" placeholder="请输入付款人名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="付款账户">
            <el-input v-model="formData.payerAccount" placeholder="请输入付款账户" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收款账户">
            <el-input v-model="formData.ourAccount" placeholder="请输入收款账户" />
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
import { computed, ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormRules } from 'element-plus'
import { paymentRecordApi, paymentPlanApi } from '@/api/crm/payment'
import type { PaymentRecordSaveDTO, PaymentPlanVO } from '@/types/crm/payment'
import type { ContractVO } from '@/types/crm/contract'
import { paymentTypeOptions as paymentTypeOptionsConst } from '@/types/crm/payment'

const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const contractList = ref<ContractVO[]>([])
const planList = ref<PaymentPlanVO[]>([])

const paymentTypeOptions = paymentTypeOptionsConst

const formData = reactive<PaymentRecordSaveDTO>({
  contractId: undefined,
  planId: undefined,
  paymentAmount: 0,
  paymentDate: '',
  paymentType: '',
  payerName: '',
  payerAccount: '',
  ourAccount: '',
  voucherUrl: '',
  remark: ''
})

const getPlanRemain = (plan: PaymentPlanVO) => {
  const remain = Number(plan.planAmount || 0) - Number(plan.receivedAmount || 0)
  return remain > 0 ? remain : 0
}

const getContractRemain = (contract?: ContractVO) => {
  if (!contract) return 0
  const remain = Number(contract.contractAmount || 0) - Number(contract.receivedAmount || 0)
  return remain > 0 ? remain : 0
}

/** 收款金额上限：选了计划取 min(计划待收, 合同待收)，否则取合同待收 */
const maxPaymentAmount = computed(() => {
  const contract = contractList.value.find(item => item.id === formData.contractId)
  const contractRemain = getContractRemain(contract)
  if (!formData.planId) {
    return Number(contractRemain.toFixed(2))
  }
  const plan = planList.value.find(item => item.id === formData.planId)
  if (!plan) {
    return Number(contractRemain.toFixed(2))
  }
  return Number(Math.min(getPlanRemain(plan), contractRemain).toFixed(2))
})

const validatePaymentAmount = (_rule: unknown, value: number, callback: (error?: Error) => void) => {
  if (value == null || value <= 0) {
    callback(new Error('收款金额必须大于0'))
    return
  }
  if (!formData.contractId) {
    callback()
    return
  }
  if (value > maxPaymentAmount.value + 0.0001) {
    callback(new Error(`收款金额不能超过待收金额 ¥${maxPaymentAmount.value.toFixed(2)}`))
    return
  }
  callback()
}

const rules: FormRules = {
  contractId: [{ required: true, message: '请选择合同', trigger: 'change' }],
  paymentAmount: [
    { required: true, message: '收款金额不能为空', trigger: 'blur' },
    { validator: validatePaymentAmount, trigger: 'blur' }
  ],
  paymentDate: [{ required: true, message: '收款日期不能为空', trigger: 'change' }]
}

const handleContractChange = async (contractId: number) => {
  formData.planId = undefined
  formData.paymentAmount = 0
  if (contractId) {
    try {
      const plans = await paymentPlanApi.getByContractId(contractId)
      planList.value = plans.filter(p => p.receivedStatus !== 2)
    } catch (error) {
      console.error('加载收款计划失败', error)
      planList.value = []
    }
  } else {
    planList.value = []
  }
}

const handlePlanChange = (planId?: number) => {
  if (!planId) return
  const plan = planList.value.find(item => item.id === planId)
  if (!plan) return
  const remain = Math.min(getPlanRemain(plan), maxPaymentAmount.value)
  if (remain > 0) {
    formData.paymentAmount = Number(remain.toFixed(2))
  }
}

const open = async (id?: number, contracts?: ContractVO[]) => {
  if (contracts) {
    contractList.value = contracts
  }

  formData.id = undefined
  formData.contractId = undefined
  formData.planId = undefined
  formData.paymentAmount = 0
  formData.paymentDate = ''
  formData.paymentType = ''
  formData.payerName = ''
  formData.payerAccount = ''
  formData.ourAccount = ''
  formData.voucherUrl = ''
  formData.remark = ''
  planList.value = []

  isEdit.value = !!id

  if (id) {
    try {
      const detail = await paymentRecordApi.getById(id)
      formData.id = detail.id
      formData.contractId = detail.contractId
      formData.planId = detail.planId
      formData.paymentAmount = Number(detail.paymentAmount ?? 0)
      formData.paymentDate = detail.paymentDate
      formData.paymentType = detail.paymentType || ''
      formData.payerName = detail.payerName || ''
      formData.payerAccount = detail.payerAccount || ''
      formData.ourAccount = detail.ourAccount || ''
      formData.voucherUrl = detail.voucherUrl || ''
      formData.remark = detail.remark || ''

      if (detail.contractId) {
        const plans = await paymentPlanApi.getByContractId(detail.contractId)
        planList.value = plans.filter(p => p.receivedStatus !== 2 || p.id === detail.planId)
      }
    } catch (error) {
      ElMessage.error('加载收款记录详情失败')
      return
    }
  }

  visible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    if (maxPaymentAmount.value <= 0) {
      ElMessage.warning('当前合同/计划已无待收金额')
      return
    }
    if (formData.paymentAmount > maxPaymentAmount.value) {
      ElMessage.warning(`收款金额不能超过待收金额 ¥${maxPaymentAmount.value.toFixed(2)}`)
      return
    }

    submitting.value = true

    if (isEdit.value) {
      await paymentRecordApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await paymentRecordApi.create(formData)
      ElMessage.success('新增成功')
    }

    emit('success')
    visible.value = false
  } catch (error: any) {
    if (error !== 'cancel' && error?.message !== 'cancel') {
      // 后端业务错误已由拦截器提示；校验失败不重复弹
      if (!error?.errors) {
        // ignore
      }
    }
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.ml-2 {
  margin-left: 8px;
}

.text-gray-500 {
  color: #909399;
}

.remain-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
