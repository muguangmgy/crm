<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑线索' : '新增线索'"
    width="600px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="线索名称" prop="leadName">
        <el-input v-model="formData.leadName" placeholder="请输入线索名称" maxlength="100" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="公司名称">
            <el-input v-model="formData.company" placeholder="请输入公司名称" maxlength="100" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="线索来源">
            <el-input v-model="formData.source" placeholder="请输入线索来源" maxlength="50" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="电话">
            <el-input v-model="formData.phone" placeholder="请输入电话" maxlength="20" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" placeholder="请输入邮箱" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联客户">
            <el-select
              v-model="formData.customerId"
              placeholder="请选择客户"
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态">
              <el-option
                v-for="item in leadStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="负责人">
        <el-input v-model="ownerName" disabled placeholder="默认当前登录用户" />
      </el-form-item>

      <el-form-item label="线索描述">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入线索描述"
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
import { leadApi } from '@/api/crm/lead'
import { customerApi } from '@/api/crm/customer'
import { useUserStore } from '@/stores/user'
import type { LeadSaveDTO } from '@/types/crm/lead'
import type { CustomerVO } from '@/types/crm/customer'
import { leadStatusOptions } from '@/types/crm/lead'

const emit = defineEmits(['success'])
const userStore = useUserStore()

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()
const ownerName = ref('')

const customerList = ref<CustomerVO[]>([])

const formData = reactive<LeadSaveDTO>({
  leadName: '',
  source: '',
  status: 1,
  phone: '',
  email: '',
  company: '',
  description: ''
})

const rules = {
  leadName: [{ required: true, message: '线索名称不能为空', trigger: 'blur' }]
}

const loadCustomerList = async () => {
  try {
    const result = await customerApi.getList({ pageNum: 1, pageSize: 100, status: 1 })
    customerList.value = result.list
  } catch (error) {
    console.error('加载客户列表失败', error)
  }
}

const open = async (id?: number) => {
  await loadCustomerList()

  formData.id = undefined
  formData.leadName = ''
  formData.source = ''
  formData.status = 1
  formData.phone = ''
  formData.email = ''
  formData.company = ''
  formData.description = ''
  formData.customerId = undefined
  formData.contactId = undefined
  formData.ownerId = userStore.userInfo?.userId
  ownerName.value = userStore.userInfo?.userName || userStore.userInfo?.userCode || ''

  isEdit.value = !!id

  if (id) {
    try {
      const detail = await leadApi.getById(id)
      formData.id = detail.id
      formData.leadName = detail.leadName
      formData.source = detail.source || ''
      formData.status = detail.status
      formData.phone = detail.phone || ''
      formData.email = detail.email || ''
      formData.company = detail.company || ''
      formData.description = detail.description || ''
      formData.customerId = detail.customerId
      formData.contactId = detail.contactId
      formData.ownerId = detail.ownerId
      ownerName.value = detail.ownerName || ownerName.value
    } catch (error) {
      ElMessage.error('加载线索详情失败')
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
      await leadApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await leadApi.create(formData)
      ElMessage.success('新增成功')
    }

    emit('success')
    visible.value = false
  } catch (error) {
    // 校验失败或接口错误已由表单/拦截器提示，此处不再重复弹窗
    console.error('保存线索失败', error)
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
