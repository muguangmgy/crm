<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userId" placeholder="请输入用户名" clearable/>
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="queryParams.module" placeholder="请输入模块名称" clearable/>
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="queryParams.operation" placeholder="请输入操作名称" clearable/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1"/>
            <el-option label="失败" :value="0"/>
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
          <el-button type="danger" @click="handleClean">
            <el-icon>
              <Delete/>
            </el-icon>
            清理日志
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
          v-loading="loading"
          :data="tableData"
          border
          stripe
          style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="80"/>
        <el-table-column prop="userCode" label="用户名" min-width="100"/>
        <el-table-column prop="userName" label="姓名" min-width="100"/>
        <el-table-column prop="module" label="模块" min-width="120"/>
        <el-table-column prop="operation" label="操作" min-width="150"/>
        <el-table-column prop="targetType" label="目标类型" min-width="120"/>
        <el-table-column prop="targetId" label="目标ID" min-width="100"/>
        <el-table-column prop="ipAddress" label="IP地址" min-width="150"/>
        <el-table-column prop="responseStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.responseStatus === 1" type="success">成功</el-tag>
            <el-tag v-if="row.responseStatus === 0" type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="100"/>
        <el-table-column prop="operTime" label="操作时间" min-width="180"/>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">
              <el-icon>
                <View/>
              </el-icon>
              详情
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
          class="pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
        v-model="detailVisible"
        title="操作日志详情"
        width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.userCode }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ detailData.module }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ detailData.operation }}</el-descriptions-item>
        <el-descriptions-item label="目标类型">{{ detailData.targetType }}</el-descriptions-item>
        <el-descriptions-item label="目标ID">{{ detailData.targetId }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.responseStatus === 1" type="success">成功</el-tag>
          <el-tag v-if="detailData.responseStatus === 0" type="danger">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detailData.duration }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.operTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">请求参数</el-divider>
      <el-input
          v-model="formattedRequestData"
          type="textarea"
          :rows="12"
          readonly
      />
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 清理日志弹窗 -->
    <el-dialog
        v-model="cleanVisible"
        title="清理日志"
        width="400px"
    >
      <el-form :model="cleanForm" label-width="100px">
        <el-form-item label="保留天数">
          <el-input-number v-model="cleanForm.keepDays" :min="1" :max="365"/>
        </el-form-item>
        <el-form-item>
          <span class="text-warning">注意：超过保留天数的日志将被永久删除</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cleanVisible = false">取消</el-button>
        <el-button type="danger" :loading="cleanLoading" @click="handleCleanSubmit">确定清理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {logApi} from '@/api/log'
import type {OperLogQueryDTO, OperLogVO} from '@/types/log'
import type {PageVO} from '@/types/api'

const loading = ref(false)
const cleanLoading = ref(false)
const tableData = ref<OperLogVO[]>([])
const total = ref(0)

const queryParams = reactive<OperLogQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  userId: undefined,
  module: '',
  operation: '',
  status: undefined
})

const detailVisible = ref(false)
const detailData = reactive<OperLogVO>({
  id: 0,
  tenantId: 0,
  userCode: '',
  module: '',
  operation: '',
  targetType: '',
  targetId: '',
  requestData: '',
  responseStatus: 0,
  ipAddress: '',
  operTime: '',
  duration: 0
})

const cleanVisible = ref(false)
const cleanForm = reactive({
  keepDays: 30
})

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<OperLogVO> = await logApi.oper.getList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch {
    // 静默处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.userId = undefined
  queryParams.module = ''
  queryParams.operation = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  loadData()
}

function handleCurrentChange(page: number) {
  queryParams.pageNum = page
  loadData()
}

function handleDetail(row: OperLogVO) {
  Object.assign(detailData, row)
  detailVisible.value = true
}

const formattedRequestData = computed(() => {
  if (!detailData.requestData) return ''
  try {
    const parsed = JSON.parse(detailData.requestData)
    return JSON.stringify(parsed, null, 2)
  } catch {
    return detailData.requestData
  }
})

function handleClean() {
  cleanVisible.value = true
}

async function handleCleanSubmit() {
  try {
    await ElMessageBox.confirm(`确定要清理超过 ${cleanForm.keepDays} 天的操作日志吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    cleanLoading.value = true
    await logApi.oper.clean(cleanForm.keepDays)
    ElMessage.success('清理成功')
    cleanVisible.value = false
    loadData()
  } catch {
    // 静默处理
  } finally {
    cleanLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}

.table-card {
  border-radius: 12px;
}

.text-warning {
  color: #e6a23c;
}
</style>