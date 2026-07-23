const { getNoticeDetail } = require('../../api/notice')

Page({
  data: {
    notice: null
  },

  onLoad(options) {
    if (options.id) {
      this.loadNotice(options.id)
    }
  },

  async loadNotice(id) {
    try {
      const res = await getNoticeDetail(id)
      this.setData({ notice: res.data })
      wx.setNavigationBarTitle({ title: res.data.title })
    } catch (e) {
      console.error('加载公告详情失败', e)
    }
  }
})
