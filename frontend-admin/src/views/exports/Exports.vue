<script setup lang="ts">
import { ref } from 'vue'
import {
  exportTimeLogs, exportTeamComparison, exportIssues,
  generateWeeklyReport, generatePresentation,
} from '@/api/exports'

interface ExportItem {
  id: string
  title: string
  desc: string
  format: string
  accent: string
  estimatedSize: string
}

const exportItems: ExportItem[] = [
  {
    id: 'workhours', title: '展示时长明细',
    desc: '导出所有队伍所有队员的完整训练展示时长记录，包含每日产出、遇到问题等详细字段',
    format: 'Excel', accent: '#00d4ff', estimatedSize: '预计 ~2.4MB',
  },
  {
    id: 'comparison', title: '队伍对比报表',
    desc: '生成各队伍训练时长、技能点覆盖率、问题率等多维度对比分析表格',
    format: 'Excel', accent: '#a855f7', estimatedSize: '预计 ~1.1MB',
  },
  {
    id: 'issues', title: '问题闭环清单',
    desc: '导出所有训练问题的完整生命周期记录，包含问题描述、处理过程、闭环时间',
    format: 'Excel', accent: '#ff9f00', estimatedSize: '预计 ~0.6MB',
  },
  {
    id: 'weekly', title: '周报文档',
     desc: '自动生成本周训练周报，包含展示时长汇总、技能进展、问题分析和下周计划',
    format: 'Word', accent: '#00ff9f', estimatedSize: '预计 ~0.8MB',
  },
  {
    id: 'report', title: '汇报材料',
    desc: '生成适合向学校领导/竞赛委员会汇报的训练成果PDF文档，含图表和分析',
    format: 'PDF', accent: '#ff2d55', estimatedSize: '预计 ~3.2MB',
  },
]

const generating = ref<string | null>(null)
const done = ref<string[]>([])
const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

const formatColors: Record<string, string> = {
  Excel: '#00ff9f',
  Word: '#00d4ff',
  PDF: '#ff2d55',
}

async function handleExport(item: ExportItem) {
  if (generating.value || done.value.includes(item.id)) return
  generating.value = item.id
  try {
    const actions: Record<string, () => Promise<void>> = {
      workhours: exportTimeLogs,
      comparison: exportTeamComparison,
      issues: exportIssues,
      weekly: generateWeeklyReport,
      report: generatePresentation,
    }
    await actions[item.id]?.()
  } catch {
    // ignore
  }
  generating.value = null
  done.value.push(item.id)
  setTimeout(() => {
    done.value = done.value.filter((d) => d !== item.id)
  }, 3000)
}
</script>

<template>
  <div class="exports">
    <div class="page-header">
      <div>
        <h1>数据导出中心</h1>
        <p>// DATA EXPORT CENTER — REPORT GENERATION HUB</p>
      </div>
    </div>

    <div class="info-banner">
      <span class="info-dot" />
      <p>
        数据基于截至 <span style="color:#00d4ff">{{ today }}</span> 的实时训练记录生成，导出前请确认筛选条件，系统将自动按条件范围汇总。
      </p>
    </div>

    <div class="export-grid">
      <div
        v-for="item in exportItems"
        :key="item.id"
        class="export-card"
        :class="{ generating: generating === item.id, done: done.includes(item.id) }"
        :style="{
          borderColor: done.includes(item.id) ? 'rgba(0,255,159,0.4)' : item.accent + '30',
          boxShadow: done.includes(item.id) ? '0 0 20px rgba(0,255,159,0.2)' : generating === item.id ? '0 0 20px ' + item.accent + '33' : 'none',
        }"
        @click="handleExport(item)"
      >
        <div class="export-accent" :style="{ width: done.includes(item.id) ? '100%' : generating === item.id ? '60%' : '40%', background: 'linear-gradient(90deg, ' + item.accent + ', transparent)' }" />

        <div class="export-icon" :style="{ background: item.accent + '12', borderColor: item.accent + '35', color: item.accent, boxShadow: '0 0 16px ' + item.accent + '20' }">
          <span v-if="generating === item.id" class="spinner" />
          <span v-else-if="done.includes(item.id)" style="color:#00ff9f;font-size:22px">✓</span>
          <span v-else style="font-size:22px">📄</span>
        </div>

        <div class="export-title-row">
          <h3>导出{{ item.title }}</h3>
          <span class="format-badge" :style="{ background: formatColors[item.format] + '15', color: formatColors[item.format], borderColor: formatColors[item.format] + '35' }">
            {{ item.format }}
          </span>
        </div>

        <p class="export-desc">{{ item.desc }}</p>

        <div class="export-footer">
          <span class="export-size">{{ item.estimatedSize }}</span>
          <div
            class="export-btn"
            :style="{
              background: done.includes(item.id) ? 'rgba(0,255,159,0.12)' : item.accent + '12',
              color: done.includes(item.id) ? '#00ff9f' : item.accent,
              borderColor: done.includes(item.id) ? 'rgba(0,255,159,0.35)' : item.accent + '30',
            }"
          >
            <template v-if="generating === item.id">
              <span class="spinner-small" /> 生成中...
            </template>
            <template v-else-if="done.includes(item.id)">
              ✓ 已完成
            </template>
            <template v-else>
              点击生成
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="note-box">
      <p>
        注：当前为原型演示，实际导出功能需接入后端 API（基于 Vue3 + Element Plus + ECharts 技术栈开发时实现）。
        数据安全说明：所有导出操作均有日志记录，请妥善保管导出文件。
      </p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.page-header {
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

.info-banner {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  background: rgba(0, 212, 255, 0.06);
  border: 1px solid rgba(0, 212, 255, 0.2);

  p {
    font-size: 12px;
    color: $color-text-muted;
    line-height: 1.6;
  }
}

.info-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: $color-accent-blue;
  box-shadow: 0 0 8px $color-accent-blue;
  flex-shrink: 0;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.export-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.export-card {
  padding: 20px;
  border-radius: 12px;
  background: rgba(4, 12, 28, 0.92);
  border: 1px solid;
  cursor: pointer;
  transition: all 0.3s;

  &:hover:not(.generating):not(.done) {
    transform: translateY(-2px);
  }
}

.export-accent {
  height: 2px;
  border-radius: 1px;
  margin-bottom: 20px;
  transition: width 0.3s;
}

.export-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid;
  margin-bottom: 16px;
}

.export-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 8px;

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: $color-text-primary;
    letter-spacing: 0.02em;
  }
}

.format-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: $font-mono;
  border: 1px solid;
  flex-shrink: 0;
  margin-left: 8px;
}

.export-desc {
  font-size: 12px;
  color: $color-text-muted;
  line-height: 1.6;
  margin-bottom: 16px;
}

.export-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 212, 255, 0.08);
}

.export-size {
  font-size: 12px;
  font-family: $font-mono;
  color: rgba(100, 116, 139, 0.5);
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid;
  transition: all 0.2s;
}

.spinner {
  width: 22px;
  height: 22px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  display: block;
}

.spinner-small {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  display: inline-block;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.note-box {
  margin-top: 24px;
  padding: 16px;
  border-radius: 8px;
  background: rgba(4, 12, 28, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);

  p {
    font-size: 12px;
    color: rgba(100, 116, 139, 0.5);
    line-height: 1.6;
  }
}
</style>
