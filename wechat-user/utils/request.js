// 开发环境用电脑IP，生产环境换成服务器域名
const BASE_URL = 'http://192.168.0.5:8080'

const request = (url, options = {}) => {
  const { silent, ...requestOptions } = options
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.request({
      url: BASE_URL + url,
      method: requestOptions.method || 'GET',
      data: requestOptions.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : '',
        ...requestOptions.header
      },
      success: (res) => {
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          reject(new Error('未登录'))
          return
        }
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          if (!silent) {
            wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          }
          reject(new Error(res.data.msg))
        }
      },
      fail: (err) => {
        if (!silent) {
          wx.showToast({ title: '网络异常', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}

module.exports = { request, BASE_URL }
