const { request } = require('../utils/request')

const getNoticeList = (params) => request('/miniapp/notice/list', { data: params })

const getNoticeDetail = (id) => request('/miniapp/notice/' + id)

module.exports = { getNoticeList, getNoticeDetail }
