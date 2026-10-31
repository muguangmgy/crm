<template>
  <el-dialog
    v-model="visible"
    title="报价单详情"
    width="700px"
    destroy-on-close
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="报价单编号">{{ detail.quoteNo }}</el-descriptions-item>
      <el-descriptions-item label="报价单名称">{{ detail.quoteName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ detail.customerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="关联商机">{{ detail.opportunityName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="报价总金额">
        ¥{{ detail.totalAmount?.toFixed(2) }}
      </el-descriptions-item>
      <el-descriptions-item label="整体折扣率">
        {{ detail.discountRate ? `${detail.discountRate}%` : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="最终金额">
        <span class="final-amount">¥{{ detail.finalAmount?.toFixed(2) || detail.totalAmount?.toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getQuoteStatusTagType(detail.status)">
          {{ getQuoteStatusLabel(detail.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="报价有效期">{{ detail.expireDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="负责人">{{ detail.ownerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="发送时间">{{ detail.sendTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="接受时间">{{ detail.acceptTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <!-- 产品明细表格 -->
    <div class="product-section">
      <h4 class="section-title">产品明细</h4>
      <el-table :data="detail.products" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="quantity" label="数量" width="100">
          <template #default="{ row }">
            {{ row.quantity?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">
            ¥{{ row.unitPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="discountRate" label="行折扣" width="80">
          <template #default="{ row }">
            {{ row.discountRate ? `${row.discountRate}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="行总价" width="120">
          <template #default="{ row }">
            ¥{{ row.totalPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="100">
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { quoteApi } from '@/api/crm/quote'
import type { QuoteVO } from '@/types/crm/quote'
import { getQuoteStatusTagType, getQuoteStatusLabel } from '@/types/crm/quote'

const visible = ref(false)
const loading = ref(false)
const detail = ref<QuoteVO>({
  id: 0,
  quoteNo: '',
  quoteName: '',
  opportunityId: 0,
  opportunityName: '',
  customerId: 0,
  customerName: '',
  totalAmount: 0,
  status: 1,
  ownerId: 0,
  ownerName: '',
  createTime: '',
  updateTime: '',
  products: []
})

// 打开弹窗
const open = async (id: number) => {
  visible.value = true
  loading.value = true
  try {
    const result = await quoteApi.getById(id)
    detail.value = result
  } catch (error) {
    ElMessage.error('加载报价单详情失败')
  } finally {
    loading.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>

<style scoped lang="scss">
.product-section {
  margin-top: 20px;

  .section-title {
    margin-bottom: 10px;
    font-size: 16px;
    font-weight: 600;
  }
}

.final-amount {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>