<template>
  <el-dialog v-model="visible" title="阶段配置" width="700px" destroy-on-close>
    <div class="stage-config">
      <div class="toolbar">
        <el-button type="primary" v-permission="'crm:opportunity:stage:add'" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增阶段
        </el-button>
      </div>

      <el-table v-loading="loading" :data="stageList" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="stageName" label="阶段名称" width="150" />
        <el-table-column prop="stageCode" label="阶段编码" width="100" />
        <el-table-column prop="probability" label="成交概率" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.probability" :stroke-width="8" />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="isWin" label="成交阶段" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isWin === 1" type="success">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isLost" label="失败阶段" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isLost === 1" type="danger">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              v-permission="'crm:opportunity:stage:edit'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:opportunity:stage:delete'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 阶段编辑弹窗 -->
    <el-dialog
      v-model="editVisible"
      :title="editForm.id ? '编辑阶段' : '新增阶段'"
      width="450px"
      append-to-body
      destroy-on-close
    >
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="阶段名称" prop="stageName">
          <el-input v-model="editForm.stageName" placeholder="请输入阶段名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="阶段编码" prop="stageCode">
          <el-input v-model="editForm.stageCode" placeholder="请输入阶段编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="成交概率" prop="probability">
          <el-input-number v-model="editForm.probability" :min="0" :max="100" placeholder="0-100" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="editForm.sort" :min="1" placeholder="排序号" />
        </el-form-item>
        <el-form-item label="成交阶段" prop="isWin">
          <el-switch v-model="isWinFlag" />
        </el-form-item>
        <el-form-item label="失败阶段" prop="isLost">
          <el-switch v-model="isLostFlag" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { opportunityApi } from '@/api/crm/opportunity'
import type { StageVO, StageSaveDTO } from '@/types/crm/opportunity'

const emit = defineEmits<{
  success: []
}>()

const visible = ref(false)
const loading = ref(false)
const stageList = ref<StageVO[]>([])

// 阶段编辑
const editVisible = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive<StageSaveDTO>({
  stageName: '',
  probability: 0,
  sort: 1,
  isWin: 0,
  isLost: 0,
  status: 1
})

const isWinFlag = computed({
  get: () => editForm.isWin === 1,
  set: (val) => { editForm.isWin = val ? 1 : 0 }
})

const isLostFlag = computed({
  get: () => editForm.isLost === 1,
  set: (val) => { editForm.isLost = val ? 1 : 0 }
})

const editRules: FormRules = {
  stageName: [{ required: true, message: '请输入阶段名称', trigger: 'blur' }],
  probability: [{ required: true, message: '请输入成交概率', trigger: 'blur' }]
}

// 加载阶段列表
const loadStageList = async () => {
  loading.value = true
  try {
    const result = await opportunityApi.getStages()
    stageList.value = result.sort((a, b) => a.sort - b.sort)
  } catch (error) {
    console.error('加载阶段列表失败', error)
  } finally {
    loading.value = false
  }
}

// 打开弹窗
const open = () => {
  loadStageList()
  visible.value = true
}

// 新增阶段
const handleAdd = () => {
  editForm.id = undefined
  editForm.stageName = ''
  editForm.stageCode = ''
  editForm.probability = 0
  editForm.sort = stageList.value.length + 1
  editForm.isWin = 0
  editForm.isLost = 0
  editForm.status = 1
  editVisible.value = true
}

// 编辑阶段
const handleEdit = (row: StageVO) => {
  editForm.id = row.id
  editForm.stageName = row.stageName
  editForm.stageCode = row.stageCode
  editForm.probability = row.probability
  editForm.sort = row.sort
  editForm.isWin = row.isWin || 0
  editForm.isLost = row.isLost || 0
  editForm.status = row.status
  editVisible.value = true
}

// 提交阶段编辑
const handleEditSubmit = async () => {
  try {
    await editFormRef.value?.validate()
    editSubmitting.value = true

    if (editForm.id) {
      await opportunityApi.updateStage(editForm)
      ElMessage.success('修改成功')
    } else {
      await opportunityApi.createStage(editForm)
      ElMessage.success('新增成功')
    }

    editVisible.value = false
    loadStageList()
    emit('success')
  } catch (error) {
    console.error('保存阶段失败', error)
  } finally {
    editSubmitting.value = false
  }
}

// 删除阶段
const handleDelete = async (row: StageVO) => {
  try {
    await ElMessageBox.confirm(`确定删除阶段【${row.stageName}】？`, '提示', { type: 'warning' })
    await opportunityApi.deleteStage(row.id)
    ElMessage.success('删除成功')
    loadStageList()
    emit('success')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.stage-config {
  .toolbar {
    margin-bottom: 16px;
  }
}
</style>