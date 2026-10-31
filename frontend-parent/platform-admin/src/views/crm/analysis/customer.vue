<template>
  <div class="customer-analysis-container">
    <!-- 搜索条件 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="客户类型">
          <el-select v-model="queryParams.customerType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="企业客户" :value="1" />
            <el-option label="个人客户" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industry" placeholder="请选择行业" clearable filterable style="width: 150px">
            <el-option v-for="item in industryDict" :key="item.itemValue" :label="item.itemLabel" :value="item.itemValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="queryParams.province" placeholder="省份" clearable style="width: 120px" />
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

    <!-- 分布分析图表 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户行业分布</span>
            </div>
          </template>
          <div ref="industryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户地区分布</span>
            </div>
          </template>
          <div ref="provinceChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户等级分布</span>
            </div>
          </template>
          <div ref="levelChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 客户价值分析 -->
    <el-card shadow="never" class="value-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>客户价值分析（TOP 20）</span>
          <el-tag type="info">按累计合同金额排序</el-tag>
        </div>
      </template>
      <el-table :data="customerValue" max-height="300" v-loading="valueLoading">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="负责人" width="90" />
        <el-table-column label="累计金额" width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatAmount(row.totalContractAmount ?? row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="contractCount" label="合同数" width="80" />
        <el-table-column label="已收款" width="120">
          <template #default="{ row }">
            ¥{{ formatAmount(row.totalReceivedAmount ?? row.receivedAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="待收款" width="120">
          <template #default="{ row }">
            <el-tag
              type="warning"
              size="small"
              v-if="getUnreceived(row) > 0"
            >
              ¥{{ formatAmount(getUnreceived(row)) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="最近成交" width="160">
          <template #default="{ row }">
            {{ formatDate(row.lastDealTime || row.lastDealDate) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 客户活跃度与流失预警 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never" class="activity-card">
          <template #header>
            <div class="card-header">
              <span>客户活跃度分析</span>
              <el-tooltip placement="top">
                <template #content>
                  <div>只统计客户跟进记录：</div>
                  <div>高活跃 ≤7天；中活跃 8-30天；低活跃 31-90天</div>
                  <div>不活跃：有跟进但 &gt;90天；未跟进：零跟进记录</div>
                </template>
                <el-tag type="info" size="small" effect="plain">评判标准</el-tag>
              </el-tooltip>
            </div>
          </template>
          <div ref="activityChartRef" class="chart-container" v-loading="activityLoading"></div>
          <el-table :data="customerActivity" max-height="200" style="margin-top: 8px">
            <el-table-column label="活跃度" width="100">
              <template #default="{ row }">
                <el-tag :type="getActivityTagType(row.activityLevel)" size="small">
                  {{ getActivityLabel(row.activityLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="customerCount" label="客户数" width="80" />
            <el-table-column label="占比" width="80">
              <template #default="{ row }">{{ row.percentage || 0 }}%</template>
            </el-table-column>
            <el-table-column label="平均跟进次数" width="110">
              <template #default="{ row }">{{ row.avgFollowCount ?? '-' }}</template>
            </el-table-column>
            <el-table-column label="平均距今天数" min-width="110">
              <template #default="{ row }">
                <span v-if="row.avgDaysSinceLastFollow != null">{{ row.avgDaysSinceLastFollow }}天</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="warning-card">
          <template #header>
            <div class="card-header warning-header">
              <el-icon class="warning-icon"><Warning /></el-icon>
              <span>客户流失预警</span>
              <el-badge :value="churnTotal" type="danger" />
              <el-tag type="info" size="small" effect="plain">未跟进≥30天</el-tag>
              <el-tag v-if="churnTotal > churnWarning.length" type="warning" size="small" effect="plain">
                展示前{{ churnWarning.length }}条
              </el-tag>
            </div>
          </template>
          <el-table :data="churnWarning" max-height="400" v-loading="churnLoading">
            <el-table-column prop="customerName" label="客户名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="ownerName" label="负责人" width="80" />
            <el-table-column label="未跟进天数" width="100">
              <template #default="{ row }">
                <el-tag :type="getWarningTagType(row.warningLevel)" size="small">
                  {{ row.daysSinceLastFollow ?? row.noFollowDays ?? 0 }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="累计金额" width="100">
              <template #default="{ row }">
                ¥{{ formatAmount(row.totalContractAmount ?? row.contractAmount ?? 0) }}
              </template>
            </el-table-column>
            <el-table-column prop="warningLevel" label="预警等级" width="80">
              <template #default="{ row }">
                <el-tag :type="getWarningTagType(row.warningLevel)" effect="dark" size="small">
                  {{ getWarningLabel(row.warningLevel) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/crm/analysis'
import { useDicts, getDictLabelFromData } from '@/composables/useDict'
import type {
  AnalysisQueryDTO,
  CustomerDistributionVO,
  CustomerValueVO,
  CustomerActivityVO,
  ChurnWarningVO
} from '@/types/crm/analysis'

const requiredDicts = ['crm_industry', 'crm_customer_level']
const { dataMap } = useDicts(requiredDicts)
const industryDict = computed(() => dataMap.value['crm_industry'] || [])

const queryParams = reactive<AnalysisQueryDTO>({
  customerType: undefined,
  industry: '',
  province: ''
})

const industryDistribution = ref<CustomerDistributionVO[]>([])
const provinceDistribution = ref<CustomerDistributionVO[]>([])
const levelDistribution = ref<CustomerDistributionVO[]>([])

const customerValue = ref<CustomerValueVO[]>([])
const valueLoading = ref(false)

const customerActivity = ref<CustomerActivityVO[]>([])
const activityLoading = ref(false)

const churnWarning = ref<ChurnWarningVO[]>([])
const churnTotal = ref(0)
const churnLoading = ref(false)

const industryChartRef = ref<HTMLElement>()
const provinceChartRef = ref<HTMLElement>()
const levelChartRef = ref<HTMLElement>()
const activityChartRef = ref<HTMLElement>()
let industryChart: echarts.ECharts | null = null
let provinceChart: echarts.ECharts | null = null
let levelChart: echarts.ECharts | null = null
let activityChart: echarts.ECharts | null = null

const formatAmount = (amount?: number) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return Number(amount).toFixed(0)
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const getUnreceived = (row: CustomerValueVO) => {
  if (row.unreceivedAmount != null) return Number(row.unreceivedAmount)
  const total = Number(row.totalContractAmount ?? row.totalAmount ?? 0)
  const received = Number(row.totalReceivedAmount ?? row.receivedAmount ?? 0)
  return Math.max(total - received, 0)
}

const ensureChart = (el: HTMLElement | undefined, chart: echarts.ECharts | null) => {
  if (!el) return null
  chart?.dispose()
  return echarts.init(el)
}

const loadDistribution = async () => {
  try {
    const [industry, region, level] = await Promise.all([
      analysisApi.getCustomerDistribution({ ...queryParams, dimension: 'industry' }),
      analysisApi.getCustomerDistribution({ ...queryParams, dimension: 'region' }),
      analysisApi.getCustomerDistribution({ ...queryParams, dimension: 'level' })
    ])
    industryDistribution.value = industry || []
    provinceDistribution.value = region || []
    levelDistribution.value = level || []
  } catch (e) {
    console.error('加载分布数据失败', e)
    industryDistribution.value = []
    provinceDistribution.value = []
    levelDistribution.value = []
  } finally {
    renderCharts()
  }
}

const loadCustomerValue = async () => {
  valueLoading.value = true
  try {
    customerValue.value = ((await analysisApi.getCustomerValue(queryParams)) || []).slice(0, 20)
  } catch (e) {
    console.error('加载客户价值失败', e)
    customerValue.value = []
  } finally {
    valueLoading.value = false
  }
}

const loadCustomerActivity = async () => {
  activityLoading.value = true
  try {
    customerActivity.value = (await analysisApi.getCustomerActivity(queryParams)) || []
  } catch (e) {
    console.error('加载客户活跃度失败', e)
    customerActivity.value = []
  } finally {
    activityLoading.value = false
    renderActivityChart()
  }
}

const loadChurnWarning = async () => {
  churnLoading.value = true
  try {
    const result = await analysisApi.getChurnWarning({ ...queryParams, page: 1, size: 50 })
    churnWarning.value = result?.list || []
    churnTotal.value = Number(result?.total || 0)
  } catch (e) {
    console.error('加载流失预警失败', e)
    churnWarning.value = []
    churnTotal.value = 0
  } finally {
    churnLoading.value = false
  }
}

const getLevelColorByName = (name: string) => {
  const colors: Record<string, string> = { '普通': '#909399', '重要': '#e6a23c', 'VIP': '#f56c6c' }
  return colors[name] || '#409eff'
}

const setEmptyChart = (chart: echarts.ECharts | null) => {
  chart?.clear()
  chart?.setOption({
    title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
  })
}

const renderCharts = () => {
  renderIndustryChart()
  renderProvinceChart()
  renderLevelChart()
}

const renderIndustryChart = () => {
  if (!industryChartRef.value) return
  industryChart = ensureChart(industryChartRef.value, industryChart)
  if (!industryChart) return
  if (!industryDistribution.value.length) {
    setEmptyChart(industryChart)
    return
  }

  industryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 1 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: industryDistribution.value.slice(0, 8).map(item => ({
        name: getDictLabelFromData(industryDict.value, item.value) || item.value || '未知',
        value: item.customerCount
      }))
    }]
  }, true)
}

const renderProvinceChart = () => {
  if (!provinceChartRef.value) return
  provinceChart = ensureChart(provinceChartRef.value, provinceChart)
  if (!provinceChart) return
  if (!provinceDistribution.value.length) {
    setEmptyChart(provinceChart)
    return
  }

  const top10 = provinceDistribution.value.slice(0, 10)
  provinceChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: top10.map(item => item.value),
      axisLabel: { rotate: 45, fontSize: 11 }
    },
    yAxis: { type: 'value', minInterval: 1, axisLabel: { fontSize: 11 } },
    series: [{
      type: 'bar',
      data: top10.map(item => item.customerCount),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 1, color: '#188df0' }
        ])
      },
      barWidth: '60%'
    }]
  }, true)
}

const renderLevelChart = () => {
  if (!levelChartRef.value) return
  levelChart = ensureChart(levelChartRef.value, levelChart)
  if (!levelChart) return
  if (!levelDistribution.value.length) {
    setEmptyChart(levelChart)
    return
  }

  levelChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 1 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: levelDistribution.value.map(item => ({
        name: item.value,
        value: item.customerCount,
        itemStyle: { color: getLevelColorByName(item.value) }
      }))
    }]
  }, true)
}

