<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { getMembers, createMember, updateMember, toggleMemberStatus } from '@/api/members'
import { getTeams } from '@/api/teams'
import type { Member, Team } from '@/types'
import { Plus, Edit, Search, Filter } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const members = ref<Member[]>([])
const allTeams = ref<Team[]>([])
const filterTeam = ref('all')
const search = ref('')
const page = ref(1)
const pageSize = ref(200)
const showDialog = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = ref({
  name: '',
  schoolName: '',
  teamName: '',
  teamId: 0,
  role: '队员' as '队长' | '队员',
})

const filtered = computed(() => {
  return members.value
})

function getLocalDateString(date = new Date()) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const statCards = computed(() => [
  { label: '总队员数', value: members.value.length, color: '#6ee7ff' },
  { label: '活跃队员', value: members.value.filter((m: Member) => m.status === 'active').length, color: '#7cffcb' },
  { label: '今日提交', value: members.value.filter((m: Member) => m.lastSubmit.startsWith(getLocalDateString())).length, color: '#a78bfa' },
  { label: '参与院校', value: new Set(members.value.map((m: Member) => m.schoolName)).size, color: '#ffd166' },
])

async function loadMembers() {
  const params: Record<string, unknown> = {
    page: page.value,
    pageSize: pageSize.value,
  }
  if (filterTeam.value !== 'all') params.teamId = Number(filterTeam.value)
  if (search.value.trim()) params.keyword = search.value.trim()
  members.value = await getMembers(params)
}

async function loadData() {
  await loadMembers()
  allTeams.value = await getTeams({ page: 1, pageSize: 200 })
}

function openAdd() {
  isEdit.value = false
  editId.value = null
  form.value = { name: '', schoolName: '', teamName: '', teamId: 0, role: '队员' }
  showDialog.value = true
}

function openEdit(member: Member) {
  isEdit.value = true
  editId.value = member.id
  form.value = {
    name: member.name,
    schoolName: member.schoolName,
    teamName: member.teamName,
    teamId: member.teamId,
    role: member.role,
  }
  showDialog.value = true
}

async function handleSubmit() {
  if (!form.value.name || !form.value.schoolName || !form.value.teamId) {
    ElMessage.warning('请填写必填字段')
    return
  }
  if (isEdit.value && editId.value) {
    await updateMember(editId.value, form.value)
    ElMessage.success('队员信息更新成功')
  } else {
    await createMember(form.value)
    ElMessage.success('队员创建成功')
  }
  showDialog.value = false
  await loadMembers()
}

async function handleToggle(member: Member) {
  await toggleMemberStatus(member.id, member.status === 'active' ? 'inactive' : 'active')
  await loadMembers()
  ElMessage.success('状态已更新')
}

watch([filterTeam, search], () => {
  page.value = 1
  loadMembers()
})

onMounted(loadData)
</script>

