import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/station/list',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'station/list',
        name: 'StationList',
        component: () => import('@/views/station/list.vue'),
        meta: { title: '站点列表' }
      },
      {
        path: 'station/map',
        name: 'StationMap',
        component: () => import('@/views/station/map.vue'),
        meta: { title: '地图视图' }
      },
      {
        path: 'charger',
        name: 'Charger',
        component: () => import('@/views/charger/index.vue'),
        meta: { title: '充电桩管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '订单列表' }
      },
      {
        path: 'order/statistics',
        name: 'OrderStatistics',
        component: () => import('@/views/order/statistics.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/feedback/index.vue'),
        meta: { title: '故障反馈' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'system/ai-config',
        name: 'AiConfig',
        component: () => import('@/views/system/ai-config.vue'),
        meta: { title: 'AI客服配置' }
      },
      {
        path: 'system/login-log',
        name: 'LoginLog',
        component: () => import('@/views/system/login-log.vue'),
        meta: { title: '登录日志' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
