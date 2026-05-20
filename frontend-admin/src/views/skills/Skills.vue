<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { DEFAULT_TRACK, TRACK_OPTIONS, getCategories, createCategory, updateCategory, createTask, updateTask } from '@/api/skills'
import type { SkillModule, SkillPoint } from '@/types'
import { Plus, ArrowUp, ArrowDown, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const modules = ref<SkillModule[]>([])
const selected = ref<SkillModule | null>(null)
const showModuleDialog = ref(false)
const showSkillDialog = ref(false)
const isEditModule = ref(false)
const isEditSkill = ref(false)
const editSkillId = ref<number | null>(null)

const moduleForm = ref<{ name: string; trackName: string }>({ name: '', trackName: DEFAULT_TRACK })
const skillForm = ref({
  name: '',
  description: '',
  expectedMinutes: 0,
  difficultyLevel: 1 as 1 | 2 | 3,
  scoreWeight: 20,
  sortOrder: 0,
  status: 'active' as 'active' | 'inactive',
})

async function loadModules() {
  modules.value = await getCategories()
  if (!selected.value && modules.value.length) {
    selected.value = modules.value[0]
  } else if (selected.value) {
    selected.value = modules.value.find((m: SkillModule) => m.id === selected.value!.id) || modules.value[0]
  }
}

function selectModule(module: SkillModule) {
  selected.value = module
}

function openAddModule() {
  isEditModule.value = false
  moduleForm.value = { name: '', trackName: DEFAULT_TRACK }
  showModuleDialog.value = true
}

function openEditModule(module: SkillModule) {
  isEditModule.value = true
  selected.value = module
  const trackName = module.trackName || DEFAULT_TRACK
  moduleForm.value = { name: module.name, trackName: (TRACK_OPTIONS as readonly string[]).includes(trackName) ? trackName : DEFAULT_TRACK }
  showModuleDialog.value = true
}

async function handleModuleSubmit() {
  if (!moduleForm.value.name) {
    ElMessage.warning('请输入模块名称')
    return
  }
  if (isEditModule.value && selected.value) {
    await updateCategory(selected.value.id, moduleForm.value)
    ElMessage.success('模块更新成功')
  } else {
    await createCategory(moduleForm.value)
    ElMessage.success('模块创建成功')
  }
  showModuleDialog.value = false
  await loadModules()
}

function openAddSkill() {
  isEditSkill.value = false
  editSkillId.value = null
  const nextSort = selected.value ? selected.value.skills.length + 1 : 0
  skillForm.value = {
    name: '',
    description: '',
    expectedMinutes: 0,
    difficultyLevel: 1,
    scoreWeight: 20,
    sortOrder: nextSort,
    status: 'active',
  }
  showSkillDialog.value = true
}

function openEditSkill(skill: SkillPoint) {
  isEditSkill.value = true
  editSkillId.value = skill.id
  skillForm.value = {
    name: skill.name,
    description: skill.description || '',
    expectedMinutes: skill.expectedMinutes || 0,
    difficultyLevel: skill.difficultyLevel || skill.difficulty || 1,
    scoreWeight: skill.scoreWeight ?? skill.weight ?? 20,
    sortOrder: skill.sortOrder || 0,
    status: skill.status || 'active',
  }
  showSkillDialog.value = true
}

async function handleSkillSubmit() {
  if (!skillForm.value.name || !selected.value) {
    ElMessage.warning('请填写技能点名称')
    return
  }
  if (isEditSkill.value && editSkillId.value) {
    await updateTask(editSkillId.value, skillForm.value)
    ElMessage.success('技能点更新成功')
  } else {
    await createTask(selected.value.id, skillForm.value)
    ElMessage.success('技能点创建成功')
  }
  showSkillDialog.value = false
  await loadModules()
}

function setDifficulty(index: number, diff: 1 | 2 | 3) {
  if (!selected.value) return
  const skill = selected.value.skills[index]
  updateTask(skill.id, { difficultyLevel: diff }).then(() => loadModules())
}

function moveSkill(index: number, direction: -1 | 1) {
  if (!selected.value) return
  const skills = selected.value.skills
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= skills.length) return
  const a = skills[index]
  const b = skills[newIndex]
  updateTask(a.id, { sortOrder: b.sortOrder }).then(() => {
    updateTask(b.id, { sortOrder: a.sortOrder }).then(() => loadModules())
  })
}

function toggleSkillStatus(skill: SkillPoint) {
  updateTask(skill.id, { status: skill.status === 'active' ? 'inactive' : 'active' }).then(() => loadModules())
}

onMounted(loadModules)
</script>

