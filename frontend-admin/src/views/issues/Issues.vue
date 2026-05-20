<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getIssues, updateIssueStatus } from '@/api/issues'
import type { Issue } from '@/types'
import { ElMessage } from 'element-plus'

type IssueStatus = 'pending' | 'processing' | 'resolved' | 'closed'

const issues = ref<Issue[]>([])

const columns: { id: IssueStatus; label: string; labelEn: string; color: string }[] = [
  { id: 'pending', label: '待处理', labelEn: 'PENDING', color: '#ff2d55' },
  { id: 'processing', label: '处理中', labelEn: 'IN PROGRESS', color: '#ff9f00' },
  { id: 'resolved', label: '已解决', labelEn: 'RESOLVED', color: '#00ff9f' },
  { id: 'closed', label: '已关闭', labelEn: 'CLOSED', color: '#64748b' },
]

const severityConfig: Record<string, { label: string; color: string }> = {
  critical: { label: '致命', color: '#ff2d55' },
  high: { label: '高危', color: '#ff6b35' },
  normal: { label: '中等', color: '#ff9f00' },
  low: { label: '低危', color: '#00ff9f' },
}

const hasRisk = computed(() =>
  issues.value.some(
    (i: Issue) => (i.severity === 'critical' || i.severity === 'high') && i.status === 'pending'
  )
)

const riskCount = computed(() =>
  issues.value.filter(
    (i: Issue) => (i.severity === 'critical' || i.severity === 'high') && i.status === 'pending'
  ).length
)

function getColIssues(status: IssueStatus) {
  return issues.value.filter((i: Issue) => i.status === status)
}

async function moveIssue(id: number, newStatus: IssueStatus) {
  await updateIssueStatus(id, newStatus)
  await loadData()
  ElMessage.success('问题状态已更新')
}

async function loadData() {
  issues.value = await getIssues()
}

const otherColumns = (currentId: string) => columns.filter((c) => c.id !== currentId)

onMounted(loadData)
</script>

<template>
  <div class="issues">
    <div class="page-header">
      <div>
        <h1>问题闭环管理</h1>
        <p>// ISSUE TRACKING — TRAINING RISK RADAR</p>
      </div>
    </div>

    <div class="summary-row">
      <div
        v-for="col in columns"
        :key="col.id"
        class="cyber-card summary-card"
      >
        <div class="summary-content">
          <span class="summary-label">{{ col.label }}</span>
          <span class="summary-num" :style="{ color: col.color }">{{ getColIssues(col.id).length }}</span>
        </div>
      </div>
    </div>

    <div v-if="hasRisk" class="risk-alert">
      <span class="risk-icon">⚠</span>
      <div>
        <p class="risk-title">训练风险警报</p>
        <p class="risk-desc">
          当前有 {{ riskCount }} 个高危/致命问题待处理，请指导老师优先介入处理，以避免影响训练进度。
        </p>
      </div>
    </div>

    <div class="kanban-row">
      <div v-for="col in columns" :key="col.id" class="kanban-col">
        <div
          class="kanban-header"
          :style="{
            background: col.color + '12',
            borderColor: col.color + '30',
            borderBottomColor: col.color + '55',
          }"
        >
          <span class="kanban-title" :style="{ color: col.color }">{{ col.label }}</span>
          <span
            class="kanban-count"
            :style="{ background: col.color + '20', color: col.color }"
          >{{ getColIssues(col.id).length }}</span>
        </div>

        <div class="kanban-body">
          <div
            v-for="issue in getColIssues(col.id)"
            :key="issue.id"
            :class="['issue-card', { 'critical-card': (issue.severity === 'critical' || issue.severity === 'high') && col.id === 'pending' }]"
            :style="{
              borderColor: (issue.severity === 'critical' || issue.severity === 'high') ? severityConfig[issue.severity].color + '55' : 'rgba(0,212,255,0.15)',
            }"
          >
            <div class="issue-top">
              <span
                class="neon-tag"
                :style="{
                  color: severityConfig[issue.severity].color,
                  background: severityConfig[issue.severity].color + '15',
                  borderColor: severityConfig[issue.severity].color + '40',
                }"
              >{{ severityConfig[issue.severity].label }}</span>
              <span class="issue-id">#{{ issue.id }}</span>
            </div>
            <p class="issue-title">{{ issue.title }}</p>
            <div class="issue-meta">
              <div class="meta-item">
                <span class="meta-text">🏫 {{ issue.teamName }} · {{ issue.memberName }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-skill">📋 {{ issue.skillName }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-time">🕐 {{ issue.submitTime }}</span>
              </div>
            </div>
            <div class="issue-assignee">
              负责：<span style="color:#00d4ff">{{ issue.assignee }}</span>
            </div>
            <div class="issue-actions">
              <button
                v-for="c in otherColumns(col.id)"
                :key="c.id"
                class="move-btn"
                :style="{ color: c.color, borderColor: c.color + '30' }"
                @click="moveIssue(issue.id, c.id)"
              >
                → {{ c.label }}
              </button>
            </div>
          </div>

          <div v-if="getColIssues(col.id).length === 0" class="empty-col">
            <span>暂无问题</span>
          </div>
        </div>
      </div>
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

.summary-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  padding: 12px 16px;
}

.summary-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.summary-label {
  font-size: 12px;
  color: $color-text-muted;
}

.summary-num {
  font-family: $font-heading;
  font-size: 1.3rem;
  font-weight: 700;
}

.risk-alert {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  margin-bottom: 20px;
  border-radius: 8px;
  background: rgba(255, 45, 85, 0.06);
  border: 1px solid rgba(255, 45, 85, 0.35);
  box-shadow: 0 0 20px rgba(255, 45, 85, 0.1);
}

.risk-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

.risk-title {
  font-size: 14px;
  font-weight: 600;
  color: $color-accent-red;
  margin-bottom: 4px;
}

.risk-desc {
  font-size: 12px;
  color: $color-text-muted;
  line-height: 1.6;
}

.kanban-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: start;
}

.kanban-col {
  min-height: 100px;
}

.kanban-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 6px 6px 0 0;
  border: 1px solid;
  border-bottom-width: 2px;
  margin-bottom: 8px;
}

.kanban-title {
  font-size: 14px;
  font-weight: 500;
}

.kanban-count {
  margin-left: auto;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}

.kanban-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.issue-card {
  background: rgba(4, 12, 28, 0.92);
  border: 1px solid;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.issue-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.issue-id {
  font-size: 11px;
  font-family: $font-mono;
  color: rgba(100, 116, 139, 0.5);
}

.issue-title {
  font-size: 12px;
  font-weight: 500;
  color: $color-text-primary;
  line-height: 1.5;
  margin-bottom: 8px;
}

.issue-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.meta-text {
  font-size: 11px;
  color: $color-text-muted;
}

.meta-skill {
  font-size: 11px;
  color: #a855f7;
}

.meta-time {
  font-size: 11px;
  color: $color-text-muted;
}

.issue-assignee {
  font-size: 12px;
  color: $color-text-muted;
  margin-bottom: 8px;
}

.issue-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding-top: 8px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}

.move-btn {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
  background: transparent;
  border: 1px solid;
  transition: all 0.15s;

  &:hover {
    background: rgba(0, 212, 255, 0.06);
  }
}

.empty-col {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed rgba(0, 212, 255, 0.12);
  border-radius: 8px;

  span {
    font-size: 12px;
    color: rgba(100, 116, 139, 0.4);
  }
}
</style>
