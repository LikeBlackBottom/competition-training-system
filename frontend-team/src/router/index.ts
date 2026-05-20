import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/team/login',
    },
    {
      path: '/team/login',
      name: 'TeamLogin',
      component: () => import('@/views/TeamLogin.vue'),
    },
    {
      path: '/team/submit',
      name: 'TeamSubmit',
      component: () => import('@/views/TeamSubmit.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/team/success',
      name: 'TeamSuccess',
      component: () => import('@/views/TeamSuccess.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, _from, next) => {
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      next('/team/login')
      return
    }
  }
  next()
})

export default router