<template>
  <div class="skills">
    <div class="page-header">
      <div>
        <h1>技能树管理</h1>
        <p>// SKILL TREE MANAGEMENT — {{ modules.length }} MODULES / {{ modules.reduce((s: number, m: SkillModule) => s + m.skills.length, 0) }} SKILLS</p>
      </div>
    </div>

    <div class="split-layout">
      <div class="module-panel">
        <div class="panel-header">
          <span>训练模块</span>
          <button class="add-btn" @click="openAddModule">
            <el-icon :size="11"><Plus /></el-icon> 新增模块
          </button>
        </div>

        <div class="module-list">
          <div
            v-for="module in modules"
            :key="module.id"
            :class="['module-item', { active: selected?.id === module.id }]"
            :style="selected?.id === module.id ? { borderColor: module.color + '55', background: module.color + '15', boxShadow: `0 0 16px ${module.color}22` } : {}"
            @click="selectModule(module)"
          >
            <div class="module-dot" :style="{ background: module.color, boxShadow: `0 0 8px ${module.color}88` }" />
            <div class="module-info">
              <span class="module-name">{{ module.name }}</span>
              <span class="module-count">{{ module.skills.length }} 个技能点</span>
            </div>
            <button class="module-edit" @click.stop="openEditModule(module)">
              <span style="color:#64748b;font-size:11px">编辑</span>
            </button>
          </div>
        </div>
      </div>

      <div class="skill-panel" v-if="selected">
        <div class="panel-header">
          <div class="panel-title">
            <span class="panel-dot" :style="{ background: selected.color, boxShadow: `0 0 8px ${selected.color}` }" />
            <span class="panel-name" :style="{ color: selected.color }">{{ selected.name }}</span>
            <span class="panel-sub">/ 技能点列表</span>
          </div>
          <button class="add-btn" @click="openAddSkill">
            <el-icon :size="11"><Plus /></el-icon> 新增技能点
          </button>
        </div>

        <div v-if="selected.skills.length === 0" class="empty-state">
          <p>暂无技能点，点击右上角新增</p>
        </div>

        <div v-else class="skill-grid">
          <div
            v-for="(skill, index) in selected.skills"
            :key="skill.id"
            class="cyber-card skill-card"
          >
            <div class="skill-top">
              <div class="skill-name">
                <span class="skill-dot" :style="{ color: selected.color }">◆</span>
                <span>{{ skill.name }}</span>
              </div>
              <div class="skill-stars">
                <button
                  v-for="d in 3"
                  :key="d"
                  @click="setDifficulty(index, d as 1|2|3)"
                  :style="{ color: (skill.difficultyLevel || skill.difficulty) >= d ? '#ff9f00' : '#3a5070' }"
                  class="star-btn"
                >
                  <el-icon :size="13"><Star /></el-icon>
                </button>
              </div>
            </div>
            <div class="skill-mid">
              <span class="skill-mid-label">预计 {{ skill.expectedMinutes || 0 }} 分钟</span>
              <div class="weight-bar">
                <div class="weight-fill" :style="{ width: (skill.scoreWeight ?? skill.weight) * 0.8 + 'px', background: `linear-gradient(90deg, ${selected.color}, ${selected.color}88)` }" />
              </div>
              <span class="weight-num" :style="{ color: selected.color }">{{ skill.scoreWeight ?? skill.weight }}%</span>
            </div>
            <p class="skill-desc">{{ skill.description || '暂无描述' }}</p>
            <div class="skill-bottom">
              <span class="diff-tag" :style="{ color: selected.color, background: selected.color + '15', borderColor: selected.color + '40' }">
                {{ (skill.difficultyLevel || skill.difficulty) === 1 ? '简单' : (skill.difficultyLevel || skill.difficulty) === 2 ? '中等' : '困难' }}
              </span>
              <span class="diff-tag" :style="{ color: skill.status === 'active' ? '#00ff9f' : '#64748b', background: skill.status === 'active' ? 'rgba(0,255,159,0.12)' : 'rgba(100,116,139,0.12)', borderColor: skill.status === 'active' ? 'rgba(0,255,159,0.3)' : 'rgba(100,116,139,0.3)' }">
                {{ skill.status === 'active' ? '启用' : '停用' }}
              </span>
              <div class="skill-actions">
                <button class="arrow-btn" @click="moveSkill(index, -1)" :disabled="index === 0">
                  <el-icon :size="12"><ArrowUp /></el-icon>
                </button>
                <button class="arrow-btn" @click="moveSkill(index, 1)" :disabled="index === selected.skills.length - 1">
                  <el-icon :size="12"><ArrowDown /></el-icon>
                </button>
                <button class="arrow-btn" @click="openEditSkill(skill)">
                  <span style="font-size:11px">编辑</span>
                </button>
                <button class="arrow-btn" @click="toggleSkillStatus(skill)">
                  <span style="font-size:11px">{{ skill.status === 'active' ? '停用' : '启用' }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showModuleDialog" :title="isEditModule ? '编辑模块' : '新增模块'" width="440px">
      <el-form :model="moduleForm" label-position="top">
        <el-form-item label="模块名称">
          <el-input v-model="moduleForm.name" placeholder="请输入模块名称" />
        </el-form-item>
        <el-form-item label="所属赛道">
          <el-select v-model="moduleForm.trackName" style="width:100%">
            <el-option
              v-for="track in TRACK_OPTIONS"
              :key="track"
              :label="track"
              :value="track"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModuleDialog = false">取消</el-button>
        <el-button type="primary" @click="handleModuleSubmit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showSkillDialog" :title="isEditSkill ? '编辑技能点' : '新增技能点'" width="440px">
      <el-form :model="skillForm" label-position="top">
        <el-form-item label="技能点名称">
          <el-input v-model="skillForm.name" placeholder="请输入技能点名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="skillForm.description" type="textarea" :rows="3" placeholder="请输入技能点描述" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预计展示时长（分钟）">
              <el-input-number v-model="skillForm.expectedMinutes" :min="0" :max="10000" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="skillForm.sortOrder" :min="0" :max="9999" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="难度等级">
          <div class="diff-selector">
            <button
              v-for="d in 3"
              :key="d"
              type="button"
              :class="['diff-btn', { active: skillForm.difficultyLevel === d }]"
              @click="skillForm.difficultyLevel = d as 1|2|3"
            >
              {{ d === 1 ? '简单' : d === 2 ? '中等' : '困难' }}
            </button>
          </div>
        </el-form-item>
        <el-form-item label="权重">
          <el-slider v-model="skillForm.scoreWeight" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="skillForm.status" style="width:100%">
            <el-option label="启用" value="active" />
            <el-option label="停用" value="inactive" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSkillDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSkillSubmit">确认</el-button>
      </template>
    </el-dialog>
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

.split-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
  align-items: start;
}

