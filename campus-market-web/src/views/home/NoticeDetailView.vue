<template>
  <div class="notice-detail-page">
    <div class="container page-padding">
      <div v-if="loading" class="loading-wrap">
        <n-spin size="large" />
      </div>
      <div v-else-if="notice" class="notice-card">
        <n-button text @click="router.back()" style="margin-bottom: 16px">
          ← 返回
        </n-button>
        <h1 class="notice-title">{{ notice.title }}</h1>
        <div class="notice-meta">
          <span>{{ formatDate(notice.createdAt) }}</span>
          <span>👁 {{ notice.viewCount }} 次浏览</span>
        </div>
        <img v-if="notice.cover" :src="notice.cover" :alt="notice.title" class="notice-cover" />
        <div class="notice-content">{{ notice.content }}</div>
      </div>
      <n-empty v-else description="公告不存在或已删除" style="padding: 80px 0" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NSpin, NButton, NEmpty } from 'naive-ui'
import request from '@/api/request'
import type { Notice } from '@/types'

const route = useRoute()
const router = useRouter()

const notice = ref<Notice | null>(null)
const loading = ref(false)

async function loadNotice() {
  loading.value = true
  try {
    const res = await request.get<any, { data: Notice }>(`/notices/${route.params.id}`)
    notice.value = res.data
  } catch (e) {
    notice.value = null
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

onMounted(loadNotice)
</script>

<style scoped>
.notice-detail-page { background: var(--color-bg); min-height: calc(100vh - 60px); }
.loading-wrap { display: flex; justify-content: center; align-items: center; min-height: 300px; }

.notice-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 32px 40px;
  box-shadow: var(--shadow-sm);
  max-width: 800px;
  margin: 0 auto;
}

.notice-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 12px;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.notice-cover {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  margin-bottom: 20px;
}

.notice-content {
  font-size: 15px;
  line-height: 1.9;
  color: var(--color-text);
  white-space: pre-wrap;
}
</style>
