import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/admin/login',
    },
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: () => import('@/views/login/AdminLogin.vue'),
      meta: { title: '管理登录' },
    },
    {
      path: '/admin/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/Dashboard.vue'),
      meta: { title: '数据看板', requiresAuth: true },
    },
    {
      path: '/admin/teams',
      name: 'Teams',
      component: () => import('@/views/teams/Teams.vue'),
      meta: { title: '队伍管理', requiresAuth: true },
    },
    {
      path: '/admin/members',
      name: 'Members',
      component: () => import('@/views/members/Members.vue'),
      meta: { title: '队员管理', requiresAuth: true },
    },
    {
      path: '/admin/skills',
      name: 'Skills',
      component: () => import('@/views/skills/Skills.vue'),
      meta: { title: '技能树管理', requiresAuth: true },
    },
    {
      path: '/admin/time-logs',
      name: 'TimeLogs',
      component: () => import('@/views/time-logs/TimeLogs.vue'),
      meta: { title: '展示时长记录', requiresAuth: true },
    },
    {
      path: '/admin/issues',
      name: 'Issues',
      component: () => import('@/views/issues/Issues.vue'),
      meta: { title: '问题闭环', requiresAuth: true },
    },
    {
      path: '/admin/exports',
      name: 'Exports',
      component: () => import('@/views/exports/Exports.vue'),
      meta: { title: '数据导出', requiresAuth: true },
    },
  ],
})

router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || '赛训数据中枢'
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      next({ name: 'AdminLogin', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
