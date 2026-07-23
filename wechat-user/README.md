# 新能源充电站管理平台 - 用户端（微信小程序）

## 项目结构

```
charging-station-miniapp/
├── api/                    # API接口
│   ├── user.js            # 用户相关接口
│   ├── station.js         # 充电站相关接口
│   ├── order.js           # 订单相关接口
│   ├── notice.js          # 公告相关接口
│   ├── feedback.js        # 反馈相关接口
│   └── recharge.js        # 充值相关接口
├── pages/
│   ├── index/             # 首页（地图+充电站列表）
│   ├── station/           # 充电站详情
│   ├── charging/          # 充电中页面
│   ├── order/             # 订单列表
│   ├── order-detail/      # 订单详情
│   ├── recharge/          # 账户充值
│   ├── ai/                # AI客服
│   ├── feedback/          # 故障反馈
│   ├── notice/            # 公告通知
│   ├── profile/           # 个人中心
│   └── login/             # 登录页
├── utils/
│   ├── request.js         # HTTP请求封装
│   └── util.js            # 工具函数
├── static/                # 静态资源
├── app.js                 # 小程序入口
├── app.json               # 小程序配置
├── app.wxss               # 全局样式
└── project.config.json    # 项目配置
```

## 功能模块

1. **首页** - 地图展示附近充电站，支持搜索和定位
2. **站点详情** - 查看充电站信息、充电桩列表、导航、扫码充电
3. **充电中** - 实时显示充电状态、电量、费用
4. **订单管理** - 查看订单列表、订单详情、结束充电、取消订单
5. **账户充值** - 多种金额选择充值
6. **AI客服** - 智能问答，支持常见问题
7. **故障反馈** - 提交问题反馈，上传图片
8. **公告通知** - 查看系统公告
9. **个人中心** - 用户信息、余额、功能入口

## 后端API

小程序通过HTTP请求与后端通信，API前缀为 `/miniapp/`

### 需要登录的接口
- `GET /miniapp/user/info` - 获取用户信息
- `PUT /miniapp/user/update` - 更新用户信息
- `POST /miniapp/order/create` - 创建充电订单
- `GET /miniapp/order/list` - 获取订单列表
- `GET /miniapp/order/{id}` - 获取订单详情
- `PUT /miniapp/order/{id}/finish` - 结束充电
- `PUT /miniapp/order/{id}/cancel` - 取消订单
- `POST /miniapp/feedback/submit` - 提交反馈
- `GET /miniapp/feedback/list` - 获取反馈列表
- `POST /miniapp/recharge` - 用户充值

### 无需登录的接口
- `POST /miniapp/user/login` - 手机号登录
- `GET /miniapp/station/list` - 充电站列表
- `GET /miniapp/station/{id}` - 充电站详情
- `GET /miniapp/station/nearby` - 附近充电站
- `GET /miniapp/charger/list/{stationId}` - 充电桩列表
- `GET /miniapp/notice/list` - 公告列表
- `GET /miniapp/notice/{id}` - 公告详情
- `POST /miniapp/ai/chat` - AI客服对话

## 配置说明

### API地址配置
在 `utils/request.js` 中修改 `BASE_URL`：
```javascript
const BASE_URL = 'http://127.0.0.1:8080'  // 开发环境
// const BASE_URL = 'https://your-domain.com'  // 生产环境
```

### TabBar图标
请将 `static/` 目录下的占位图标替换为真实图标（81x81像素PNG）

## 使用说明

1. 使用微信开发者工具导入项目
2. 在 `project.config.json` 中填入你的小程序AppID
3. 配置后端API地址
4. 替换静态资源（图标、Logo等）
5. 预览或发布小程序
