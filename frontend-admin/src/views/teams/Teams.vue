<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getTeams, createTeam, updateTeam, toggleTeamStatus } from '@/api/teams'
import type { Team } from '@/types'
import { Plus, Edit, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const teams = ref<Team[]>([])
const trackOptions = ['新一代信息技术', '人工智能']
const search = ref('')
const showDialog = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = ref({
  schoolName: '',
  teamName: '',
  trackName: '',
  inviteCode: '',
})

const filtered = computed(() => {
  if (!search.value) return teams.value
  const s = search.value.toLowerCase()
  return teams.value.filter(
    (t: Team) =>
      t.teamName.includes(s) ||
      t.schoolName.includes(s) ||
      t.inviteCode.toLowerCase().includes(s) ||
      t.track.includes(s)
  )
})

const statCards = computed(() => [
  { label: '全部队伍', value: teams.value.length, color: '#00d4ff' },
  { label: '正常启用', value: teams.value.filter((t: Team) => t.status === 'active').length, color: '#00ff9f' },
  { label: '今日已提交', value: teams.value.filter((t: Team) => t.todaySubmitted).length, color: '#a855f7' },
  { label: '今日未提交', value: teams.value.filter((t: Team) => !t.todaySubmitted).length, color: '#ff9f00' },
])

async function loadTeams() {
  teams.value = await getTeams({ page: 1, pageSize: 200 })
}

function openAdd() {
  isEdit.value = false
  editId.value = null
  form.value = { schoolName: '', teamName: '', trackName: trackOptions[0], inviteCode: '' }
  showDialog.value = true
}

function openEdit(team: Team) {
  isEdit.value = true
  editId.value = team.id
  form.value = {
    schoolName: team.schoolName,
    teamName: team.teamName,
    trackName: trackOptions.includes(team.trackName || team.track) ? (team.trackName || team.track) : trackOptions[0],
    inviteCode: team.inviteCode,
  }
  showDialog.value = true
}

async function handleSubmit() {
  if (!form.value.schoolName || !form.value.teamName || !form.value.trackName || !form.value.inviteCode) {
    ElMessage.warning('请填写必填字段')
    return
  }
  if (isEdit.value && editId.value) {
    await updateTeam(editId.value, { ...form.value })
    ElMessage.success('队伍信息更新成功')
  } else {
    await createTeam(form.value)
    ElMessage.success('队伍创建成功')
  }
  showDialog.value = false
  await loadTeams()
}

async function handleToggle(team: Team) {
  await toggleTeamStatus(team.id, team.status === 'active' ? 'inactive' : 'active')
  await loadTeams()
  ElMessage.success('状态已更新')
}

onMounted(loadTeams)
</script>

<template>
  <div class="teams">
    <div class="page-header">
      <div>
        <h1>队伍管理</h1>
        <p>// TEAM MANAGEMENT — {{ teams.length }} TEAMS REGISTERED</p>
      </div>
      <button class="cyber-btn-primary" @click="openAdd">
        <el-icon :size="15"><Plus /></el-icon>
        新增队伍
      </button>
    </div>

    <div class="stat-row">
      <div
        v-for="s in statCards"
        :key="s.label"
        class="cyber-card stat-mini"
      >
        <span class="stat-label">{{ s.label }}</span>
        <span class="stat-num" :style="{ color: s.color }">{{ s.value }}</span>
      </div>
    </div>

    <div class="cyber-card search-card">
      <el-input
        v-model="search"
        placeholder="搜索院校、队伍名、邀请码..."
        :prefix-icon="Search"
        style="max-width:320px"
      />
    </div>

    <div class="cyber-card table-card">
      <el-table :data="filtered" style="width:100%" class="cyber-table">
        <el-table-column prop="schoolName" label="院校名称" min-width="180" />
        <el-table-column prop="teamName" label="队伍名称" width="120" />
        <el-table-column prop="track" label="赛道" width="130">
          <template #default="{ row }">
            <span class="track-tag">{{ row.track }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="inviteCode" label="邀请码" width="120">
          <template #default="{ row }">
            <span class="neon-tag" style="background:rgba(0,212,255,0.1);color:#00d4ff;border:1px solid rgba(0,212,255,0.25)">{{ row.inviteCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="memberCount" label="队员数量" width="90" align="center">
          <template #default="{ row }">
            <span style="font-family:'Orbitron',sans-serif;color:#00d4ff">{{ row.memberCount }}</span> 人
          </template>
        </el-table-column>
        <el-table-column prop="todaySubmitted" label="今日提交" width="90">
          <template #default="{ row }">
            <span v-if="row.todaySubmitted" class="neon-tag" style="background:rgba(0,255,159,0.1);color:#00ff9f;border:1px solid rgba(0,255,159,0.25)">已提交</span>
            <span v-else class="neon-tag" style="background:rgba(255,159,0,0.1);color:#ff9f00;border:1px solid rgba(255,159,0,0.25)">未提交</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalHours" label="累计展示时长" width="100">
          <template #default="{ row }">
            <span style="font-family:'Orbitron',sans-serif;color:#00d4ff">{{ row.totalHours }}</span> 分钟
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <span v-if="row.status==='active'" class="neon-tag" style="background:rgba(0,255,159,0.1);color:#00ff9f;border:1px solid rgba(0,255,159,0.25)">启用</span>
            <span v-else class="neon-tag" style="background:rgba(100,116,139,0.1);color:#64748b;border:1px solid rgba(100,116,139,0.25)">停用</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button size="small" text @click="openEdit(row)">
                <el-icon :size="13"><Edit /></el-icon> 编辑
              </el-button>
              <el-button
                size="small" text
                :style="{ color: row.status === 'active' ? '#ff9f00' : '#00ff9f' }"
                @click="handleToggle(row)"
              >
                {{ row.status === 'active' ? '禁用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span>共 {{ filtered.length }} 条记录</span>
      </div>
    </div>

    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '编辑队伍' : '新增队伍'"
      width="560px"
    >
      <el-form :model="form" label-position="top" label-width="auto">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="院校名称">
              <el-input v-model="form.schoolName" placeholder="如：重庆财经职业学院" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="队伍名称">
              <el-input v-model="form.teamName" placeholder="如：CQSX-A队" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="赛道">
              <el-select v-model="form.trackName" placeholder="选择赛道" style="width:100%">
                <el-option
                  v-for="track in trackOptions"
                  :key="track"
                  :label="track"
                  :value="track"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邀请码">
              <el-input v-model="form.inviteCode" placeholder="如：CQSX0075" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
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

.search-card {
  margin-bottom: 16px;
  padding: 12px 16px;
}

.table-card {
  overflow: hidden;
}

.table-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);

  span {
    font-size: 12px;
    color: $color-text-muted;
  }
}

.track-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(168, 85, 247, 0.1);
  color: $color-accent-purple;
  border: 1px solid rgba(168, 85, 247, 0.25);
}

.actions {
  display: flex;
  gap: 4px;
}
</style>
