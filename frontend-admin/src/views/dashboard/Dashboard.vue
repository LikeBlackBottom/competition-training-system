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
  { label: '总队伍数', key: 'totalTeams' as const, icon: Monitor, accent: 'blue', sub: '6所院校' },
  { label: '今日已提交', key: 'todaySubmitted' as const, icon: Check, accent: 'cyan', sub: '' },
  { label: '今日未提交', key: 'todayNotSubmitted' as const, icon: Close, accent: 'orange', sub: '需关注' },
  { label: '累计展示时长', key: 'totalHours' as const, icon: Clock, accent: 'purple', sub: '分钟' },
  { label: '待解决问题', key: 'pendingIssues' as const, icon: WarningFilled, accent: 'red', sub: '' },
]

const accentColors: Record<string, string> = {
  blue: '#00d4ff',
  cyan: '#00ff9f',
  orange: '#ff9f00',
  purple: '#a855f7',
  red: '#ff2d55',
}

const barChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      backgroundColor: 'rgba(4,12,28,0.97)',
      borderColor: 'rgba(0,212,255,0.3)',
      textStyle: { color: '#c0d8f0', fontSize: 12 },
    },
    grid: { top: 10, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'category',
      data: data ? data.teamHoursRank.map((t: { name: string; hours: number }) => t.name) : [],
      axisLine: { lineStyle: { color: 'rgba(0,212,255,0.15)' } },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: '#5a7090', fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(0,212,255,0.08)' } },
    },
    series: [{
      type: 'bar',
      data: data ? data.teamHoursRank.map((t: { name: string; hours: number }, i: number) => ({
        value: t.hours,
        itemStyle: {
          color: i === 0 ? '#00d4ff' : i === 1 ? '#a855f7' : 'rgba(0,212,255,0.5)',
          borderRadius: [4, 4, 0, 0],
        },
      })) : [],
    }],
  }
})

const pieChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      backgroundColor: 'rgba(4,12,28,0.97)',
      borderColor: 'rgba(0,212,255,0.3)',
      textStyle: { color: '#c0d8f0', fontSize: 12 },
    },
    legend: {
      orient: 'vertical',
      right: 0,
      top: 'center',
      textStyle: { color: '#5a7090', fontSize: 11 },
    },
    series: [{
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['42%', '50%'],
      itemStyle: { borderColor: 'transparent' },
      label: { show: false },
      emphasis: { scaleSize: 8 },
      data: data ? data.skillDist : [],
    }],
  }
})

const lineChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      backgroundColor: 'rgba(4,12,28,0.97)',
      borderColor: 'rgba(0,212,255,0.3)',
      textStyle: { color: '#c0d8f0', fontSize: 12 },
    },
    grid: { top: 10, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'category',
      data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.date) : [],
      axisLine: { lineStyle: { color: 'rgba(0,212,255,0.15)' } },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(0,212,255,0.08)' } },
    },
    series: [
      {
        type: 'line',
        name: '展示时长(分钟)',
        data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.hours) : [],
        smooth: true,
        lineStyle: { color: '#00d4ff', width: 2 },
        itemStyle: { color: '#00d4ff' },
        symbolSize: 6,
      },
      {
        type: 'line',
        name: '记录数',
        data: data ? data.dailyTrend.map((d: { date: string; hours: number; records: number }) => d.records) : [],
        smooth: true,
        lineStyle: { color: '#a855f7', width: 2 },
        itemStyle: { color: '#a855f7' },
        symbolSize: 6,
      },
    ],
  }
})

const issueChartOption = computed(() => {
  const data = dashboard.value
  return {
    tooltip: {
      backgroundColor: 'rgba(4,12,28,0.97)',
      borderColor: 'rgba(0,212,255,0.3)',
      textStyle: { color: '#c0d8f0', fontSize: 12 },
    },
    grid: { top: 10, right: 20, bottom: 20, left: 40 },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(255,45,85,0.08)' } },
    },
    yAxis: {
      type: 'category',
      data: data ? data.issueSeverityDist.map((d: { name: string; count: number; color: string }) => d.name) : [],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#5a7090', fontSize: 12 },
    },
    series: [{
      type: 'bar',
      data: data ? data.issueSeverityDist.map((d: { name: string; count: number; color: string }) => ({
        value: d.count,
        itemStyle: { color: d.color, borderRadius: [0, 4, 4, 0] },
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
        <VChart :option="barChartOption" autoresize style="height:220px" />
      </div>
      <div class="cyber-card chart-card chart-narrow">
        <div class="chart-header">
          <h3>技能点展示时长分布</h3>
          <span>SKILL DISPLAY MINUTES DISTRIBUTION</span>
        </div>
        <VChart :option="pieChartOption" autoresize style="height:220px" />
      </div>
    </div>

    <div class="chart-row">
      <div class="cyber-card chart-card chart-wide">
        <div class="chart-header">
          <h3>每日训练趋势</h3>
          <span>DAILY TRAINING TREND (7 DAYS)</span>
        </div>
        <VChart :option="lineChartOption" autoresize style="height:200px" />
      </div>
      <div class="cyber-card chart-card chart-narrow">
        <div class="chart-header">
          <h3>问题严重程度分布</h3>
          <span>ISSUE SEVERITY DISTRIBUTION</span>
        </div>
        <VChart :option="issueChartOption" autoresize style="height:200px" />
      </div>
    </div>

    <div class="cyber-card">
      <div class="section-title">
        <h2>最近提交记录</h2>
        <span>RECENT SUBMISSIONS</span>
        <div class="title-divider" />
      </div>
      <el-table
        :data="dashboard.recentRecords"
        class="cyber-table"
        style="width: 100%"
      >
        <el-table-column prop="teamName" label="队伍" width="100" />
        <el-table-column prop="memberName" label="队员" width="90" />
        <el-table-column prop="moduleName" label="模块" width="90">
          <template #default="{ row }">
            <span class="neon-tag" style="background:rgba(0,212,255,0.1);color:#00d4ff;border:1px solid rgba(0,212,255,0.25)">{{ row.moduleName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="skillName" label="技能点" width="90" />
        <el-table-column prop="hours" label="展示时长（分钟）" width="70">
          <template #default="{ row }">
            <span style="color:#00d4ff;font-family:'Orbitron',sans-serif;font-size:13px">{{ row.hours }} 分钟</span>
          </template>
        </el-table-column>
        <el-table-column prop="needHelp" label="需协助" width="80">
          <template #default="{ row }">
            <span v-if="row.needHelp" class="neon-tag" style="background:rgba(255,159,0,0.1);color:#ff9f00;border:1px solid rgba(255,159,0,0.25)">需要</span>
            <span v-else style="color:#3a5070">否</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
      </el-table>
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
    color: $color-text-primary;
    letter-spacing: 0.06em;
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

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 20px 0;

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
    background: linear-gradient(90deg, rgba(0,212,255,0.3), transparent);
    margin-left: 8px;
  }
}
</style>
