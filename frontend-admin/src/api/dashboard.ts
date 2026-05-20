import request from './request'
import type { DashboardData, WorkRecord } from '@/types'

function mapRecord(item: Record<string, unknown>): WorkRecord {
  const progressStatus = (item.progressStatus || item.completionStatus || '已完成') as string
  return {
    id: item.id as number,
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
    recordStatus: (item.isVoided ? 'voided' : 'normal') as WorkRecord['recordStatus'],
    submitTime: (item.createdAt || item.submitTime || '') as string,
  }
}

export async function getDashboard(): Promise<DashboardData> {
  const data: Record<string, unknown> = await request.get('/api/admin/dashboard')
  const summary = (data.summary || data || {}) as Record<string, unknown>

  return {
    totalTeams: (summary.totalTeams || 0) as number,
    todaySubmitted: (summary.submittedTodayTeams || 0) as number,
    todayNotSubmitted: (summary.notSubmittedTodayTeams || 0) as number,
    totalHours: (summary.totalDisplayMinutes || summary.totalHours || 0) as number,
    pendingIssues: (summary.pendingIssues || 0) as number,
    teamHoursRank: ((data.teamRanking || []) as Record<string, unknown>[]).map((item) => ({
      name: (item.name || item.teamName || '') as string,
      hours: (item.hours || item.totalDisplayMinutes || 0) as number,
    })),
    skillDist: ((data.skillDistribution || []) as Record<string, unknown>[]).map((item) => ({
      name: (item.name || item.categoryName || '') as string,
      value: (item.value || 0) as number,
      color: (item.color || '#00d4ff') as string,
    })),
    dailyTrend: ((data.dailyTrend || []) as Record<string, unknown>[]).map((item) => ({
      date: (item.date || '') as string,
      hours: (item.hours || item.totalDisplayMinutes || 0) as number,
      records: (item.records || item.recordCount || 0) as number,
    })),
    issueSeverityDist: ((data.issueSeverityDist || []) as Record<string, unknown>[]).map((item) => ({
      name: (item.name || '') as string,
      count: (item.count || 0) as number,
      color: (item.color || '#ff2d55') as string,
    })),
    recentRecords: ((data.recentLogs || []) as Record<string, unknown>[]).map(mapRecord),
  }
}
