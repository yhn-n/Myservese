<template>
  <div class="home-page">
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2 class="welcome-title">{{ greeting }}，管理员</h2>
        <p class="welcome-desc">欢迎回到充电站管理平台，以下是今日运营概览</p>
      </div>
      <div class="welcome-date">
        <el-icon :size="16"><Calendar /></el-icon>
        <span>{{ currentDate }}</span>
      </div>
    </div>

    <!-- Stats Row -->
    <div class="stats-grid">
      <div class="stat-card stat-card--info" @click="$router.push('/station/list')">
        <div class="stat-card__icon">
          <el-icon :size="24"><Location /></el-icon>
        </div>
        <div class="stat-card__body">
          <span class="stat-card__label">充电站点</span>
          <span class="stat-card__value">{{ stats.stationCount || 0 }}</span>
        </div>
        <div class="stat-card__footer">
          <span class="stat-card__hint">管理全部网点</span>
          <el-icon :size="14"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="stat-card stat-card--success" @click="$router.push('/charger')">
        <div class="stat-card__icon">
          <el-icon :size="24"><Lightning /></el-icon>
        </div>
        <div class="stat-card__body">
          <span class="stat-card__label">充电桩</span>
          <span class="stat-card__value">{{ stats.chargerCount || 0 }}</span>
        </div>
        <div class="stat-card__footer">
          <span class="stat-card__hint">在线设备</span>
          <el-icon :size="14"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="stat-card stat-card--warning" @click="$router.push('/user')">
        <div class="stat-card__icon">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-card__body">
          <span class="stat-card__label">注册用户</span>
          <span class="stat-card__value">{{ stats.userCount || 0 }}</span>
        </div>
        <div class="stat-card__footer">
          <span class="stat-card__hint">用户管理</span>
          <el-icon :size="14"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="stat-card stat-card--danger" @click="$router.push('/order/list')">
        <div class="stat-card__icon">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-card__body">
          <span class="stat-card__label">订单总数</span>
          <span class="stat-card__value">{{ stats.orderCount || 0 }}</span>
        </div>
        <div class="stat-card__footer">
          <span class="stat-card__hint">交易记录</span>
          <el-icon :size="14"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
      <!-- Quick Actions -->
      <div class="panel quick-panel">
        <div class="panel__header">
          <span class="panel__title">快捷操作</span>
        </div>
        <div class="panel__body">
          <div class="action-grid">
            <div class="action-item" @click="$router.push('/station/list')">
              <div class="action-item__icon action-item__icon--blue">
                <el-icon :size="20"><Location /></el-icon>
              </div>
              <span class="action-item__text">站点管理</span>
            </div>
            <div class="action-item" @click="$router.push('/charger')">
              <div class="action-item__icon action-item__icon--green">
                <el-icon :size="20"><Lightning /></el-icon>
              </div>
              <span class="action-item__text">充电桩管理</span>
            </div>
            <div class="action-item" @click="$router.push('/order/list')">
              <div class="action-item__icon action-item__icon--orange">
                <el-icon :size="20"><Document /></el-icon>
              </div>
              <span class="action-item__text">订单管理</span>
            </div>
            <div class="action-item" @click="$router.push('/user')">
              <div class="action-item__icon action-item__icon--purple">
                <el-icon :size="20"><User /></el-icon>
              </div>
              <span class="action-item__text">用户管理</span>
            </div>
            <div class="action-item" @click="$router.push('/feedback')">
              <div class="action-item__icon action-item__icon--red">
                <el-icon :size="20"><Warning /></el-icon>
              </div>
              <span class="action-item__text">故障反馈</span>
            </div>
            <div class="action-item" @click="$router.push('/notice')">
              <div class="action-item__icon action-item__icon--cyan">
                <el-icon :size="20"><Bell /></el-icon>
              </div>
              <span class="action-item__text">公告管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- System Info -->
      <div class="panel info-panel">
        <div class="panel__header">
          <span class="panel__title">系统信息</span>
        </div>
        <div class="panel__body">
          <div class="info-list">
            <div class="info-row">
              <span class="info-row__label">系统名称</span>
              <span class="info-row__value">充电站管理平台</span>
            </div>
            <div class="info-row">
              <span class="info-row__label">系统版本</span>
              <span class="info-row__value">v1.0.0</span>
            </div>
            <div class="info-row">
              <span class="info-row__label">运行状态</span>
              <span class="info-row__value">
                <span class="status-dot status-dot--online"></span>
                正常运行
              </span>
            </div>
            <div class="info-row">
              <span class="info-row__label">数据库</span>
              <span class="info-row__value">
                <span class="status-dot status-dot--online"></span>
                连接正常
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Location, Lightning, User, Document, Warning, Bell, Calendar, ArrowRight } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'

