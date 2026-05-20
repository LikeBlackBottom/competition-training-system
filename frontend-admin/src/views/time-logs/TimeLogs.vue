<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getTimeLogs, voidRecord } from '@/api/time-logs'
import { getTeams } from '@/api/teams'
import { getCategories } from '@/api/skills'
import { exportTimeLogs } from '@/api/exports'
import type { WorkRecord, Team, SkillModule } from '@/types'
import { Filter, CircleCloseFilled, Download } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const records = ref<WorkRecord[]>([])
const allTeams = ref<Team[]>([])
const allModules = ref<SkillModule[]>([])
const filterExpanded = ref(true)

const filters = ref({
  date: '',
  teamName: 'all',
  moduleName: 'all',
  progressStatus: 'all',
  needHelp: 'all',
})

const filtered = computed(() => {
  let result = records.value
  if (filters.value.date) {
    result = result.filter((r: WorkRecord) => r.date.includes(filters.value.date))
  }
  if (filters.value.teamName !== 'all') {
    result = result.filter((r: WorkRecord) => r.teamName === filters.value.teamName)
  }
  if (filters.value.moduleName !== 'all') {
    result = result.filter((r: WorkRecord) => r.moduleName === filters.value.moduleName)
  }
  if (filters.value.progressStatus !== 'all') {
    result = result.filter((r: WorkRecord) => r.completionStatus === filters.value.progressStatus)
  }
  if (filters.value.needHelp === 'yes') {
    result = result.filter((r: WorkRecord) => r.needHelp)
  } else if (filters.value.needHelp === 'no') {
    result = result.filter((r: WorkRecord) => !r.needHelp)
  }
  return result
})

const totalHours = computed(() =>
  filtered.value.filter((r: WorkRecord) => r.recordStatus !== 'voided').reduce((s: number, r: WorkRecord) => s + r.hours, 0)
)

async function loadData() {
  records.value = await getTimeLogs()
  allTeams.value = await getTeams()
  allModules.value = await getCategories()
}