const renderActivityChart = () => {
  if (!activityChartRef.value) return
  activityChart = ensureChart(activityChartRef.value, activityChart)
  if (!activityChart) return
  if (!customerActivity.value.length) {
    setEmptyChart(activityChart)
    return
  }

  activityChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { bottom: 0, textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '45%'],
      data: customerActivity.value.map(item => ({
        name: getActivityLabel(item.activityLevel),
        value: item.customerCount || 0,
        itemStyle: { color: getActivityColor(item.activityLevel) }
      }))
    }]
  }, true)
}

const getActivityTagType = (level?: string) => {
  const map: Record<string, string> = {
    HIGH: 'success',
    MEDIUM: 'warning',
    LOW: 'info',
    INACTIVE: 'danger',
    NEVER: 'info'
  }
  return map[(level || '').toUpperCase()] || 'info'
}

const getActivityLabel = (level?: string) => {
  const map: Record<string, string> = {
    HIGH: '高活跃',
    MEDIUM: '中活跃',
    LOW: '低活跃',
    INACTIVE: '不活跃',
    NEVER: '未跟进'
  }
  return map[(level || '').toUpperCase()] || level || '未知'
}

const getActivityColor = (level?: string) => {
  const map: Record<string, string> = {
    HIGH: '#67c23a',
    MEDIUM: '#e6a23c',
    LOW: '#909399',
    INACTIVE: '#f56c6c',
    NEVER: '#c0c4cc'
  }
  return map[(level || '').toUpperCase()] || '#409eff'
}

