<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import CyberBg from '@/components/CyberBg.vue'

const router = useRouter()
const authStore = useAuthStore()

const code = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (!code.value.trim()) {
    error.value = '请输入邀请码'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await authStore.login(code.value.trim().toUpperCase())
    ElMessage.success('登录成功')
    router.push('/team/submit')
  } catch (e: unknown) {
    error.value = (e as Error).message || '邀请码无效或已过期，请联系指导老师获取'
  } finally {
    loading.value = false
  }
}

function handleInput(val: string) {
  code.value = val.toUpperCase()
  error.value = ''
}
</script>

<template>
  <CyberBg class="login-page">
    <!-- Decorative corner lines -->
    <div class="corner-lines">
      <div class="corner-line corner-tl" />
      <div class="corner-line corner-tr" />
      <div class="corner-line corner-bl" />
      <div class="corner-line corner-br" />
    </div>

    <!-- Background glow orbs -->
    <div class="glow-orb glow-orb-top" />
    <div class="glow-orb glow-orb-bottom" />

    <div class="login-wrapper">
      <!-- Logo & Title -->
      <div class="login-header">
        <div class="logo-icon">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <path d="M14 2L3 9v10l11 7 11-7V9L14 2z" stroke="#00d4ff" stroke-width="1.5" stroke-linejoin="round" />
            <path d="M14 9v10M8 6l-5 3.5M20 6l5 3.5" stroke="#00d4ff" stroke-width="1.5" stroke-linecap="round" />
          </svg>
        </div>
        <h1 class="login-title">赛训数据中枢</h1>
        <p class="login-subtitle">Competition Training Command Center</p>
      </div>

      <!-- Login Card -->
      <div class="login-card-wrapper">
        <div class="login-card-corner login-card-corner-tl" />
        <div class="login-card-corner login-card-corner-br" />

        <div class="login-card-header">
          <h2 class="login-card-label">// TEAM PORTAL ACCESS</h2>
          <p class="login-card-desc">请使用指导老师分配的邀请码登录队员端</p>
        </div>

        <form class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label">队伍邀请码</label>
            <el-input
              v-model="code"
              placeholder="请输入邀请码，例如 CQSX0075"
              class="invite-input"
              :class="{ 'is-error': error }"
              @input="handleInput"
              @keyup.enter="handleLogin"
            />
            <div v-if="error" class="error-message">
              <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                <path d="M6 1v6M6 9.5v.5" stroke="#ff2d55" stroke-width="1.5" stroke-linecap="round" />
                <circle cx="6" cy="6" r="5" stroke="#ff2d55" stroke-width="1" />
              </svg>
              {{ error }}
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <svg v-if="!loading" width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M8 1v8M4 5l4-4 4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M3 11v3h10v-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
            </svg>
            <span>{{ loading ? '验证中...' : '进入队员端' }}</span>
          </el-button>
        </form>

        <!-- Rules warning -->
        <div class="rules-warning">
          <div class="rules-warning-header">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M7 1v7M7 11v.5" stroke="#ff9f00" stroke-width="1.5" stroke-linecap="round" />
              <circle cx="7" cy="7" r="6" stroke="#ff9f00" stroke-width="1" />
            </svg>
            <span class="rules-warning-title">队员端权限说明</span>
          </div>
          <p class="rules-warning-text">
            队员仅可新增训练记录，提交后<span class="text-red">不可查看、修改或删除</span>。如需更改请联系指导老师。
          </p>
        </div>
      </div>

      <p class="footer-text">COMPETITION TRAINING MANAGEMENT SYSTEM v2.0</p>
    </div>
  </CyberBg>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
}

.corner-lines {
  pointer-events: none;
  position: fixed;
  z-index: 5;
  inset: 0;
}

.corner-line {
  position: absolute;
  width: 64px;
  height: 64px;
  opacity: 0.4;
}

.corner-tl {
  top: 32px;
  left: 32px;
  border-top: 2px solid #00d4ff;
  border-left: 2px solid #00d4ff;
}

.corner-tr {
  top: 32px;
  right: 32px;
  border-top: 2px solid #00d4ff;
  border-right: 2px solid #00d4ff;
}

.corner-bl {
  bottom: 32px;
  left: 32px;
  border-bottom: 2px solid #a855f7;
  border-left: 2px solid #a855f7;
}

