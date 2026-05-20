export interface TeamInfo {
  id: number
  teamName: string
  schoolName: string
  track: string
}

export interface SkillModule {
  id: number
  name: string
  color: string
  skills: SkillPoint[]
}

export interface SkillPoint {
  id: number
  moduleId: number
  name: string
}

export interface Member {
  id: number
  name: string
}

export interface FormOption {
  modules: SkillModule[]
  members: Member[]
}

export interface SubmitRecord {
  memberId: number
  moduleId: number
  skillId: number
  durationMinutes: number
  completionStatus: 'notStarted' | 'inProgress' | 'completed' | 'partial' | 'mastered' | 'blocked'
  todayOutput: string
  issue: string
  needHelp: boolean
}
