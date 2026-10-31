<template>
  <div class="opportunity-analysis-container">
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
        <el-form-item>
          <el-tag type="info" effect="plain" size="small">时间：漏斗/推进按创建或成交日；已成交金额按成交日</el-tag>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 销售漏斗与转化率 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never" class="funnel-card">
          <template #header>
            <div class="card-header">
              <span>销售漏斗</span>
            </div>
          </template>
          <div ref="funnelChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="conversion-card">
          <template #header>
            <div class="card-header">
              <span>阶段推进率</span>
            </div>
          </template>
          <div ref="conversionChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="summary-card">
          <template #header>
            <div class="card-header">
              <span>漏斗统计</span>
            </div>
          </template>
          <div class="summary-content">
            <div class="summary-item">
              <div class="summary-label">总商机数</div>
              <div class="summary-value">{{ funnelSummary.totalCount }}</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">总商机金额</div>
              <div class="summary-value highlight">¥{{ formatAmount(funnelSummary.totalAmount) }}</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">成交占比</div>
              <div class="summary-value">{{ funnelSummary.overallRate || 0 }}%</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">加权预测成交额</div>
              <div class="summary-value success">¥{{ formatAmount(prediction.predictedAmount) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成交预测汇总 -->
    <el-card shadow="never" class="prediction-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>成交预测</span>
          <el-tag type="success">{{ prediction.period || '管道预测' }}</el-tag>
          <el-tag type="info" effect="plain">高/中/低为管道期望金额；预测额=期望×概率</el-tag>
        </div>
      </template>
      <el-row :gutter="20" class="prediction-stats">
        <el-col :span="4">
          <div class="pred-item">
            <div class="pred-label">高概率管道 (≥70%)</div>
            <div class="pred-value">¥{{ formatAmount(prediction.highProbabilityAmount) }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="pred-item">
            <div class="pred-label">中概率管道 (30-69%)</div>
            <div class="pred-value">¥{{ formatAmount(prediction.mediumProbabilityAmount) }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="pred-item">
            <div class="pred-label">低概率管道 (&lt;30%)</div>
            <div class="pred-value">¥{{ formatAmount(prediction.lowProbabilityAmount) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="pred-item">
            <div class="pred-label">加权预测 / 在途商机</div>
            <div class="pred-value success">
              ¥{{ formatAmount(prediction.predictedAmount) }}
              <span class="pred-sub">/ {{ prediction.opportunityCount || 0 }}个</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="pred-item">
            <div class="pred-label">区间已成交金额</div>
            <div class="pred-value">¥{{ formatAmount(prediction.closedAmount) }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 阶段推进详情表 -->
    <el-card shadow="never" class="conversion-detail-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>阶段推进详情</span>
          <el-tag type="info" effect="plain" size="small">无流转历史时的存量近似口径</el-tag>
        </div>
      </template>
      <el-table :data="conversionData" v-loading="conversionLoading">
        <el-table-column prop="stageName" label="阶段" width="120" />
        <el-table-column prop="enterCount" label="当前阶段数" width="120" />
        <el-table-column prop="exitCount" label="后续阶段数" width="120" />
        <el-table-column prop="conversionRate" label="推进率" min-width="180">
          <template #default="{ row }">
            <el-progress
              :percentage="Number(row.conversionRate || 0)"
              :stroke-width="10"
              :color="getConversionColor(Number(row.conversionRate || 0))"
            />
          </template>
        </el-table-column>
        <el-table-column prop="avgStayDays" label="平均停留天数" width="120">
          <template #default="{ row }">
            {{ row.avgStayDays != null ? row.avgStayDays + '天' : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/crm/analysis'
import { useAnalysisFilters } from '@/composables/useAnalysisFilters'
import type { AnalysisQueryDTO, FunnelVO, ConversionVO, PredictionVO } from '@/types/crm/analysis'

const pad = (n: number) => String(n).padStart(2, '0')
const formatYmd = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
const defaultEnd = new Date()
const defaultStart = new Date(defaultEnd.getFullYear(), 0, 1)

const queryParams = reactive<AnalysisQueryDTO>({
  startDate: formatYmd(defaultStart),
  endDate: formatYmd(defaultEnd),
  deptId: undefined,
  userId: undefined
})

const { deptOptions, loadFilterOptions, filterUsersByDept } = useAnalysisFilters()
const filteredUsers = computed(() => filterUsersByDept(queryParams.deptId))

const funnelData = ref<FunnelVO[]>([])
const conversionData = ref<ConversionVO[]>([])
const prediction = reactive<PredictionVO>({
  predictedAmount: 0,
  highProbabilityAmount: 0,
  mediumProbabilityAmount: 0,
  lowProbabilityAmount: 0,
  opportunityCount: 0,
  closedAmount: 0,
  period: '管道预测'
})

const getFunnelAmount = (item: FunnelVO) => Number(item.opportunityAmount ?? item.amount ?? 0)

const funnelSummary = computed(() => {
  const total = funnelData.value.reduce((acc, item) => {
    acc.totalCount += Number(item.opportunityCount || 0)
    acc.totalAmount += getFunnelAmount(item)
    return acc
  }, { totalCount: 0, totalAmount: 0 })

  // 成交占比 = 成交阶段商机 / 漏斗总商机（存量快照，非历史转化）
  const winStage = funnelData.value.find(item => Number(item.probability) === 100)
  const overallRate = total.totalCount > 0 && winStage
    ? Math.round((Number(winStage.opportunityCount || 0) / total.totalCount) * 100)
    : 0

  return {
    totalCount: total.totalCount,
    totalAmount: total.totalAmount,
    overallRate
  }
})

const conversionLoading = ref(false)
const predictionLoading = ref(false)

const funnelChartRef = ref<HTMLElement>()
const conversionChartRef = ref<HTMLElement>()
let funnelChart: echarts.ECharts | null = null
let conversionChart: echarts.ECharts | null = null

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

const ensureChart = (el: HTMLElement | undefined, chart: echarts.ECharts | null) => {
  if (!el) return null
  chart?.dispose()
  return echarts.init(el)
}

const loadFunnel = async () => {
  try {
    const data = (await analysisApi.getFunnel(queryParams)) || []
    funnelData.value = data.filter((item, index, arr) =>
      arr.findIndex(i => i.stageId === item.stageId) === index
    )
  } catch (e) {
    console.error('加载漏斗数据失败', e)
    funnelData.value = []
  } finally {
    renderFunnelChart()
  }
}

const loadConversion = async () => {
  conversionLoading.value = true
  try {
    const data = (await analysisApi.getConversion(queryParams)) || []
    conversionData.value = data.filter((item, index, arr) =>
      arr.findIndex(i => i.stageId === item.stageId) === index
    )
  } catch (e) {
    console.error('加载转化率数据失败', e)
    conversionData.value = []
  } finally {
    conversionLoading.value = false
    renderConversionChart()
  }
}

const loadPrediction = async () => {
  predictionLoading.value = true
  try {
    const data = await analysisApi.getPrediction(queryParams)
    prediction.predictedAmount = Number(data?.predictedAmount || 0)
    prediction.highProbabilityAmount = Number(data?.highProbabilityAmount || 0)
    prediction.mediumProbabilityAmount = Number(data?.mediumProbabilityAmount || 0)
    prediction.lowProbabilityAmount = Number(data?.lowProbabilityAmount || 0)
    prediction.opportunityCount = Number(data?.opportunityCount || 0)
    prediction.closedAmount = Number(data?.closedAmount || 0)
    prediction.period = data?.period || '管道预测'
  } catch (e) {
    console.error('加载成交预测失败', e)
    prediction.predictedAmount = 0
    prediction.highProbabilityAmount = 0
    prediction.mediumProbabilityAmount = 0
    prediction.lowProbabilityAmount = 0
    prediction.opportunityCount = 0
    prediction.closedAmount = 0
    prediction.period = '管道预测'
  } finally {
    predictionLoading.value = false
  }
}

const renderFunnelChart = () => {
  if (!funnelChartRef.value) return
  funnelChart = ensureChart(funnelChartRef.value, funnelChart)
  if (!funnelChart) return

  if (!funnelData.value.length) {
    funnelChart.clear()
    funnelChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return
  }

  funnelChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = funnelData.value[params.dataIndex]
        return `${item.stageName}<br/>
                商机数: ${item.opportunityCount}个<br/>
                金额: ¥${formatAmount(getFunnelAmount(item))}<br/>
                概率: ${item.probability || 0}%`
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
        formatter: '{b}\n{c}个',
        fontSize: 12
      },
      itemStyle: { borderWidth: 0 },
      emphasis: { label: { fontSize: 14 } },
      data: funnelData.value.map(item => ({
        name: item.stageName,
        value: Number(item.opportunityCount || 0),
        itemStyle: { color: getFunnelColor(Number(item.probability || 0)) }
      }))
    }]
  }, true)
}

const getFunnelColor = (probability: number) => {
  if (probability === 100) return '#67c23a'
  if (probability >= 70) return '#5470c6'
  if (probability >= 50) return '#91cc75'
  if (probability >= 30) return '#fac858'
  if (probability >= 10) return '#ee6666'
  return '#73c0de'
}

const renderConversionChart = () => {
  if (!conversionChartRef.value) return
  conversionChart = ensureChart(conversionChartRef.value, conversionChart)
  if (!conversionChart) return

  if (!conversionData.value.length) {
    conversionChart.clear()
    conversionChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return
  }

  conversionChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['当前阶段数', '推进率'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: conversionData.value.map(item => item.stageName),
      axisLabel: { rotate: 30, fontSize: 11 }
    },
    yAxis: [
      { type: 'value', name: '商机数' },
      { type: 'value', name: '推进率', max: 100, axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '当前阶段数',
        type: 'bar',
        data: conversionData.value.map(item => item.enterCount || 0),
        itemStyle: { color: '#5470c6' },
        barWidth: '40%'
      },
      {
        name: '推进率',
        type: 'line',
        yAxisIndex: 1,
        data: conversionData.value.map(item => Number(item.conversionRate || 0)),
        itemStyle: { color: '#fac858' },
        smooth: true,
        label: {
          show: true,
          formatter: '{c}%',
          position: 'top'
        }
      }
    ]
  }, true)
}

const getConversionColor = (rate: number) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#5470c6'
  if (rate >= 40) return '#e6a23c'
  return '#f56c6c'
}

const handleSearch = () => {
  loadFunnel()
  loadConversion()
  loadPrediction()
}

const handleReset = () => {
  queryParams.startDate = formatYmd(defaultStart)
  queryParams.endDate = formatYmd(defaultEnd)
  queryParams.deptId = undefined
  queryParams.userId = undefined
  handleSearch()
}

const handleResize = () => {
  funnelChart?.resize()
  conversionChart?.resize()
}

onMounted(async () => {
  await loadFilterOptions()
  handleSearch()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  funnelChart?.dispose()
  conversionChart?.dispose()
})
</script>

<style scoped>
.opportunity-analysis-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.funnel-card, .conversion-card {
  height: 350px;
}

.summary-card {
  height: 350px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-container {
  height: 280px;
}

.summary-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.summary-label {
  color: #606266;
  font-size: 14px;
}

.summary-value {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.summary-value.highlight {
  color: #e6a23c;
}

.summary-value.success {
  color: #67c23a;
}

.prediction-card {
  min-height: 140px;
}

.prediction-stats {
  padding: 10px 0;
}

.pred-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.pred-label {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}

.pred-value {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.pred-value.success {
  color: #67c23a;
}

.pred-sub {
  font-size: 13px;
  color: #909399;
  font-weight: 400;
}

:deep(.el-card__header) {
  padding: 12px 16px;
}

:deep(.el-progress) {
  display: inline-flex;
  align-items: center;
  width: 90%;
}

:deep(.el-progress-bar) {
  flex: 1;
}
</style>
