const { request } = require('../utils/request')

const submitFeedback = (data) => request('/miniapp/feedback/submit', { method: 'POST', data })

const getFeedbackList = (params) => request('/miniapp/feedback/list', { data: params })

module.exports = { submitFeedback, getFeedbackList }
