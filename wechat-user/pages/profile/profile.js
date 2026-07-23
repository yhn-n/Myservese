const { getUserInfo } = require('../../api/user')

Page({
  data: {
    userInfo: {}
  },

  onShow() {
    if (this.getTabBar) {
      this.getTabBar().setData({ selected: 2 })
    }
    const token = wx.getStorageSync('token')
    if (token) {
      this.loadUserInfo()
    } else {
      this.setData({ userInfo: {} })
    }
  },

  async loadUserInfo() {
    try {
      const res = await getUserInfo()
      this.setData({ userInfo: res.data })
      wx.setStorageSync('userInfo', res.data)
    } catch (e) {
      console.error(e)
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goRecharge() {
    if (!this.data.userInfo.id) {
      this.goLogin()
      return
    }
    wx.navigateTo({ url: '/pages/recharge/recharge' })
  },

  goPage(e) {
    const url = e.currentTarget.dataset.url
    wx.navigateTo({ url })
  },

  showAbout() {
    wx.showModal({
      title: '关于我们',
      content: '新能源充电站管理平台 v1.0.0\n智能充电，绿色出行',
      showCancel: false
    })
  },

  showAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '用户协议内容待补充',
      showCancel: false
    })
  },

  handleLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          this.setData({ userInfo: {} })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  }
})
