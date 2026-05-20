import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { TeamInfo } from '@/types'
import { codeLogin } from '@/api/team'

export const useAuthStore = defineStore('auth', () => {
  const teamInfo = ref<TeamInfo | null>(null)
  const token = ref<string>('')

  const isLoggedIn = computed(() => !!token.value && !!teamInfo.value)

  function restoreSession() {
    const savedToken = localStorage.getItem('teamToken')
    const savedTeamInfo = localStorage.getItem('teamInfo')
    if (savedToken && savedTeamInfo) {
      token.value = savedToken
      try {
        teamInfo.value = JSON.parse(savedTeamInfo)
      } catch {
        // ignore parse error
      }
    }
  }

  async function login(code: string) {
    const result = await codeLogin(code)
    teamInfo.value = result.teamInfo
    token.value = result.token
    localStorage.setItem('teamToken', result.token)
    localStorage.setItem('teamInfo', JSON.stringify(result.teamInfo))
  }

  function logout() {
    teamInfo.value = null
    token.value = ''
    localStorage.removeItem('teamToken')
    localStorage.removeItem('teamInfo')
  }

  restoreSession()

  return { teamInfo, token, isLoggedIn, login, logout }
})
