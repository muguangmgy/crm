<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑发票' : '新增发票'"
    width="600px"
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
            :label="item.contractName || item.contractNo || `合同#${item.id}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="客户名称">
        <el-input v-model="formData.customerName" disabled placeholder="自动带出" />
      </el-form-item>

      <el-form-item label="发票类型" prop="invoiceType">
        <el-select v-model="formData.invoiceType" placeholder="请选择发票类型">
          <el-option
            v-for="item in invoiceTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="发票金额" prop="invoiceAmount">
        <el-input-number
          v-model="formData.invoiceAmount"
          :min="0"
          :precision="2"
          placeholder="请输入金额"
          style="width: 200px"
        />
        <span class="ml-2 text-gray-500">元</span>
      </el-form-item>

      <el-form-item label="开票日期" prop="invoiceDate">
        <el-date-picker
          v-model="formData.invoiceDate"
          type="date"
          placeholder="请选择开票日期"
          value-format="YYYY-MM-DD"
          style="width: 200px"
        />
      </el-form-item>

      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>

      <el-form-item label="附件">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="true"
          :limit="1"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :file-list="fileList"
        >
          <el-button type="primary" plain>
            <el-icon><Upload /></el-icon>
            上传附件
          </el-button>
          <template #tip>
            <div class="el-upload__tip">只能上传pdf、jpg、png文件，且不超过10MB</div>
          </template>
        </el-upload>
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
import { Upload } from '@element-plus/icons-vue'
import { invoiceApi } from '@/api/crm/invoice'
import type { InvoiceSaveDTO, InvoiceType } from '@/types/crm/invoice'
import type { ContractVO } from '@/types/crm/contract'
import { invoiceTypeOptions as invoiceTypeOptionsConst } from '@/types/crm/invoice'

const emit = defineEmits(['success'])

const invoiceTypeOptions = invoiceTypeOptionsConst

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const contractList = ref<ContractVO[]>([])
const fileList = ref<any[]>([])

const uploadUrl = '/api/file/upload'
const uploadHeaders = {
  Authorization: localStorage.getItem('token') || ''
}

const formData = reactive<InvoiceSaveDTO & { customerName?: string }>({
  contractId: undefined,
  invoiceType: 1 as InvoiceType,
  invoiceAmount: 0,
  invoiceDate: '',
  remark: '',
  fileUrl: '',
  customerName: ''
})

const rules = {
  contractId: [{ required: true, message: '请选择关联合同', trigger: 'change' }],
  invoiceType: [{ required: true, message: '请选择发票类型', trigger: 'change' }],
  invoiceAmount: [{ required: true, message: '请输入发票金额', trigger: 'blur' }],
  invoiceDate: [{ required: true, message: '请选择开票日期', trigger: 'change' }]
}

const handleContractChange = (contractId: number) => {
  const contract = contractList.value.find(item => item.id === contractId)
  if (contract) {
    formData.customerName = contract.customerName || ''
  }
}

const handleUploadSuccess = (response: any) => {
  if (response.code === 200) {
    formData.fileUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const open = async (id?: number, contracts?: ContractVO[]) => {
  if (contracts) {
    contractList.value = contracts
  }

  formData.id = undefined
  formData.contractId = undefined
  formData.invoiceType = 1
  formData.invoiceAmount = 0
  formData.invoiceDate = ''
  formData.remark = ''
  formData.fileUrl = ''
  formData.customerName = ''
  fileList.value = []

  isEdit.value = !!id

  if (id) {
    try {
      const detail = await invoiceApi.getById(id)
      formData.id = detail.id
      formData.contractId = detail.contractId
      formData.invoiceType = detail.invoiceType
      formData.invoiceAmount = Number(detail.invoiceAmount ?? 0)
      formData.invoiceDate = detail.invoiceDate || ''
      formData.remark = detail.remark || ''
      formData.fileUrl = detail.fileUrl || ''
      formData.customerName = detail.customerName || ''

      if (detail.fileUrl) {
        fileList.value = [{ name: '发票附件', url: detail.fileUrl }]
      }
    } catch (error) {
      ElMessage.error('加载发票详情失败')
      return
    }
  }

  visible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    if (isEdit.value) {
      await invoiceApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await invoiceApi.create(formData)
      ElMessage.success('新增成功')
    }

    visible.value = false
    emit('success')
  } catch (error) {
    // validation or api error
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
