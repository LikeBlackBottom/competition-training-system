<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent, TooltipComponent, LegendComponent, TitleComponent,
} from 'echarts/components'
import { getDashboard } from '@/api/dashboard'
import type { DashboardData } from '@/types'
import { Monitor, Clock, WarningFilled, Check, Close } from '@element-plus/icons-vue'

use([CanvasRenderer, BarChart, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const dashboard = ref<DashboardData | null>(null)

const statCards = [
  { label: '总队伍数', key: 'totalTeams' as const, icon: Monitor, accent: 'blue', sub: '所有院校' },
  { label: '今日已提交', key: 'todaySubmitted' as const, icon: Check, accent: 'mint', sub: '' },
  { label: '今日未提交', key: 'todayNotSubmitted' as const, icon: Close, accent: 'orange', sub: '需关注' },
  { label: '累计展示时长', key: 'totalHours' as const, icon: Clock, accent: 'purple', sub: '分钟' },
  { label: '待解决问题', key: 'pendingIssues' as const, icon: WarningFilled, accent: 'red', sub: '' },
]

const accentColors: Record<string, string> = {
  blue: '#6ee7ff',
  mint: '#7cffcb',
  orange: '#ffd166',
  purple: '#a78bfa',
  red: '#ff5c9e',
  pink: '#ff7adf',
}

/* Pixel-palette for charts */
const chartColors = ['#ff7adf', '#6ee7ff', '#a78bfa', '#7cffcb', '#ffd166', '#ff5c9e', '#9bf6ff', '#ff4fc3']

const chartTooltipStyle = {
  backgroundColor: 'rgba(13,15,42,0.96)',
  borderColor: 'rgba(255,122,223,0.3)',
  textStyle: { color: '#f8f7ff', fontSize: 12 },
}

const barChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      ...chartTooltipStyle,
      confine: true,
    },
    grid: { top: 10, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'category',
      data: data ? data.teamHoursRank.map((t: { name: string; hours: number }) => t.name) : [],
      axisLine: { lineStyle: { color: 'rgba(255,122,223,0.15)' } },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: '#7f8bb3', fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(255,122,223,0.06)' } },
    },
    series: [{
      type: 'bar',
      data: data ? data.teamHoursRank.map((t: { name: string; hours: number }, i: number) => ({
        value: t.hours,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: chartColors[i % chartColors.length] },
              { offset: 1, color: 'rgba(255,122,223,0.3)' },
            ],
          },
          borderRadius: [4, 4, 0, 0],
        },
      })) : [],
    }],
  }
})

const pieChartOption = computed(() => {
  const data = dashboard.value
  const skillCount = data?.skillDist?.length || 0
  const useBottomLegend = skillCount > 5
  return {
    tooltip: {
      ...chartTooltipStyle,
      trigger: 'item',
      confine: true,
    },
    legend: useBottomLegend ? {
      type: 'scroll',
      orient: 'horizontal',
      bottom: 4,
      left: 'center',
      itemWidth: 14,
      itemHeight: 8,
      textStyle: {
        color: '#b8c3e8',
        fontSize: 11,
        width: 100,
        overflow: 'truncate',
      },
      pageTextStyle: { color: '#b8c3e8' },
      pageIconColor: '#ff7adf',
      pageIconInactiveColor: '#5a6080',
    } : {
      type: 'scroll',
      orient: 'vertical',
      right: 8,
      top: 'middle',
      itemWidth: 14,
      itemHeight: 8,
      textStyle: {
        color: '#b8c3e8',
        fontSize: 11,
        width: 120,
        overflow: 'truncate',
      },
      pageTextStyle: { color: '#b8c3e8' },
      pageIconColor: '#ff7adf',
      pageIconInactiveColor: '#5a6080',
    },
    color: chartColors,
    series: [{
      type: 'pie',
      radius: ['46%', '68%'],
      center: useBottomLegend ? ['50%', '45%'] : ['34%', '52%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderColor: 'rgba(9,11,31,0.8)',
        borderWidth: 2,
      },
      label: { show: false },
      labelLine: { show: false },
      emphasis: {
        scaleSize: 6,
        itemStyle: {
          shadowBlur: 20,
          shadowColor: 'rgba(255,122,223,0.4)',
        },
      },
      data: data ? data.skillDist : [],
    }],
  }
})

const lineChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      ...chartTooltipStyle,
      confine: true,
    },
    grid: { top: 20, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'category',
      data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.date) : [],
      axisLine: { lineStyle: { color: 'rgba(255,122,223,0.15)' } },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(255,122,223,0.06)' } },
    },
    series: [
      {
        type: 'line',
        name: '展示时长(分钟)',
        data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.hours) : [],
        smooth: true,
        lineStyle: { color: '#ff7adf', width: 2 },
        itemStyle: { color: '#ff7adf' },
        symbolSize: 6,
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(255,122,223,0.15)' },
              { offset: 1, color: 'rgba(255,122,223,0)' },
            ],
          },
        },
      },
      {
        type: 'line',
        name: '记录数',
        data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.records) : [],
        smooth: true,
        lineStyle: { color: '#6ee7ff', width: 2 },
        itemStyle: { color: '#6ee7ff' },
        symbolSize: 6,
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(110,231,255,0.12)' },
              { offset: 1, color: 'rgba(110,231,255,0)' },
            ],
          },
        },
      },
    ],
  }
})

const issueChartOption = computed(() => {
  const data = dashboard.value
  const issueColors = ['#ff5c9e', '#ffd166', '#a78bfa', '#6ee7ff']
  return {
    tooltip: {
      ...chartTooltipStyle,
      confine: true,
    },
    grid: { top: 10, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(255,92,158,0.06)' } },
    },
    yAxis: {
      type: 'category',
      data: data ? data.issueSeverityDist.map((d: { name: string; count: number; color: string }) => d.name) : [],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#7f8bb3', fontSize: 12 },
    },
    series: [{
      type: 'bar',
      data: data ? data.issueSeverityDist.map((d: { name: string; count: number; color: string }, i: number) => ({
        value: d.count,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: issueColors[i % issueColors.length] },
              { offset: 1, color: 'rgba(255,122,223,0.3)' },
            ],
          },
          borderRadius: [0, 4, 4, 0],
        },
      })) : [],
    }],
  }
})

onMounted(async () => {
  dashboard.value = await getDashboard()
})
</script>

