const { login, register, wxLogin } = require('../../api/user')

Page({
  data: {
    mode: 'login',
    phone: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    loading: false,
    agreed: false,
    focusField: '',
    showPassword: false
  },

  onLoad() {
    const token = wx.getStorageSync('token')
    if (token) {
      wx.switchTab({ url: '/pages/index/index' })
    }
  },

  goBack() {
    const pages = getCurrentPages()
    if (pages.length > 1) {
      wx.navigateBack()
    } else {
      wx.switchTab({ url: '/pages/index/index' })
    }
  },

  switchMode(e) {
    const mode = e.currentTarget.dataset.mode
    this.setData({
      mode,
      phone: '',
      password: '',
      confirmPassword: '',
      nickname: ''
    })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  onFocus(e) {
    this.setData({ focusField: e.currentTarget.dataset.field })
  },

  onBlur() {
    this.setData({ focusField: '' })
  },

  clearPhone() {
    this.setData({ phone: '' })
  },

  togglePassword() {
    this.setData({ showPassword: !this.data.showPassword })
  },

  toggleAgree() {
    this.setData({ agreed: !this.data.agreed })
  },

  validate() {
    const { phone, password, confirmPassword, mode } = this.data
    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    if (!password || password.length < 6) {
      wx.showToast({ title: '密码不少于6位', icon: 'none' })
      return false
    }
    if (mode === 'register') {
      if (password !== confirmPassword) {
        wx.showToast({ title: '两次密码输入不一致', icon: 'none' })
        return false
      }
    }
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议', icon: 'none' })
      return false
    }
    return true
  },

  async handleSubmit() {
    if (!this.validate()) return

    const { phone, password, nickname, mode } = this.data
    this.setData({ loading: true })

    try {
      if (mode === 'login') {
        const res = await login(phone, password)
        wx.setStorageSync('token', res.data.token)
        wx.setStorageSync('userInfo', res.data.user)
        wx.showToast({ title: '登录成功', icon: 'success' })
      } else {
        await register(phone, password, nickname)
        wx.showToast({ title: '注册成功，请登录', icon: 'success' })
        this.setData({ mode: 'login', password: '', confirmPassword: '', nickname: '' })
        return
      }
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (e) {
      console.error(mode === 'login' ? '登录失败' : '注册失败', e)
      wx.showToast({ title: e.message || '操作失败，请重试', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  async wxLogin() {
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议', icon: 'none' })
      return
    }
    try {
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        })
      })
      const res = await wxLogin(loginRes.code)
      wx.setStorageSync('token', res.data.token)
      wx.setStorageSync('userInfo', res.data.user)
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (e) {
      console.error('微信登录失败', e)
      wx.showToast({ title: e.message || '微信登录失败', icon: 'none' })
    }
  },

  openAgreement() {
    wx.showModal({ title: '用户协议', content: '用户协议内容待补充', showCancel: false })
  },

  openPrivacy() {
    wx.showModal({ title: '隐私政策', content: '隐私政策内容待补充', showCancel: false })
  }
})
