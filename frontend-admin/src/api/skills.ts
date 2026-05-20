import request from './request'
import type { SkillModule, SkillPoint } from '@/types'

interface BackendCategory {
  id: number
  name: string
  trackName?: string
  color?: string
  taskCount?: number
  tasks?: BackendTask[]
  skills?: BackendTask[]
}

interface BackendTask {
  id: number
  categoryId?: number
  moduleId?: number
  name: string
  description?: string
  expectedMinutes?: number
  difficultyLevel?: number
  scoreWeight?: number
  difficulty?: number
  weight?: number
  sortOrder?: number
  status?: string
  createdAt?: string
  updatedAt?: string
}

const CATEGORY_COLORS = ['#00d4ff', '#a855f7', '#00ff9f', '#ff9f00', '#ff2d55', '#06b6d4']
export const TRACK_OPTIONS = ['新一代信息技术', '人工智能'] as const
export const DEFAULT_TRACK = TRACK_OPTIONS[0]

function mapTask(t: BackendTask, parentId: number): SkillPoint {
  return {
    id: t.id,
    moduleId: t.categoryId || t.moduleId || parentId,
    name: t.name,
    description: t.description || '',
    expectedMinutes: t.expectedMinutes || 0,
    difficulty: (t.difficultyLevel || t.difficulty || 1) as SkillPoint['difficulty'],
    difficultyLevel: (t.difficultyLevel || t.difficulty || 1) as SkillPoint['difficultyLevel'],
    weight: t.scoreWeight ?? t.weight ?? 20,
    scoreWeight: t.scoreWeight ?? t.weight ?? 20,
    sortOrder: t.sortOrder || 0,
    status: (t.status === 'inactive' ? 'inactive' : 'active') as SkillPoint['status'],
  }
}

function mapCategory(c: BackendCategory, index: number): SkillModule {
  const tasks = c.tasks || c.skills || []
  return {
    id: c.id,
    name: c.name,
    trackName: c.trackName || DEFAULT_TRACK,
    color: c.color || CATEGORY_COLORS[index % CATEGORY_COLORS.length],
    skills: tasks.map((t) => mapTask(t, c.id)),
  }
}

export async function getCategories(): Promise<SkillModule[]> {
  const data = await request.get('/api/admin/skills/categories')
  const list = data.records ?? data
  const modules = (list as BackendCategory[]).map((c, i) => mapCategory(c, i))
  const tasksByModule = await Promise.all(modules.map((module) => getTasks({ categoryId: module.id })))
  return modules.map((module, index) => ({
    ...module,
    skills: tasksByModule[index],
  }))
}

export async function createCategory(data: { name: string; trackName?: string }): Promise<SkillModule> {
  const result = await request.post('/api/admin/skills/categories', {
    name: data.name,
    trackName: data.trackName || DEFAULT_TRACK,
  })
  return mapCategory(result as BackendCategory, 0)
}

export async function updateCategory(id: number, data: Partial<SkillModule>): Promise<SkillModule> {
  const payload: Record<string, unknown> = {}
  if (data.name !== undefined) payload.name = data.name
  if (data.trackName !== undefined) payload.trackName = data.trackName
  const result = await request.put(`/api/admin/skills/categories/${id}`, payload)
  return mapCategory(result as BackendCategory, 0)
}

export async function getTasks(params?: Record<string, unknown>): Promise<SkillPoint[]> {
  const data = await request.get('/api/admin/skills/tasks', { params })
  const list = data.records ?? data
  return (list as BackendTask[]).map((t) => mapTask(t, t.categoryId || t.moduleId || 0))
}

export async function createTask(moduleId: number, data: Partial<SkillPoint>): Promise<SkillPoint> {
  const payload = {
    categoryId: moduleId,
    name: data.name,
    description: data.description || '',
    expectedMinutes: data.expectedMinutes ?? 0,
    difficultyLevel: data.difficultyLevel || data.difficulty || 1,
    scoreWeight: data.scoreWeight ?? data.weight ?? 20,
    sortOrder: data.sortOrder,
    status: data.status || 'active',
  }
  const result = await request.post('/api/admin/skills/tasks', payload)
  return mapTask(result as BackendTask, moduleId)
}

export async function updateTask(id: number, data: Partial<SkillPoint>): Promise<SkillPoint> {
  const payload: Record<string, unknown> = {}
  if (data.name !== undefined) payload.name = data.name
  if (data.description !== undefined) payload.description = data.description
  if (data.expectedMinutes !== undefined) payload.expectedMinutes = data.expectedMinutes
  if (data.difficultyLevel !== undefined || data.difficulty !== undefined) payload.difficultyLevel = data.difficultyLevel || data.difficulty
  if (data.scoreWeight !== undefined || data.weight !== undefined) payload.scoreWeight = data.scoreWeight ?? data.weight
  if (data.sortOrder !== undefined) payload.sortOrder = data.sortOrder
  if (data.status !== undefined) payload.status = data.status
  if (data.moduleId !== undefined) payload.categoryId = data.moduleId
  const result = await request.put(`/api/admin/skills/tasks/${id}`, payload)
  return mapTask(result as BackendTask, data.moduleId || 0)
}
