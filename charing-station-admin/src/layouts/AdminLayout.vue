<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo">
        <span class="brand-mark"></span>
        <span v-if="!isCollapse" class="brand-name">充电站管理平台</span>
      </div>
      <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-sub-menu index="station">
          <template #title>
            <el-icon><Location /></el-icon>
            <span>网点管理</span>
          </template>
          <el-menu-item index="/station/list">站点列表</el-menu-item>
          <el-menu-item index="/station/map">地图视图</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/charger">
          <el-icon><Lightning /></el-icon>
          <template #title>充电桩管理</template>
        </el-menu-item>

        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-sub-menu index="order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">订单列表</el-menu-item>
          <el-menu-item index="/order/statistics">数据统计</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/feedback">
          <el-icon><Warning /></el-icon>
          <template #title>故障反馈</template>
        </el-menu-item>

        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/ai-config">AI客服配置</el-menu-item>
          <el-menu-item index="/system/login-log">登录日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta?.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-info">
              <el-avatar :size="30" class="admin-avatar">{{ adminInfo?.realName?.charAt(0) }}</el-avatar>
              <span class="admin-name">{{ adminInfo?.realName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HomeFilled, Location, Lightning, User, Document,
  Warning, Bell, Setting, Fold, Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const adminInfo = computed(() => {
  const info = localStorage.getItem('admin_info')
  return info ? JSON.parse(info) : null
})

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta?.title)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background-color: var(--color-background);
}

.admin-aside {
  display: flex;
  flex-direction: column;
  background-color: var(--color-surface);
  border-right: 1px solid var(--color-border);
  overflow-y: auto;
  overflow-x: hidden;
  transition: width var(--motion-base) var(--easing-standard);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 18px;
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}

.brand-mark {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  background:
    conic-gradient(from 220deg at 50% 50%, var(--color-primary), var(--color-elevated), var(--color-primary));
  border: 1px solid var(--color-border-strong);
}

.brand-name {
  font-family: var(--font-display);
  font-weight: 600;
  letter-spacing: -0.01em;
  color: var(--color-text-primary);
  font-size: 0.9375rem;
  white-space: nowrap;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding: 0 20px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--color-text-secondary);
  transition: color var(--motion-base) var(--easing-standard);
}

.collapse-btn:hover {
  color: var(--color-text-primary);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.admin-avatar {
  background-color: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 600;
  font-size: 0.875rem;
}

.admin-name {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.admin-main {
  background-color: var(--color-background);
  background-image:
    radial-gradient(at 20% 30%, rgba(91, 107, 255, 0.06) 0%, transparent 50%),
    radial-gradient(at 80% 70%, rgba(43, 224, 140, 0.04) 0%, transparent 50%);
  padding: 20px;
  overflow-y: auto;
}
</style>
