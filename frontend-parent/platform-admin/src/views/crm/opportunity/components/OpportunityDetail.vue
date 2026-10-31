<template>
  <el-dialog v-model="visible" title="商机详情" width="700px" destroy-on-close>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="商机编号">{{ detail.oppNo }}</el-descriptions-item>
      <el-descriptions-item label="商机名称">{{ detail.oppName }}</el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ detail.customerName }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前阶段">
        <el-tag>{{ detail.stageName }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="成交概率">
        <el-progress :percentage="detail.probability || 0" :stroke-width="8" />
      </el-descriptions-item>
      <el-descriptions-item label="预期金额">
        <span v-if="detail.expectedAmount">¥{{ detail.expectedAmount.toFixed(2) }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="实际金额">
        <span v-if="detail.actualAmount">¥{{ detail.actualAmount.toFixed(2) }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="预期成交日">{{ detail.expectedDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="成交日期">{{ detail.closeDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="商机来源">{{ detail.source || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getOpportunityStatusTagType(detail.status)">
          {{ getOpportunityStatusLabel(detail.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="负责人">{{ detail.ownerName }}</el-descriptions-item>
      <el-descriptions-item label="中标报价单">
        {{ detail.winnerQuoteNo || detail.winnerQuoteName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
    </el-descriptions>

    <!-- 阶段进度条 -->
    <div class="stage-progress" v-if="stageList.length > 0">
      <h4>阶段进度</h4>
      <el-steps :active="currentStageIndex" align-center>
        <el-step
          v-for="stage in stageList"
          :key="stage.id"
          :title="stage.stageName"
          :description="`${stage.probability}%`"
        />
      </el-steps>
    </div>

    <!-- 商机描述 -->
    <div class="detail-section" v-if="detail.description">
      <h4>商机描述</h4>
      <p>{{ detail.description }}</p>
    </div>

    <!-- 备注 -->
    <div class="detail-section" v-if="detail.remark">
      <h4>备注</h4>
      <p>{{ detail.remark }}</p>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { opportunityApi } from '@/api/crm/opportunity'
import type { OpportunityVO, StageVO } from '@/types/crm/opportunity'
import { getOpportunityStatusTagType, getOpportunityStatusLabel } from '@/types/crm/opportunity'

const visible = ref(false)
const detail = ref<OpportunityVO>({} as OpportunityVO)
const stageList = ref<StageVO[]>([])

// 计算当前阶段索引
const currentStageIndex = computed(() => {
  const index = stageList.value.findIndex(s => s.id === detail.value.stageId)
  return index >= 0 ? index : 0
})

// 打开弹窗
const open = async (id: number, stages: StageVO[]) => {
  stageList.value = stages
  try {
    detail.value = await opportunityApi.getById(id)
    visible.value = true
  } catch (error) {
    console.error('加载商机详情失败', error)
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.stage-progress {
  margin-top: 24px;

  h4 {
    margin-bottom: 16px;
    font-size: 16px;
    color: #303133;
  }
}

.detail-section {
  margin-top: 24px;

  h4 {
    margin-bottom: 12px;
    font-size: 16px;
    color: #303133;
  }

  p {
    color: #606266;
    line-height: 1.6;
  }
}
</style>