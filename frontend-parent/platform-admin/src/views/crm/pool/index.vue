<template>
  <div class="page-container" v-loading="!dictLoaded">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable/>
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="queryParams.customerType" placeholder="请选择类型" clearable>
            <el-option v-for="item in customerTypeDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="Number(item.itemValue)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industry" placeholder="请选择行业" clearable filterable>
            <el-option v-for="item in industryDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="item.itemValue"/>
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="customerNo" label="客户编号" width="150"/>
        <el-table-column prop="customerName" label="客户名称" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="handleDetail(row)">{{ row.customerName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="customerType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.customerType === 1 ? 'primary' : 'success'">
              {{ getDictLabelFromData(customerTypeDict, row.customerType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130"/>
        <el-table-column prop="industry" label="行业" width="100">
          <template #default="{ row }">
            <span v-if="row.customerType === 1 && row.industry">{{
                getDictLabelFromData(industryDict, row.industry) || row.industry
              }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ getDictLabelFromData(customerLevelDict, row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="进入时间" width="180"/>
        <el-table-column label="操作" width="120" fixed="right"
                         v-if="userStore.hasPermission('crm:customer:pool:take')">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleTake(row)">领取</el-button>
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
          class="pagination"
          @size-change="loadData"
          @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useUserStore} from '@/stores/user'
import {customerApi} from '@/api/crm/customer'
import {getDictLabelFromData, useDicts} from '@/composables/useDict'
import type {CustomerQueryDTO, CustomerVO} from '@/types/crm/customer'

const router = useRouter()
const userStore = useUserStore()

// 需要的字典列表
const requiredDicts = ['crm_customer_type', 'crm_industry', 'crm_customer_level']

// 预加载字典
const {dataMap, loaded: dictLoaded} = useDicts(requiredDicts)

// 字典数据
const customerTypeDict = computed(() => dataMap.value['crm_customer_type'] || [])
const industryDict = computed(() => dataMap.value['crm_industry'] || [])
const customerLevelDict = computed(() => dataMap.value['crm_customer_level'] || [])

const queryParams = reactive<CustomerQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  customerType: undefined,
  industry: ''
})

const loading = ref(false)
const tableData = ref<CustomerVO[]>([])
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await customerApi.getPool(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.customerName = ''
  queryParams.customerType = undefined
  queryParams.industry = ''
  queryParams.pageNum = 1
  loadData()
}

const handleTake = async (row: CustomerVO) => {
  await ElMessageBox.confirm('确定领取此客户?', '提示')
  await customerApi.takeFromPool(row.id)
  ElMessage.success('领取成功')
  loadData()
}

const getLevelType = (level: number) => {
  const types: Record<number, string> = {1: '', 2: 'warning', 3: 'danger'}
  return types[level] || ''
}

// 详情
const handleDetail = (row: CustomerVO) => {
  router.push(`/crm/customer/detail/${row.id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>