<template>
  <div class="members">
    <div class="page-header">
      <div>
        <h1>队员管理</h1>
        <p>// MEMBER MANAGEMENT — {{ members.length }} MEMBERS REGISTERED</p>
      </div>
      <button class="cyber-btn-primary" @click="openAdd">
        <el-icon :size="15"><Plus /></el-icon>
        新增队员
      </button>
    </div>

    <div class="stat-row">
      <div v-for="s in statCards" :key="s.label" class="cyber-card stat-mini">
        <span class="stat-label">{{ s.label }}</span>
        <span class="stat-num" :style="{ color: s.color }">{{ s.value }}</span>
      </div>
    </div>

    <div class="cyber-card filter-card">
      <div class="filter-row">
        <div class="filter-left">
          <el-icon :size="14" color="#64748b"><Filter /></el-icon>
          <span class="filter-label">按队伍筛选：</span>
          <div class="filter-tags">
            <button
              :class="['filter-tag', { active: filterTeam === 'all' }]"
              @click="filterTeam = 'all'"
            >全部队伍</button>
            <button
              v-for="team in allTeams"
              :key="team.id"
              :class="['filter-tag', { active: filterTeam === String(team.id) }]"
              @click="filterTeam = String(team.id)"
            >{{ team.teamName }}</button>
          </div>
        </div>
        <div class="filter-right">
          <el-input
            v-model="search"
            placeholder="搜索队员..."
            :prefix-icon="Search"
            style="width:180px"
          />
        </div>
      </div>
    </div>

    <div class="cyber-card table-card">
      <el-table :data="filtered" style="width:100%" class="cyber-table">
        <el-table-column label="队员姓名" width="130">
          <template #default="{ row }">
            <div class="member-name">
              <div class="avatar">{{ row.name.charAt(0) }}</div>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="schoolName" label="所属院校" min-width="180" />
        <el-table-column prop="teamName" label="所属队伍" width="120">
          <template #default="{ row }">
            <span class="neon-tag" style="background:rgba(110,231,255,0.1);color:#6ee7ff;border:1px solid rgba(110,231,255,0.25)">{{ row.teamName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{ row }">
            <span :class="['role-tag', row.role === '队长' ? 'role-leader' : 'role-member']">{{ row.role }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <span v-if="row.status==='active'" class="neon-tag" style="background:rgba(124,255,203,0.1);color:#7cffcb;border:1px solid rgba(124,255,203,0.25)">正常</span>
            <span v-else class="neon-tag" style="background:rgba(100,116,139,0.1);color:#64748b;border:1px solid rgba(100,116,139,0.25)">停用</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalHours" label="累计展示时长" width="100">
          <template #default="{ row }">
            <span style="font-family:'Orbitron',sans-serif;color:#6ee7ff">{{ row.totalHours }}</span> 分钟
          </template>
        </el-table-column>
        <el-table-column prop="lastSubmit" label="最近提交" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button size="small" text @click="openEdit(row)">
                <el-icon :size="13"><Edit /></el-icon> 编辑
              </el-button>
              <el-button
                size="small" text
                :style="{ color: row.status === 'active' ? '#ffd166' : '#7cffcb' }"
                @click="handleToggle(row)"
              >
                {{ row.status === 'active' ? '停用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span>显示 {{ filtered.length }} / {{ members.length }} 条记录</span>
      </div>
    </div>

    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '编辑队员' : '新增队员'"
      width="500px"
    >
      <el-form :model="form" label-position="top">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.role" style="width:100%">
                <el-option label="队长" value="队长" />
                <el-option label="队员" value="队员" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属院校">
          <el-input v-model="form.schoolName" placeholder="请输入院校名称" />
        </el-form-item>
        <el-form-item label="所属队伍">
          <el-select v-model="form.teamId" style="width:100%" placeholder="选择队伍">
            <el-option
              v-for="team in allTeams"
              :key="team.id"
              :label="team.teamName"
              :value="team.id"
            />
          </el-select>
        </el-form-item>
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

.stat-num {
  font-family: $font-heading;
  font-size: 1.3rem;
  font-weight: 700;
}

.filter-card {
  margin-bottom: 16px;
  padding: 12px 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 12px;
  color: $color-text-muted;
}

.filter-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.filter-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
  border: 1px solid rgba(110, 231, 255, 0.15);
  color: $color-text-muted;

  &.active {
    background: rgba(110, 231, 255, 0.15);
    border-color: rgba(110, 231, 255, 0.4);
    color: $color-accent-blue;
  }
}

.table-card {
  overflow: hidden;
}

.table-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(110, 231, 255, 0.1);

  span {
    font-size: 12px;
    color: $color-text-muted;
  }
}

.member-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(110,231,255,0.3), rgba(167,139,250,0.3));
  color: $color-text-primary;
  border: 1px solid rgba(110, 231, 255, 0.2);
}

.role-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;

  &.role-leader {
    background: rgba(167, 139, 250, 0.12);
    color: $color-accent-purple;
    border: 1px solid rgba(167, 139, 250, 0.3);
  }

  &.role-member {
    background: rgba(110, 231, 255, 0.08);
    color: $color-accent-blue;
    border: 1px solid rgba(110, 231, 255, 0.2);
  }
}

.actions {
  display: flex;
  gap: 4px;
}
</style>
