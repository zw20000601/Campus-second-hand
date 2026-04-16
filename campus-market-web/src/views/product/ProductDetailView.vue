<template>
  <div class="detail-page">
    <div v-if="loading" class="loading-wrap">
      <n-spin size="large" />
    </div>
    <div v-else-if="product" class="container page-padding">
      <!-- 面包屑 -->
      <n-breadcrumb style="margin-bottom: 16px;">
        <n-breadcrumb-item @click="$router.push('/')">首页</n-breadcrumb-item>
        <n-breadcrumb-item @click="$router.push('/market')">商品列表</n-breadcrumb-item>
        <n-breadcrumb-item>商品详情</n-breadcrumb-item>
      </n-breadcrumb>

      <div class="detail-layout">
        <!-- 左侧图片 -->
        <div class="image-section">
          <div class="main-image-wrap">
            <img :src="currentImage || product.coverImage" :alt="product.title" class="main-image" />
            <div v-if="product.status === 3" class="sold-overlay">已售出</div>
          </div>
          <div v-if="product.images?.length > 1" class="image-thumbs">
            <img
              v-for="(img, idx) in product.images"
              :key="idx"
              :src="img"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
              class="thumb"
            />
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="info-section">
          <!-- 标题 + 状态 -->
          <div class="product-title">{{ product.title }}</div>

          <!-- 价格 -->
          <div class="price-block">
            <span class="price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">原价 ¥{{ product.originalPrice }}</span>
          </div>

          <!-- 商品属性 -->
          <div class="attr-list">
            <div class="attr-item">
              <span class="attr-label">成色</span>
              <n-tag type="info" size="small">{{ product.conditionDesc }}</n-tag>
            </div>
            <div class="attr-item">
              <span class="attr-label">交易方式</span>
              <span class="attr-value">{{ product.tradeTypeDesc }}</span>
            </div>
            <div v-if="product.tradeLocation" class="attr-item">
              <span class="attr-label">交易地点</span>
              <span class="attr-value">{{ product.tradeLocation }}</span>
            </div>
            <div class="attr-item">
              <span class="attr-label">分类</span>
              <span class="attr-value">{{ product.categoryName }}</span>
            </div>
            <div class="attr-item">
              <span class="attr-label">学校</span>
              <span class="attr-value">{{ product.schoolName }} {{ product.campusName }}</span>
            </div>
            <div class="attr-item">
              <span class="attr-label">发布时间</span>
              <span class="attr-value">{{ formatDate(product.createdAt) }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <n-button
              :type="product.favorited ? 'default' : 'error'"
              :ghost="!product.favorited"
              size="large"
              style="flex: 1"
              @click="toggleFavorite"
            >
              {{ product.favorited ? '❤️ 已收藏' : '🤍 收藏' }}
            </n-button>
            <n-button
              type="primary"
              size="large"
              style="flex: 2"
              :disabled="product.status !== 1"
              @click="handleContact"
            >
              联系卖家
            </n-button>
          </div>

          <div v-if="product.status !== 1" class="status-tip">
            <n-alert :type="product.status === 3 ? 'warning' : 'default'" size="small">
              {{ product.status === 3 ? '该商品已售出' : '该商品暂不可交易' }}
            </n-alert>
          </div>

          <!-- 举报 -->
          <div class="report-link" @click="showReportModal = true">
            <n-button text size="small" type="warning">⚠️ 举报此商品</n-button>
          </div>
        </div>
      </div>

      <!-- 卖家信息 -->
      <div class="seller-card">
        <div class="seller-info">
          <n-avatar :src="product.userAvatar" round :size="48" />
          <div class="seller-detail">
            <div class="seller-name">{{ product.userNickname }}</div>
            <div class="seller-sub">{{ product.schoolName }}</div>
          </div>
        </div>
      </div>

      <!-- 商品描述 -->
      <div v-if="product.description" class="desc-section">
        <h3 class="section-title">商品描述</h3>
        <p class="desc-text">{{ product.description }}</p>
      </div>

      <!-- 留言区 -->
      <div class="message-section">
        <h3 class="section-title">商品留言 ({{ product.messageCount }})</h3>

        <!-- 发留言 -->
        <div v-if="userStore.isLoggedIn" class="message-input">
          <n-avatar :src="userStore.user?.avatar" round :size="36" />
          <div class="input-wrap">
            <n-input
              v-model:value="newMessage"
              type="textarea"
              :rows="2"
              placeholder="留下你的疑问..."
              :maxlength="200"
            />
            <n-button type="primary" size="small" style="margin-top: 8px" @click="submitMessage">
              发送留言
            </n-button>
          </div>
        </div>
        <n-alert v-else type="info" size="small" style="margin-bottom: 16px;">
          <RouterLink to="/login" class="link">登录</RouterLink>后可以发表留言
        </n-alert>

        <!-- 留言列表 -->
        <div v-for="msg in messages" :key="msg.id" class="message-item">
          <n-avatar :src="msg.userAvatar" round :size="36" />
          <div class="msg-content">
            <div class="msg-header">
              <span class="msg-user">{{ msg.userNickname }}</span>
              <span class="msg-time">{{ formatDate(msg.createdAt) }}</span>
              <n-button v-if="userStore.isLoggedIn" text size="tiny" style="margin-left: auto" @click="openReply(msg)">
                {{ replyTo?.id === msg.id ? '收起' : '回复' }}
              </n-button>
            </div>
            <div class="msg-text">{{ msg.content }}</div>

            <!-- 回复列表 -->
            <div v-if="msg.replies?.length" class="reply-list">
              <div v-for="reply in msg.replies" :key="reply.id" class="reply-item">
                <n-avatar :src="reply.userAvatar" round :size="28" />
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="reply-user">{{ reply.userNickname }}</span>
                    <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                  <div class="reply-text">{{ reply.content }}</div>
                </div>
              </div>
            </div>

            <!-- 行内回复输入框 -->
            <div v-if="replyTo?.id === msg.id" class="reply-input-wrap">
              <span class="reply-to-label">回复 @{{ replyTo.nickname }}：</span>
              <n-input
                v-model:value="replyContent"
                type="textarea"
                :rows="2"
                placeholder="写下你的回复..."
                :maxlength="200"
              />
              <div class="reply-input-actions">
                <n-button size="small" @click="replyTo = null; replyContent = ''">取消</n-button>
                <n-button type="primary" size="small" @click="submitReply(msg.id)">发送</n-button>
              </div>
            </div>
          </div>
        </div>

        <n-empty v-if="messages.length === 0 && !loadingMessages" description="暂无留言，快来抢沙发！" />
      </div>
    </div>

    <!-- 举报弹窗 -->
    <n-modal v-model:show="showReportModal" title="举报商品" preset="card" style="max-width: 400px">
      <n-form ref="reportFormRef" :model="reportForm" label-placement="top">
        <n-form-item label="举报原因" required>
          <n-select v-model:value="reportForm.reason" :options="reportReasons" />
        </n-form-item>
        <n-form-item label="补充说明">
          <n-input v-model:value="reportForm.description" type="textarea" :rows="3" :maxlength="200" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" @click="submitReport">提交举报</n-button>
        <n-button @click="showReportModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NSpin, NBreadcrumb, NBreadcrumbItem, NTag, NButton, NAlert,
  NAvatar, NInput, NModal, NForm, NFormItem, NSelect, NEmpty, useMessage
} from 'naive-ui'
import request from '@/api/request'
import { productApi } from '@/api/modules/product'
import { favoriteApi } from '@/api/modules/favorite'
import { messageApi } from '@/api/modules/message'
import { useUserStore } from '@/stores/user'
import type { MessageVO, ProductVO } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const product = ref<ProductVO | null>(null)
const loading = ref(false)
const currentImage = ref('')
const messages = ref<MessageVO[]>([])
const loadingMessages = ref(false)
const newMessage = ref('')
const replyTo = ref<{ id: number; nickname: string } | null>(null)
const replyContent = ref('')
const showReportModal = ref(false)
const reportForm = ref({ reason: null as number | null, description: '' })
const reportReasons = [
  { label: '虚假信息', value: 1 },
  { label: '商品违规', value: 2 },
  { label: '价格欺诈', value: 3 },
  { label: '重复发布', value: 4 },
  { label: '其他', value: 5 },
]