async function handleVoid(id: number) {
  try {
    await ElMessageBox.confirm('确定要作废此条记录吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await voidRecord(id)
    await loadData()
    ElMessage.success('记录已作废')
  } catch {
    // user cancelled
  }
}

function clearFilters() {
  filters.value = { date: '', teamName: 'all', moduleName: 'all', progressStatus: 'all', needHelp: 'all' }
}

const statusLabels: Record<string, { label: string; color: string }> = {
  completed: { label: '已完成', color: '#00ff9f' },
  partial: { label: '部分完成', color: '#ff9f00' },
  blocked: { label: '受阻', color: '#ff2d55' },
  notStarted: { label: '未开始', color: '#64748b' },
  '未开始': { label: '未开始', color: '#64748b' },
  '进行中': { label: '进行中', color: '#ff9f00' },
  '部分完成': { label: '部分完成', color: '#ff9f00' },
  '已完成': { label: '已完成', color: '#00ff9f' },
  '已掌握': { label: '已掌握', color: '#00ff9f' },
  '受阻': { label: '受阻', color: '#ff2d55' },
}

onMounted(loadData)
</script>

<template>
  <div class="time-logs">
    <div class="page-header">
      <div>
        <h1>展示时长记录管理</h1>
        <p>// DISPLAY TIME RECORDS — ADMIN VIEW ONLY</p>
      </div>
      <button class="cyber-btn-primary" @click="exportTimeLogs">
        <el-icon :size="14"><Download /></el-icon>
        导出数据
      </button>
    </div>

    <div class="stat-row">
      <div class="cyber-card stat-mini">
        <span class="stat-label">筛选记录数</span>
        <span class="stat-num" style="color:#00d4ff">{{ filtered.length }}</span>
      </div>
      <div class="cyber-card stat-mini">
        <span class="stat-label">有效展示时长</span>
        <span class="stat-num" style="color:#00ff9f">{{ totalHours }} 分钟</span>
      </div>
      <div class="cyber-card stat-mini">
        <span class="stat-label">需协助记录</span>
        <span class="stat-num" style="color:#ff9f00">{{ filtered.filter((r: WorkRecord) => r.needHelp).length }}</span>
      </div>
      <div class="cyber-card stat-mini">
        <span class="stat-label">已作废</span>
        <span class="stat-num" style="color:#ff2d55">{{ filtered.filter((r: WorkRecord) => r.recordStatus === 'voided').length }}</span>
      </div>
    </div>

    <div class="cyber-card filter-panel">
      <div class="filter-toggle" @click="filterExpanded = !filterExpanded">
        <div class="filter-toggle-left">
          <el-icon :size="14" color="#00d4ff"><Filter /></el-icon>
          <span>高级筛选</span>
          <span v-if="filters.date || filters.teamName !== 'all' || filters.moduleName !== 'all' || filters.progressStatus !== 'all' || filters.needHelp !== 'all'" class="filter-badge">已筛选</span>
        </div>
        <span style="color:#64748b;font-size:14px">{{ filterExpanded ? '▲' : '▼' }}</span>
      </div>
      <div v-if="filterExpanded" class="filter-body">
        <div class="filter-grid">
          <div class="filter-item">
            <label>日期</label>
            <el-date-picker
              v-model="filters.date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width:100%"
            />
          </div>
          <div class="filter-item">
            <label>队伍</label>
            <el-select v-model="filters.teamName" style="width:100%">
              <el-option label="全部队伍" value="all" />
              <el-option v-for="t in allTeams" :key="t.id" :label="t.teamName" :value="t.teamName" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>训练模块</label>
            <el-select v-model="filters.moduleName" style="width:100%">
              <el-option label="全部模块" value="all" />
              <el-option v-for="m in allModules" :key="m.id" :label="m.name" :value="m.name" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>完成状态</label>
            <el-select v-model="filters.progressStatus" style="width:100%">
              <el-option label="全部" value="all" />
              <el-option label="未开始" value="未开始" />
              <el-option label="进行中" value="进行中" />
              <el-option label="部分完成" value="部分完成" />
              <el-option label="已完成" value="已完成" />
              <el-option label="已掌握" value="已掌握" />
              <el-option label="受阻" value="受阻" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>是否需要协助</label>
            <el-select v-model="filters.needHelp" style="width:100%">
              <el-option label="全部" value="all" />
              <el-option label="需要协助" value="yes" />
              <el-option label="不需要" value="no" />
            </el-select>
          </div>
          <div class="filter-item filter-actions">
            <el-button @click="clearFilters">清除筛选</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="cyber-card table-card">
      <el-table :data="filtered" style="width:100%" size="small" class="cyber-table">
        <el-table-column prop="date" label="日期" width="100" />
        <el-table-column prop="teamName" label="队伍" width="100">
          <template #default="{ row }">
            <span class="neon-tag" style="background:rgba(0,212,255,0.1);color:#00d4ff;border:1px solid rgba(0,212,255,0.25)">{{ row.teamName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="memberName" label="队员" width="80" />
        <el-table-column prop="moduleName" label="模块" width="100">
          <template #default="{ row }">
            <span style="color:#a855f7">{{ row.moduleName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="skillName" label="技能点" width="90" />
        <el-table-column prop="hours" label="展示时长（分钟）" width="70">
          <template #default="{ row }">
            <span style="font-family:'Orbitron',sans-serif;color:#00ff9f">{{ row.hours }} 分钟</span>
          </template>
        </el-table-column>
        <el-table-column prop="completionStatus" label="完成状态" width="85">
          <template #default="{ row }">
            <span
              class="neon-tag"
              :style="{
                background: (statusLabels[row.completionStatus]?.color || '#64748b') + '15',
                color: statusLabels[row.completionStatus]?.color || '#64748b',
                border: '1px solid ' + (statusLabels[row.completionStatus]?.color || '#64748b') + '40',
              }"
            >{{ statusLabels[row.completionStatus]?.label || row.completionStatus }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="todayOutput" label="今日产出" min-width="180" show-overflow-tooltip />
        <el-table-column prop="issue" label="遇到问题" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span :style="{ color: row.issue ? '#ff9f00' : '#3a5070' }">{{ row.issue || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="needHelp" label="需协助" width="70">
          <template #default="{ row }">
            <span v-if="row.needHelp" class="neon-tag" style="background:rgba(255,159,0,0.1);color:#ff9f00;border:1px solid rgba(255,159,0,0.25)">需要</span>
            <span v-else style="color:#3a5070">否</span>
          </template>
        </el-table-column>
        <el-table-column label="记录状态" width="80">
          <template #default="{ row }">
            <span v-if="row.recordStatus === 'voided'" class="neon-tag" style="background:rgba(255,45,85,0.1);color:#ff2d55;border:1px solid rgba(255,45,85,0.25)">已作废</span>
            <span v-else class="neon-tag" style="background:rgba(0,255,159,0.1);color:#00ff9f;border:1px solid rgba(0,255,159,0.25)">正常</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.recordStatus !== 'voided'"
              size="small"
              text
              type="danger"
              @click="handleVoid(row.id)"
            >
              <el-icon :size="13"><CircleCloseFilled /></el-icon> 作废
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span>显示 {{ filtered.length }} 条记录</span>
        <span>有效展示时长 <span style="color:#00ff9f;font-family:'Orbitron',sans-serif">{{ totalHours }} 分钟</span></span>
        <span>注：记录仅可作废，不做物理删除</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
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

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-mini {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
}

.stat-label {
  font-size: 12px;
  color: $color-text-muted;
}

.stat-num {
  font-family: $font-heading;
  font-size: 1.3rem;
  font-weight: 700;
}

.filter-panel {
  margin-bottom: 16px;
}

.filter-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  cursor: pointer;
  user-select: none;

  &:hover {
    background: rgba(0, 212, 255, 0.02);
  }
}

.filter-toggle-left {
  display: flex;
  align-items: center;
  gap: 8px;

  span {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: $color-accent-blue;
    font-weight: 500;
  }
}

.filter-badge {
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(0, 212, 255, 0.12);
  color: $color-accent-blue !important;
  font-size: 11px !important;
}

.filter-body {
  padding: 0 16px 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-top: 12px;
}

.filter-item {
  label {
    display: block;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: $color-text-muted;
    margin-bottom: 6px;
  }
}

.filter-actions {
  display: flex;
  align-items: flex-end;
}

.table-card {
  overflow: hidden;
}

.table-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;

  span {
    font-size: 12px;
    color: $color-text-muted;
  }
}
</style>
