<template>
  <n-layout has-sider style="min-height: 100vh;">
    <!-- 侧边栏 -->
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="220"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <!-- Logo -->
      <div class="sider-logo" @click="$router.push('/dashboard')">
        <span class="logo-icon">🏫</span>
        <span v-if="!collapsed" class="logo-text">校园二手管理</span>
      </div>

      <!-- 导航菜单 -->
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :options="menuOptions"
        :value="activeMenu"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <n-layout>
      <!-- 顶栏 -->
      <n-layout-header bordered class="header">
        <div class="header-inner">
          <div class="header-left">
            <n-breadcrumb>
              <n-breadcrumb-item>{{ currentPageTitle }}</n-breadcrumb-item>
            </n-breadcrumb>
          </div>
          <div class="header-right">
            <n-dropdown :options="userMenu" @select="handleUserMenu">
              <div class="admin-info">
                <n-avatar round :size="30">A</n-avatar>
                <span class="admin-name">{{ adminStore.adminInfo?.nickname || '管理员' }}</span>
              </div>
            </n-dropdown>
          </div>
        </div>
      </n-layout-header>

      <!-- 主内容区 -->
      <n-layout-content class="main-content">
        <RouterView />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  NLayout, NLayoutSider, NLayoutHeader, NLayoutContent,
  NMenu, NBreadcrumb, NBreadcrumbItem, NDropdown, NAvatar
} from 'naive-ui'
import { useAdminStore } from '@/stores/admin'
import { adminApi } from '@/api'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()
const collapsed = ref(false)

const menuOptions = [
  { label: '数据看板', key: 'dashboard', icon: () => '📊' },
  { label: '商品管理', key: 'products', icon: () => '📦' },
  { label: '用户管理', key: 'users', icon: () => '👥' },
  { label: '举报管理', key: 'reports', icon: () => '⚠️' },
  { label: '分类管理', key: 'categories', icon: () => '🏷️' },
  { label: '学校管理', key: 'schools', icon: () => '🏫' },
  { label: '公告管理', key: 'notices', icon: () => '📢' },
]

const titleMap: Record<string, string> = {
  dashboard: '数据看板',
  products: '商品管理',
  users: '用户管理',
  reports: '举报管理',
  categories: '分类管理',
  schools: '学校管理',
  notices: '公告管理',
}

const activeMenu = computed(() => route.name as string)
const currentPageTitle = computed(() => titleMap[route.name as string] || '管理后台')

const userMenu = [
  { label: '退出登录', key: 'logout' },
]

function handleMenuSelect(key: string) {
  router.push({ name: key })
}

async function handleUserMenu(key: string) {
  if (key === 'logout') {
    await adminApi.logout()
    adminStore.logout()
    router.push({ name: 'admin-login' })
  }
}
</script>

<style scoped>
.sider-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 12px;
}
.logo-icon { font-size: 20px; }
.logo-text { font-size: 14px; font-weight: 700; color: #0CBFB5; }

.header {
  height: 60px;
  padding: 0;
}
.header-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}
.admin-info:hover { background: #f5f5f5; }
.admin-name { font-size: 14px; }

.main-content {
  padding: 20px;
  background: #F5F6FA;
  min-height: calc(100vh - 60px);
}
</style>
