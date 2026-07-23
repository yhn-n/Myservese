import request from '@/untils/request.js'

export function getStationList(params) {
  return request.get('/station/list', { params })
}

export function getAllStations() {
  return request.get('/station/options')
}

export function getStationDetail(id) {
  return request.get(`/station/${id}`)
}

export function addStation(data) {
  return request.post('/station', data)
}

export function updateStation(data) {
  return request.put('/station', data)
}

export function deleteStation(id) {
  return request.delete(`/station/${id}`)
}