.corner-br {
  bottom: 32px;
  right: 32px;
  border-bottom: 2px solid #a855f7;
  border-right: 2px solid #a855f7;
}

.glow-orb {
  position: fixed;
  width: 384px;
  height: 384px;
  border-radius: 50%;
  pointer-events: none;
}

.glow-orb-top {
  top: 25%;
  left: 25%;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.06) 0%, transparent 70%);
}

.glow-orb-bottom {
  bottom: 25%;
  right: 25%;
  background: radial-gradient(circle, rgba(168, 85, 247, 0.06) 0%, transparent 70%);
}

.login-wrapper {
  width: 100%;
  max-width: 448px;
  padding: 0 16px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 12px;
  margin-bottom: 16px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.4);
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.4);
  animation: float 4s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.login-title {
  margin-bottom: 8px;
  font-family: 'Orbitron', sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: #c0d8f0;
  letter-spacing: 0.08em;
  text-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

.login-subtitle {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: #5a7090;
  font-family: 'Share Tech Mono', monospace;
}

.login-card-wrapper {
  position: relative;
  border-radius: 12px;
  padding: 32px;
  background: rgba(4, 12, 28, 0.95);
  border: 1px solid rgba(0, 212, 255, 0.35);
  box-shadow: 0 0 40px rgba(0, 212, 255, 0.15),
    0 0 80px rgba(168, 85, 247, 0.08),
    inset 0 0 40px rgba(0, 0, 0, 0.3);
  animation: borderGlow 3s ease-in-out infinite;
}

@keyframes borderGlow {
  0%, 100% { opacity: 0.85; }
  50% { opacity: 1; }
}

.login-card-corner {
  position: absolute;
  width: 48px;
  height: 48px;
}

.login-card-corner-tl {
  top: 0;
  left: 0;
  border-top: 2px solid #00d4ff;
  border-left: 2px solid #00d4ff;
  border-radius: 8px 0 0 0;
}

.login-card-corner-br {
  bottom: 0;
  right: 0;
  border-bottom: 2px solid #a855f7;
  border-right: 2px solid #a855f7;
  border-radius: 0 0 8px 0;
}

.login-card-header {
  margin-bottom: 24px;
}

.login-card-label {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #00d4ff;
  font-family: 'Share Tech Mono', monospace;
  letter-spacing: 0.1em;
}

.login-card-desc {
  font-size: 12px;
  color: #5a7090;
}

.login-form {
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 12px;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  margin-bottom: 8px;
  color: #00d4ff;
  font-family: 'Share Tech Mono', monospace;
}

.invite-input :deep(.el-input__wrapper) {
  background: rgba(0, 212, 255, 0.04) !important;
  box-shadow: 0 0 0 1px rgba(0, 212, 255, 0.25) inset !important;
  padding: 6px 16px !important;
  transition: all 0.2s;
}

.invite-input :deep(.el-input__inner) {
  color: #00d4ff !important;
  letter-spacing: 0.15em !important;
  font-family: 'Share Tech Mono', monospace !important;
  font-size: 14px !important;
}

.invite-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(0, 212, 255, 0.5) inset !important;
}

.invite-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(0, 212, 255, 0.8) inset,
    0 0 16px rgba(0, 212, 255, 0.2) !important;
}

.invite-input.is-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px rgba(255, 45, 85, 0.7) inset !important;
}

.invite-input.is-error :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(255, 45, 85, 0.7) inset,
    0 0 12px rgba(255, 45, 85, 0.15) !important;
}

.error-message {
  margin-top: 8px;
  font-size: 12px;
  color: #ff2d55;
  display: flex;
  align-items: center;
  gap: 6px;
}

.login-btn {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
  letter-spacing: 0.05em !important;
}

.rules-warning {
  margin-top: 24px;
  padding: 12px;
  border-radius: 6px;
  background: rgba(255, 159, 0, 0.06);
  border: 1px solid rgba(255, 159, 0, 0.25);
}

.rules-warning-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rules-warning-title {
  font-size: 12px;
  font-weight: 500;
  color: #ff9f00;
}

.rules-warning-text {
  font-size: 12px;
  line-height: 1.6;
  color: #5a7090;
}

.footer-text {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #3a5070;
  font-family: 'Share Tech Mono', monospace;
}
</style>
