const { request } = require('../utils/request')

const getStationList = (params) => request('/miniapp/station/list', { data: params })

const getStationDetail = (id) => request('/miniapp/station/' + id)

const getNearbyStations = (longitude, latitude, radius) =>
  request('/miniapp/station/nearby', { data: { longitude, latitude, radius } })

const getChargerList = (stationId) => request('/miniapp/charger/list/' + stationId)

const getChargerByCode = (code) => request('/miniapp/charger/code/' + code)

module.exports = { getStationList, getStationDetail, getNearbyStations, getChargerList, getChargerByCode }
