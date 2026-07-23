Component({
  data: {
    selected: 0,
    safeAreaBottom: false,
    list: [
      { pagePath: '/pages/index/index', text: '首页' },
      { pagePath: '/pages/order/order', text: '订单' },
      { pagePath: '/pages/profile/profile', text: '我的' }
    ]
  },
  lifetimes: {
    attached() {
      const info = wx.getWindowInfo ? wx.getWindowInfo() : wx.getSystemInfoSync()
      this.setData({
        safeAreaBottom: info.safeArea && info.safeArea.bottom < info.screenHeight
      })
    }
  },
  methods: {
    switchTab(e) {
      const { index, path } = e.currentTarget.dataset
      if (this.data.selected === index) return
      wx.switchTab({ url: path })
    }
  }
})
