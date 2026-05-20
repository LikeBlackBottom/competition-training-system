<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Lock, User, WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = '请输入账号和密码'
    return
  }
  loading.value = true
  try {
    await authStore.login(username.value, password.value)
    const redirect = (route.query.redirect as string) || '/admin/dashboard'
    router.push(redirect)
  } catch (e: any) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="bg-decorations">
      <div class="corner-top-left" />
      <div class="corner-bottom-right" />
      <div class="accent-line top-line-left" />
      <div class="accent-line top-line-short" />
      <div class="accent-line bottom-line-right" />
      <div class="accent-line bottom-line-short" />
      <div class="status-left">
        <div>SYS: ONLINE</div>
        <div>AUTH: REQUIRED</div>
      </div>
      <div class="status-right">
        <div>CTMS v2.0.1</div>
        <div>SEC-LEVEL: A1</div>
      </div>
    </div>

    <div class="login-container">
      <div class="login-header">
        <div class="login-badge">
          <span class="badge-text">ADMIN COMMAND CENTER</span>
        </div>
        <h1 class="login-title">
          赛训管理中枢
        </h1>
        <p class="login-subtitle">Competition Training Administration Center</p>
      </div>

      <div class="login-card">
        <div class="card-accent-bar" />
        <div class="card-header">
          <el-icon :size="16" color="#00d4ff"><Lock /></el-icon>
          <span>指导老师登录</span>
          <span class="blink-cursor">█</span>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>管理账号</label>
            <div class="input-wrap">
              <el-icon :size="15" class="input-icon"><User /></el-icon>
              <input
                v-model="username"
                type="text"
                placeholder="请输入账号"
                class="cyber-input"
                :class="{ 'input-error': error }"
                @input="error = ''"
              />
            </div>
          </div>

          <div class="form-group">
            <label>登录密码</label>
            <div class="input-wrap">
              <el-icon :size="15" class="input-icon"><Lock /></el-icon>
              <input
                v-model="password"
                type="password"
                placeholder="请输入密码"
                class="cyber-input"
                :class="{ 'input-error': error }"
                @input="error = ''"
              />
            </div>
          </div>

          <div v-if="error" class="error-msg">
            <el-icon :size="12"><WarningFilled /></el-icon>
            {{ error }}
          </div>

          <button type="submit" class="cyber-btn-primary login-btn" :disabled="loading">
            <template v-if="loading">
              <span class="spinner" />
              验证身份...
            </template>
            <template v-else>
              <el-icon :size="15"><Lock /></el-icon>
              进入管理后台
            </template>
          </button>
        </form>

        <div class="demo-hint">请使用管理员账号登录系统</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $color-bg-primary;
  background-image:
    linear-gradient(rgba(0, 212, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  position: relative;
  overflow: hidden;
}

.bg-decorations {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.corner-top-left {
  position: absolute;
  top: 24px;
  left: 24px;
  width: 80px;
  height: 80px;
  border-top: 2px solid rgba(0, 212, 255, 0.5);
  border-left: 2px solid rgba(0, 212, 255, 0.5);
  border-radius: 4px 0 0 0;
}

.corner-bottom-right {
  position: absolute;
  bottom: 24px;
  right: 24px;
  width: 80px;
  height: 80px;
  border-bottom: 2px solid rgba(168, 85, 247, 0.5);
  border-right: 2px solid rgba(168, 85, 247, 0.5);
  border-radius: 0 0 4px 0;
}

.accent-line {
  position: absolute;
  height: 1px;
}

.top-line-left {
  top: 80px;
  left: 32px;
  width: 256px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.4), transparent);
}

.top-line-short {
  top: 96px;
  left: 32px;
  width: 160px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.2), transparent);
}

.bottom-line-right {
  bottom: 80px;
  right: 32px;
  width: 256px;
  background: linear-gradient(270deg, rgba(168, 85, 247, 0.4), transparent);
}

.bottom-line-short {
  bottom: 96px;
  right: 32px;
  width: 160px;
  background: linear-gradient(270deg, rgba(168, 85, 247, 0.2), transparent);
}

.status-left {
  position: absolute;
  bottom: 32px;
  left: 32px;
  font-family: $font-mono;
  font-size: 12px;
  color: rgba(100, 116, 139, 0.5);
  line-height: 1.6;
}

.status-right {
  position: absolute;
  top: 32px;
  right: 32px;
  text-align: right;
  font-family: $font-mono;
  font-size: 12px;
  color: rgba(100, 116, 139, 0.5);
  line-height: 1.6;
}

.login-container {
  width: 100%;
  max-width: 380px;
  padding: 0 16px;
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  background: rgba(0, 212, 255, 0.06);
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.badge-text {
  font-family: $font-mono;
  font-size: 11px;
  letter-spacing: 0.1em;
  color: $color-accent-blue;
}

.login-title {
  font-family: $font-heading;
  font-size: 2rem;
  font-weight: 800;
  color: $color-text-primary;
  letter-spacing: 0.08em;
  line-height: 1.2;
}

.login-subtitle {
  margin-top: 8px;
  font-size: 11px;
  font-family: $font-mono;
  letter-spacing: 0.05em;
  color: $color-text-muted;
}

.login-card {
  border-radius: 12px;
  padding: 32px;
  position: relative;
  overflow: hidden;
  background: rgba(4, 12, 28, 0.98);
  border: 1px solid rgba(0, 212, 255, 0.3);
  box-shadow: 0 0 60px rgba(0, 212, 255, 0.12), 0 0 120px rgba(168, 85, 247, 0.06);

  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.8), transparent);
    animation: dataScan 3s linear infinite;
  }
}

@keyframes dataScan {
  0% { top: 0; opacity: 0.8; }
  100% { top: 100%; opacity: 0; }
}

.card-accent-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.8), rgba(168, 85, 247, 0.8), transparent);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
  font-size: 14px;
  font-weight: 500;
  color: $color-text-primary;
}

.blink-cursor {
  font-family: $font-mono;
  font-size: 14px;
  color: $color-accent-blue;
  margin-left: auto;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.form-group {
  margin-bottom: 16px;

  label {
    display: block;
    font-size: 11px;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: $color-accent-blue;
    margin-bottom: 8px;
  }
}

.input-wrap {
  position: relative;

  .input-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: $color-text-muted;
  }

  .cyber-input {
    width: 100%;
    padding: 12px 16px 12px 38px;
    border-radius: 6px;
    outline: none;
    transition: all 0.2s;
    font-size: 14px;
    background: rgba(0, 212, 255, 0.04);
    border: 1px solid rgba(0, 212, 255, 0.2);
    color: $color-text-primary;
    font-family: $font-body;

    &:focus {
      border-color: rgba(0, 212, 255, 0.7);
      box-shadow: 0 0 12px rgba(0, 212, 255, 0.15);
    }

    &.input-error {
      border-color: rgba(255, 45, 85, 0.6);
    }
  }
}

.error-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: $color-accent-red;
  margin-bottom: 16px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 4px;

  .spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.demo-hint {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: rgba(100, 116, 139, 0.6);
}
</style>
