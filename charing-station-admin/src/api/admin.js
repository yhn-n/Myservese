import request from '@/untils/request.js'

export function login(data) {
  return request.post('/admin/login', data)
}
