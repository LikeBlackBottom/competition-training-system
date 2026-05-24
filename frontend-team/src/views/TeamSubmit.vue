<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getFormOptions, submitRecord } from '@/api/team'
import type { FormOption, SkillPoint, SubmitRecord } from '@/types'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import CyberBg from '@/components/CyberBg.vue'

const router = useRouter()
const authStore = useAuthStore()

const formOptions = ref<FormOption>({ modules: [], members: [] })
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formLoading = ref(true)

const form = ref({
  memberId: null as number | null,
  moduleId: null as number | null,
  skillId: null as number | null,
  durationMinutes: null as number | null,
  completionStatus: '' as string,
  todayOutput: '',
  issue: '',
  needHelp: false,
})

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

const filteredSkills = computed<SkillPoint[]>(() => {
  if (!form.value.moduleId) return []
  const mod = formOptions.value.modules.find(
    (m) => m.id === form.value.moduleId,
  )
  return mod?.skills || []
})

watch(
  () => form.value.moduleId,
  () => {
    form.value.skillId = null
  },
)

const rules: FormRules = {
  memberId: [{ required: true, message: '请选择队员', trigger: 'change' }],
  moduleId: [{ required: true, message: '请选择训练模块', trigger: 'change' }],
  skillId: [{ required: true, message: '请选择技能点', trigger: 'change' }],
  durationMinutes: [
    { required: true, message: '请输入展示时长', trigger: 'blur' },
    {
      type: 'number',
      min: 1,
      max: 180,
      message: '展示时长范围为 1-180 分钟',
      trigger: 'blur',
    },
  ],
  completionStatus: [
    { required: true, message: '请选择完成状态', trigger: 'change' },
  ],
  todayOutput: [
    { required: true, message: '请填写今日产出', trigger: 'blur' },
  ],
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const data: SubmitRecord = {
        memberId: form.value.memberId!,
        moduleId: form.value.moduleId!,
        skillId: form.value.skillId!,
        durationMinutes: form.value.durationMinutes!,
        completionStatus: form.value.completionStatus as
          | 'notStarted'
          | 'inProgress'
          | 'completed'
          | 'partial'
          | 'mastered'
          | 'blocked',
        todayOutput: form.value.todayOutput,
        issue: form.value.issue,
        needHelp: form.value.needHelp,
      }
      await submitRecord(data)
      router.push('/team/success')
    } catch {
      ElMessage.error('提交失败，请重试')
    } finally {
      submitting.value = false
    }
  })
}

function handleLogout() {
  authStore.logout()
  router.push('/team/login')
}

const isFormValid = computed(() => {
  return (
    form.value.memberId &&
    form.value.moduleId &&
    form.value.skillId &&
    form.value.durationMinutes &&
    form.value.completionStatus &&
    form.value.todayOutput.trim()
  )
})

onMounted(async () => {
  if (authStore.teamInfo) {
    try {
      formOptions.value = await getFormOptions(authStore.teamInfo.id)
    } catch {
      ElMessage.error('获取表单选项失败')
    } finally {
      formLoading.value = false
    }
  }
})
</script>

