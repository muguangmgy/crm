<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="计划编号">
          <el-input v-model="queryParams.planNo" placeholder="请输入计划编号" clearable />
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
        <el-form-item label="收款状态">
          <el-select v-model="queryParams.receivedStatus" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in receivedStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期">
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
          <el-button type="success" v-permission="'crm:payment:plan:add'" @click="handleAdd">
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
        <el-table-column prop="planNo" label="计划编号" width="150" />
        <el-table-column prop="contractNo" label="合同编号" width="150" />
        <el-table-column prop="planAmount" label="计划金额" width="120">
          <template #default="{ row }">
            ¥{{ row.planAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="planDate" label="计划日期" width="120" />
        <el-table-column prop="receivedAmount" label="已收金额" width="120">
          <template #default="{ row }">
            ¥{{ row.receivedAmount?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="receivedStatus" label="收款状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getReceivedStatusTagType(row.receivedStatus)">
              {{ getReceivedStatusLabel(row.receivedStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:payment:plan:edit'" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:payment:plan:delete'"
              v-if="row.receivedStatus === 0"
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

    <!-- 收款计划表单弹窗 -->
    <PlanForm ref="planFormRef" @success="loadList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { paymentPlanApi } from '@/api/crm/payment'
import { contractApi } from '@/api/crm/contract'
import PlanForm from './PlanForm.vue'
import type { PaymentPlanVO, PaymentPlanQueryDTO } from '@/types/crm/payment'
import type { ContractVO } from '@/types/crm/contract'
import {
  getReceivedStatusTagType,
  getReceivedStatusLabel,
  receivedStatusOptions
} from '@/types/crm/payment'

// 查询参数
const queryParams = reactive<PaymentPlanQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 数据
const tableData = ref<PaymentPlanVO[]>([])
const total = ref(0)
const loading = ref(false)
const contractList = ref<ContractVO[]>([])

// 弹窗引用
const planFormRef = ref()

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
    const result = await paymentPlanApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载收款计划列表失败', error)
  } finally {
    loading.value = false
  }
}

// 日期范围变化
const handleDateChange = (val: [string, string] | null) => {
  if (val) {
    queryParams.planDateStart = val[0]
    queryParams.planDateEnd = val[1]
  } else {
    queryParams.planDateStart = undefined
    queryParams.planDateEnd = undefined
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadList()
}

// 重置
const handleReset = () => {
  queryParams.planNo = undefined
  queryParams.contractId = undefined
  queryParams.receivedStatus = undefined
  queryParams.planDateStart = undefined
  queryParams.planDateEnd = undefined
  dateRange.value = null
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  planFormRef.value?.open(undefined, contractList.value)
}

// 编辑
const handleEdit = (row: PaymentPlanVO) => {
  planFormRef.value?.open(row.id, contractList.value)
}

// 删除
const handleDelete = async (row: PaymentPlanVO) => {
  try {
    await ElMessageBox.confirm(`确定删除收款计划【${row.planNo}】？`, '提示', { type: 'warning' })
    await paymentPlanApi.delete(row.id)
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