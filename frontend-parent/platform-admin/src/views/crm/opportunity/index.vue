<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="商机名称">
          <el-input v-model="queryParams.oppName" placeholder="请输入商机名称" clearable/>
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
        <el-form-item label="阶段">
          <el-select v-model="queryParams.stageId" placeholder="请选择阶段" clearable>
            <el-option
                v-for="item in stageList"
                :key="item.id"
                :label="item.stageName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
                v-for="item in opportunityStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商机来源">
          <el-input v-model="queryParams.source" placeholder="请输入来源" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
          <el-button type="success" v-permission="'crm:opportunity:add'" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增
          </el-button>
          <el-button type="warning" v-permission="'crm:opportunity:stage:add'" @click="handleStageConfig">
            <el-icon>
              <Setting/>
            </el-icon>
            阶段配置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="oppNo" label="商机编号" width="150"/>
        <el-table-column prop="oppName" label="商机名称" min-width="150"/>
        <el-table-column prop="customerName" label="客户名称" width="150"/>
        <el-table-column prop="stageName" label="当前阶段" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.stageName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="probability" label="成交概率" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.probability || 0" :stroke-width="8"/>
          </template>
        </el-table-column>
        <el-table-column prop="expectedAmount" label="预期金额" width="120">
          <template #default="{ row }">
            <span v-if="row.expectedAmount">¥{{ row.expectedAmount.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDate" label="预期成交日" width="120"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOpportunityStatusTagType(row.status)">
              {{ getOpportunityStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100"/>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:opportunity:query'" @click="handleView(row)">
              查看
            </el-button>
            <el-button
                type="primary"
                link
                v-permission="'crm:opportunity:edit'"
                v-if="row.status === 1"
                @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
                type="warning"
                link
                v-permission="'crm:opportunity:advance'"
                v-if="row.status === 1 && canAdvance(row.stageId)"
                @click="handleAdvance(row)"
            >
              推进
            </el-button>
            <el-button
                type="success"
                link
                v-permission="'crm:opportunity:close'"
                v-if="row.status === 1"
                @click="handleWin(row)"
            >
              成交
            </el-button>
            <el-button
                type="danger"
                link
                v-permission="'crm:opportunity:close'"
                v-if="row.status === 1"
                @click="handleLose(row)"
            >
              失败
            </el-button>
            <el-button
                type="danger"
                link
                v-permission="'crm:opportunity:delete'"
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

    <!-- 商机表单弹窗 -->
    <OpportunityForm ref="oppFormRef" @success="loadList"/>

    <!-- 商机详情弹窗 -->
    <OpportunityDetail ref="oppDetailRef"/>

    <!-- 成交/失败弹窗 -->
    <OpportunityCloseForm ref="oppCloseRef" @success="loadList"/>

    <!-- 阶段推进弹窗 -->
    <AdvanceDialog ref="advanceRef" @success="loadList"/>

    <!-- 阶段配置弹窗 -->
    <StageConfig ref="stageConfigRef" @success="loadStageList"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Refresh, Search, Setting} from '@element-plus/icons-vue'
import {opportunityApi} from '@/api/crm/opportunity'
import {customerApi} from '@/api/crm/customer'
import OpportunityForm from './components/OpportunityForm.vue'
import OpportunityDetail from './components/OpportunityDetail.vue'
import OpportunityCloseForm from './components/OpportunityCloseForm.vue'
import AdvanceDialog from './components/AdvanceDialog.vue'
import StageConfig from './components/StageConfig.vue'
import type {OpportunityQueryDTO, OpportunityVO, StageVO} from '@/types/crm/opportunity'
import {getOpportunityStatusLabel, getOpportunityStatusTagType, opportunityStatusOptions} from '@/types/crm/opportunity'
import type {CustomerVO} from '@/types/crm/customer'

// 查询参数
const queryParams = reactive<OpportunityQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<OpportunityVO[]>([])
const total = ref(0)
const loading = ref(false)
const stageList = ref<StageVO[]>([])
const customerList = ref<CustomerVO[]>([])

// 弹窗引用
const oppFormRef = ref()
const oppDetailRef = ref()
const oppCloseRef = ref()
const advanceRef = ref()
const stageConfigRef = ref()

// 判断是否可推进（存在下一阶段）
const canAdvance = (currentStageId: number): boolean => {
  const currentIndex = stageList.value.findIndex(s => s.id === currentStageId)
  if (currentIndex === -1) return false
  const currentStage = stageList.value[currentIndex]
  // 成交阶段或失败阶段不能推进
  if (currentStage.isWin === 1 || currentStage.isLost === 1) return false
  // 存在下一个启用阶段
  return currentIndex < stageList.value.length - 1
}

// 加载阶段列表
const loadStageList = async () => {
  try {
    const result = await opportunityApi.getActiveStages()
    stageList.value = result.sort((a, b) => a.sort - b.sort)
  } catch (error) {
    console.error('加载阶段列表失败', error)
  }
}

// 加载客户列表（用于下拉选择）
const loadCustomerList = async () => {
  try {
    const result = await customerApi.getList({pageNum: 1, pageSize: 100})
    customerList.value = result.list
  } catch (error) {
    console.error('加载客户列表失败', error)
  }
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await opportunityApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载商机列表失败', error)
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
  queryParams.oppName = undefined
  queryParams.customerId = undefined
  queryParams.stageId = undefined
  queryParams.status = undefined
  queryParams.source = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  oppFormRef.value?.open(undefined, stageList.value, customerList.value)
}

// 编辑
const handleEdit = (row: OpportunityVO) => {
  oppFormRef.value?.open(row.id, stageList.value, customerList.value)
}

// 查看
const handleView = (row: OpportunityVO) => {
  oppDetailRef.value?.open(row.id, stageList.value)
}

// 阶段推进
const handleAdvance = (row: OpportunityVO) => {
  advanceRef.value?.open(row, stageList.value)
}

// 成交
const handleWin = (row: OpportunityVO) => {
  oppCloseRef.value?.open(row, true)
}

// 失败
const handleLose = (row: OpportunityVO) => {
  oppCloseRef.value?.open(row, false)
}

// 删除
const handleDelete = async (row: OpportunityVO) => {
  try {
    await ElMessageBox.confirm(`确定删除商机【${row.oppName}】？`, '提示', {type: 'warning'})
    await opportunityApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 阶段配置
const handleStageConfig = () => {
  stageConfigRef.value?.open()
}

// 初始化
onMounted(() => {
  loadStageList()
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