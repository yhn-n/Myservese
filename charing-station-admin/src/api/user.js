import request from '@/untils/request.js'

export function getUserList(params) {
  return request.get('/admin/user/list', { params })
}

export function getUserDetail(id) {
  return request.get(`/admin/user/${id}`)
}

export function updateUser(data) {
  return request.put('/admin/user', data)
}

export function deleteUser(id) {
  return request.delete(`/admin/user/${id}`)
}
