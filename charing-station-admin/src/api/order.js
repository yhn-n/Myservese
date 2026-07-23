import request from '@/untils/request.js'

export function getStatistics() {
  return request.get('/admin/order/statistics')
}

export function getOrderList(params) {
  return request.get('/admin/order/list', { params })
}
