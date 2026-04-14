<template>
  <div class="my-layout">
    <div class="container page-padding">
      <div class="my-inner">
        <!-- 左侧菜单 -->
        <aside class="my-sidebar">
          <div class="user-card">
            <n-avatar :src="userStore.user?.avatar" round :size="60" />
            <div class="user-info">
              <div class="user-name">{{ userStore.user?.nickname }}</div>
              <div class="user-school">{{ userStore.user?.schoolName || '未设置学校' }}</div>
            </div>
          </div>
          <n-menu
            :options="menuOptions"
            :value="activeKey"
            @update:value="handleMenuSelect"
          />
        </aside>

        <!-- 右侧内容 -->
        <main class="my-content">
          <RouterView />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NMenu, NAvatar } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeKey = computed(() => route.name as string)

const menuOptions = [
  { label: '我的发布', key: 'my-products' },
  { label: '我的收藏', key: 'my-favorites' },
  { label: '个人资料', key: 'my-profile' },
]

function handleMenuSelect(key: string) {
  router.push({ name: key })
}
</script>

<style scoped>
.my-layout { background: var(--color-bg); min-height: calc(100vh - 60px); }
.my-inner { display: flex; gap: 20px; align-items: flex-start; }
.my-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 80px;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--color-primary-light);
  border-radius: var(--radius-sm);
  margin-bottom: 16px;
}
.user-name { font-size: 15px; font-weight: 600; color: var(--color-text); }
.user-school { font-size: 12px; color: var(--color-text-secondary); margin-top: 2px; }
.my-content { flex: 1; min-width: 0; }
</style>
