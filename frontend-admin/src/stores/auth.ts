import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import type { AdminInfo } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('adminToken') || '')
  const adminInfo = ref<AdminInfo | null>(null)

  const savedInfo = localStorage.getItem('adminInfo')
  if (savedInfo) {
    try {
      adminInfo.value = JSON.parse(savedInfo)
    } catch {
      // ignore parse error
    }
  }

  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => adminInfo.value?.name || '')
  const userRole = computed(() => adminInfo.value?.role || '')

  async function login(username: string, password: string) {
    const result = await loginApi(username, password)
    token.value = result.token
    adminInfo.value = result.admin
    localStorage.setItem('adminToken', result.token)
    localStorage.setItem('adminInfo', JSON.stringify(result.admin))
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
    router.push('/admin/login')
  }

  return { token, adminInfo, userName, userRole, isLoggedIn, login, logout }
})
