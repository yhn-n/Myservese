import request from '@/untils/request.js'

export function getFeedbackList(params) {
  return request.get('/admin/feedback/list', { params })
}

export function getFeedbackDetail(id) {
  return request.get(`/admin/feedback/${id}`)
}

export function updateFeedback(data) {
  return request.put('/admin/feedback', data)
}

export function deleteFeedback(id) {
  return request.delete(`/admin/feedback/${id}`)
}