async function loadProduct() {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await productApi.detail(id)
    product.value = res.data
    currentImage.value = res.data.coverImage
  } catch (e: any) {
    message.error(e.message || '商品不存在')
    router.push('/market')
  } finally {
    loading.value = false
  }
}

async function loadMessages() {
  if (!product.value) return
  loadingMessages.value = true
  try {
    const res = await messageApi.list(product.value.id, { pageNum: 1, pageSize: 20 })
    messages.value = res.data.list
  } finally {
    loadingMessages.value = false
  }
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) { router.push('/login'); return }
  if (!product.value) return
  try {
    if (product.value.favorited) {
      await favoriteApi.remove(product.value.id)
      product.value.favorited = false
      product.value.favoriteCount--
    } else {
      await favoriteApi.add(product.value.id)
      product.value.favorited = true
      product.value.favoriteCount++
    }
  } catch (e: any) {
    message.error(e.message)
  }
}

async function submitMessage() {
  if (!newMessage.value.trim()) return
  try {
    await messageApi.publish(product.value!.id, newMessage.value)
    newMessage.value = ''
    loadMessages()
    if (product.value) product.value.messageCount++
  } catch (e: any) {
    message.error(e.message)
  }
}

function openReply(msg: MessageVO) {
  if (replyTo.value?.id === msg.id) {
    replyTo.value = null
    replyContent.value = ''
  } else {
    replyTo.value = { id: msg.id, nickname: msg.userNickname }
    replyContent.value = ''
  }
}

