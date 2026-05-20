import request from './request'
import type { Issue } from '@/types'

interface BackendIssue {
  id: number
  title?: string
  description?: string
  teamName?: string
  skillName?: string
  taskName?: string
  severity?: string
  assignee?: string
  memberName?: string
  submitTime?: string
  createdAt?: string
  status?: string
}

function mapIssue(item: BackendIssue): Issue {
  return {
    id: item.id,
    title: (item.title || item.description || '') as string,
    teamName: (item.teamName || '') as string,
    skillName: (item.skillName || item.taskName || '') as string,
    severity: (item.severity || 'normal') as Issue['severity'],
    assignee: (item.assignee || '') as string,
    memberName: (item.memberName || '') as string,
    submitTime: (item.submitTime || item.createdAt || '') as string,
    status: (item.status || 'pending') as Issue['status'],
  }
}

export async function getIssues(params?: Record<string, unknown>): Promise<Issue[]> {
  const data = await request.get('/api/admin/issues', { params })
  const list = data.records ?? data
  return (list as BackendIssue[]).map(mapIssue)
}

export async function createIssue(data: Partial<Issue>): Promise<Issue> {
  const result = await request.post('/api/admin/issues', {
    title: data.title,
    teamName: data.teamName,
    skillName: data.skillName,
    severity: data.severity,
    assignee: data.assignee,
    memberName: data.memberName,
  })
  return mapIssue(result as BackendIssue)
}

export async function updateIssueStatus(id: number, status: Issue['status']): Promise<Issue> {
  const result = await request.put(`/api/admin/issues/${id}`, { status })
  return mapIssue(result as BackendIssue)
}
