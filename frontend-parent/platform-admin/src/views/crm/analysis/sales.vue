<template>
  <div class="sales-analysis-container">
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
          <el-select
            v-model="queryParams.deptId"
            placeholder="请选择部门"
            clearable
            filterable
            style="width: 150px"
            @change="onDeptChange"
          >
            <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="销售员">
          <el-select v-model="queryParams.userId" placeholder="请选择销售员" clearable filterable style="width: 150px">
            <el-option v-for="user in filteredUsers" :key="user.id" :label="user.userName" :value="user.id" />
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

    <!-- 业绩趋势图 -->
    <el-card shadow="never" class="chart-card">
      <template #header>
        <div class="card-header">
          <span>业绩趋势分析</span>
          <el-radio-group v-model="queryParams.timeDimension" @change="loadTrend">
            <el-radio-button value="MONTH">按月</el-radio-button>
            <el-radio-button value="QUARTER">按季度</el-radio-button>
            <el-radio-button value="YEAR">按年</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="trendChartRef" class="chart-container"></div>
    </el-card>

    <!-- 业绩统计区域 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <span>销售员业绩统计</span>
            </div>
          </template>
          <el-table :data="salesPerformance" max-height="350" v-loading="salesLoading">
            <el-table-column prop="userName" label="销售员" width="100" />
            <el-table-column prop="deptName" label="部门" width="100" show-overflow-tooltip />
            <el-table-column prop="dealAmount" label="成交金额" width="120">
              <template #default="{ row }">
                <span class="amount-text">¥{{ formatAmount(row.dealAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="dealCount" label="成交商机" width="80" />
            <el-table-column prop="contractCount" label="合同数" width="70" />
            <el-table-column prop="receivedAmount" label="收款金额" width="120">
              <template #default="{ row }">¥{{ formatAmount(row.receivedAmount) }}</template>
            </el-table-column>
            <el-table-column prop="newCustomerCount" label="新增客户" width="80" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <span>团队业绩统计</span>
            </div>
          </template>
          <el-table :data="teamPerformance" max-height="350" v-loading="teamLoading">
            <el-table-column prop="deptName" label="团队" width="120" />
            <el-table-column prop="memberCount" label="成员数" width="70" />
            <el-table-column prop="dealAmount" label="成交金额" width="120">
              <template #default="{ row }">
                <span class="amount-text">¥{{ formatAmount(row.dealAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="dealCount" label="成交商机" width="80" />
            <el-table-column prop="contractCount" label="合同数" width="70" />
            <el-table-column prop="receivedAmount" label="收款金额" width="120">
              <template #default="{ row }">¥{{ formatAmount(row.receivedAmount) }}</template>
            </el-table-column>
            <el-table-column prop="newCustomerCount" label="新增客户" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 业绩排名 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never" class="ranking-card">
          <template #header>
            <div class="card-header">
              <span>销售员业绩排名</span>
            </div>
          </template>
          <div ref="userRankChartRef" class="rank-chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="ranking-card">
          <template #header>
            <div class="card-header">
              <span>团队业绩排名</span>
            </div>
          </template>
          <div ref="deptRankChartRef" class="rank-chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/crm/analysis'
import { useAnalysisFilters } from '@/composables/useAnalysisFilters'
import type { AnalysisQueryDTO, TrendVO, SalesPerformanceVO, TeamPerformanceVO, RankingVO } from '@/types/crm/analysis'

const pad = (n: number) => String(n).padStart(2, '0')
const formatDate = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
const defaultEnd = new Date()
const defaultStart = new Date(defaultEnd.getFullYear(), 0, 1)

const queryParams = reactive<AnalysisQueryDTO>({
  startDate: formatDate(defaultStart),
  endDate: formatDate(defaultEnd),
  deptId: undefined,
  userId: undefined,
  timeDimension: 'MONTH'
})

const { deptOptions, loadFilterOptions, filterUsersByDept } = useAnalysisFilters()
const filteredUsers = computed(() => filterUsersByDept(queryParams.deptId))

const trendData = ref<TrendVO[]>([])
const salesPerformance = ref<SalesPerformanceVO[]>([])
const teamPerformance = ref<TeamPerformanceVO[]>([])
const userRanking = ref<RankingVO[]>([])

const salesLoading = ref(false)
const teamLoading = ref(false)

const trendChartRef = ref<HTMLElement>()
const userRankChartRef = ref<HTMLElement>()
const deptRankChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let userRankChart: echarts.ECharts | null = null
let deptRankChart: echarts.ECharts | null = null

const formatAmount = (amount?: number) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return Number(amount).toFixed(0)
}

const onDeptChange = () => {
  if (queryParams.userId && !filteredUsers.value.some(u => u.id === queryParams.userId)) {
    queryParams.userId = undefined
  }
}

const loadTrend = async () => {
  try {
    trendData.value = (await analysisApi.getTrend(queryParams)) || []
  } catch (e) {
    console.error('加载趋势数据失败', e)
    trendData.value = []
  } finally {
    renderTrendChart()
  }
}

const loadSalesPerformance = async () => {
  salesLoading.value = true
  try {
    salesPerformance.value = (await analysisApi.getSalesPerformance(queryParams)) || []
    userRanking.value = (await analysisApi.getSalesRanking(queryParams)) || []
  } catch (e) {
    console.error('加载销售员业绩失败', e)
    salesPerformance.value = []
    userRanking.value = []
  } finally {
    salesLoading.value = false
    renderUserRankChart()
  }
}

const loadTeamPerformance = async () => {
  teamLoading.value = true
  try {
    teamPerformance.value = (await analysisApi.getTeamPerformance(queryParams)) || []
  } catch (e) {
    console.error('加载团队业绩失败', e)
    teamPerformance.value = []
  } finally {
    teamLoading.value = false
    renderDeptRankChart()
  }
}

const ensureChart = (el: HTMLElement | undefined, chart: echarts.ECharts | null) => {
  if (!el) return null
  if (chart) {
    chart.dispose()
  }
  return echarts.init(el)
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = ensureChart(trendChartRef.value, trendChart)
  if (!trendChart) return

  if (!trendData.value.length) {
    trendChart.clear()
    trendChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return
  }

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['成交金额', '收款金额', '新增客户'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.value.map(item => item.period),
      axisLabel: { rotate: 45 }
    },
    yAxis: [
      {
        type: 'value',
        name: '金额',
        axisLabel: { formatter: (value: number) => formatAmount(value) }
      },
      {
        type: 'value',
        name: '客户数',
        axisLabel: { formatter: '{value}' }
      }
    ],
    series: [
      {
        name: '成交金额',
        type: 'bar',
        data: trendData.value.map(item => item.dealAmount || 0),
        itemStyle: { color: '#5470c6' },
        barWidth: '30%'
      },
      {
        name: '收款金额',
        type: 'bar',
        data: trendData.value.map(item => item.receivedAmount ?? item.paymentAmount ?? 0),
        itemStyle: { color: '#91cc75' },
        barWidth: '30%'
      },
      {
        name: '新增客户',
        type: 'line',
        yAxisIndex: 1,
        data: trendData.value.map(item => item.newCustomerCount || 0),
        itemStyle: { color: '#fac858' },
        smooth: true
      }
    ]
  }, true)
}

const renderRankBar = (
  el: HTMLElement | undefined,
  chart: echarts.ECharts | null,
  names: string[],
  values: number[],
  ranks: number[]
) => {
  if (!el) return null
  const instance = ensureChart(el, chart)
  if (!instance) return null

  if (!names.length) {
    instance.clear()
    instance.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return instance
  }

  instance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const data = params[0]
        return `${data.name}<br/>成交金额: ¥${formatAmount(data.value)}`
      }
    },
    grid: {
      left: '3%',
      right: '15%',
      bottom: '3%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: { formatter: (value: number) => formatAmount(value) }
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLabel: { width: 80, overflow: 'truncate' }
    },
    series: [{
      type: 'bar',
      data: values.map((value, index) => ({
        value,
        itemStyle: { color: getRankColor(ranks[index]) }
      })),
      label: {
        show: true,
        position: 'right',
        formatter: (params: any) => `¥${formatAmount(params.value)}`
      },
      barWidth: '50%'
    }]
  }, true)
  return instance
}