<template>
  <CyberBg>
    <!-- Header -->
    <header class="page-header">
      <div class="header-left">
        <span class="header-brand">赛训数据中枢</span>
        <div class="header-divider" />
        <span class="header-team">{{ authStore.teamInfo?.teamName }}</span>
        <span class="header-track">{{ authStore.teamInfo?.track }}</span>
      </div>
      <div class="header-right">
        <span class="header-date">{{ today }}</span>
        <span class="header-status">
          <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
            <circle cx="5" cy="5" r="4" stroke="#7cffcb" stroke-width="1" />
            <path d="M3 5l1.5 1.5L7 3.5" stroke="#7cffcb" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          今日可提交
        </span>
        <el-button class="logout-btn" @click="handleLogout">
          <svg width="13" height="13" viewBox="0 0 13 13" fill="none">
            <path d="M5 1H3a1 1 0 00-1 1v9a1 1 0 001 1h2M9 9.5l3-3-3-3M12 6.5H5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          退出
        </el-button>
      </div>
    </header>

    <div class="page-content" v-loading="formLoading">
      <div class="content-header">
        <h1 class="content-title">新增训练记录</h1>
        <p class="content-subtitle">Add Training Record — {{ today }}</p>
      </div>

      <div class="content-grid">
        <!-- Main Form -->
        <div class="form-col">
          <div class="cyber-card form-card">
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              class="submit-form"
            >
              <div class="form-row">
                <el-form-item label="队员" prop="memberId" class="form-half">
                  <el-select
                    v-model="form.memberId"
                    placeholder="请选择队员"
                    class="cyber-select"
                  >
                    <el-option
                      v-for="m in formOptions.members"
                      :key="m.id"
                      :label="`${m.name}`"
                      :value="m.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="训练模块" prop="moduleId" class="form-half">
                  <el-select
                    v-model="form.moduleId"
                    placeholder="请选择训练模块"
                    class="cyber-select"
                  >
                    <el-option
                      v-for="m in formOptions.modules"
                      :key="m.id"
                      :label="m.name"
                      :value="m.id"
                    />
                  </el-select>
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="技能点" prop="skillId" class="form-half">
                  <el-select
                    v-model="form.skillId"
                    placeholder="请先选择训练模块"
                    class="cyber-select"
                    :disabled="!form.moduleId"
                  >
                    <el-option
                      v-for="s in filteredSkills"
                      :key="s.id"
                      :label="s.name"
                      :value="s.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="代码展示时长（分钟）" prop="durationMinutes" class="form-half">
                  <el-input-number
                    v-model="form.durationMinutes"
                    :min="1"
                    :max="180"
                    :step="1"
                    placeholder="1-180 分钟"
                    controls-position="right"
                    class="cyber-number-input"
                  />
                </el-form-item>
              </div>

              <el-form-item label="完成状态" prop="completionStatus">
                <el-radio-group v-model="form.completionStatus" class="cyber-radio-group">
                  <el-radio value="notStarted">未开始</el-radio>
                  <el-radio value="inProgress">进行中</el-radio>
                  <el-radio value="completed">已完成</el-radio>
                  <el-radio value="partial">部分完成</el-radio>
                  <el-radio value="mastered">已掌握</el-radio>
                  <el-radio value="blocked">受阻</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="今日产出" prop="todayOutput">
                <el-input
                  v-model="form.todayOutput"
                  type="textarea"
                  :rows="3"
                  placeholder="请简要描述今日训练的成果、完成的任务、输出的材料等..."
                  class="cyber-textarea"
                />
              </el-form-item>

              <el-form-item label="遇到问题">
                <el-input
                  v-model="form.issue"
                  type="textarea"
                  :rows="2"
                  placeholder="如遇到技术难点、理解困难、环境问题等，请如实填写（无问题可留空）..."
                  class="cyber-textarea"
                />
              </el-form-item>

              <el-form-item label="是否需要老师协助">
                <el-radio-group v-model="form.needHelp" class="cyber-radio-group">
                  <el-radio :value="false">不需要</el-radio>
                  <el-radio :value="true">需要协助</el-radio>
                </el-radio-group>
              </el-form-item>

              <div class="form-warning">
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                  <path d="M7 1v7M7 11v.5" stroke="#ffd166" stroke-width="1.5" stroke-linecap="round" />
                  <circle cx="7" cy="7" r="6" stroke="#ffd166" stroke-width="1" />
                </svg>
                提交后不可查看、修改或删除，请仔细核对
              </div>

              <el-button
                type="primary"
                size="large"
                :loading="submitting"
                :disabled="!isFormValid || submitting"
                class="submit-btn"
                @click="handleSubmit"
              >
                <svg v-if="!submitting" width="15" height="15" viewBox="0 0 15 15" fill="none">
                  <path d="M14 1L7 8M14 1L8 14l-2-6-6-2L14 1z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
                {{ submitting ? '提交中...' : '提交训练记录' }}
              </el-button>
            </el-form>
          </div>
        </div>

        <!-- Info Sidebar -->
        <div class="sidebar-col">
          <div class="cyber-card accent-purple sidebar-card">
            <div class="sidebar-card-header">
              <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                <circle cx="7.5" cy="7.5" r="6.5" stroke="#a78bfa" stroke-width="1.3" />
                <path d="M7.5 4v3.5M7.5 10.5v.3" stroke="#a78bfa" stroke-width="1.5" stroke-linecap="round" />
              </svg>
              <span class="sidebar-card-title text-purple">今日训练提交说明</span>
            </div>
            <div class="sidebar-card-body">
              <p>1. 每条记录对应<span class="text-white">一个技能点</span>的训练，可提交多条</p>
              <p>2. 代码展示时长请如实填写单位分钟，范围 1-180 分钟</p>
              <p>3. 今日产出请尽量具体，便于老师评估进度</p>
              <p>4. 遇到问题请如实填写，不会影响成绩</p>
              <p>5. 需要老师协助的问题将在管理端<span class="text-orange">优先处理</span></p>
            </div>
          </div>

          <div class="cyber-card accent-red sidebar-card">
            <div class="sidebar-card-header">
              <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                <path d="M7.5 1.5v7M7.5 11.5v.5" stroke="#ff5c9e" stroke-width="1.5" stroke-linecap="round" />
                <circle cx="7.5" cy="7.5" r="6.5" stroke="#ff5c9e" stroke-width="1.3" />
              </svg>
              <span class="sidebar-card-title text-red">权限提示</span>
            </div>
            <div class="sidebar-card-body">
              <p>提交后<span class="text-red"> 不可查看</span>历史记录</p>
              <p>提交后<span class="text-red"> 不可修改</span>已提交数据</p>
              <p>提交后<span class="text-red"> 不可删除</span>已有记录</p>
              <p class="sidebar-card-note">如需修改请联系指导老师在管理端操作</p>
            </div>
          </div>

          <div class="cyber-card sidebar-card">
            <div class="sidebar-card-header">
              <span class="sidebar-card-label">// TEAM STATUS</span>
            </div>
            <div class="sidebar-card-body sidebar-card-stats">
              <div class="stat-row">
                <span class="stat-label">队伍</span>
                <span class="stat-value">{{ authStore.teamInfo?.teamName }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">院校</span>
                <span class="stat-value">{{ authStore.teamInfo?.schoolName }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">赛道</span>
                <span class="stat-value text-cyan">{{ authStore.teamInfo?.track }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </CyberBg>
</template>

<style scoped>
.page-header {
  position: sticky;
  top: 0;
  z-index: 20;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(5, 7, 22, 0.95);
  border-bottom: 1px solid rgba(255, 122, 223, 0.12);
  backdrop-filter: blur(12px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-brand {
  font-size: 12px;
  font-family: 'Share Tech Mono', monospace;
  letter-spacing: 0.1em;
  color: #ff7adf;
}

.header-divider {
  width: 1px;
  height: 16px;
  background: rgba(255, 122, 223, 0.2);
}

.header-team {
  font-size: 14px;
  font-weight: 500;
  color: #f8f7ff;
}

.header-track {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
  font-family: 'Share Tech Mono', monospace;
  background: rgba(124, 255, 203, 0.08);
  border: 1px solid rgba(124, 255, 203, 0.25);
  color: #7cffcb;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-date {
  font-size: 12px;
  font-family: 'Share Tech Mono', monospace;
  color: #7f8bb3;
}

.header-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 3px;
  font-size: 12px;
  background: rgba(124, 255, 203, 0.06);
  color: #7cffcb;
  border: 1px solid rgba(124, 255, 203, 0.2);
}

.logout-btn {
  background: transparent !important;
  border: 1px solid rgba(255, 122, 223, 0.2) !important;
  color: #b8c3e8 !important;
  font-size: 12px !important;
  padding: 6px 14px !important;
  display: flex !important;
  align-items: center !important;
  gap: 6px !important;
}

.logout-btn:hover {
  border-color: rgba(255, 122, 223, 0.4) !important;
  color: #ff7adf !important;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.content-header {
  margin-bottom: 24px;
}

.content-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.3rem;
  color: #f8f7ff;
  letter-spacing: 0.06em;
  margin-bottom: 4px;
  text-shadow: 0 0 12px rgba(255, 122, 223, 0.2);
}

.content-subtitle {
  font-size: 12px;
  color: #7f8bb3;
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.form-card {
  padding: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.submit-form :deep(.el-form-item__label) {
  font-size: 11px !important;
}

.cyber-select {
  width: 100%;
}

.cyber-number-input {
  width: 100%;
}

.cyber-radio-group {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.cyber-radio-group :deep(.el-radio) {
  margin-right: 0;
}

.cyber-radio-group :deep(.el-radio__label) {
  font-size: 14px !important;
}

.cyber-textarea {
  width: 100%;
}

.form-warning {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  border-radius: 4px;
  font-size: 13px;
  color: #ffd166;
  background: rgba(255, 209, 102, 0.05);
  border: 1px solid rgba(255, 209, 102, 0.18);
}

.submit-btn {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
  letter-spacing: 0.05em !important;
}

.sidebar-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card {
  padding: 20px;
}

.sidebar-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.sidebar-card-title {
  font-size: 14px;
  font-weight: 500;
}

.sidebar-card-label {
  font-size: 12px;
  color: #ff7adf;
  font-family: 'Share Tech Mono', monospace;
}

.sidebar-card-body {
  font-size: 12px;
  line-height: 1.8;
  color: #7f8bb3;
}

.sidebar-card-body p {
  margin-bottom: 2px;
}

.sidebar-card-note {
  color: #5a6080;
  padding-top: 4px;
  margin-top: 4px;
  border-top: 1px solid rgba(255, 122, 223, 0.06);
}

.text-white {
  color: #f8f7ff;
}

.text-purple {
  color: #a78bfa;
}

.text-red {
  color: #ff5c9e;
}

.text-orange {
  color: #ffd166;
}

.text-cyan {
  color: #6ee7ff;
}

.sidebar-card-stats .stat-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.sidebar-card-stats .stat-row:last-child {
  margin-bottom: 0;
}

.stat-label {
  color: #7f8bb3;
  font-size: 12px;
}

.stat-value {
  color: #f8f7ff;
  font-size: 12px;
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    gap: 12px;
  }

  .header-left,
  .header-right {
    width: 100%;
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
