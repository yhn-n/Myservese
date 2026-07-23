import request from '@/untils/request.js'

export function getNoticeList(params) {
  return request.get('/admin/notice/list', { params })
}

export function getNoticeDetail(id) {
  return request.get(`/admin/notice/${id}`)
}

export function addNotice(data) {
  return request.post('/admin/notice', data)
}

export function updateNotice(data) {
  return request.put('/admin/notice', data)
}

export function deleteNotice(id) {
  return request.delete(`/admin/notice/${id}`)
}