const stats = ref({})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const d = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekDays[d.getDay()]}`
})

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: 20px;
  min-height: calc(100vh - 64px);
}

/* Welcome Banner */
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px;
  background: linear-gradient(135deg, var(--color-primary-soft) 0%, rgba(43, 224, 140, 0.08) 100%);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

.welcome-title {
  margin: 0 0 4px 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.welcome-desc {
  margin: 0;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.welcome-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8125rem;
  color: var(--color-text-muted);
  white-space: nowrap;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .stats-grid { grid-template-columns: 1fr; }
}

.stat-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--motion-base) var(--easing-standard);
  overflow: hidden;
}

.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.stat-card--info::before { background: var(--color-info); }
.stat-card--success::before { background: var(--color-success); }
.stat-card--warning::before { background: var(--color-warning); }
.stat-card--danger::before { background: var(--color-danger); }

.stat-card:hover {
  border-color: var(--color-border-strong);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card__icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
}

.stat-card--info .stat-card__icon {
  background: var(--color-info-soft);
  color: var(--color-info);
}
.stat-card--success .stat-card__icon {
  background: var(--color-success-soft);
  color: var(--color-success);
}
.stat-card--warning .stat-card__icon {
  background: var(--color-warning-soft);
  color: var(--color-warning);
}
.stat-card--danger .stat-card__icon {
  background: var(--color-danger-soft);
  color: var(--color-danger);
}

.stat-card__body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-card__label {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
}

.stat-card__value {
  font-family: var(--font-mono);
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--color-text-primary);
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.stat-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

.stat-card:hover .stat-card__footer {
  color: var(--color-text-secondary);
}

/* Content Grid */
.content-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
}

@media (max-width: 960px) {
  .content-grid { grid-template-columns: 1fr; }
}

/* Panels */
.panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.panel__header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
}

.panel__title {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.panel__body {
  padding: 20px;
}

/* Quick Actions */
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

@media (max-width: 640px) {
  .action-grid { grid-template-columns: repeat(2, 1fr); }
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--motion-base) var(--easing-standard);
}

.action-item:hover {
  background: var(--color-elevated);
}

.action-item__icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  transition: transform var(--motion-base) var(--easing-standard);
}

.action-item:hover .action-item__icon {
  transform: scale(1.1);
}

.action-item__icon--blue { background: var(--color-primary-soft); color: var(--color-primary); }
.action-item__icon--green { background: var(--color-success-soft); color: var(--color-success); }
.action-item__icon--orange { background: var(--color-warning-soft); color: var(--color-warning); }
.action-item__icon--purple { background: rgba(162, 120, 255, 0.12); color: #a278ff; }
.action-item__icon--red { background: var(--color-danger-soft); color: var(--color-danger); }
.action-item__icon--cyan { background: var(--color-info-soft); color: var(--color-info); }

.action-item__text {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.action-item:hover .action-item__text {
  color: var(--color-text-primary);
}

/* System Info */
.info-list {
  display: flex;
  flex-direction: column;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
}

.info-row:not(:last-child) {
  border-bottom: 1px solid var(--color-border);
}

.info-row__label {
  font-size: 0.8125rem;
  color: var(--color-text-muted);
}

.info-row__value {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot--online {
  background: var(--color-success);
  box-shadow: 0 0 8px var(--color-success);
}
</style>
