<template>
  <el-dialog
    v-model="visible"
    title="发票详情"
    width="600px"
    destroy-on-close
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="发票编号">{{ detail.invoiceNo }}</el-descriptions-item>
      <el-descriptions-item label="关联合同">{{ detail.contractName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ detail.customerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="发票类型">
        <el-tag :type="getInvoiceTypeTagType(detail.invoiceType)">
          {{ detail.invoiceTypeName || getInvoiceTypeLabel(detail.invoiceType) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="发票金额">
        <span class="amount-display">¥{{ Number(detail.invoiceAmount ?? 0).toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getInvoiceStatusTagType(detail.invoiceStatus)">
          {{ detail.invoiceStatusName || getInvoiceStatusLabel(detail.invoiceStatus) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="开票日期">{{ detail.invoiceDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="负责人">{{ detail.ownerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="附件" :span="2">
        <el-link v-if="detail.fileUrl" type="primary" :href="detail.fileUrl" target="_blank">
          查看附件
        </el-link>
        <span v-else>-</span>
      </el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { invoiceApi } from '@/api/crm/invoice'
import type { InvoiceVO } from '@/types/crm/invoice'
import {
  getInvoiceTypeTagType,
  getInvoiceTypeLabel,
  getInvoiceStatusTagType,
  getInvoiceStatusLabel
} from '@/types/crm/invoice'

const visible = ref(false)
const loading = ref(false)
const detail = ref<InvoiceVO>({
  id: 0,
  invoiceNo: '',
  contractId: 0,
  contractName: '',
  customerId: 0,
  customerName: '',
  invoiceType: 1,
  invoiceAmount: 0,
  invoiceStatus: 1,
  ownerId: 0,
  ownerName: '',
  createTime: '',
  updateTime: ''
})

const open = async (id: number) => {
  visible.value = true
  loading.value = true
  try {
    detail.value = await invoiceApi.getById(id)
  } catch (error) {
    ElMessage.error('加载发票详情失败')
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.amount-display {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>
