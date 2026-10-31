<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑商机' : '新增商机'"
    width="600px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="商机名称" prop="oppName">
        <el-input v-model="formData.oppName" placeholder="请输入商机名称" maxlength="100" />
      </el-form-item>
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
      <el-form-item label="联系人" prop="contactId">
        <el-select v-model="formData.contactId" placeholder="请选择联系人" clearable filterable>
          <el-option
            v-for="item in contactList"
            :key="item.id"
            :label="item.contactName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="阶段" prop="stageId">
        <el-select v-model="formData.stageId" placeholder="请选择阶段" @change="handleStageChange">
          <el-option
            v-for="item in stageList"
            :key="item.id"
            :label="`${item.stageName} (${item.probability}%)`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预期金额" prop="expectedAmount">
        <el-input-number
          v-model="formData.expectedAmount"
          :precision="2"
          :min="0"
          placeholder="请输入预期金额"
        />
      </el-form-item>
      <el-form-item label="预期成交日" prop="expectedDate">
        <el-date-picker
          v-model="formData.expectedDate"
          type="date"
          placeholder="请选择预期成交日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="商机来源" prop="source">
        <el-input v-model="formData.source" placeholder="请输入商机来源" maxlength="50" />
      </el-form-item>
      <el-form-item label="负责人">
        <el-input v-model="ownerName" disabled placeholder="默认当前登录用户" />
      </el-form-item>
      <el-form-item label="商机描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入商机描述"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注"
          maxlength="500"
          show-word-limit
        />
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
import type { FormInstance, FormRules } from 'element-plus'
import { opportunityApi } from '@/api/crm/opportunity'
import { customerApi } from '@/api/crm/customer'
import { useUserStore } from '@/stores/user'
import type { OpportunitySaveDTO, StageVO } from '@/types/crm/opportunity'
import type { CustomerVO, ContactVO } from '@/types/crm/customer'

const emit = defineEmits<{
  success: []
}>()

const userStore = useUserStore()
const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const oppId = ref<number>()
const ownerName = ref('')

const stageList = ref<StageVO[]>([])
const customerList = ref<CustomerVO[]>([])
const contactList = ref<ContactVO[]>([])

const formData = reactive<OpportunitySaveDTO>({
  customerId: undefined,
  oppName: '',
  stageId: undefined
})

const formRules: FormRules = {
  oppName: [{ required: true, message: '请输入商机名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  stageId: [{ required: true, message: '请选择阶段', trigger: 'change' }]
}

// 加载联系人列表
const handleCustomerChange = async (customerId: number) => {
  if (!customerId) {
    contactList.value = []
    return
  }
  try {
    const result = await customerApi.getContacts(customerId)
    contactList.value = result
  } catch (error) {
    console.error('加载联系人列表失败', error)
  }
}

// 阶段变更时更新概率显示
const handleStageChange = (stageId: number) => {
  // 仅用于界面显示，无需额外操作
}

// 打开弹窗
const open = async (id: number | undefined, stages: StageVO[], customers: CustomerVO[]) => {
  stageList.value = stages
  customerList.value = customers
  oppId.value = id
  isEdit.value = !!id

  ownerName.value = userStore.userInfo?.userName || userStore.userInfo?.userCode || ''

  if (id) {
    // 编辑模式：加载详情
    try {
      const detail = await opportunityApi.getById(id)
      formData.id = detail.id
      formData.customerId = detail.customerId
      formData.contactId = detail.contactId
      formData.oppName = detail.oppName
      formData.stageId = detail.stageId
      formData.expectedAmount = detail.expectedAmount
      formData.expectedDate = detail.expectedDate
      formData.source = detail.source
      formData.ownerId = detail.ownerId
      formData.description = detail.description
      formData.remark = detail.remark
      ownerName.value = detail.ownerName || ownerName.value
      // 加载联系人
      if (detail.customerId) {
        const contacts = await customerApi.getContacts(detail.customerId)
        contactList.value = contacts
      }
    } catch (error) {
      ElMessage.error('加载商机详情失败')
      return
    }
  } else {
    // 新增模式：重置表单
    resetForm()
  }

  visible.value = true
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.customerId = undefined
  formData.contactId = undefined
  formData.oppName = ''
  formData.stageId = stageList.value[0]?.id
  formData.expectedAmount = undefined
  formData.expectedDate = undefined
  formData.source = ''
  formData.ownerId = userStore.userInfo?.userId
  formData.description = ''
  formData.remark = ''
  ownerName.value = userStore.userInfo?.userName || userStore.userInfo?.userCode || ''
  contactList.value = []
}

// 关闭弹窗
const handleClose = () => {
  formRef.value?.resetFields()
  contactList.value = []
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (isEdit.value) {
      await opportunityApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await opportunityApi.create(formData)
      ElMessage.success('新增成功')
    }

    visible.value = false
    emit('success')
  } catch (error) {
    console.error('保存商机失败', error)
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>