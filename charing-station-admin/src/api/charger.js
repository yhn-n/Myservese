import request from '@/untils/request.js'

export function getChargerList(params) {
  return request.get('/admin/charger/list', { params })
}

export function getChargerDetail(id) {
  return request.get(`/admin/charger/${id}`)
}

export function addCharger(data) {
  return request.post('/admin/charger', data)
}

export function updateCharger(data) {
  return request.put('/admin/charger', data)
}

export function deleteCharger(id) {
  return request.delete(`/admin/charger/${id}`)
}

export function getChargerQrcode(id) {
  return request.get(`/admin/charger/${id}/qrcode`)
}
