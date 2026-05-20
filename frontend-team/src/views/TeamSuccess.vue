<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import CyberBg from '@/components/CyberBg.vue'

const router = useRouter()
const authStore = useAuthStore()
const showContent = ref(false)

function handleContinue() {
  router.push('/team/submit')
}

function handleLogout() {
  authStore.logout()
  router.push('/team/login')
}

onMounted(() => {
  setTimeout(() => {
    showContent.value = true
  }, 100)
})
</script>

<template>
  <CyberBg class="success-page">
    <div class="success-wrapper">
      <!-- Success Icon -->
      <div class="success-icon-wrapper">
        <div class="success-ring success-ring-1" />
        <div class="success-ring success-ring-2" />
        <div class="success-check">
          <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
            <path
              d="M6 18l8 8L30 10"
              stroke="#00ff9f"
              stroke-width="3"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </div>
      </div>

      <!-- Card -->
      <div v-show="showContent" class="cyber-card accent-cyan glow-cyan success-card">
        <div class="card-anim card-anim-1">
          <h1 class="success-title">训练记录已提交</h1>
          <p class="success-subtitle">TRAINING RECORD SUBMITTED SUCCESSFULLY</p>
        </div>

        <div class="card-anim card-anim-2">
          <div class="success-notice">
            <p>
              请联系指导老师确认，队员端<span class="text-red">不支持</span>查看、修改或删除记录。
            </p>
          </div>
        </div>

        <div class="card-anim card-anim-2">
          <div class="forbidden-grid">
            <div class="forbidden-item">
              <span class="forbidden-icon">N</span>
              <span class="forbidden-label">不可查看</span>
            </div>
            <div class="forbidden-item">
              <span class="forbidden-icon">E</span>
              <span class="forbidden-label">不可修改</span>
            </div>
            <div class="forbidden-item">
              <span class="forbidden-icon">D</span>
              <span class="forbidden-label">不可删除</span>
            </div>
          </div>
        </div>

        <div class="card-anim card-anim-3">
          <div class="success-actions">
            <el-button type="primary" size="large" class="success-btn-primary" @click="handleContinue">
              <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                <path d="M2.5 7.5h10M7.5 2.5l5 5-5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              继续提交一条记录
            </el-button>
            <el-button class="success-btn-ghost" size="large" @click="handleLogout">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                <path d="M5 1H3a1 1 0 00-1 1v10a1 1 0 001 1h2M9 10l3-3-3-3M12 7H5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              退出登录
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="showContent" class="footer-protection">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
          <path d="M6 1v8M3 3l3-2 3 2v5H3V3z" stroke="#3a5070" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
        <span class="footer-text">RECORD PROTECTED - READ ONLY AFTER SUBMISSION</span>
      </div>
    </div>
  </CyberBg>
</template>

<style scoped>
.success-page {
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-wrapper {
  width: 100%;
  max-width: 448px;
  padding: 0 16px;
  text-align: center;
}

.success-icon-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
}

.success-ring {
  position: absolute;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  border: 2px solid rgba(0, 255, 159, 0.5);
  animation: ringPulse 2s ease-out infinite;
}

.success-ring-2 {
  border-color: rgba(0, 255, 159, 0.3);
  animation-delay: 0.4s;
}

@keyframes ringPulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
  }
}

.success-check {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 255, 159, 0.12);
  border: 2px solid rgba(0, 255, 159, 0.6);
  box-shadow: 0 0 40px rgba(0, 255, 159, 0.4), 0 0 80px rgba(0, 255, 159, 0.15);
  animation: checkPop 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275) 0.2s both;
}

@keyframes checkPop {
  0% {
    transform: scale(0) rotate(-20deg);
    opacity: 0;
  }
  60% {
    transform: scale(1.15) rotate(5deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
  }
}

.success-card {
  padding: 32px;
}

.card-anim {
  opacity: 0;
  animation: fadeUp 0.5s ease-out forwards;
}

.card-anim-1 {
  animation-delay: 0.2s;
}

.card-anim-2 {
  animation-delay: 0.5s;
}

.card-anim-3 {
  animation-delay: 0.8s;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.success-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.5rem;
  color: #00ff9f;
  letter-spacing: 0.06em;
  margin-bottom: 8px;
  text-shadow: 0 0 20px rgba(0, 255, 159, 0.5);
}

.success-subtitle {
  font-size: 12px;
  letter-spacing: 0.15em;
  color: #5a7090;
  font-family: 'Share Tech Mono', monospace;
}

.success-notice {
  margin: 24px 0;
  padding: 16px;
  border-radius: 8px;
  background: rgba(0, 255, 159, 0.05);
  border: 1px solid rgba(0, 255, 159, 0.15);
}

.success-notice p {
  font-size: 14px;
  line-height: 1.6;
  color: #5a7090;
  margin: 0;
}

.forbidden-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.forbidden-item {
  padding: 12px 8px;
  border-radius: 8px;
  text-align: center;
  background: rgba(255, 45, 85, 0.06);
  border: 1px solid rgba(255, 45, 85, 0.2);
}

.forbidden-icon {
  display: block;
  font-size: 20px;
  margin-bottom: 4px;
  color: #ff2d55;
  font-family: 'Share Tech Mono', monospace;
  font-weight: bold;
}

.forbidden-label {
  font-size: 12px;
  color: #ff2d55;
}

.success-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.success-btn-primary {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
}

.success-btn-ghost {
  width: 100%;
  height: 44px !important;
  background: transparent !important;
  border: 1px solid rgba(0, 212, 255, 0.25) !important;
  color: #c0d8f0 !important;
  font-size: 14px !important;
}

.success-btn-ghost:hover {
  border-color: rgba(0, 212, 255, 0.5) !important;
  color: #00d4ff !important;
}

.footer-protection {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.footer-text {
  font-size: 12px;
  color: #3a5070;
  font-family: 'Share Tech Mono', monospace;
}
</style>
