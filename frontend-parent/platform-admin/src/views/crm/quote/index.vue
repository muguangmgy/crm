<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="报价单名称">
          <el-input v-model="queryParams.quoteName" placeholder="请输入报价单名称" clearable />
        </el-form-item>
        <el-form-item label="报价单编号">
          <el-input v-model="queryParams.quoteNo" placeholder="请输入报价单编号" clearable />
        </el-form-item>
        <el-form-item label="关联商机">
          <el-select
            v-model="queryParams.opportunityId"
            placeholder="请选择商机"
            clearable
            filterable
          >
            <el-option
              v-for="item in opportunityList"
              :key="item.id"
              :label="item.oppName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in quoteStatusOptions"
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
          <el-button type="success" v-permission="'crm:quote:add'" @click="handleAdd">
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
        <el-table-column prop="quoteNo" label="报价单编号" width="150" />
        <el-table-column prop="quoteName" label="报价单名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="opportunityName" label="关联商机" width="150" />
        <el-table-column prop="totalAmount" label="报价金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="最终金额" width="120">
          <template #default="{ row }">
            <span v-if="row.finalAmount">¥{{ row.finalAmount?.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getQuoteStatusTagType(row.status)">
              {{ getQuoteStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="expireDate" label="有效期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:quote:query'" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              type="primary"
              link
              v-permission="'crm:quote:edit'"
              v-if="row.status === 1"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="warning"
              link
              v-permission="'crm:quote:send'"
              v-if="row.status === 1"
              @click="handleSend(row)"
            >
              发送
            </el-button>
            <el-button
              type="success"
              link
              v-permission="'crm:quote:accept'"
              v-if="row.status === 2"
              @click="handleAccept(row)"
            >
              中标
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:quote:reject'"
              v-if="row.status === 2"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="'crm:quote:delete'"
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

    <!-- 报价单表单弹窗 -->
    <QuoteForm ref="quoteFormRef" @success="loadList" />

    <!-- 报价单详情弹窗 -->
    <QuoteDetail ref="quoteDetailRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { quoteApi } from '@/api/crm/quote'
import { opportunityApi } from '@/api/crm/opportunity'
import QuoteForm from './components/QuoteForm.vue'
import QuoteDetail from './components/QuoteDetail.vue'
import type { QuoteVO, QuoteQueryDTO, quoteStatusOptions } from '@/types/crm/quote'
import type { OpportunityVO } from '@/types/crm/opportunity'
import { getQuoteStatusTagType, getQuoteStatusLabel, quoteStatusOptions as quoteStatusOptionsConst } from '@/types/crm/quote'

// 使用常量
const quoteStatusOptions = quoteStatusOptionsConst

// 查询参数
const queryParams = reactive<QuoteQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<QuoteVO[]>([])
const total = ref(0)
const loading = ref(false)
const opportunityList = ref<OpportunityVO[]>([])

// 弹窗引用
const quoteFormRef = ref()
const quoteDetailRef = ref()

// 加载商机列表（用于下拉选择）
const loadOpportunityList = async () => {
  try {
    const result = await opportunityApi.getList({ pageNum: 1, pageSize: 100, status: 1 })
    opportunityList.value = result.list
  } catch (error) {
    console.error('加载商机列表失败', error)
  }
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await quoteApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载报价单列表失败', error)
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
  queryParams.quoteName = undefined
  queryParams.quoteNo = undefined
  queryParams.opportunityId = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  quoteFormRef.value?.open(undefined, opportunityList.value)
}

// 编辑
const handleEdit = (row: QuoteVO) => {
  quoteFormRef.value?.open(row.id, opportunityList.value)
}

// 查看
const handleView = (row: QuoteVO) => {
  quoteDetailRef.value?.open(row.id)
}

// 发送报价
const handleSend = async (row: QuoteVO) => {
  try {
    await ElMessageBox.confirm(`确定发送报价单【${row.quoteName || row.quoteNo}】？`, '提示', { type: 'warning' })
    await quoteApi.send(row.id)
    ElMessage.success('报价单已发送')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发送失败')
    }
  }
}

// 报价中标
const handleAccept = async (row: QuoteVO) => {
  try {
    await ElMessageBox.confirm(`确定将报价单【${row.quoteName || row.quoteNo}】标记为中标？`, '提示', { type: 'success' })
    await quoteApi.accept(row.id)
    ElMessage.success('报价单已中标')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 报价拒绝
const handleReject = async (row: QuoteVO) => {
  try {
    await ElMessageBox.confirm(`确定将报价单【${row.quoteName || row.quoteNo}】标记为拒绝？`, '提示', { type: 'warning' })
    await quoteApi.reject(row.id)
    ElMessage.success('报价单已拒绝')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row: QuoteVO) => {
  try {
    await ElMessageBox.confirm(`确定删除报价单【${row.quoteName || row.quoteNo}】？`, '提示', { type: 'warning' })
    await quoteApi.delete(row.id)
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
  loadOpportunityList()
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