<template>
  <div class="dashboard-container">
    <!-- 顶部数据卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon customer">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboardData.newCustomerCount || 0 }}</div>
            <div class="stat-label">本月新增客户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon deal">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(dashboardData.dealAmount) }}</div>
            <div class="stat-label">本月成交金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon payment">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(paymentAmount) }}</div>
            <div class="stat-label">本月收款金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon follow">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboardData.pendingFollowCount || 0 }}</div>
            <div class="stat-label">待跟进客户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon opportunity">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboardData.expiringContractCount || 0 }}</div>
            <div class="stat-label">即将到期合同</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon pending">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboardData.overduePaymentCount || 0 }}</div>
            <div class="stat-label">逾期收款</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 销售漏斗 -->
      <el-col :span="6">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>销售漏斗</span>
            </div>
          </template>
          <div ref="funnelChartRef" class="chart-container funnel-chart"></div>
        </el-card>
      </el-col>

      <!-- 业绩趋势 -->
      <el-col :span="6">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>业绩趋势（近12个月）</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container trend-chart"></div>
        </el-card>
      </el-col>

      <!-- 客户行业分布 -->
      <el-col :span="6">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户行业分布</span>
            </div>
          </template>
          <div ref="industryChartRef" class="chart-container industry-chart"></div>
        </el-card>
      </el-col>

      <!-- 客户地区分布 -->
      <el-col :span="6">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户地区分布</span>
            </div>
          </template>
          <div ref="provinceChartRef" class="chart-container province-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预警提醒区域 -->
    <el-row :gutter="20" class="warning-row">
      <!-- 即将到期合同 -->
      <el-col :span="8">
        <el-card shadow="never" class="warning-card">
          <template #header>
            <div class="card-header warning-header">
              <el-icon class="warning-icon"><Warning /></el-icon>
              <span>即将到期合同</span>
              <el-badge :value="warnings.expiringContracts?.length || 0" type="warning" />
            </div>
          </template>
          <el-table :data="warnings.expiringContracts" max-height="200" size="small" v-if="warnings.expiringContracts?.length">
            <el-table-column label="合同" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">{{ row.contractName || row.contractNo || '-' }}</template>
            </el-table-column>
            <el-table-column prop="customerName" label="客户" width="100" show-overflow-tooltip />
            <el-table-column label="剩余天数" width="80">
              <template #default="{ row }">
                <el-tag :type="(row.remainingDays ?? row.daysToExpire ?? 0) <= 7 ? 'danger' : 'warning'" size="small">
                  {{ row.remainingDays ?? row.daysToExpire ?? 0 }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="contractAmount" label="金额" width="100">
              <template #default="{ row }">¥{{ formatAmount(row.contractAmount) }}</template>
            </el-table-column>
          </el-table>
          <el-empty description="暂无即将到期合同" :image-size="60" v-else />
        </el-card>
      </el-col>

      <!-- 逾期收款 -->
      <el-col :span="8">
        <el-card shadow="never" class="warning-card">
          <template #header>
            <div class="card-header warning-header">
              <el-icon class="warning-icon danger"><CircleCloseFilled /></el-icon>
              <span>逾期收款</span>
              <el-badge :value="warnings.overduePayments?.length || 0" type="danger" />
            </div>
          </template>
          <el-table :data="warnings.overduePayments" max-height="200" size="small" v-if="warnings.overduePayments?.length">
            <el-table-column label="合同" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">{{ row.contractName || row.contractNo || '-' }}</template>
            </el-table-column>
            <el-table-column prop="customerName" label="客户" width="100" show-overflow-tooltip />
            <el-table-column prop="overdueDays" label="逾期天数" width="80">
              <template #default="{ row }">
                <el-tag type="danger" size="small">{{ row.overdueDays }}天</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="待收金额" width="100">
              <template #default="{ row }">
                ¥{{ formatAmount(row.overdueAmount ?? ((row.planAmount || 0) - (row.receivedAmount || 0))) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty description="暂无逾期收款" :image-size="60" v-else />
        </el-card>
      </el-col>

      <!-- 流失预警客户 -->
      <el-col :span="8">
        <el-card shadow="never" class="warning-card">
          <template #header>
            <div class="card-header warning-header">
              <el-icon class="warning-icon"><UserFilled /></el-icon>
              <span>流失预警客户</span>
              <el-badge :value="warnings.churnCustomers?.length || 0" type="info" />
            </div>
          </template>
          <el-table :data="warnings.churnCustomers" max-height="200" size="small" v-if="warnings.churnCustomers?.length">
            <el-table-column prop="customerName" label="客户名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="ownerName" label="负责人" width="80" show-overflow-tooltip />
            <el-table-column label="未跟进" width="80">
              <template #default="{ row }">
                <el-tag :type="getWarningTagType(row.warningLevel)" size="small">
                  {{ row.noFollowDays ?? row.daysSinceLastFollow ?? 0 }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="累计金额" width="100">
              <template #default="{ row }">
                ¥{{ formatAmount(row.contractAmount ?? row.totalContractAmount ?? 0) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty description="暂无流失预警" :image-size="60" v-else />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive, nextTick } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/crm/analysis'
import type {
  DashboardVO,
  FunnelVO,
  TrendVO,
  CustomerDistributionVO,
  ExpiringContractVO,
  OverduePaymentVO,
  ChurnWarningVO
} from '@/types/crm/analysis'

const dashboardData = reactive<DashboardVO>({
  newCustomerCount: 0,
  dealAmount: 0,
  receivedAmount: 0,
  pendingFollowCount: 0,
  expiringContractCount: 0,
  overduePaymentCount: 0,
  churnWarningCount: 0
})

const warnings = reactive<{
  expiringContracts: ExpiringContractVO[]
  overduePayments: OverduePaymentVO[]
  churnCustomers: ChurnWarningVO[]
}>({
  expiringContracts: [],
  overduePayments: [],
  churnCustomers: []
})

const funnelData = ref<FunnelVO[]>([])
const trendData = ref<TrendVO[]>([])
const industryData = ref<CustomerDistributionVO[]>([])
const provinceData = ref<CustomerDistributionVO[]>([])

const paymentAmount = computed(() =>
  Number(dashboardData.receivedAmount ?? dashboardData.paymentAmount ?? 0)
)

const funnelChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const industryChartRef = ref<HTMLElement>()
const provinceChartRef = ref<HTMLElement>()
let funnelChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null
let industryChart: echarts.ECharts | null = null
let provinceChart: echarts.ECharts | null = null

const formatAmount = (amount?: number | string | null) => {
  const value = Number(amount || 0)
  if (!value) return '0'
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + '万'
  }
  return value.toFixed(0)
}

const getWarningTagType = (level?: number | string) => {
  if (typeof level === 'string') {
    const map: Record<string, string> = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
    return map[level.toUpperCase()] || 'info'
  }
  const types: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'info' }
  return types[level || 0] || 'info'
}

const getFunnelAmount = (item: FunnelVO) => Number(item.opportunityAmount ?? item.amount ?? 0)

const getTrendPayment = (item: TrendVO) => Number(item.receivedAmount ?? item.paymentAmount ?? 0)

const loadDashboard = async () => {
  try {
    const data = await analysisApi.getDashboard()
    Object.assign(dashboardData, data)

    funnelData.value = data.salesFunnel || []
    trendData.value = data.salesTrend || []
    industryData.value = data.industryDistribution || []
    provinceData.value = data.regionDistribution || []

    // 汇总接口已带预警列表时优先使用，减少对 warnings 的依赖
    if (data.expiringContracts?.length) {
      warnings.expiringContracts = data.expiringContracts
    }
    if (data.overduePayments?.length) {
      warnings.overduePayments = data.overduePayments
    }

    await nextTick()
    renderFunnelChart()
    renderTrendChart()
    renderIndustryChart()
    renderProvinceChart()
  } catch (e) {
    console.error('加载仪表盘数据失败', e)
  }
}

const loadWarnings = async () => {
  try {
    const data = await analysisApi.getWarnings()
    warnings.expiringContracts = data.expiringContracts || warnings.expiringContracts || []
    warnings.overduePayments = data.overduePayments || warnings.overduePayments || []
    warnings.churnCustomers = data.churnCustomers || []
  } catch (e) {
    console.error('加载预警数据失败', e)
  }
}

const setEmptyChart = (chart: echarts.ECharts | null) => {
  chart?.clear()
  chart?.setOption({
    title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
  })
}

const renderFunnelChart = () => {
  if (!funnelChartRef.value) return
  if (!funnelChart) {
    funnelChart = echarts.init(funnelChartRef.value)
  }
  if (!funnelData.value.length) {
    setEmptyChart(funnelChart)
    return
  }
  funnelChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        return `${params.name}<br/>商机数: ${params.value}个<br/>金额: ¥${formatAmount(getFunnelAmount(funnelData.value[params.dataIndex]))}`
      }
    },
    series: [{
      type: 'funnel',
      left: '10%',
      top: '10%',
      bottom: '10%',
      width: '80%',
      min: 0,
      max: Math.max(...funnelData.value.map(i => Number(i.opportunityCount || 0)), 1),
      minSize: '20%',
      maxSize: '100%',
      sort: 'none',
      gap: 2,
      label: {
        show: true,
        position: 'inside',
        formatter: '{b}',
        fontSize: 12
      },
      itemStyle: {
        borderWidth: 0
      },
      data: funnelData.value.map((item, index) => ({
        name: item.stageName,
        value: item.opportunityCount,
        itemStyle: {
          color: getFunnelColor(index)
        }
      }))
    }]
  }, true)
}