.module-panel, .skill-panel {
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: 8px;
  padding: 0;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.panel-header > span:first-child {
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: $color-accent-blue;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.panel-name {
  font-size: 14px;
  font-weight: 500;
}

.panel-sub {
  font-size: 12px;
  color: $color-text-muted;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: rgba(0, 255, 159, 0.05);
  border: 1px solid rgba(0, 255, 159, 0.25);
  color: $color-accent-cyan;
  transition: all 0.2s;

  &:hover {
    background: rgba(0, 255, 159, 0.12);
  }
}

.module-list {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.module-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
  background: rgba(4, 12, 28, 0.8);
}

.module-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.module-info {
  flex: 1;
  min-width: 0;
}

.module-name {
  font-size: 14px;
  font-weight: 500;
  color: $color-text-primary;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.module-count {
  font-size: 11px;
  color: $color-text-muted;
}

.module-edit {
  background: none;
  border: none;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.module-item:hover .module-edit {
  opacity: 1;
}

.empty-state {
  padding: 40px;
  text-align: center;
  color: $color-text-muted;
}

.skill-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 16px;
}

.skill-card {
  padding: 16px;
}

.skill-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.skill-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: $color-text-primary;
}

.skill-dot {
  font-size: 8px;
}

.skill-stars {
  display: flex;
  gap: 2px;
}

.star-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.skill-mid {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: $color-text-muted;
  margin-bottom: 8px;
}

.skill-mid-label {
  font-size: 12px;
  color: $color-text-muted;
}

.weight-bar {
  width: 80px;
  height: 6px;
  background: rgba(0, 212, 255, 0.08);
  border-radius: 3px;
  overflow: hidden;
}

.weight-fill {
  height: 100%;
  border-radius: 3px;
}

.weight-num {
  font-family: $font-mono;
  font-size: 12px;
}

.skill-bottom {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: space-between;
}

.skill-desc {
  min-height: 32px;
  margin: 0 0 12px;
  color: $color-text-muted;
  font-size: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.diff-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid;
}

.skill-actions {
  display: flex;
  gap: 4px;
}

.arrow-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
  background: none;
  border: 1px solid rgba(0, 212, 255, 0.1);
  color: $color-text-muted;
  cursor: pointer;

  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }
}

.diff-selector {
  display: flex;
  gap: 8px;
}

.diff-btn {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  background: rgba(0, 212, 255, 0.04);
  border: 1px solid rgba(0, 212, 255, 0.2);
  color: $color-text-muted;
  transition: all 0.2s;

  &.active {
    background: rgba(0, 212, 255, 0.12);
    border-color: rgba(0, 212, 255, 0.4);
    color: $color-accent-blue;
  }
}
</style>
