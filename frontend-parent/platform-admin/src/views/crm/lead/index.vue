<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="线索名称">
          <el-input v-model="queryParams.leadName" placeholder="请输入线索名称" clearable/>
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="queryParams.company" placeholder="请输入公司名称" clearable/>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="queryParams.phone" placeholder="请输入电话" clearable/>
        </el-form-item>
        <el-form-item label="线索来源">
          <el-input v-model="queryParams.source" placeholder="请输入线索来源" clearable/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
                v-for="item in leadStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
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
          <el-button type="success" v-permission="'crm:lead:add'" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="leadName" label="线索名称" min-width="150"/>
        <el-table-column prop="company" label="公司名称" width="150"/>
        <el-table-column prop="phone" label="电话" width="130"/>
        <el-table-column prop="email" label="邮箱" width="180"/>
        <el-table-column prop="source" label="线索来源" width="100"/>
        <el-table-column prop="customerName" label="关联客户" width="120">
          <template #default="{ row }">
            {{ row.customerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getLeadStatusTagType(row.status)">
              {{ getLeadStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100"/>
        <el-table-column prop="convertedOppName" label="转商机" width="120">
          <template #default="{ row }">
            <span v-if="row.convertedOppId">
              <el-link type="primary" @click="handleViewOpportunity(row.convertedOppId)">
                {{ row.convertedOppName }}
              </el-link>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:lead:query'" @click="handleView(row)">
              查看
            </el-button>
            <el-button
                type="primary"
                link
                v-permission="'crm:lead:edit'"
                v-if="row.status !== 3 && row.status !== 4"
                @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
                type="warning"
                link
                v-permission="'crm:lead:convert'"
                v-if="row.status === 1 || row.status === 2"
                @click="handleConvert(row)"
            >
              转商机
            </el-button>
            <el-button
                type="danger"
                link
                v-permission="'crm:lead:delete'"
                v-if="row.status !== 3"
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

    <!-- 线索表单弹窗 -->
    <LeadForm ref="leadFormRef" @success="loadList"/>

    <!-- 线索详情弹窗 -->
    <LeadDetail ref="leadDetailRef"/>

    <!-- 转商机弹窗 -->
    <LeadConvert ref="leadConvertRef" @success="loadList"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Refresh, Search} from '@element-plus/icons-vue'
import {leadApi} from '@/api/crm/lead'
import LeadForm from './components/LeadForm.vue'
import LeadDetail from './components/LeadDetail.vue'
import LeadConvert from './components/LeadConvert.vue'
import type {LeadQueryDTO, LeadVO} from '@/types/crm/lead'
import {getLeadStatusLabel, getLeadStatusTagType, leadStatusOptions} from '@/types/crm/lead'

// 查询参数
const queryParams = reactive<LeadQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<LeadVO[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗引用
const leadFormRef = ref()
const leadDetailRef = ref()
const leadConvertRef = ref()

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await leadApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载线索列表失败', error)
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
  queryParams.leadName = undefined
  queryParams.company = undefined
  queryParams.phone = undefined
  queryParams.source = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  leadFormRef.value?.open(undefined)
}

// 编辑
const handleEdit = (row: LeadVO) => {
  leadFormRef.value?.open(row.id)
}

// 查看
const handleView = (row: LeadVO) => {
  leadDetailRef.value?.open(row.id)
}

// 转商机
const handleConvert = (row: LeadVO) => {
  leadConvertRef.value?.open(row)
}

// 查看商机
const handleViewOpportunity = (oppId: number) => {
  // 跳转到商机详情页面
  ElMessage.info(`跳转到商机ID: ${oppId}`)
  // 实际项目中可以使用 router.push 或打开商机详情弹窗
}

// 删除
const handleDelete = async (row: LeadVO) => {
  try {
    await ElMessageBox.confirm(`确定删除线索【${row.leadName}】？`, '提示', {type: 'warning'})
    await leadApi.delete(row.id)
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