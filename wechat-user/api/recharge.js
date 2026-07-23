const { request } = require('../utils/request')

const recharge = (amount) => request('/miniapp/recharge', { method: 'POST', data: { amount } })

module.exports = { recharge }
