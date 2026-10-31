<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userCode" placeholder="请输入用户名" clearable/>
        </el-form-item>
        <el-form-item label="登录类型">
          <el-select v-model="queryParams.loginType" placeholder="请选择类型" clearable>
            <el-option label="登录" :value="1"/>
            <el-option label="注销" :value="2"/>
            <el-option label="刷新" :value="3"/>
          </el-select>
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
        <el-table-column prop="loginType" label="登录类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.loginType === 1" type="success">登录</el-tag>
            <el-tag v-if="row.loginType === 2" type="info">注销</el-tag>
            <el-tag v-if="row.loginType === 3" type="warning">刷新</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" min-width="150"/>
        <el-table-column prop="browser" label="浏览器" min-width="120"/>
        <el-table-column prop="os" label="操作系统" min-width="120"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">成功</el-tag>
            <el-tag v-if="row.status === 0" type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" min-width="150"/>
        <el-table-column prop="loginTime" label="登录时间" min-width="180"/>
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
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {logApi} from '@/api/log'
import type {LoginLogQueryDTO, LoginLogVO} from '@/types/log'
import type {PageVO} from '@/types/api'

const loading = ref(false)
const cleanLoading = ref(false)
const tableData = ref<LoginLogVO[]>([])
const total = ref(0)

const queryParams = reactive<LoginLogQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  userCode: undefined,
  loginType: undefined,
  status: undefined
})

const cleanVisible = ref(false)
const cleanForm = reactive({
  keepDays: 30
})

async function loadData() {
  loading.value = true
  try {
    const res: PageVO<LoginLogVO> = await logApi.login.getList(queryParams)
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
  queryParams.loginType = undefined
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

function handleClean() {
  cleanVisible.value = true
}

async function handleCleanSubmit() {
  try {
    await ElMessageBox.confirm(`确定要清理超过 ${cleanForm.keepDays} 天的登录日志吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    cleanLoading.value = true
    await logApi.login.clean(cleanForm.keepDays)
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