<template>
  <div class="dashboard" v-if="dashboard">
    <div class="page-title">
      <h1>数据指挥舱</h1>
      <p>// TRAINING DATA COMMAND CENTER — REAL-TIME OVERVIEW</p>
    </div>

    <div class="stat-grid">
      <div
        v-for="card in statCards"
        :key="card.key"
        class="stat-card"
        :style="{ borderLeftColor: accentColors[card.accent] }"
      >
        <div class="stat-info">
          <span class="stat-label">{{ card.label }}</span>
          <span class="stat-value" :style="{ color: accentColors[card.accent] }">
            {{ dashboard[card.key] }}
          </span>
          <span v-if="card.sub" class="stat-sub">{{ card.sub }}</span>
        </div>
        <div class="stat-icon" :style="{ color: accentColors[card.accent] }">
          <el-icon :size="24"><component :is="card.icon" /></el-icon>
        </div>
      </div>
    </div>

    <div class="chart-row">
      <div class="cyber-card chart-card chart-wide">
        <div class="chart-header">
          <h3>队伍展示时长排行</h3>
          <span>TEAM DISPLAY MINUTES RANKING</span>
        </div>
        <VChart :option="barChartOption" autoresize style="height:240px" />
      </div>
      <div class="cyber-card chart-card chart-narrow">
        <div class="chart-header">
          <h3>技能点展示时长分布</h3>
          <span>SKILL DISPLAY MINUTES DISTRIBUTION</span>
        </div>
        <VChart :option="pieChartOption" autoresize style="height:280px" />
      </div>
    </div>

    <div class="chart-row">
      <div class="cyber-card chart-card chart-wide">
        <div class="chart-header">
          <h3>每日训练趋势</h3>
          <span>DAILY TRAINING TREND (7 DAYS)</span>
        </div>
        <VChart :option="lineChartOption" autoresize style="height:220px" />
      </div>
      <div class="cyber-card chart-card chart-narrow">
        <div class="chart-header">
          <h3>问题严重程度分布</h3>
          <span>ISSUE SEVERITY DISTRIBUTION</span>
        </div>
        <VChart :option="issueChartOption" autoresize style="height:220px" />
      </div>
    </div>

    <div class="cyber-card recent-card">
      <div class="section-title">
        <h2>最近提交记录</h2>
        <span>RECENT SUBMISSIONS</span>
        <div class="title-divider" />
      </div>
      <div class="table-wrapper">
        <el-table
          :data="dashboard.recentRecords"
          class="cyber-table"
          style="width: 100%"
        >
          <el-table-column prop="teamName" label="队伍" min-width="10%" show-overflow-tooltip />
          <el-table-column prop="memberName" label="队员" min-width="8%" show-overflow-tooltip />
          <el-table-column prop="moduleName" label="模块" min-width="11%">
            <template #default="{ row }">
              <span class="neon-tag tag-blue">{{ row.moduleName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="skillName" label="技能点" min-width="16%" show-overflow-tooltip />
          <el-table-column prop="hours" label="展示时长" min-width="10%">
            <template #header>
              <span>展示时长</span><br><span class="sub-header">分钟</span>
            </template>
            <template #default="{ row }">
              <span class="value-highlight">{{ row.hours }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="needHelp" label="需协助" min-width="8%">
            <template #default="{ row }">
              <span v-if="row.needHelp" class="neon-tag tag-orange">需要</span>
              <span v-else class="text-dim">否</span>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" min-width="16%" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" min-width="12%">
            <template #default="{ row }">
              <span v-if="row.isInvalid" class="neon-tag tag-red">已作废</span>
              <span v-else class="neon-tag tag-mint">正常</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.dashboard {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-title {
  margin-bottom: 24px;

  h1 {
    font-family: $font-heading;
    font-size: 1.3rem;
    color: var(--pink-primary, $color-accent-pink);
    letter-spacing: 0.06em;
    text-shadow: 0 0 12px rgba(255, 122, 223, 0.3);
  }

  p {
    font-size: 12px;
    color: $color-text-muted;
    font-family: $font-mono;
    margin-top: 4px;
  }
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  border-left: 3px solid;
  padding: 16px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: $color-text-muted;
  margin-bottom: 4px;
}

.stat-value {
  font-family: $font-heading;
  font-size: 1.6rem;
  font-weight: 700;
  text-shadow: 0 0 20px currentColor;
}

.stat-sub {
  font-size: 11px;
  color: $color-text-muted;
  margin-top: 2px;
}

.stat-icon {
  opacity: 0.6;
}

.chart-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-card {
  padding: 20px;
}

.chart-header {
  margin-bottom: 12px;

  h3 {
    font-size: 14px;
    color: $color-text-primary;
    letter-spacing: 0.02em;
  }

  span {
    font-size: 11px;
    color: $color-text-muted;
    font-family: $font-mono;
  }
}

.recent-card {
  padding-bottom: 20px;
}

.table-wrapper {
  padding: 0 20px 0;
  width: 100%;
  overflow-x: auto;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 20px 16px;

  h2 {
    font-family: $font-heading;
    font-size: 1rem;
    color: $color-text-primary;
    letter-spacing: 0.05em;
  }

  span {
    font-size: 11px;
    color: $color-text-muted;
    font-family: $font-mono;
  }

  .title-divider {
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, rgba(255,122,223,0.3), rgba(110,231,255,0.15), transparent);
    margin-left: 8px;
  }
}

.value-highlight {
  color: var(--blue-primary, #6ee7ff);
  font-family: $font-heading;
  font-size: 13px;
}

.sub-header {
  font-size: 10px;
  color: $color-text-muted;
  font-weight: normal;
}

.text-dim {
  color: $color-text-muted;
  font-size: 12px;
}

.tag-blue {
  background: rgba(110, 231, 255, 0.1) !important;
  color: #6ee7ff !important;
  border: 1px solid rgba(110, 231, 255, 0.25) !important;
}

.tag-orange {
  background: rgba(255, 209, 102, 0.1) !important;
  color: #ffd166 !important;
  border: 1px solid rgba(255, 209, 102, 0.25) !important;
}

.tag-red {
  background: rgba(255, 92, 158, 0.1) !important;
  color: #ff5c9e !important;
  border: 1px solid rgba(255, 92, 158, 0.25) !important;
}

.tag-mint {
  background: rgba(124, 255, 203, 0.1) !important;
  color: #7cffcb !important;
  border: 1px solid rgba(124, 255, 203, 0.25) !important;
}
</style>