const renderUserRankChart = () => {
  const top10 = userRanking.value.slice(0, 10)
  const names = top10.map(item => item.userName || item.targetName || '-').reverse()
  const values = top10.map(item => Number(item.dealAmount || 0)).reverse()
  const ranks = top10.map(item => item.rankNum).reverse()
  userRankChart = renderRankBar(userRankChartRef.value, userRankChart, names, values, ranks)
}

const renderDeptRankChart = () => {
  const top10 = [...teamPerformance.value]
    .sort((a, b) => Number(b.dealAmount || 0) - Number(a.dealAmount || 0))
    .slice(0, 10)
  const names = top10.map(item => item.deptName || '-').reverse()
  const values = top10.map(item => Number(item.dealAmount || 0)).reverse()
  const ranks = top10.map((_, index) => index + 1).reverse()
  deptRankChart = renderRankBar(deptRankChartRef.value, deptRankChart, names, values, ranks)
}

const getRankColor = (rank: number) => {
  if (rank === 1) return '#f56c6c'
  if (rank === 2) return '#e6a23c'
  if (rank === 3) return '#5cb85c'
  return '#409eff'
}

const handleSearch = () => {
  loadTrend()
  loadSalesPerformance()
  loadTeamPerformance()
}

const handleReset = () => {
  queryParams.startDate = formatDate(defaultStart)
  queryParams.endDate = formatDate(defaultEnd)
  queryParams.deptId = undefined
  queryParams.userId = undefined
  queryParams.timeDimension = 'MONTH'
  handleSearch()
}

const handleResize = () => {
  trendChart?.resize()
  userRankChart?.resize()
  deptRankChart?.resize()
}

onMounted(async () => {
  await loadFilterOptions()
  handleSearch()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  userRankChart?.dispose()
  deptRankChart?.dispose()
})
</script>

<style scoped>
.sales-analysis-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-container {
  height: 350px;
}

.table-card {
  height: 420px;
}

.ranking-card {
  height: 300px;
}

.rank-chart-container {
  height: 240px;
}

.amount-text {
  color: #f56c6c;
  font-weight: 500;
}

:deep(.el-card__header) {
  padding: 12px 16px;
}

:deep(.el-radio-group) {
  margin-left: auto;
}
</style>
