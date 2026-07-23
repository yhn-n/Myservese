const { getStationList } = require('../../api/station')

const HISTORY_KEY = 'search_history'
const MAX_HISTORY = 10

Page({
  data: {
    keyword: '',
    stations: [],
    searching: false,
    history: []
  },

  onLoad() {
    this.loadHistory()
  },

  loadHistory() {
    const history = wx.getStorageSync(HISTORY_KEY) || []
    this.setData({ history })
  },

  saveHistory(keyword) {
    if (!keyword || !keyword.trim()) return
    const key = keyword.trim()
    let history = wx.getStorageSync(HISTORY_KEY) || []
    history = history.filter(item => item !== key)
    history.unshift(key)
    if (history.length > MAX_HISTORY) {
      history = history.slice(0, MAX_HISTORY)
    }
    wx.setStorageSync(HISTORY_KEY, history)
    this.setData({ history })
  },

  clearHistory() {
    wx.removeStorageSync(HISTORY_KEY)
    this.setData({ history: [] })
  },

  onInput(e) {
    const keyword = e.detail.value
    this.setData({ keyword })

    if (!keyword || !keyword.trim()) {
      this.setData({ stations: [] })
      return
    }

    this.search(keyword)
  },

  onSearch(e) {
    const keyword = e.detail.value
    this.search(keyword)
    this.saveHistory(keyword)
  },

  onHistoryTap(e) {
    const keyword = e.currentTarget.dataset.item
    this.setData({ keyword })
    this.search(keyword)
    this.saveHistory(keyword)
  },

  clearKeyword() {
    this.setData({ keyword: '', stations: [] })
  },

  async search(keyword) {
    if (!keyword || !keyword.trim()) {
      this.setData({ stations: [] })
      return
    }

    this.setData({ searching: true })
    try {
      const res = await getStationList({
        pageNum: 1,
        pageSize: 50,
        keyword: keyword.trim()
      })
      const list = res.data.records || []
      this.setData({ stations: list })
    } catch (e) {
      console.error('搜索失败', e)
      wx.showToast({ title: '搜索失败', icon: 'none' })
    } finally {
      this.setData({ searching: false })
    }
  },

  goStation(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/station/station?id=' + id })
  },

  goBack() {
    wx.navigateBack()
  }
})