const getWarningTagType = (level?: number | string) => {
  if (typeof level === 'string') {
    const map: Record<string, string> = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
    return map[level.toUpperCase()] || 'info'
  }
  const types: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'info' }
  return types[level || 0] || 'info'
}

const getWarningLabel = (level?: number | string) => {
  if (typeof level === 'string') {
    const map: Record<string, string> = { HIGH: '高危', MEDIUM: '风险', LOW: '关注' }
    return map[level.toUpperCase()] || level
  }
  const labels: Record<number, string> = { 1: '高危', 2: '风险', 3: '关注' }
  return labels[level || 0] || '未知'
}

const handleSearch = () => {
  loadDistribution()
  loadCustomerValue()
  loadCustomerActivity()
  loadChurnWarning()
}

const handleReset = () => {
  queryParams.customerType = undefined
  queryParams.industry = ''
  queryParams.province = ''
  handleSearch()
}

const handleResize = () => {
  industryChart?.resize()
  provinceChart?.resize()
  levelChart?.resize()
  activityChart?.resize()
}

onMounted(() => {
  handleSearch()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  industryChart?.dispose()
  provinceChart?.dispose()
  levelChart?.dispose()
  activityChart?.dispose()
})
</script>

<style scoped>
.customer-analysis-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.chart-card {
  height: 300px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-container {
  height: 220px;
}

.activity-card {
  min-height: 460px;
}

.warning-header {
  color: #f56c6c;
}

.warning-icon {
  color: #f56c6c;
  font-size: 18px;
}

.amount-text {
  color: #f56c6c;
  font-weight: 500;
}

:deep(.el-card__header) {
  padding: 12px 16px;
}

:deep(.el-badge) {
  margin-left: auto;
}
</style>
