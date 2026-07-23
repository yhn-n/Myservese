const { getOrderDetail, finishOrder } = require('../../api/order')
const { formatStatus } = require('../../utils/util')

Page({
  data: {
    order: null
  },

  onLoad(options) {
    if (options.id) {
      this.loadOrder(options.id)
    }
  },

  async loadOrder(id) {
    try {
      const res = await getOrderDetail(id)
      const order = res.data
      order.statusText = formatStatus(order.status)
      this.setData({ order })
    } catch (e) {
      console.error('加载订单详情失败', e)
    }
  },

  async finishCharge() {
    wx.showModal({
      title: '确认',
      content: '确定要结束充电吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await finishOrder(this.data.order.id)
            wx.showToast({ title: '已结束', icon: 'success' })
            this.loadOrder(this.data.order.id)
          } catch (e) {
            console.error(e)
          }
        }
      }
    })
  }
})
