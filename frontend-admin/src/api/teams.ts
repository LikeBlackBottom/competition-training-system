import request from './request'
import type { Team } from '@/types'

interface BackendTeam {
  id: number
  institutionName?: string
  schoolName?: string
  teamName: string
  trackName?: string
  track?: string
  loginCode?: string
  inviteCode?: string
  memberCount: number
  todaySubmitStatus?: string
  todaySubmitted?: boolean
  totalDisplayMinutes?: number
  totalHours?: number
  status: string
}

function mapTeam(t: BackendTeam): Team {
  return {
    id: t.id,
    schoolName: t.institutionName || t.schoolName || '',
    teamName: t.teamName || '',
    trackName: t.trackName || t.track || '',
    track: t.trackName || t.track || '',
    inviteCode: t.loginCode || t.inviteCode || '',
    memberCount: t.memberCount || 0,
    todaySubmitted: t.todaySubmitStatus === 'submitted' ? true : (t.todaySubmitted || false),
    totalHours: t.totalDisplayMinutes || t.totalHours || 0,
    status: (t.status === 'active' ? 'active' : 'inactive') as Team['status'],
  }
}

export async function getTeams(params?: Record<string, unknown>): Promise<Team[]> {
  const data = await request.get('/api/admin/teams', { params })
  const list = data.records ?? data
  return (list as BackendTeam[]).map(mapTeam)
}

export async function createTeam(data: Partial<Team>): Promise<Team> {
  const payload = {
    institutionName: data.schoolName,
    teamName: data.teamName,
    trackName: data.trackName || data.track,
    loginCode: data.inviteCode,
  }
  const result = await request.post('/api/admin/teams', payload)
  return mapTeam(result as BackendTeam)
}

export async function updateTeam(id: number, data: Partial<Team>): Promise<Team> {
  const payload: Record<string, unknown> = {}
  if (data.schoolName !== undefined) payload.institutionName = data.schoolName
  if (data.teamName !== undefined) payload.teamName = data.teamName
  if (data.trackName !== undefined || data.track !== undefined) payload.trackName = data.trackName || data.track
  if (data.inviteCode !== undefined) payload.loginCode = data.inviteCode
  if (data.status !== undefined) payload.status = data.status
  const result = await request.put(`/api/admin/teams/${id}`, payload)
  return mapTeam(result as BackendTeam)
}

export async function toggleTeamStatus(id: number, status: Team['status']): Promise<Team> {
  const result = await request.put(`/api/admin/teams/${id}`, { status })
  return mapTeam(result as BackendTeam)
}
