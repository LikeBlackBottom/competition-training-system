<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Monitor, UserFilled, Avatar, Collection, Timer,
  WarningFilled, Download, Cpu,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const today = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long',
  })
})

const currentPageTitle = computed(() => {
  const titles: Record<string, string> = {
    Dashboard: '数据看板',
    Teams: '队伍管理',
    Members: '队员管理',
    Skills: '技能树管理',
    TimeLogs: '展示时长记录',
    Issues: '问题闭环',
    Exports: '数据导出',
  }
  return titles[(route.name as string) || ''] || ''
})

const navItems = [
  { route: '/admin/dashboard', label: '数据看板', enLabel: 'Dashboard', icon: Monitor },
  { route: '/admin/teams', label: '队伍管理', enLabel: 'Teams', icon: UserFilled },
  { route: '/admin/members', label: '队员管理', enLabel: 'Members', icon: Avatar },
  { route: '/admin/skills', label: '技能树管理', enLabel: 'Skill Tree', icon: Collection },
  { route: '/admin/time-logs', label: '展示时长记录', enLabel: 'Display Time', icon: Timer },
  { route: '/admin/issues', label: '问题闭环', enLabel: 'Issues', icon: WarningFilled },
  { route: '/admin/exports', label: '数据导出', enLabel: 'Export', icon: Download },
]

function isActive(itemRoute: string) {
  return route.path === itemRoute
}
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-icon">
          <el-icon :size="18"><Cpu /></el-icon>
        </div>
        <div class="logo-text">
          <span class="logo-title">CTMS</span>
          <span class="logo-sub">赛训数据中枢</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div
          v-for="item in navItems"
          :key="item.route"
          class="nav-item"
          :class="{ active: isActive(item.route) }"
          @click="router.push(item.route)"
        >
          <el-icon :size="16"><component :is="item.icon" /></el-icon>
          <div class="nav-item-text">
            <span class="nav-label">{{ item.label }}</span>
            <span class="nav-en-label">{{ item.enLabel }}</span>
          </div>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="user-avatar">{{ authStore.userName?.charAt(0) }}</div>
          <div class="user-detail">
            <span class="user-name">{{ authStore.userName }}</span>
            <span class="user-role">{{ authStore.userRole }}</span>
          </div>
        </div>
        <button class="logout-btn" @click="authStore.logout">
          <el-icon :size="13"><Download /></el-icon>
          退出登录
        </button>
      </div>
    </aside>

    <div class="main-area">
      <header class="top-header">
        <div class="header-left">
          <span class="header-path">竞赛训练数据中台</span>
          <span class="header-sep">/</span>
          <span class="header-current">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="header-date">{{ today }}</span>
          <div class="system-status">
            <span class="status-dot" />
            SYSTEM ONLINE
          </div>
        </div>
      </header>

      <main class="main-content">
        <div class="bg-orb" />
        <div class="content-wrapper">
          <slot />
        </div>
      </main>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.admin-layout {
  display: flex;
  min-height: 100vh;
  background: $color-bg-primary;
  background-image:
    linear-gradient(rgba(255, 122, 223, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(110, 231, 255, 0.015) 1px, transparent 1px);
  background-size: 40px 40px;
}

.sidebar {
  width: 240px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  background: rgba(5, 7, 22, 0.98);
  border-right: 1px solid rgba(255, 122, 223, 0.12);
  backdrop-filter: blur(20px);
  animation: sidebarGlow 4s ease-in-out infinite;
}

@keyframes sidebarGlow {
  0%, 100% { box-shadow: 2px 0 30px rgba(255, 122, 223, 0.04); }
  50% { box-shadow: 2px 0 30px rgba(255, 122, 223, 0.08); }
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 122, 223, 0.1);
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: rgba(255, 122, 223, 0.1);
  border: 1px solid rgba(255, 122, 223, 0.3);
  box-shadow: 0 0 16px rgba(255, 122, 223, 0.15);
  color: $color-accent-pink;
}

.logo-title {
  font-family: $font-heading;
  font-size: 0.7rem;
  color: $color-accent-pink;
  letter-spacing: 0.1em;
  display: block;
}

.logo-sub {
  font-size: 11px;
  color: $color-text-muted;
}

.sidebar-nav {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.25s;
  color: $color-text-muted;
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    pointer-events: none;
    background:
      linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px),
      linear-gradient(0deg, rgba(255,255,255,0.015) 1px, transparent 1px);
    background-size: 8px 8px;
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover {
    background: rgba(255, 122, 223, 0.05);
    color: $color-text-primary;

    &::after {
      opacity: 0.15;
    }
  }

  &.active {
    background: linear-gradient(90deg, rgba(255, 122, 223, 0.12), rgba(110, 231, 255, 0.06));
    border-color: rgba(255, 122, 223, 0.2);
    color: $color-accent-pink;
    box-shadow: inset 3px 0 0 $color-accent-pink;

    &::after {
      opacity: 0.2;
    }
  }
}

.nav-item-text {
  flex: 1;
  min-width: 0;
}

.nav-label {
  font-size: 14px;
  font-weight: 500;
  display: block;
}

.nav-en-label {
  font-size: 11px;
  opacity: 0.5;
  font-family: $font-mono;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 122, 223, 0.08);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, $color-accent-pink, $color-accent-blue);
  color: #fff;
}

.user-detail {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 12px;
  font-weight: 500;
  color: $color-text-primary;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: $color-text-muted;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px;
  border-radius: 4px;
  font-size: 12px;
  color: $color-text-muted;
  background: transparent;
  border: 1px solid rgba(255, 92, 158, 0.15);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    color: $color-accent-red;
    background: rgba(255, 92, 158, 0.06);
    border-color: rgba(255, 92, 158, 0.3);
  }
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.top-header {
  flex-shrink: 0;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(5, 7, 22, 0.95);
  border-bottom: 1px solid rgba(255, 122, 223, 0.08);
  backdrop-filter: blur(12px);
}

.header-path {
  font-size: 12px;
  font-family: $font-mono;
  color: $color-text-muted;
}

.header-sep {
  margin: 0 8px;
  color: rgba(255, 122, 223, 0.2);
  font-size: 10px;
}

.header-current {
  font-size: 12px;
  font-weight: 500;
  color: $color-accent-pink;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-date {
  font-size: 12px;
  font-family: $font-mono;
  color: $color-text-muted;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 11px;
  font-family: $font-mono;
  background: rgba(124, 255, 203, 0.06);
  border: 1px solid rgba(124, 255, 203, 0.2);
  color: $color-accent-mint;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: $color-accent-mint;
  animation: pulsate 2s ease-in-out infinite;
}

@keyframes pulsate {
  0%, 100% { opacity: 1; box-shadow: 0 0 4px $color-accent-mint; }
  50% { opacity: 0.3; box-shadow: none; }
}

.main-content {
  flex: 1;
  overflow: auto;
  padding: 24px;
  position: relative;
}

.bg-orb {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  background: radial-gradient(ellipse 60% 40% at 80% 20%, rgba(255, 122, 223, 0.03), transparent 60%);
}

.content-wrapper {
  position: relative;
  z-index: 10;
}
</style>
