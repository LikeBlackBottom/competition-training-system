export interface Team {
  id: number
  schoolName: string
  teamName: string
  trackName?: string
  track: string
  inviteCode: string
  memberCount: number
  todaySubmitted: boolean
  totalHours: number // 单位：分钟
  status: 'active' | 'inactive'
}

export interface Member {
  id: number
  name: string
  schoolName: string
  teamName: string
  teamId: number
  role: '队长' | '队员'
  status: 'active' | 'inactive'
  totalHours: number // 单位：分钟
  lastSubmit: string
}

export interface SkillModule {
  id: number
  name: string
  trackName?: string
  color: string
  skills: SkillPoint[]
}

export interface SkillPoint {
  id: number
  moduleId: number
  name: string
  description?: string
  expectedMinutes?: number
  difficulty: 1 | 2 | 3
  difficultyLevel?: 1 | 2 | 3
  weight: number
  scoreWeight?: number
  sortOrder: number
  status?: 'active' | 'inactive'
}

export interface WorkRecord {
  id: number
  date: string
  teamName: string
  memberName: string
  moduleName: string
  skillName: string
  hours: number // 单位：分钟
  completionStatus: string
  todayOutput: string
  issue: string
  needHelp: boolean
  recordStatus: 'normal' | 'voided'
  submitTime: string
}

export interface Issue {
  id: number
  title: string
  teamName: string
  skillName: string
  severity: 'critical' | 'high' | 'normal' | 'low'
  assignee: string
  memberName: string
  submitTime: string
  status: 'pending' | 'processing' | 'resolved' | 'closed'
}

export interface DashboardData {
  totalTeams: number
  todaySubmitted: number
  todayNotSubmitted: number
  totalHours: number // 单位：分钟
  pendingIssues: number
  teamHoursRank: { name: string; hours: number }[] // hours单位：分钟
  skillDist: { name: string; value: number; color: string }[]
  dailyTrend: { date: string; hours: number; records: number }[] // hours单位：分钟
  issueSeverityDist: { name: string; count: number; color: string }[]
  recentRecords: WorkRecord[]
}

export interface FormOption {
  modules: SkillModule[]
  members: { id: number; name: string }[]
}

export interface TeamInfo {
  id: number
  teamName: string
  schoolName: string
  track: string
}
