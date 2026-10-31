<template>
  <el-dialog
    v-model="visible"
    :title="isWin ? '商机成交' : '商机失败'"
    width="500px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item v-if="isWin" label="实际金额" prop="actualAmount">
        <el-input-number
          v-model="formData.actualAmount"
          :precision="2"
          :min="0"
          placeholder="请输入实际成交金额"
        />
      </el-form-item>
      <el-form-item v-if="isWin" label="中标报价单" prop="winnerQuoteId">
        <el-select v-model="formData.winnerQuoteId" placeholder="请选择中标报价单" clearable>
          <el-option
            v-for="item in quoteList"
            :key="item.id"
            :label="item.quoteName || item.quoteNo"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button :type="isWin ? 'success' : 'danger'" :loading="submitting" @click="handleSubmit">
        确认{{ isWin ? '成交' : '失败' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { opportunityApi } from '@/api/crm/opportunity'
import { quoteApi } from '@/api/crm/quote'
import type { OpportunityCloseDTO, OpportunityVO } from '@/types/crm/opportunity'
import type { QuoteVO } from '@/types/crm/quote'

const emit = defineEmits<{
  success: []
}>()

const visible = ref(false)
const isWin = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const oppId = ref<number>()
const quoteList = ref<QuoteVO[]>([])

const formData = reactive<OpportunityCloseDTO>({
  isWin: true,
  actualAmount: undefined,
  winnerQuoteId: undefined,
  remark: ''
})

const formRules: FormRules = {
  actualAmount: [
    {
      validator: (rule, value, callback) => {
        if (isWin.value && !value) {
          callback(new Error('请输入实际成交金额'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 打开弹窗
const open = async (opp: OpportunityVO, win: boolean) => {
  oppId.value = opp.id
  isWin.value = win
  formData.isWin = win
  formData.actualAmount = win ? opp.expectedAmount : undefined
  formData.winnerQuoteId = undefined
  formData.remark = ''

  // 加载该商机的报价单列表（成交时可选）
  if (win) {
    try {
      const result = await quoteApi.getList({ opportunityId: opp.id, pageNum: 1, pageSize: 100 })
      quoteList.value = result.list
    } catch (error) {
      console.error('加载报价单列表失败', error)
      quoteList.value = []
    }
  }

  visible.value = true
}

// 关闭弹窗
const handleClose = () => {
  formRef.value?.resetFields()
  quoteList.value = []
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    await opportunityApi.close(oppId.value!, formData)
    ElMessage.success(isWin.value ? '商机已标记为成交' : '商机已标记为失败')

    visible.value = false
    emit('success')
  } catch (error) {
    console.error('操作失败', error)
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>