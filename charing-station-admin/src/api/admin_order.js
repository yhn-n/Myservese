import request from '@/untils/request.js'

export function getOrderList(params) {
  return request.get('/admin/order/list', { params })
}

export function getOrderDetail(id) {
  return request.get(`/admin/order/${id}`)
}
