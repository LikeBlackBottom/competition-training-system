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
    <!-- Pixel sparkles -->
    <div class="sparkles">
      <div class="sparkle sp-1" />
      <div class="sparkle sp-2" />
      <div class="sparkle sp-3" />
      <div class="sparkle sp-4" />
      <div class="sparkle sp-5" />
      <div class="sparkle sp-6" />
      <div class="sparkle sp-7" />
      <div class="sparkle sp-8" />
    </div>

    <div class="success-wrapper">
      <!-- Success Icon -->
      <div class="success-icon-wrapper">
        <div class="success-ring success-ring-1" />
        <div class="success-ring success-ring-2" />
        <div class="success-check">
          <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
            <path
              d="M6 18l8 8L30 10"
              stroke="#7cffcb"
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
          <path d="M6 1v8M3 3l3-2 3 2v5H3V3z" stroke="#5a6080" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" />
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

/* Pixel sparkles */
.sparkles {
  pointer-events: none;
  position: fixed;
  inset: 0;
  z-index: 5;
}

.sparkle {
  position: absolute;
  width: 3px;
  height: 3px;
  animation: sparkle 2s ease-in-out infinite;
}

.sp-1 { top: 10%; left: 15%; background: #ff7adf; animation-delay: 0s; }
.sp-2 { top: 25%; right: 20%; background: #6ee7ff; animation-delay: 0.3s; }
.sp-3 { bottom: 30%; left: 10%; background: #a78bfa; animation-delay: 0.6s; }
.sp-4 { bottom: 15%; right: 15%; background: #7cffcb; animation-delay: 0.9s; }
.sp-5 { top: 40%; left: 8%; background: #ffd166; animation-delay: 1.2s; }
.sp-6 { top: 60%; right: 10%; background: #ff7adf; animation-delay: 1.5s; }
.sp-7 { bottom: 40%; left: 25%; background: #6ee7ff; animation-delay: 0.4s; }
.sp-8 { top: 15%; right: 30%; background: #a78bfa; animation-delay: 1.8s; }

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  25% { opacity: 1; transform: scale(1.5); box-shadow: 0 0 6px currentColor; }
  50% { opacity: 0.6; transform: scale(1); }
  75% { opacity: 1; transform: scale(1.2); box-shadow: 0 0 4px currentColor; }
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
  border: 2px solid rgba(124, 255, 203, 0.5);
  animation: ringPulse 2s ease-out infinite;
}

.success-ring-2 {
  border-color: rgba(124, 255, 203, 0.3);
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
  background: rgba(124, 255, 203, 0.1);
  border: 2px solid rgba(124, 255, 203, 0.5);
  box-shadow: 0 0 32px rgba(124, 255, 203, 0.3), 0 0 64px rgba(255, 122, 223, 0.1);
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
  color: #7cffcb;
  letter-spacing: 0.06em;
  margin-bottom: 8px;
  text-shadow: 0 0 16px rgba(124, 255, 203, 0.4);
}

.success-subtitle {
  font-size: 12px;
  letter-spacing: 0.15em;
  color: #7f8bb3;
  font-family: 'Share Tech Mono', monospace;
}

.text-red {
  color: #ff5c9e;
}

.success-notice {
  margin: 24px 0;
  padding: 16px;
  border-radius: 4px;
  background: rgba(124, 255, 203, 0.04);
  border: 1px solid rgba(124, 255, 203, 0.12);
}

.success-notice p {
  font-size: 14px;
  line-height: 1.6;
  color: #7f8bb3;
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
  border-radius: 4px;
  text-align: center;
  background: rgba(255, 92, 158, 0.05);
  border: 1px solid rgba(255, 92, 158, 0.18);
}

.forbidden-icon {
  display: block;
  font-size: 20px;
  margin-bottom: 4px;
  color: #ff5c9e;
  font-family: 'Share Tech Mono', monospace;
  font-weight: bold;
}

.forbidden-label {
  font-size: 12px;
  color: #ff5c9e;
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
  border: 1px solid rgba(255, 122, 223, 0.2) !important;
  color: #b8c3e8 !important;
  font-size: 14px !important;
}

.success-btn-ghost:hover {
  border-color: rgba(255, 122, 223, 0.4) !important;
  color: #ff7adf !important;
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
  color: #5a6080;
  font-family: 'Share Tech Mono', monospace;
}
</style>
