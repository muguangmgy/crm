<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="父字典类型">
        <el-input :value="internalParentName || '根节点'" disabled />
      </el-form-item>
      <el-form-item label="字典编码" prop="dictCode">
        <el-input v-model="formData.dictCode" placeholder="请输入字典编码" />
      </el-form-item>
      <el-form-item label="字典名称" prop="dictName">
        <el-input v-model="formData.dictName" placeholder="请输入字典名称" />
      </el-form-item>
      <el-form-item label="租户" prop="tenantId" v-if="isPlatformRole">
        <el-select v-model="formData.tenantId" placeholder="请选择租户">
          <el-option label="全局" :value="0" />
          <el-option v-for="item in tenantOptions" :key="item.id" :label="item.tenantName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序号" prop="sort">
        <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
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
</template>

<script setup lang="ts">
import { computed, onMounted, ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { dictTypeApi } from '@/api/dict-type'
import { tenantApi } from '@/api/tenant'
import { useUserStore } from '@/stores/user'
import type { DictTypeTreeVO } from '@/types/dict-type'
import type { TenantVO } from '@/types/tenant'
import type { Status } from '@/types/api'

const props = defineProps<{
  parentId?: number
  parentName?: string
  parentTenantId?: number
}>()
const emit = defineEmits<{ (e: 'success'): void }>()

const userStore = useUserStore()
const isPlatformRole = computed(() => userStore.isPlatformRole)

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑字典类型' : '新增字典类型')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const tenantOptions = ref<TenantVO[]>([])

const formData = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  tenantId: undefined as number | undefined,
  dictCode: '',
  dictName: '',
  sort: 0,
  status: 1 as Status,
  remark: ''
})

// 内部存储传入的父节点信息（用于显示）
const internalParentName = ref('')

const formRules: FormRules = {
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

async function loadTenantOptions() {
  if (!isPlatformRole.value) return
  try { tenantOptions.value = await tenantApi.getOptions() } catch {}
}

function open(node?: DictTypeTreeVO, parentId?: number, parentName?: string, parentTenantId?: number) {
  formData.id = node?.id
  formData.parentId = parentId ?? props.parentId ?? 0
  internalParentName.value = parentName ?? props.parentName ?? ''
  // 租户默认值：编辑时保持原有，新增时根据父节点判断
  if (node?.id) {
    formData.tenantId = node.tenantId
  } else if (isPlatformRole.value) {
    const effectiveParentId = parentId ?? props.parentId ?? 0
    const effectiveParentTenantId = parentTenantId ?? props.parentTenantId ?? 0
    formData.tenantId = effectiveParentId === 0 ? 0 : effectiveParentTenantId
  } else {
    formData.tenantId = undefined
  }
  formData.dictCode = node?.dictCode || ''
  formData.dictName = node?.dictName || ''
  formData.sort = node?.sort || 0
  formData.status = node?.status ?? 1
  formData.remark = node?.remark || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  submitLoading.value = true
  try {
    const data = { ...formData }
    if (formData.id) {
      await dictTypeApi.update(data)
      ElMessage.success('修改成功')
    } else {
      await dictTypeApi.create(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('success')
  } catch {} finally { submitLoading.value = false }
}

onMounted(() => loadTenantOptions())
defineExpose({ open })
</script>