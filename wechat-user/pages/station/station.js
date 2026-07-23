const { getStationDetail, getChargerList } = require('../../api/station')

Page({
  data: {
    station: null,
    chargers: []
  },

  onLoad(options) {
    if (options.id) {
      this.loadStation(options.id)
      this.loadChargers(options.id)
    }
  },

  async loadStation(id) {
    try {
      const res = await getStationDetail(id)
      this.setData({ station: res.data })
      wx.setNavigationBarTitle({ title: res.data.name })
    } catch (e) {
      console.error('加载站点详情失败', e)
    }
  },

  async loadChargers(stationId) {
    try {
      const res = await getChargerList(stationId)
      this.setData({ chargers: res.data || [] })
    } catch (e) {
      console.error('加载充电桩失败', e)
    }
  },

  callPhone() {
    if (this.data.station.phone) {
      wx.makePhoneCall({ phoneNumber: this.data.station.phone })
    }
  },

  navigateTo() {
    const { station } = this.data
    if (station.latitude && station.longitude) {
      wx.openLocation({
        latitude: parseFloat(station.latitude),
        longitude: parseFloat(station.longitude),
        name: station.name,
        address: station.address
      })
    }
  },

  startCharge() {
    wx.scanCode({
      success: (res) => {
        wx.navigateTo({
          url: '/pages/charging/charging?chargerCode=' + res.result
        })
      },
      fail: () => {
        wx.showToast({ title: '扫码取消', icon: 'none' })
      }
    })
  }
})
