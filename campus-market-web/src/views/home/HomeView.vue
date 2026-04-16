<template>
  <div class="home">
    <!-- Hero Banner -->
    <section class="hero hero-gradient">
      <div class="container hero-content">
        <div class="hero-text">
          <h1 class="hero-title">校园二手交易</h1>
          <p class="hero-subtitle">买卖闲置好物，从校园开始</p>
          <div class="hero-search">
            <n-input
              v-model:value="keyword"
              placeholder="搜索你想要的闲置..."
              round
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #suffix>
                <n-button type="primary" round @click="handleSearch">搜索</n-button>
              </template>
            </n-input>
          </div>
          <div class="hero-stats">
            <span>📦 千余件闲置等你发现</span>
            <span>🎓 专属校园交易平台</span>
            <span>✅ 安全放心</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 公告 -->
    <section v-if="notices.length" class="notice-bar">
      <div class="container">
        <div
          v-for="n in notices"
          :key="n.id"
          class="notice-item"
          @click="router.push(`/notice/${n.id}`)"
        >
          📢 {{ n.title }}
        </div>
      </div>
    </section>

    <!-- 分类入口 -->
    <section class="categories-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">商品分类</h2>
          <RouterLink to="/market" class="section-more">查看全部 →</RouterLink>
        </div>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            @click="goCategory(cat.id)"
          >
            <span class="cat-icon">{{ cat.icon }}</span>
            <span class="cat-name">{{ cat.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新商品 -->
    <section class="products-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">最新发布</h2>
          <RouterLink to="/market" class="section-more">查看更多 →</RouterLink>
        </div>

        <div v-if="loading" class="loading-grid">
          <n-skeleton v-for="i in 8" :key="i" height="280px" :sharp="false" />
        </div>

        <div v-else class="product-grid">
          <ProductCard
            v-for="product in products"
            :key="product.id"
            :product="product"
            @click="router.push(`/market/${product.id}`)"
          />
        </div>

        <div v-if="!loading && products.length === 0" class="empty-state">
          <n-empty description="暂无商品，快来发布第一件闲置吧！" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NInput, NButton, NAlert, NSkeleton, NEmpty } from 'naive-ui'
import { productApi } from '@/api/modules/product'
import { schoolApi } from '@/api/modules/school'
import type { ProductCategory, ProductListVO, Notice } from '@/types'
import ProductCard from '@/components/common/ProductCard.vue'

// 假设分类数据通过接口获取
const router = useRouter()
const keyword = ref('')
const categories = ref<ProductCategory[]>([])
const products = ref<ProductListVO[]>([])
const notices = ref<Notice[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    // 获取分类（直接从后端）
    const [catRes, productRes, noticeRes] = await Promise.all([
      fetch('/api/categories').then(r => r.json()),
      productApi.list({ pageNum: 1, pageSize: 8, sortBy: 'newest' }),
      fetch('/api/notices?pageNum=1&pageSize=5').then(r => r.json()),
    ])
    categories.value = catRes.data?.slice(0, 10) || []
    products.value = productRes.data?.list || []
    notices.value = noticeRes.data?.list || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  if (keyword.value.trim()) {
    router.push({ name: 'market', query: { keyword: keyword.value.trim() } })
  }
}

function goCategory(id: number) {
  router.push({ name: 'market', query: { categoryId: id } })
}

onMounted(loadData)
</script>

<style scoped>
.hero {
  padding: 48px 0;
  color: #fff;
}
.hero-content {
  display: flex;
  align-items: center;
  gap: 40px;
}
.hero-title {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 8px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
}
.hero-search {
  max-width: 480px;
  margin-bottom: 20px;
}
.hero-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  opacity: 0.85;
}

.notice-bar {
  padding: 12px 0;
}
.notice-item {
  padding: 8px 12px;
  background: #e8f4fd;
  border-left: 3px solid var(--color-primary);
  border-radius: 4px;
  font-size: 14px;
  color: var(--color-text);
  cursor: pointer;
  transition: background 0.15s;
  margin-bottom: 6px;
}
.notice-item:last-child { margin-bottom: 0; }
.notice-item:hover { background: #d0eaf8; }

.categories-section,
.products-section {
  padding: 28px 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
}
.section-more {
  font-size: 13px;
  color: var(--color-primary);
  text-decoration: none;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 12px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  background: #fff;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}
.category-item:hover {
  background: var(--color-primary-light);
  transform: translateY(-2px);
}
.cat-icon { font-size: 24px; }
.cat-name { font-size: 12px; color: var(--color-text); }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.loading-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .category-grid { grid-template-columns: repeat(5, 1fr); }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .hero-title { font-size: 24px; }
  .hero-stats { flex-wrap: wrap; gap: 10px; }
}
</style>
