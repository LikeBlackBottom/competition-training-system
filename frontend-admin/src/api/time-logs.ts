import request from './request'
import type { WorkRecord } from '@/types'

interface BackendTimeLog {
  id: number
  recordDate?: string
  date?: string
  institutionName?: string
  teamName?: string
  memberName?: string
  categoryName?: string
  moduleName?: string
  taskName?: string
  skillName?: string
  durationMinutes?: number
  hours?: number
  progressStatus?: string
  completionStatus?: string
  resultDesc?: string
  todayOutput?: string
  problemDesc?: string
  issue?: string
  needSupport?: boolean
  needHelp?: boolean
  isVoided?: boolean
  recordStatus?: string
  createdAt?: string
  submitTime?: string
}

function mapRecord(item: BackendTimeLog): WorkRecord {
  const progressStatus = item.progressStatus || item.completionStatus || '已完成'
  return {
    id: item.id,
    date: (item.recordDate || item.date || '') as string,
    teamName: (item.teamName || '') as string,
    memberName: (item.memberName || '') as string,
    moduleName: (item.categoryName || item.moduleName || '') as string,
    skillName: (item.taskName || item.skillName || '') as string,
    hours: (item.durationMinutes || item.hours || 0) as number,
    completionStatus: progressStatus,
    todayOutput: (item.resultDesc || item.todayOutput || '') as string,
    issue: (item.problemDesc || item.issue || '') as string,
    needHelp: (item.needSupport ?? item.needHelp ?? false) as boolean,
    recordStatus: (item.isVoided ? 'voided' : (item.recordStatus === 'voided' ? 'voided' : 'normal')) as WorkRecord['recordStatus'],
    submitTime: (item.createdAt || item.submitTime || '') as string,
  }
}

export async function getTimeLogs(params?: Record<string, unknown>): Promise<WorkRecord[]> {
  const data = await request.get('/api/admin/time-logs', { params })
  const list = data.records ?? data
  return (list as BackendTimeLog[]).map(mapRecord)
}

export async function voidRecord(id: number, voidReason?: string): Promise<WorkRecord> {
  const result = await request.post(`/api/admin/time-logs/${id}/void`, {
    voidReason: voidReason || '管理员作废',
  })
  return mapRecord(result as BackendTimeLog)
}
