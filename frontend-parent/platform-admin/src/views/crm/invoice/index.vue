<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="发票编号">
          <el-input v-model="queryParams.invoiceNo" placeholder="请输入发票编号" clearable />
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
              :label="item.contractName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发票类型">
          <el-select v-model="queryParams.invoiceType" placeholder="请选择发票类型" clearable>
            <el-option
              v-for="item in invoiceTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.invoiceStatus" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in invoiceStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
          <el-button type="success" v-permission="'crm:invoice:add'" @click="handleAdd">
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
        <el-table-column prop="invoiceNo" label="发票编号" width="150" />
        <el-table-column prop="contractName" label="关联合同" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="invoiceType" label="发票类型" width="140">
          <template #default="{ row }">
            <el-tag :type="getInvoiceTypeTagType(row.invoiceType)">
              {{ getInvoiceTypeLabel(row.invoiceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="invoiceAmount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ Number(row.invoiceAmount ?? 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="invoiceDate" label="开票日期" width="120" />
        <el-table-column prop="invoiceStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getInvoiceStatusTagType(row.invoiceStatus)">
              {{ row.invoiceStatusName || getInvoiceStatusLabel(row.invoiceStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:invoice:query'" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              type="primary"
              link
              v-permission="'crm:invoice:edit'"
              v-if="row.invoiceStatus === 1"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="warning"
              link
              v-permission="'crm:invoice:cancel'"
              v-if="row.invoiceStatus === 1"
              @click="handleCancel(row)"
            >
              作废
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:invoice:delete'"
              v-if="row.invoiceStatus === 1"
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

    <!-- 发票表单弹窗 -->
    <InvoiceForm ref="invoiceFormRef" @success="loadList" />

    <!-- 发票详情弹窗 -->
    <InvoiceDetail ref="invoiceDetailRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { invoiceApi } from '@/api/crm/invoice'
import { contractApi } from '@/api/crm/contract'
import InvoiceForm from './components/InvoiceForm.vue'
import InvoiceDetail from './components/InvoiceDetail.vue'
import type { InvoiceVO, InvoiceQueryDTO } from '@/types/crm/invoice'
import type { ContractVO } from '@/types/crm/contract'
import {
  getInvoiceTypeTagType,
  getInvoiceTypeLabel,
  getInvoiceStatusTagType,
  getInvoiceStatusLabel,
  invoiceTypeOptions as invoiceTypeOptionsConst,
  invoiceStatusOptions as invoiceStatusOptionsConst
} from '@/types/crm/invoice'

// 使用常量
const invoiceTypeOptions = invoiceTypeOptionsConst
const invoiceStatusOptions = invoiceStatusOptionsConst

// 查询参数
const queryParams = reactive<InvoiceQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<InvoiceVO[]>([])
const total = ref(0)
const loading = ref(false)
const contractList = ref<ContractVO[]>([])

// 弹窗引用
const invoiceFormRef = ref()
const invoiceDetailRef = ref()

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
    const result = await invoiceApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载发票列表失败', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadList()
}

// 重置
const handleReset = () => {
  queryParams.invoiceNo = undefined
  queryParams.contractId = undefined
  queryParams.invoiceType = undefined
  queryParams.invoiceStatus = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  invoiceFormRef.value?.open(undefined, contractList.value)
}

// 编辑
const handleEdit = (row: InvoiceVO) => {
  invoiceFormRef.value?.open(row.id, contractList.value)
}

// 查看
const handleView = (row: InvoiceVO) => {
  invoiceDetailRef.value?.open(row.id)
}

// 作废发票
const handleCancel = async (row: InvoiceVO) => {
  try {
    await ElMessageBox.confirm(
      `确定作废发票【${row.invoiceNo}】？作废后不可恢复。`,
      '作废确认',
      { type: 'warning' }
    )
    await invoiceApi.cancel(row.id)
    ElMessage.success('发票已作废')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('作废失败')
    }
  }
}

// 删除
const handleDelete = async (row: InvoiceVO) => {
  try {
    await ElMessageBox.confirm(`确定删除发票【${row.invoiceNo}】？`, '提示', { type: 'warning' })
    await invoiceApi.delete(row.id)
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