const getFunnelColor = (index: number) => {
  const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']
  return colors[index % colors.length]
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  if (!trendData.value.length) {
    setEmptyChart(trendChart)
    return
  }
  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['成交金额', '收款金额'],
      top: 0,
      textStyle: { fontSize: 10 }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '25%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.value.map(item => item.period),
      axisLabel: { fontSize: 10, rotate: 45 }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value: number) => formatAmount(value)
      }
    },
    series: [
      {
        name: '成交金额',
        type: 'line',
        smooth: true,
        data: trendData.value.map(item => item.dealAmount),
        itemStyle: { color: '#5470c6' },
        areaStyle: { opacity: 0.1 }
      },
      {
        name: '收款金额',
        type: 'line',
        smooth: true,
        data: trendData.value.map(item => getTrendPayment(item)),
        itemStyle: { color: '#91cc75' },
        areaStyle: { opacity: 0.1 }
      }
    ]
  }, true)
}

const renderIndustryChart = () => {
  if (!industryChartRef.value) return
  if (!industryChart) {
    industryChart = echarts.init(industryChartRef.value)
  }
  if (!industryData.value.length) {
    setEmptyChart(industryChart)
    return
  }
  industryChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}个 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { fontSize: 10 }
    },
    series: [{
      type: 'pie',
      radius: ['30%', '60%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 1
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 12, fontWeight: 'bold' }
      },
      data: industryData.value.map(item => ({
        name: item.value,
        value: item.customerCount
      }))
    }]
  }, true)
}

