const { createOrder, finishOrder } = require('../../api/order')
const { getChargerByCode } = require('../../api/station')

Page({
  data: {
    orderId: null,
    stationId: null,
    stationName: '',
    chargerId: null,
    chargerCode: '',
    gunId: 1,
    startTime: '',
    energy: '0.00',
    cost: '0.00',
    price: 1.2,
    progress: 0,
    loading: true
  },

  onLoad(options) {
    this.setData({
      chargerCode: options.chargerCode || '',
      startTime: this.formatTime(new Date())
    })

    if (options.chargerCode) {
      this.lookupCharger(options.chargerCode)
    } else {
      this.setData({ loading: false })
      wx.showToast({ title: '无效的充电桩', icon: 'none' })
    }
  },

  formatTime(date) {
    const pad = n => n.toString().padStart(2, '0')
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
  },

  async lookupCharger(code) {
    try {
      const res = await getChargerByCode(code)
      const { charger, station } = res.data
      this.setData({
        stationId: station.id,
        stationName: station.name,
        chargerId: charger.id,
        price: charger.price || 1.2,
        loading: false
      })
      this.startCharge()
    } catch (e) {
      console.error('查询充电桩失败', e)
      this.setData({ loading: false })
      wx.showToast({ title: '充电桩不存在', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
    }
  },

  async startCharge() {
    if (!this.data.stationId || !this.data.chargerId) return
    try {
      const res = await createOrder({
        stationId: this.data.stationId,
        chargerId: this.data.chargerId,
        gunId: this.data.gunId
      })
      this.setData({ orderId: res.data.id })
      this.startTimer()
    } catch (e) {
      console.error('创建订单失败', e)
      wx.showToast({ title: '创建订单失败', icon: 'none' })
    }
  },

  startTimer() {
    this.timer = setInterval(() => {
      const { energy, price } = this.data
      const newEnergy = (parseFloat(energy) + 0.1).toFixed(2)
      const newCost = (parseFloat(newEnergy) * price).toFixed(2)
      const progress = Math.min(parseFloat(newEnergy) / 60 * 100, 100)
      this.setData({ energy: newEnergy, cost: newCost, progress })
    }, 3000)
  },

  async stopCharge() {
    wx.showModal({
      title: '确认结束',
      content: '确定要结束充电吗？',
      success: async (res) => {
        if (res.confirm && this.data.orderId) {
          clearInterval(this.timer)
          try {
            await finishOrder(this.data.orderId)
            wx.showToast({ title: '充电结束', icon: 'success' })
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
          } catch (e) {
            console.error('结束充电失败', e)
          }
        }
      }
    })
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer)
  }
})
