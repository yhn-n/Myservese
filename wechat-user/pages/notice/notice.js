const { getNoticeList } = require('../../api/notice')

Page({
  data: {
    notices: [],
    pageNum: 1,
    hasMore: true,
    loading: false
  },

  onShow() {
    this.setData({ notices: [], pageNum: 1, hasMore: true })
    this.loadNotices()
  },

  async loadNotices() {
    if (this.data.loading) return
    this.setData({ loading: true })

    try {
      const res = await getNoticeList({
        pageNum: this.data.pageNum,
        pageSize: 10
      })
      const records = res.data.records || []

      this.setData({
        notices: [...this.data.notices, ...records],
        hasMore: records.length === 10,
        pageNum: this.data.pageNum + 1
      })
    } catch (e) {
      console.error('加载公告失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadMore() {
    if (this.data.hasMore) {
      this.loadNotices()
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/notice/notice-detail?id=' + id })
  }
})
