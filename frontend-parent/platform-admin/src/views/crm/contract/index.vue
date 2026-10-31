<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="合同名称">
          <el-input v-model="queryParams.contractName" placeholder="请输入合同名称" clearable />
        </el-form-item>
        <el-form-item label="合同编号">
          <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable />
        </el-form-item>
        <el-form-item label="客户">
          <el-select
            v-model="queryParams.customerId"
            placeholder="请选择客户"
            clearable
            filterable
          >
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.customerName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in contractStatusOptions"
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
          <el-button type="success" v-permission="'crm:contract:add'" @click="handleAdd">
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
        <el-table-column prop="contractNo" label="合同编号" width="150" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="contractAmount" label="合同金额" width="120">
          <template #default="{ row }">
            ¥{{ row.contractAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getContractStatusTagType(row.status)">
              {{ getContractStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:contract:query'" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              type="primary"
              link
              v-permission="'crm:contract:edit'"
              v-if="row.status === 1"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="warning"
              link
              v-permission="'crm:contract:submit'"
              v-if="row.status === 1"
              @click="handleSubmit(row)"
            >
              提交审批
            </el-button>
            <el-button
              type="success"
              link
              v-permission="'crm:contract:approve'"
              v-if="row.status === 2"
              @click="handleApprove(row)"
            >
              审批通过
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:contract:reject'"
              v-if="row.status === 2"
              @click="handleReject(row)"
            >
              驳回
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:contract:terminate'"
              v-if="row.status === 3"
              @click="handleTerminate(row)"
            >
              终止
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:contract:delete'"
              v-if="row.status === 1"
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

    <!-- 合同表单弹窗 -->
    <ContractForm ref="contractFormRef" @success="loadList" />

    <!-- 合同详情弹窗 -->
    <ContractDetail ref="contractDetailRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { contractApi } from '@/api/crm/contract'
import { customerApi } from '@/api/crm/customer'
import ContractForm from './components/ContractForm.vue'
import ContractDetail from './components/ContractDetail.vue'
import type { ContractVO, ContractQueryDTO } from '@/types/crm/contract'
import type { CustomerVO } from '@/types/crm/customer'
import { getContractStatusTagType, getContractStatusLabel, contractStatusOptions as contractStatusOptionsConst } from '@/types/crm/contract'

// 使用常量
const contractStatusOptions = contractStatusOptionsConst

// 查询参数
const queryParams = reactive<ContractQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<ContractVO[]>([])
const total = ref(0)
const loading = ref(false)
const customerList = ref<CustomerVO[]>([])

// 弹窗引用
const contractFormRef = ref()
const contractDetailRef = ref()

// 加载客户列表（用于下拉选择）
const loadCustomerList = async () => {
  try {
    const result = await customerApi.getList({ pageNum: 1, pageSize: 100, status: 1 })
    customerList.value = result.list
  } catch (error) {
    console.error('加载客户列表失败', error)
  }
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await contractApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载合同列表失败', error)
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
  queryParams.contractName = undefined
  queryParams.contractNo = undefined
  queryParams.customerId = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  contractFormRef.value?.open(undefined, customerList.value)
}

// 编辑
const handleEdit = (row: ContractVO) => {
  contractFormRef.value?.open(row.id, customerList.value)
}

// 查看
const handleView = (row: ContractVO) => {
  contractDetailRef.value?.open(row.id)
}

// 提交审批
const handleSubmit = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定提交合同【${row.contractName || row.contractNo}】审批？`, '提示', { type: 'warning' })
    await contractApi.submit(row.id)
    ElMessage.success('合同已提交审批')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

// 审批通过
const handleApprove = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定审批通过合同【${row.contractName || row.contractNo}】？`, '提示', { type: 'success' })
    await contractApi.approve(row.id)
    ElMessage.success('合同审批通过')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 驳回
const handleReject = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定驳回合同【${row.contractName || row.contractNo}】？`, '提示', { type: 'warning' })
    await contractApi.reject(row.id)
    ElMessage.success('合同已驳回')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 终止合同
const handleTerminate = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定终止合同【${row.contractName || row.contractNo}】？`, '提示', { type: 'warning' })
    await contractApi.terminate(row.id)
    ElMessage.success('合同已终止')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定删除合同【${row.contractName || row.contractNo}】？`, '提示', { type: 'warning' })
    await contractApi.delete(row.id)
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
  loadCustomerList()
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