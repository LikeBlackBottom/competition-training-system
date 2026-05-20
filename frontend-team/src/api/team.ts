import request from './request'
import type { TeamInfo, FormOption, SubmitRecord, SkillModule, SkillPoint } from '@/types'

interface BackendTeam {
  id: number
  institutionName?: string
  schoolName?: string
  teamName: string
  trackName?: string
  track?: string
}

interface BackendMember {
  id: number
  name: string
}

interface BackendCategory {
  id: number
  name: string
  color?: string
  tasks?: BackendTask[]
  skills?: BackendTask[]
}

interface BackendTask {
  id: number
  categoryId?: number
  moduleId?: number
  name: string
}

function mapTeam(t: BackendTeam): TeamInfo {
  return {
    id: t.id,
    teamName: t.teamName,
    schoolName: t.institutionName || t.schoolName || '',
    track: t.trackName || t.track || '',
  }
}

const CATEGORY_COLORS = ['#00d4ff', '#a855f7', '#00ff9f', '#ff9f00', '#ff2d55', '#06b6d4']

function mapCategory(c: BackendCategory, index: number): SkillModule {
  const tasks = c.tasks || c.skills || []
  return {
    id: c.id,
    name: c.name,
    color: c.color || CATEGORY_COLORS[index % CATEGORY_COLORS.length],
    skills: tasks.map((t) => ({
      id: t.id,
      moduleId: t.categoryId || t.moduleId || c.id,
      name: t.name,
    })) as SkillPoint[],
  }
}

export async function codeLogin(inviteCode: string): Promise<{ teamInfo: TeamInfo; token: string }> {
  const data = await request.post('/api/team/auth/code-login', {
    loginCode: inviteCode.toUpperCase().trim(),
  })
  return {
    teamInfo: mapTeam(data.team as BackendTeam),
    token: data.token as string,
  }
}

export async function getFormOptions(_teamId: number): Promise<FormOption> {
  const data = await request.get('/api/team/form-options')
  return {
    members: (data.members || []) as BackendMember[],
    modules: ((data.categories || []) as BackendCategory[]).map((c, i) => mapCategory(c, i)),
  }
}

const STATUS_MAP: Record<string, string> = {
  notStarted: '未开始',
  inProgress: '进行中',
  completed: '已完成',
  partial: '部分完成',
  mastered: '已掌握',
  blocked: '受阻',
}

export async function submitRecord(data: SubmitRecord): Promise<{ success: boolean }> {
  await request.post('/api/team/time-logs', {
    memberId: data.memberId,
    taskId: data.skillId,
    durationMinutes: data.durationMinutes,
    progressStatus: STATUS_MAP[data.completionStatus] || data.completionStatus,
    resultDesc: data.todayOutput,
    problemDesc: data.issue || '',
    needSupport: data.needHelp,
  })
  return { success: true }
}
