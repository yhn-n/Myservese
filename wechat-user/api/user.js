const { request } = require('../utils/request')

const login = (phone, password, options) => {
  // 支持两种调用方式:
  // login(phone, password) / login(phone, password, options)
  // login(phone, options) — password 为空字符串，用于自动登录
  if (typeof password === 'object') {
    options = password
    password = ''
  }
  return request('/miniapp/user/login', { method: 'POST', data: { phone, password }, ...(options || {}) })
}

const register = (phone, password, nickname, options = {}) => request('/miniapp/user/register', { method: 'POST', data: { phone, password, nickname }, ...options })

const wxLogin = (code, options = {}) => request('/miniapp/user/wx-login', { method: 'POST', data: { code }, ...options })

const getUserInfo = () => request('/miniapp/user/info')

const updateUserInfo = (data) => request('/miniapp/user/update', { method: 'PUT', data })

module.exports = { login, register, wxLogin, getUserInfo, updateUserInfo }
