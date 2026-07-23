const { getStationList, getNearbyStations } = require('../../api/station')

Page({
  data: {
    latitude: 30.5728,
    longitude: 104.0668,
    statusBarHeight: 0,
    markers: [],
    stations: []
  },

  onLoad() {
    const sysInfo = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: sysInfo.statusBarHeight })
    this.getLocation()
  },

  onShow() {
    if (this.getTabBar) {
      this.getTabBar().setData({ selected: 0 })
    }
    this.loadStations()
  },

  getLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        })
        this.loadNearbyStations(res.longitude, res.latitude)
      },
      fail: () => {
        this.loadStations()
      }
    })
  },

  async loadNearbyStations(longitude, latitude) {
    try {
      const res = await getNearbyStations(longitude, latitude, 10)
      const list = res.data || []
      const markers = list.map((s, i) => ({
        id: i,
        latitude: parseFloat(s.latitude),
        longitude: parseFloat(s.longitude),
        title: s.name,
        width: 30,
        height: 30
      }))
      this.setData({ stations: list, markers })
    } catch (e) {
      this.loadStations()
    }
  },

  async loadStations() {
    try {
      const res = await getStationList({ pageNum: 1, pageSize: 20 })
      const list = res.data.records || []
      this.setData({ stations: list })
    } catch (e) {
      console.error('加载充电站失败', e)
    }
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  goStation(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/station/station?id=' + id })
  },

  onMarkerTap(e) {
    const markerId = e.markerId
    const station = this.data.stations[markerId]
    if (station) {
      wx.navigateTo({ url: '/pages/station/station?id=' + station.id })
    }
  },

  onMapTap() {}
})
