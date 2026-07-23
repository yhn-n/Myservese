const { recharge } = require('../../api/recharge')
const { getUserInfo } = require('../../api/user')

Page({
  data: {
    balance: '0.00',
    selectedAmount: 50,
    customAmount: '',
    amounts: [
      { value: 30, desc: '' },
      { value: 50, desc: '推荐' },
      { value: 100, desc: '送10元' },
      { value: 200, desc: '送30元' },
      { value: 500, desc: '送100元' },
      { value: 1000, desc: '送250元' }
    ],
    loading: false
  },

  onShow() {
    this.loadBalance()
  },

  async loadBalance() {
    try {
      const res = await getUserInfo()
      this.setData({ balance: res.data.balance || '0.00' })
    } catch (e) {
      console.error(e)
    }
  },

  selectAmount(e) {
    this.setData({
      selectedAmount: e.currentTarget.dataset.value,
      customAmount: ''
    })
  },

  onCustomInput(e) {
    this.setData({
      customAmount: e.detail.value,
      selectedAmount: 0
    })
  },

  async handleRecharge() {
    const amount = this.data.customAmount || this.data.selectedAmount
    if (!amount || amount <= 0) {
      wx.showToast({ title: '请选择充值金额', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    try {
      await recharge(amount)
      wx.showToast({ title: '充值成功', icon: 'success' })
      this.loadBalance()
    } catch (e) {
      console.error('充值失败', e)
    } finally {
      this.setData({ loading: false })
    }
  }
})
