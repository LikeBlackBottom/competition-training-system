import request from './request'
import type { Member } from '@/types'

interface BackendMember {
  id: number
  name: string
  institutionName?: string
  schoolName?: string
  teamName: string
  teamId: number
  role?: string
  status: string
  totalDisplayMinutes?: number
  totalHours?: number
  lastSubmit?: string
  lastSubmitTime?: string
}

function mapMember(m: BackendMember): Member {
  return {
    id: m.id,
    name: m.name || '',
    schoolName: m.institutionName || m.schoolName || '',
    teamName: m.teamName || '',
    teamId: m.teamId || 0,
    role: (m.role === '队长' ? '队长' : '队员') as Member['role'],
    status: (m.status === 'active' ? 'active' : 'inactive') as Member['status'],
    totalHours: m.totalDisplayMinutes || m.totalHours || 0,
    lastSubmit: m.lastSubmitTime || m.lastSubmit || '',
  }
}

export async function getMembers(params?: Record<string, unknown>): Promise<Member[]> {
  const data = await request.get('/api/admin/members', { params })
  const list = data.records ?? data
  return (list as BackendMember[]).map(mapMember)
}

export async function createMember(data: Partial<Member>): Promise<Member> {
  const payload = {
    name: data.name,
    institutionName: data.schoolName,
    teamName: data.teamName,
    teamId: data.teamId,
    role: data.role,
  }
  const result = await request.post('/api/admin/members', payload)
  return mapMember(result as BackendMember)
}

export async function updateMember(id: number, data: Partial<Member>): Promise<Member> {
  const payload: Record<string, unknown> = {}
  if (data.name !== undefined) payload.name = data.name
  if (data.schoolName !== undefined) payload.institutionName = data.schoolName
  if (data.teamName !== undefined) payload.teamName = data.teamName
  if (data.teamId !== undefined) payload.teamId = data.teamId
  if (data.role !== undefined) payload.role = data.role
  if (data.status !== undefined) payload.status = data.status
  const result = await request.put(`/api/admin/members/${id}`, payload)
  return mapMember(result as BackendMember)
}

export async function toggleMemberStatus(id: number, status: Member['status']): Promise<Member> {
  const result = await request.put(`/api/admin/members/${id}`, { status })
  return mapMember(result as BackendMember)
}
