const { getOrderList, finishOrder, cancelOrder } = require('../../api/order')
const { formatStatus } = require('../../utils/util')

Page({
  data: {
    currentTab: -1,
    orders: [],
    pageNum: 1,
    hasMore: true,
    loading: false
  },

  onShow() {
    if (this.getTabBar) {
      this.getTabBar().setData({ selected: 1 })
    }
    this.setData({ orders: [], pageNum: 1, hasMore: true })
    this.loadOrders()
  },

  switchTab(e) {
    const tab = parseInt(e.currentTarget.dataset.tab)
    this.setData({ currentTab: tab, orders: [], pageNum: 1, hasMore: true })
    this.loadOrders()
  },

  async loadOrders() {
    if (this.data.loading) return
    this.setData({ loading: true })

    try {
      const params = {
        pageNum: this.data.pageNum,
        pageSize: 10
      }
      if (this.data.currentTab !== -1) {
        params.status = this.data.currentTab
      }

      const res = await getOrderList(params)
      const records = (res.data.records || []).map(item => ({
        ...item,
        statusText: formatStatus(item.status)
      }))

      this.setData({
        orders: [...this.data.orders, ...records],
        hasMore: records.length === 10,
        pageNum: this.data.pageNum + 1
      })
    } catch (e) {
      console.error('加载订单失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadMore() {
    if (this.data.hasMore) {
      this.loadOrders()
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
  },

  async finishOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认',
      content: '确定要结束充电吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await finishOrder(id)
            wx.showToast({ title: '已结束', icon: 'success' })
            this.setData({ orders: [], pageNum: 1, hasMore: true })
            this.loadOrders()
          } catch (e) {
            console.error(e)
          }
        }
      }
    })
  },

  async cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认',
      content: '确定要取消订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await cancelOrder(id)
            wx.showToast({ title: '已取消', icon: 'success' })
            this.setData({ orders: [], pageNum: 1, hasMore: true })
            this.loadOrders()
          } catch (e) {
            console.error(e)
          }
        }
      }
    })
  }
})
