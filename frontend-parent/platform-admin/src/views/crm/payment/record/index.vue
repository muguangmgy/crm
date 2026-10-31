<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="收款编号">
          <el-input v-model="queryParams.paymentNo" placeholder="请输入收款编号" clearable />
        </el-form-item>
        <el-form-item label="关联合同">
          <el-select
            v-model="queryParams.contractId"
            placeholder="请选择合同"
            clearable
            filterable
          >
            <el-option
              v-for="item in contractList"
              :key="item.id"
              :label="item.contractNo + ' - ' + item.contractName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收款方式">
          <el-select v-model="queryParams.paymentType" placeholder="请选择收款方式" clearable>
            <el-option
              v-for="item in paymentTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in paymentRecordStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收款日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" v-permission="'crm:payment:record:add'" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="paymentNo" label="收款编号" min-width="150" />
        <el-table-column prop="contractNo" label="合同编号" min-width="150" />
        <el-table-column prop="planNo" label="关联计划" width="150">
          <template #default="{ row }">
            {{ row.planNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentAmount" label="收款金额" width="120">
          <template #default="{ row }">
            ¥{{ row.paymentAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentDate" label="收款日期" width="120" />
        <el-table-column prop="paymentType" label="收款方式" width="100">
          <template #default="{ row }">
            {{ row.paymentTypeName || getPaymentTypeLabel(row.paymentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="payerName" label="付款人" width="100">
          <template #default="{ row }">
            {{ row.payerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentRecordStatusTagType(row.status)">
              {{ getPaymentRecordStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:payment:record:edit'" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              type="success"
              link
              v-permission="'crm:payment:record:confirm'"
              v-if="row.status === 2"
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              type="warning"
              link
              v-permission="'crm:payment:record:confirm'"
              v-if="row.status === 1"
              @click="handleUnconfirm(row)"
            >
              取消确认
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:payment:record:delete'"
              v-if="row.status === 2"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </el-card>

    <!-- 收款记录表单弹窗 -->
    <RecordForm ref="recordFormRef" @success="loadList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { paymentRecordApi, paymentPlanApi } from '@/api/crm/payment'
import { contractApi } from '@/api/crm/contract'
import RecordForm from './RecordForm.vue'
import type { PaymentRecordVO, PaymentRecordQueryDTO } from '@/types/crm/payment'
import type { ContractVO } from '@/types/crm/contract'
import {
  getPaymentRecordStatusTagType,
  getPaymentRecordStatusLabel,
  getPaymentTypeLabel,
  paymentRecordStatusOptions,
  paymentTypeOptions
} from '@/types/crm/payment'

// 查询参数
const queryParams = reactive<PaymentRecordQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 数据
const tableData = ref<PaymentRecordVO[]>([])
const total = ref(0)
const loading = ref(false)
const contractList = ref<ContractVO[]>([])

// 弹窗引用
const recordFormRef = ref()

// 加载合同下拉（生效合同，不受负责人数据权限限制）
const loadContractList = async () => {
  try {
    contractList.value = await contractApi.getOptions(3)
  } catch (error) {
    console.error('加载合同列表失败', error)
  }
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await paymentRecordApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载收款记录列表失败', error)
  } finally {
    loading.value = false
  }
}

// 日期范围变化
const handleDateChange = (val: [string, string] | null) => {
  if (val) {
    queryParams.paymentDateStart = val[0]
    queryParams.paymentDateEnd = val[1]
  } else {
    queryParams.paymentDateStart = undefined
    queryParams.paymentDateEnd = undefined
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadList()
}

// 重置
const handleReset = () => {
  queryParams.paymentNo = undefined
  queryParams.contractId = undefined
  queryParams.paymentType = undefined
  queryParams.status = undefined
  queryParams.paymentDateStart = undefined
  queryParams.paymentDateEnd = undefined
  dateRange.value = null
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  recordFormRef.value?.open(undefined, contractList.value)
}

// 编辑
const handleEdit = (row: PaymentRecordVO) => {
  recordFormRef.value?.open(row.id, contractList.value)
}

// 收款确认
const handleConfirm = async (row: PaymentRecordVO) => {
  try {
    await ElMessageBox.confirm(`确定确认收款【${row.paymentNo}】？`, '提示', { type: 'success' })
    await paymentRecordApi.confirm(row.id)
    ElMessage.success('收款已确认')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 取消确认
const handleUnconfirm = async (row: PaymentRecordVO) => {
  try {
    await ElMessageBox.confirm(`确定取消确认收款【${row.paymentNo}】？`, '提示', { type: 'warning' })
    await paymentRecordApi.unconfirm(row.id)
    ElMessage.success('已取消确认')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row: PaymentRecordVO) => {
  try {
    await ElMessageBox.confirm(`确定删除收款记录【${row.paymentNo}】？`, '提示', { type: 'warning' })
    await paymentRecordApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 初始化
onMounted(() => {
  loadContractList()
  loadList()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .el-pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}
</style>