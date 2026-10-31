<template>
  <div class="contract-analysis-container">
    <!-- 搜索条件 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryParams.startDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <span style="margin: 0 8px">至</span>
          <el-date-picker
            v-model="queryParams.endDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="queryParams.deptId" placeholder="请选择部门" clearable filterable style="width: 150px">
            <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 收款进度概览 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(summary.totalContractAmount) }}</div>
            <div class="stat-label">生效合同金额</div>
          </div>
          <el-progress :percentage="100" :stroke-width="8" color="#409eff" :show-text="false" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card success">
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(summary.totalReceived) }}</div>
            <div class="stat-label">已收款金额</div>
          </div>
          <el-progress :percentage="summary.receiveRate" :stroke-width="8" color="#67c23a" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card warning">
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(summary.totalPending) }}</div>
            <div class="stat-label">待收款金额</div>
          </div>
          <el-progress :percentage="Math.max(100 - summary.receiveRate, 0)" :stroke-width="8" color="#e6a23c" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card info">
          <div class="stat-content">
            <div class="stat-value">{{ summary.receiveRate }}%</div>
            <div class="stat-label">收款率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 合同执行分析与应收账款 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card shadow="never" class="status-card">
          <template #header>
            <div class="card-header">
              <span>合同状态分布</span>
              <el-tag type="info" size="small" effect="plain">含全部状态</el-tag>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="aging-card">
          <template #header>
            <div class="card-header">
              <span>应收账款账龄</span>
            </div>
          </template>
          <div ref="agingChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="rate-card">
          <template #header>
            <div class="card-header">
              <span>收款进度</span>
            </div>
          </template>
          <div class="rate-content">
            <el-progress type="dashboard" :percentage="summary.receiveRate" :width="120">
              <template #default="{ percentage }">
                <span class="rate-value">{{ percentage }}%</span>
              </template>
            </el-progress>
            <div class="rate-label">整体收款率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 收款进度明细 -->
    <el-card shadow="never" class="progress-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>合同收款进度明细</span>
          <el-tag type="info">生效合同 {{ paymentList.length }}条</el-tag>
        </div>
      </template>
      <el-table :data="paymentList" max-height="280" v-loading="progressLoading">
        <el-table-column prop="contractNo" label="合同编号" width="140" />
        <el-table-column prop="customerName" label="客户" min-width="140" show-overflow-tooltip />
        <el-table-column prop="contractAmount" label="合同金额" width="110">
          <template #default="{ row }">¥{{ formatAmount(row.contractAmount) }}</template>
        </el-table-column>
        <el-table-column prop="receivedAmount" label="已收款" width="110">
          <template #default="{ row }">¥{{ formatAmount(row.receivedAmount) }}</template>
        </el-table-column>
        <el-table-column prop="pendingAmount" label="待收款" width="110">
          <template #default="{ row }">¥{{ formatAmount(row.pendingAmount) }}</template>
        </el-table-column>
        <el-table-column prop="progressRate" label="进度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="Number(row.progressRate || 0)" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
              {{ getPaymentStatusLabel(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextPaymentDate" label="下次收款" width="120" />
      </el-table>
    </el-card>

    <!-- 即将到期合同 -->
    <el-card shadow="never" class="expiring-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header warning-header">
          <el-icon class="warning-icon"><Warning /></el-icon>
          <span>即将到期合同</span>
          <el-tag type="warning" size="small" effect="plain">30天内 · 实时预警</el-tag>
          <el-badge :value="expiringContracts.length" type="warning" />
        </div>
      </template>
      <el-table :data="expiringContracts" max-height="200" v-loading="warningLoading">
        <el-table-column prop="contractNo" label="合同编号" width="120" />
        <el-table-column prop="customerName" label="客户" min-width="150" show-overflow-tooltip />
        <el-table-column prop="contractAmount" label="合同金额" width="100">
          <template #default="{ row }">¥{{ formatAmount(row.contractAmount) }}</template>
        </el-table-column>
        <el-table-column prop="endDate" label="到期日期" width="120" />
        <el-table-column label="剩余天数" width="100">
          <template #default="{ row }">
            <el-tag :type="(row.daysToExpire ?? row.remainingDays ?? 0) <= 7 ? 'danger' : 'warning'" size="small">
              {{ row.daysToExpire ?? row.remainingDays ?? 0 }}天
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="80" />
      </el-table>
    </el-card>

    <!-- 逾期收款 -->
    <el-card shadow="never" class="overdue-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header danger-header">
          <el-icon class="danger-icon"><CircleCloseFilled /></el-icon>
          <span>逾期收款计划</span>
          <el-tag type="danger" size="small" effect="plain">实时预警 · 受部门筛选</el-tag>
          <el-badge :value="overduePayments.length" type="danger" />
        </div>
      </template>
      <el-table :data="overduePayments" max-height="200" v-loading="warningLoading">
        <el-table-column prop="contractNo" label="合同编号" width="120" />
        <el-table-column prop="customerName" label="客户" min-width="150" show-overflow-tooltip />
        <el-table-column label="逾期金额" width="110">
          <template #default="{ row }">
            <span class="overdue-amount">¥{{ formatAmount(row.overdueAmount ?? row.planAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="planDate" label="计划日期" width="120" />
        <el-table-column prop="overdueDays" label="逾期天数" width="90">
          <template #default="{ row }">
            <el-tag type="danger" size="small">{{ row.overdueDays }}天</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/crm/analysis'
import { useAnalysisFilters } from '@/composables/useAnalysisFilters'
import type {
  AnalysisQueryDTO,
  ContractStatusItemVO,
  AgingItemVO,
  PaymentProgressItemVO,
  ExpiringContractVO,
  OverduePaymentVO
} from '@/types/crm/analysis'

const pad = (n: number) => String(n).padStart(2, '0')
const formatYmd = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
const defaultEnd = new Date()
const defaultStart = new Date(defaultEnd.getFullYear(), 0, 1)

const queryParams = reactive<AnalysisQueryDTO>({
  startDate: formatYmd(defaultStart),
  endDate: formatYmd(defaultEnd),
  deptId: undefined
})

const { deptOptions, loadFilterOptions } = useAnalysisFilters()

const statusList = ref<ContractStatusItemVO[]>([])
const agingList = ref<AgingItemVO[]>([])
const paymentList = ref<PaymentProgressItemVO[]>([])
const expiringContracts = ref<ExpiringContractVO[]>([])
const overduePayments = ref<OverduePaymentVO[]>([])

const executionLoading = ref(false)
const receivableLoading = ref(false)
const progressLoading = ref(false)
const warningLoading = ref(false)

const statusChartRef = ref<HTMLElement>()
const agingChartRef = ref<HTMLElement>()
let statusChart: echarts.ECharts | null = null
let agingChart: echarts.ECharts | null = null

const summary = computed(() => {
  const totalContractAmount = paymentList.value.reduce((sum, item) => sum + Number(item.contractAmount || 0), 0)
  const totalReceived = paymentList.value.reduce((sum, item) => sum + Number(item.receivedAmount || 0), 0)
  const totalPending = paymentList.value.reduce((sum, item) => sum + Number(item.pendingAmount || 0), 0)
  const receiveRate = totalContractAmount > 0
    ? Math.round((totalReceived / totalContractAmount) * 100)
    : 0
  return { totalContractAmount, totalReceived, totalPending, receiveRate }
})

const formatAmount = (amount?: number) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return Number(amount).toFixed(0)
}

const ensureChart = (el: HTMLElement | undefined, chart: echarts.ECharts | null) => {
  if (!el) return null
  chart?.dispose()
  return echarts.init(el)
}

const setEmptyChart = (chart: echarts.ECharts | null) => {
  chart?.clear()
  chart?.setOption({
    title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
  })
}

const loadContractExecution = async () => {
  executionLoading.value = true
  try {
    statusList.value = (await analysisApi.getContractExecution(queryParams)) || []
  } catch (e) {
    console.error('加载合同执行分析失败', e)
    statusList.value = []
  } finally {
    executionLoading.value = false
    renderStatusChart()
  }
}

const loadReceivable = async () => {
  receivableLoading.value = true
  try {
    agingList.value = (await analysisApi.getReceivable(queryParams)) || []
  } catch (e) {
    console.error('加载应收账款分析失败', e)
    agingList.value = []
  } finally {
    receivableLoading.value = false
    renderAgingChart()
  }
}

const loadPaymentProgress = async () => {
  progressLoading.value = true
  try {
    paymentList.value = (await analysisApi.getPaymentProgress(queryParams)) || []
  } catch (e) {
    console.error('加载收款进度分析失败', e)
    paymentList.value = []
  } finally {
    progressLoading.value = false
  }
}

const loadWarnings = async () => {
  warningLoading.value = true
  try {
    // 预警为当前实时数据，受部门筛选；时间范围仅作用于上方图表/明细
    const data = await analysisApi.getWarnings({
      deptId: queryParams.deptId,
      limit: 50
    })
    expiringContracts.value = data?.expiringContracts || []
    overduePayments.value = data?.overduePayments || []
  } catch (e) {
    console.error('加载预警数据失败', e)
    expiringContracts.value = []
    overduePayments.value = []
  } finally {
    warningLoading.value = false
  }
}

const renderStatusChart = () => {
  if (!statusChartRef.value) return
  statusChart = ensureChart(statusChartRef.value, statusChart)
  if (!statusChart) return
  if (!statusList.value.length) {
    setEmptyChart(statusChart)
    return
  }

  statusChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { fontSize: 10 } },
    series: [{
      type: 'pie',
      radius: ['35%', '60%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 1 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 12 } },
      data: statusList.value.map(item => ({
        name: item.statusName,
        value: item.contractCount,
        itemStyle: { color: getStatusColor(item.status) }
      }))
    }]
  }, true)
}

const getStatusColor = (status: string | number) => {
  const key = String(status)
  const colors: Record<string, string> = {
    '1': '#909399',
    DRAFT: '#909399',
    '2': '#e6a23c',
    APPROVING: '#e6a23c',
    '3': '#67c23a',
    EFFECTIVE: '#67c23a',
    '4': '#f56c6c',
    EXPIRED: '#f56c6c',
    '5': '#303133',
    TERMINATED: '#303133'
  }
  return colors[key] || '#409eff'
}

const renderAgingChart = () => {
  if (!agingChartRef.value) return
  agingChart = ensureChart(agingChartRef.value, agingChart)
  if (!agingChart) return
  if (!agingList.value.length) {
    setEmptyChart(agingChart)
    return
  }

  agingChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const data = params[0]
        const item = agingList.value[data.dataIndex]
        return `${item.agingRangeName || item.agingRange}<br/>金额: ¥${formatAmount(item.receivableAmount ?? item.amount)}<br/>占比: ${item.percentage || 0}%`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: agingList.value.map(item => item.agingRangeName || item.agingRange),
      axisLabel: { rotate: 30, fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      axisLabel: { formatter: (value: number) => formatAmount(value) }
    },
    series: [{
      type: 'bar',
      data: agingList.value.map(item => ({
        value: Number(item.receivableAmount ?? item.amount ?? 0),
        itemStyle: { color: getAgingColor(item.agingRangeName || item.agingRange) }
      })),
      barWidth: '60%'
    }]
  }, true)
}

const getAgingColor = (range: string) => {
  if (range.includes('0-30')) return '#67c23a'
  if (range.includes('31-60')) return '#e6a23c'
  if (range.includes('61-90')) return '#f56c6c'
  return '#909399'
}

const getPaymentStatusType = (status?: string) => {
  const map: Record<string, string> = {
    COMPLETED: 'success',
    NORMAL: 'info',
    OVERDUE: 'danger'
  }
  return map[status || ''] || 'info'
}

const getPaymentStatusLabel = (status?: string) => {
  const map: Record<string, string> = {
    COMPLETED: '已完成',
    NORMAL: '正常',
    OVERDUE: '逾期'
  }
  return map[status || ''] || status || '-'
}

const handleSearch = () => {
  loadContractExecution()
  loadReceivable()
  loadPaymentProgress()
  loadWarnings()
}

const handleReset = () => {
  queryParams.startDate = formatYmd(defaultStart)
  queryParams.endDate = formatYmd(defaultEnd)
  queryParams.deptId = undefined
  handleSearch()
}

const handleResize = () => {
  statusChart?.resize()
  agingChart?.resize()
}

onMounted(async () => {
  await loadFilterOptions()
  handleSearch()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  agingChart?.dispose()
})
</script>

<style scoped>
.contract-analysis-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.stat-card {
  padding: 15px;
  margin-bottom: 20px;
}

.stat-card.success .stat-value { color: #67c23a; }
.stat-card.warning .stat-value { color: #e6a23c; }
.stat-card.info .stat-value { color: #409eff; }

.stat-content .stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.stat-content .stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.status-card, .aging-card, .rate-card {
  height: 280px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-container {
  height: 220px;
}

.rate-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 220px;
}

.rate-value {
  font-size: 24px;
  font-weight: 600;
}

.rate-label {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.warning-header, .danger-header {
  color: #f56c6c;
}

.warning-icon { color: #e6a23c; }
.danger-icon { color: #f56c6c; }

.overdue-amount {
  color: #f56c6c;
  font-weight: 500;
}

:deep(.el-card__header) {
  padding: 12px 16px;
}

:deep(.el-badge) {
  margin-left: auto;
}

:deep(.el-progress__text) {
  font-size: 12px !important;
}
</style>
