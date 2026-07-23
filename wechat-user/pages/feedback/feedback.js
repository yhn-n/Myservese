const { submitFeedback, getFeedbackList } = require('../../api/feedback')
const { getStationList } = require('../../api/station')

Page({
  data: {
    types: [
      { value: 0, label: '充电桩故障' },
      { value: 1, label: '充电异常' },
      { value: 2, label: '计费问题' },
      { value: 3, label: '环境问题' },
      { value: 4, label: '其他问题' }
    ],
    typeIndex: -1,
    stations: [],
    stationIndex: -1,
    content: '',
    images: [],
    feedbacks: [],
    loading: false
  },

  onShow() {
    this.loadStations()
    this.loadFeedbacks()
  },

  async loadStations() {
    try {
      const res = await getStationList({ pageNum: 1, pageSize: 100 })
      this.setData({ stations: res.data.records || [] })
    } catch (e) {
      console.error(e)
    }
  },

  async loadFeedbacks() {
    try {
      const res = await getFeedbackList({ pageNum: 1, pageSize: 10 })
      this.setData({ feedbacks: res.data.records || [] })
    } catch (e) {
      console.error(e)
    }
  },

  onTypeChange(e) {
    this.setData({ typeIndex: parseInt(e.detail.value) })
  },

  onStationChange(e) {
    this.setData({ stationIndex: parseInt(e.detail.value) })
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  chooseImage() {
    const remaining = 3 - this.data.images.length
    wx.chooseMedia({
      count: remaining,
      mediaType: ['image'],
      success: (res) => {
        const newImages = res.tempFiles.map(f => f.tempFilePath)
        this.setData({ images: [...this.data.images, ...newImages] })
      }
    })
  },

  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = [...this.data.images]
    images.splice(index, 1)
    this.setData({ images })
  },

  previewImage(e) {
    wx.previewImage({
      current: e.currentTarget.dataset.url,
      urls: this.data.images
    })
  },

  async submitFeedback() {
    const { typeIndex, types, stations, stationIndex, content, images } = this.data

    if (typeIndex < 0) {
      wx.showToast({ title: '请选择反馈类型', icon: 'none' })
      return
    }
    if (!content.trim()) {
      wx.showToast({ title: '请输入反馈内容', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    try {
      const data = {
        type: types[typeIndex].value,
        content: content.trim(),
        images: images.length > 0 ? images.join(',') : ''
      }
      if (stationIndex >= 0) {
        data.stationId = stations[stationIndex].id
      }

      await submitFeedback(data)
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({ content: '', images: [], typeIndex: -1, stationIndex: -1 })
      this.loadFeedbacks()
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ loading: false })
    }
  }
})