async function submitReply(parentId: number) {
  if (!replyContent.value.trim()) return
  try {
    await messageApi.publish(product.value!.id, replyContent.value, parentId)
    replyTo.value = null
    replyContent.value = ''
    loadMessages()
    if (product.value) product.value.messageCount++
  } catch (e: any) {
    message.error(e.message)
  }
}

async function submitReport() {
  if (!reportForm.value.reason) { message.warning('请选择举报原因'); return }
  try {
    await request.post('/reports', {
      productId: product.value?.id,
      reason: reportForm.value.reason,
      description: reportForm.value.description,
    })
    message.success('举报已提交，我们会尽快处理')
    showReportModal.value = false
  } catch (e: any) {
    message.error(e.message)
  }
}

function handleContact() {
  message.info('联系方式：请在留言区留下您的联系方式（第一版暂不支持站内私信）')
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

onMounted(async () => {
  await loadProduct()
  await loadMessages()
})
</script>

<style scoped>
.detail-page { background: var(--color-bg); min-height: calc(100vh - 60px); }
.loading-wrap { display: flex; justify-content: center; align-items: center; min-height: 400px; }

.detail-layout {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 28px;
  margin-bottom: 24px;
}

.image-section {}
.main-image-wrap { position: relative; border-radius: var(--radius-md); overflow: hidden; }
.main-image { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; }
.sold-overlay {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; font-weight: 700; color: #fff;
}
.image-thumbs { display: flex; gap: 8px; margin-top: 8px; flex-wrap: wrap; }
.thumb { width: 64px; height: 64px; object-fit: cover; border-radius: 6px; cursor: pointer; border: 2px solid transparent; }
.thumb.active { border-color: var(--color-primary); }

.info-section {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}
.product-title { font-size: 20px; font-weight: 700; color: var(--color-text); margin-bottom: 16px; line-height: 1.4; }
.price-block { margin-bottom: 20px; }
.price { font-size: 32px; font-weight: 800; color: #FF4B6E; }
.original-price { font-size: 14px; color: #BBBBBB; text-decoration: line-through; margin-left: 8px; }

.attr-list { margin-bottom: 20px; }
.attr-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 14px;
}
.attr-label { width: 70px; flex-shrink: 0; color: var(--color-text-secondary); }
.attr-value { color: var(--color-text); }

.actions { display: flex; gap: 12px; margin-bottom: 12px; }
.status-tip { margin-bottom: 12px; }
.report-link { text-align: right; }

.seller-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.seller-info { display: flex; align-items: center; gap: 12px; }
.seller-name { font-size: 15px; font-weight: 600; }
.seller-sub { font-size: 12px; color: var(--color-text-secondary); margin-top: 2px; }

.desc-section,
.message-section {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.section-title { font-size: 16px; font-weight: 700; margin-bottom: 14px; }
.desc-text { font-size: 14px; line-height: 1.8; color: var(--color-text); white-space: pre-wrap; }

.message-input { display: flex; gap: 12px; margin-bottom: 16px; }
.input-wrap { flex: 1; }

.message-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--color-border); }
.msg-content { flex: 1; min-width: 0; }
.msg-header { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.msg-user { font-size: 13px; font-weight: 600; }
.msg-time { font-size: 11px; color: var(--color-text-secondary); }
.msg-text { font-size: 14px; color: var(--color-text); line-height: 1.6; }

.reply-list { margin-top: 10px; border-left: 2px solid var(--color-border); padding-left: 12px; }
.reply-item { display: flex; gap: 8px; padding: 6px 0; border-bottom: 1px solid var(--color-border); }
.reply-item:last-child { border-bottom: none; }
.reply-content { flex: 1; min-width: 0; }
.reply-header { display: flex; align-items: center; gap: 8px; margin-bottom: 3px; }
.reply-user { font-size: 12px; font-weight: 600; }
.reply-time { font-size: 11px; color: var(--color-text-secondary); }
.reply-text { font-size: 13px; color: var(--color-text); line-height: 1.5; }

.reply-input-wrap { margin-top: 10px; background: var(--color-bg); border-radius: 6px; padding: 10px; }
.reply-to-label { display: block; font-size: 12px; color: var(--color-primary); margin-bottom: 6px; }
.reply-input-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 6px; }

.link { color: var(--color-primary); text-decoration: none; }
</style>
