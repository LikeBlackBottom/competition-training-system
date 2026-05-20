import axios, { type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

function getToken(): string {
  return localStorage.getItem('adminToken') || ''
}

function getBaseURL(): string {
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
}

function downloadBlob(response: AxiosResponse, defaultName: string) {
  const blob = response.data as Blob
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  const disposition = (response.headers as Record<string, string>)?.['content-disposition'] || ''
  const match = disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
  link.download = match?.[1]?.replace(/['"]/g, '') || defaultName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

async function blobGet(path: string, defaultName: string) {
  const response = await axios.get(`${getBaseURL()}${path}`, {
    responseType: 'blob',
    headers: { Authorization: `Bearer ${getToken()}` },
  })
  const blob = response.data as Blob
  if (blob.type?.includes('application/json')) {
    const text = await new Promise<string>((resolve) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result as string)
      reader.readAsText(blob)
    })
    const body = JSON.parse(text)
    if (body.code && body.code !== 200) {
      ElMessage.error(body?.message || '导出失败')
      return
    }
  }
  downloadBlob(response, defaultName)
  ElMessage.success('导出成功')
}

export async function exportTimeLogs(): Promise<void> {
  try {
    await blobGet('/api/admin/export/time-logs', '展示时长明细.xlsx')
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } }; message?: string }
    ElMessage.error(err?.response?.data?.message || (err as Error).message || '导出失败')
  }
}

export async function exportTeamComparison(): Promise<void> {
  try {
    await blobGet('/api/admin/export/time-logs', '队伍对比报表.xlsx')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '导出失败')
  }
}

export async function exportIssues(): Promise<void> {
  try {
    await blobGet('/api/admin/export/time-logs', '问题闭环清单.xlsx')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '导出失败')
  }
}

export async function generateWeeklyReport(): Promise<void> {
  try {
    await blobGet('/api/admin/export/time-logs', '周报文档.docx')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '导出失败')
  }
}

export async function generatePresentation(): Promise<void> {
  try {
    await blobGet('/api/admin/export/time-logs', '汇报材料.pdf')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '导出失败')
  }
}
