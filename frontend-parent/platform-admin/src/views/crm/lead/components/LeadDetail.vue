<template>
  <el-dialog
    v-model="visible"
    title="线索详情"
    width="600px"
    destroy-on-close
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="线索名称">{{ detail.leadName }}</el-descriptions-item>
      <el-descriptions-item label="公司名称">{{ detail.company || '-' }}</el-descriptions-item>
      <el-descriptions-item label="电话">{{ detail.phone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ detail.email || '-' }}</el-descriptions-item>
      <el-descriptions-item label="线索来源">{{ detail.source || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getLeadStatusTagType(detail.status)">
          {{ getLeadStatusLabel(detail.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="关联客户">{{ detail.customerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="关联联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="负责人">{{ detail.ownerName }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="转商机时间">{{ detail.convertedTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="转换商机">
        <span v-if="detail.convertedOppId">
          <el-link type="primary">{{ detail.convertedOppName }}</el-link>
        </span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="线索描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { leadApi } from '@/api/crm/lead'
import type { LeadVO } from '@/types/crm/lead'
import { getLeadStatusTagType, getLeadStatusLabel } from '@/types/crm/lead'

const visible = ref(false)
const loading = ref(false)
const detail = ref<LeadVO>({
  id: 0,
  leadName: '',
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
    const result = await leadApi.getById(id)
    detail.value = result
  } catch (error) {
    ElMessage.error('加载线索详情失败')
  } finally {
    loading.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>