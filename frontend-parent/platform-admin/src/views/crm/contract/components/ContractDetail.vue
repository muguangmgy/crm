<template>
  <el-dialog
    v-model="visible"
    title="合同详情"
    width="600px"
    destroy-on-close
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="合同编号">{{ detail.contractNo }}</el-descriptions-item>
      <el-descriptions-item label="合同名称">{{ detail.contractName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ detail.customerName }}</el-descriptions-item>
      <el-descriptions-item label="关联商机">{{ detail.opportunityName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="关联报价单">{{ detail.quoteName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="合同金额">
        <span class="amount-display">¥{{ detail.contractAmount?.toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="开始日期">{{ detail.startDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="结束日期">{{ detail.endDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="签订日期">{{ detail.signDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getContractStatusTagType(detail.status)">
          {{ getContractStatusLabel(detail.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="负责人">{{ detail.ownerName }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { contractApi } from '@/api/crm/contract'
import type { ContractVO } from '@/types/crm/contract'
import { getContractStatusTagType, getContractStatusLabel } from '@/types/crm/contract'

const visible = ref(false)
const loading = ref(false)
const detail = ref<ContractVO>({
  id: 0,
  contractNo: '',
  contractName: '',
  customerId: 0,
  customerName: '',
  contractAmount: 0,
  status: 1,
  ownerId: 0,
  ownerName: '',
  createTime: '',
  updateTime: ''
})

// 打开弹窗
const open = async (id: number) => {
  visible.value = true
  loading.value = true
  try {
    const result = await contractApi.getById(id)
    detail.value = result
  } catch (error) {
    ElMessage.error('加载合同详情失败')
  } finally {
    loading.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>

<style scoped lang="scss">
.amount-display {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>