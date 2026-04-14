<template>
  <div class="dashboard">
    <h2 class="page-title">数据看板</h2>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.key" class="stat-card" :style="{ borderColor: stat.color }">
        <div class="stat-icon" :style="{ background: stat.bg }">{{ stat.icon }}</div>
        <div class="stat-info">
          <div class="stat-value">{{ statData[stat.key] ?? '-' }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div class="action-btns">
        <n-button type="primary" @click="$router.push('/products')">去审核商品</n-button>
        <n-button type="warning" @click="$router.push('/reports')">处理举报</n-button>
        <n-button @click="$router.push('/notices')">发布公告</n-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NButton } from 'naive-ui'
import { dashboardApi } from '@/api'

const statData = ref<Record<string, number>>({})

const stats = [
  { key: 'totalUsers', label: '总用户数', icon: '👥', color: '#0CBFB5', bg: '#E6F9F8' },
  { key: 'totalProducts', label: '总商品数', icon: '📦', color: '#3B82F6', bg: '#EFF6FF' },
  { key: 'onShelfProducts', label: '在售商品', icon: '✅', color: '#10B981', bg: '#ECFDF5' },
  { key: 'pendingAuditProducts', label: '待审核商品', icon: '🕐', color: '#F59E0B', bg: '#FFFBEB' },
  { key: 'pendingReports', label: '待处理举报', icon: '⚠️', color: '#EF4444', bg: '#FEF2F2' },
]

onMounted(async () => {
  try {
    const res: any = await dashboardApi.getStats()
    statData.value = res.data
  } catch (e) {}
})
</script>

<style scoped>
.dashboard { }
.page-title { font-size: 20px; font-weight: 700; margin-bottom: 24px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border-left: 4px solid;
}
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-value { font-size: 28px; font-weight: 800; color: #2D2D2D; }
.stat-label { font-size: 13px; color: #888; margin-top: 2px; }

.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.quick-actions h3 { font-size: 15px; font-weight: 600; margin-bottom: 14px; }
.action-btns { display: flex; gap: 12px; }
</style>
