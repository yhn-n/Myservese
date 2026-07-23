const { request } = require('../utils/request')

const createOrder = (data) => request('/miniapp/order/create', { method: 'POST', data })

const getOrderList = (params) => request('/miniapp/order/list', { data: params })

const getOrderDetail = (id) => request('/miniapp/order/' + id)

const finishOrder = (id) => request('/miniapp/order/' + id + '/finish', { method: 'PUT' })

const cancelOrder = (id) => request('/miniapp/order/' + id + '/cancel', { method: 'PUT' })

module.exports = { createOrder, getOrderList, getOrderDetail, finishOrder, cancelOrder }
