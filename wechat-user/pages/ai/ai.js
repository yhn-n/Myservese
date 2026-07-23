const { request } = require('../../utils/request')

let msgId = 0

Page({
  data: {
    messages: [],
    inputValue: '',
    loading: false,
    scrollToId: '',
    quickQuestions: [
      '充电站怎么找？',
      '充电怎么收费？',
      '怎么扫码充电？',
      '充电中可以中断吗？',
      '如何查看充电记录？',
      '充值后余额没到账？'
    ]
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value })
  },

  sendQuickQuestion(e) {
    const question = e.currentTarget.dataset.question
    this.setData({ inputValue: question })
    this.sendMessage()
  },

  async sendMessage() {
    const { inputValue, messages } = this.data
    if (!inputValue.trim()) return

    const userMsg = {
      id: ++msgId,
      role: 'user',
      content: inputValue.trim()
    }

    this.setData({
      messages: [...messages, userMsg],
      inputValue: '',
      loading: true,
      scrollToId: 'msg-' + userMsg.id
    })

    try {
      const res = await request('/miniapp/ai/chat', {
        method: 'POST',
        data: { message: inputValue.trim() }
      })

      const assistantMsg = {
        id: ++msgId,
        role: 'assistant',
        content: res.data.reply || '抱歉，我暂时无法回答这个问题。'
      }

      this.setData({
        messages: [...this.data.messages, assistantMsg],
        scrollToId: 'msg-' + assistantMsg.id
      })
    } catch (e) {
      const errorMsg = {
        id: ++msgId,
        role: 'assistant',
        content: '网络异常，请稍后再试。'
      }
      this.setData({
        messages: [...this.data.messages, errorMsg],
        scrollToId: 'msg-' + errorMsg.id
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
