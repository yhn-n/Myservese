import request from '@/untils/request.js'

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}