const renderProvinceChart = () => {
  if (!provinceChartRef.value) return
  if (!provinceChart) {
    provinceChart = echarts.init(provinceChartRef.value)
  }
  if (!provinceData.value.length) {
    setEmptyChart(provinceChart)
    return
  }
  const top10 = provinceData.value.slice(0, 10)
  provinceChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: top10.map(item => item.value),
      axisLabel: { fontSize: 10, rotate: 45 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 10 }
    },
    series: [{
      type: 'bar',
      data: top10.map(item => item.customerCount),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      barWidth: '50%'
    }]
  }, true)
}

const handleResize = () => {
  funnelChart?.resize()
  trendChart?.resize()
  industryChart?.resize()
  provinceChart?.resize()
}

const loadAllData = async () => {
  await Promise.all([
    loadDashboard(),
    loadWarnings()
  ])
}

onMounted(() => {
  loadAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  funnelChart?.dispose()
  trendChart?.dispose()
  industryChart?.dispose()
  provinceChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-card .stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stat-icon.customer { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.deal { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.payment { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.opportunity { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.follow { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.pending { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

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

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 300px;
}

.card-header {
  font-size: 14px;
  font-weight: 500;
}

.chart-container {
  height: 240px;
}

.warning-row .warning-card {
  height: 280px;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.warning-icon {
  color: #e6a23c;
  font-size: 16px;
}

.warning-icon.danger {
  color: #f56c6c;
}

:deep(.el-card__header) {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-badge) {
  margin-left: auto;
}
</style>
