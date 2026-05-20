import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

instance.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') {
      return response
    }
    const body = response.data
    if (body && body.code === 200) {
      return body.data
    }
    if (body && body.code === 401) {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      window.location.href = '/admin/login'
      return Promise.reject(new Error('未授权，请重新登录'))
    }
    ElMessage.error(body?.message || '请求失败')
    return Promise.reject(new Error(body?.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      window.location.href = '/admin/login'
    }
    const message = error.response?.data?.message || error.message || '网络错误，请检查后端服务是否启动'
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

const request = {
  get<T = any>(url: string, config?: any): Promise<T> {
    return instance.get(url, config) as any
  },
  post<T = any>(url: string, data?: any, config?: any): Promise<T> {
    return instance.post(url, data, config) as any
  },
  put<T = any>(url: string, data?: any, config?: any): Promise<T> {
    return instance.put(url, data, config) as any
  },
  delete<T = any>(url: string, config?: any): Promise<T> {
    return instance.delete(url, config) as any
  },
}

export default request
