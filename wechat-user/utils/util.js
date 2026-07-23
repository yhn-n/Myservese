const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return `${year}-${padZero(month)}-${padZero(day)} ${padZero(hour)}:${padZero(minute)}:${padZero(second)}`
}

const padZero = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

const formatStatus = status => {
  const map = {
    0: '待支付',
    1: '充电中',
    2: '已完成',
    3: '已取消'
  }
  return map[status] || '未知'
}

module.exports = { formatTime, formatStatus }
