<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ statistics.totalStations || 0 }}</div>
          <div class="stat-label">站点数量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">¥{{ statistics.totalRevenue || 0 }}</div>
          <div class="stat-label">总收入</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>近7天订单趋势</span>
          </template>
          <div ref="lineChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>充电类型分布</span>
          </template>
          <div ref="pieChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getStatistics } from '@/api/order'
const lineChartRef = ref(null)
const pieChartRef = ref(null)
const statistics = ref({})
let lineChart = null
let pieChart = null
const initLineChart = (data) => {
  lineChart = echarts.init(lineChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '收入(元)'] },
    xAxis: {
      type: 'category',
      data: data.dates || []
    },
    yAxis: [
      { type: 'value', name: '订单数' },
      { type: 'value', name: '收入(元)' }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        data: data.orderCounts || [],
        smooth: true
      },
      {
        name: '收入(元)',
        type: 'line',
        yAxisIndex: 1,
        data: data.revenues || [],
        smooth: true,
        itemStyle: { color: '#67c23a' }
      }
    ]
  }
  lineChart.setOption(option)
}
const initPieChart = (data) => {
  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '充电类型',
      type: 'pie',
      radius: '50%',
      data: [
        { value: data.dcFast || 0, name: '直流快充' },
        { value: data.acSlow || 0, name: '交流慢充' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  pieChart.setOption(option)
}
onMounted(async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data
    initLineChart(res.data || {})
    initPieChart(res.data || {})
  } catch (e) { /* ignore */ }
  window.addEventListener('resize', handleResize)
})
const handleResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  pieChart?.dispose()
})
</script>