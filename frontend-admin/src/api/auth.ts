import request from './request'

export interface AdminInfo {
  id: number
  username: string
  name: string
  role: string
}

export interface LoginResult {
  token: string
  admin: AdminInfo
}

export function login(username: string, password: string): Promise<LoginResult> {
  return request.post('/api/admin/auth/login', { username, password })
}
