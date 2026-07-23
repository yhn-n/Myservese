// 在微信开发者工具的控制台中运行此脚本生成TabBar图标
// 或者使用其他图标生成工具

const fs = require('fs')
const path = require('path')

// 简单的1x1像素PNG（透明）作为占位符
// 实际使用时请替换为真实图标
const placeholderPNG = Buffer.from(
  'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVQI12NgAAIABQAB' +
  'Nl7BcQAAAABJRU5ErkJggg==',
  'base64'
)

const icons = [
  'tab-home.png',
  'tab-home-active.png',
  'tab-order.png',
  'tab-order-active.png',
  'tab-profile.png',
  'tab-profile-active.png',
  'default-avatar.png',
  'marker.png',
  'logo.png'
]

const staticDir = path.join(__dirname, 'static')

icons.forEach(icon => {
  const filePath = path.join(staticDir, icon)
  if (!fs.existsSync(filePath)) {
    fs.writeFileSync(filePath, placeholderPNG)
    console.log(`Created: ${icon}`)
  } else {
    console.log(`Exists: ${icon}`)
  }
})

console.log('Done! Please replace placeholder icons with real icons.')
