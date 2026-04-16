<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="container header-inner">
        <!-- Logo -->
        <RouterLink to="/" class="logo">
          <span class="logo-icon">🏫</span>
          <span class="logo-text">校园二手</span>
        </RouterLink>

        <!-- 搜索框 -->
        <div class="search-box">
          <n-input
            v-model:value="keyword"
            placeholder="搜索二手好物..."
            round
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <n-icon :component="SearchIcon" />
            </template>
          </n-input>
        </div>

        <!-- 右侧操作 -->
        <div class="header-actions">
          <RouterLink to="/publish" class="btn-publish">
            <n-button type="primary" round size="small">
              <template #icon><n-icon :component="AddIcon" /></template>
              发布闲置
            </n-button>
          </RouterLink>

          <!-- 已登录 -->
          <template v-if="userStore.isLoggedIn">
            <n-dropdown :options="userMenuOptions" @select="handleUserMenu">
              <div class="user-avatar">
                <n-avatar
                  :src="userStore.user?.avatar"
                  round
                  :size="32"
                  :fallback-src="defaultAvatar"
                />
                <span class="user-name">{{ userStore.user?.nickname }}</span>
              </div>
            </n-dropdown>
          </template>

          <!-- 未登录 -->
          <template v-else>
            <RouterLink to="/login">
              <n-button text>登录</n-button>
            </RouterLink>
            <RouterLink to="/register">
              <n-button type="primary" text>注册</n-button>
            </RouterLink>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="app-main">
      <RouterView />
    </main>

    <!-- 底部 -->
    <footer class="app-footer">
      <div class="container">
        <p>© 2024 校园二手交易平台 · 让闲置发挥价值</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { NInput, NIcon, NButton, NDropdown, NAvatar } from 'naive-ui'
import { Search as SearchIcon, Add as AddIcon, Person, Heart, LogOut } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/modules/auth'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0OCA0OCI+PGNpcmNsZSBjeD0iMjQiIGN5PSIyNCIgcj0iMjQiIGZpbGw9IiMwQ0JGQjUiLz48cGF0aCBkPSJNMjQgMjZjLTUgMC05LTQtOS05czQtOSA5LTkgOSA0IDkgOS00IDkgLTkgOXoiIGZpbGw9IndoaXRlIi8+PHBhdGggZD0iTTggNDJjMC04IDctMTQgMTYtMTRzMTYgNiAxNiAxNC04IDYtMTYgNi0xNiAwLTE2LTZ6IiBmaWxsPSJ3aGl0ZSIvPjwvc3ZnPg=='

const userMenuOptions = [
  {
    label: '个人资料',
    key: 'my-profile',
    icon: () => h(NIcon, null, { default: () => h(Person) }),
  },
  { type: 'divider', key: 'd1' },
  {
    label: '我的发布',
    key: 'my-products',
    icon: () => h(NIcon, null, { default: () => h(Person) }),
  },
  {
    label: '我的收藏',
    key: 'my-favorites',
    icon: () => h(NIcon, null, { default: () => h(Heart) }),
  },
  { type: 'divider', key: 'd2' },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h(NIcon, null, { default: () => h(LogOut) }),
  },
]

function handleSearch() {
  if (keyword.value.trim()) {
    router.push({ name: 'market', query: { keyword: keyword.value.trim() } })
  }
}

async function handleUserMenu(key: string) {
  if (key === 'logout') {
    await authApi.logout()
    userStore.logout()
    router.push('/')
  } else {
    router.push({ name: key })
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  height: 60px;
}

.header-inner {
  height: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon { font-size: 24px; }
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
  white-space: nowrap;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.btn-publish { text-decoration: none; }

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}
.user-avatar:hover { background: var(--color-primary-light); }
.user-name {
  font-size: 14px;
  color: var(--color-text);
  max-width: 80px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.app-main {
  flex: 1;
  background: var(--color-bg);
}

.app-footer {
  background: #fff;
  border-top: 1px solid var(--color-border);
  padding: 16px 0;
  text-align: center;
  color: var(--color-text-secondary);
  font-size: 12px;
}
</style>
