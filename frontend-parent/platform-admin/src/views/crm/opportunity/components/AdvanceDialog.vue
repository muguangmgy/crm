<template>
  <el-dialog v-model="visible" title="阶段推进" width="500px" destroy-on-close>
    <div class="advance-content">
      <p class="current-info">
        当前商机：<strong>{{ opportunity?.oppName }}</strong>
      </p>
      <p class="current-info">
        当前阶段：<el-tag>{{ opportunity?.stageName }}</el-tag>
      </p>

      <div class="stage-steps">
        <el-steps :active="currentStageIndex" align-center>
          <el-step
            v-for="stage in stageList"
            :key="stage.id"
            :title="stage.stageName"
            :description="`${stage.probability}%`"
          />
        </el-steps>
      </div>

      <el-form label-width="100px" class="select-form">
        <el-form-item label="推进到阶段">
          <el-select v-model="nextStageId" placeholder="请选择目标阶段">
            <el-option
              v-for="stage in nextStages"
              :key="stage.id"
              :label="`${stage.stageName} (${stage.probability}%)`"
              :value="stage.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确认推进</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { opportunityApi } from '@/api/crm/opportunity'
import type { OpportunityVO, StageVO } from '@/types/crm/opportunity'

const emit = defineEmits<{
  success: []
}>()

const visible = ref(false)
const submitting = ref(false)
const opportunity = ref<OpportunityVO>()
const stageList = ref<StageVO[]>([])
const nextStageId = ref<number>()

// 计算当前阶段索引
const currentStageIndex = computed(() => {
  if (!opportunity.value) return 0
  const index = stageList.value.findIndex(s => s.id === opportunity.value!.stageId)
  return index >= 0 ? index : 0
})

// 可推进的下一阶段：只能向前；失败阶段走「成交/失败」操作，不出现在推进下拉中
const nextStages = computed(() => {
  if (!opportunity.value) return []
  const currentIndex = stageList.value.findIndex(s => s.id === opportunity.value!.stageId)
  if (currentIndex === -1) return []

  return stageList.value.slice(currentIndex + 1).filter(s => s.isLost !== 1)
})

// 打开弹窗
const open = (opp: OpportunityVO, stages: StageVO[]) => {
  opportunity.value = opp
  stageList.value = stages
  nextStageId.value = nextStages.value[0]?.id
  visible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!nextStageId.value) {
    ElMessage.warning('请选择目标阶段')
    return
  }

  submitting.value = true
  try {
    await opportunityApi.advance(opportunity.value!.id, nextStageId.value)
    ElMessage.success('阶段推进成功')
    visible.value = false
    emit('success')
  } catch (error) {
    console.error('阶段推进失败', error)
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.advance-content {
  .current-info {
    margin-bottom: 12px;
    color: #606266;

    strong {
      color: #303133;
    }
  }

  .stage-steps {
    margin: 24px 0;
  }

  .select-form {
    margin-top: 16px;
  }
}
</style>