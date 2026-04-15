<template>
  <div class="market-page">
    <div class="container page-padding">
      <div class="market-layout">
        <!-- 左侧筛选 -->
        <aside class="filter-panel">
          <div class="filter-section">
            <div class="filter-title">商品分类</div>
            <div class="filter-options">
              <div
                class="filter-option"
                :class="{ active: !query.categoryId }"
                @click="query.categoryId = undefined"
              >全部分类</div>
              <div
                v-for="cat in categories"
                :key="cat.id"
                class="filter-option"
                :class="{ active: query.categoryId === cat.id }"
                @click="query.categoryId = cat.id"
              >
                {{ cat.icon }} {{ cat.name }}
              </div>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">成色</div>
            <div class="filter-options">
              <div
                class="filter-option"
                :class="{ active: !query.conditionLevel }"
                @click="query.conditionLevel = undefined"
              >不限</div>
              <div
                v-for="(label, idx) in conditionLabels"
                :key="idx"
                class="filter-option"
                :class="{ active: query.conditionLevel === idx + 1 }"
                @click="query.conditionLevel = idx + 1"
              >{{ label }}</div>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">交易方式</div>
            <div class="filter-options">
              <div class="filter-option" :class="{ active: !query.tradeType }" @click="query.tradeType = undefined">不限</div>
              <div class="filter-option" :class="{ active: query.tradeType === 1 }" @click="query.tradeType = 1">线下交易</div>
              <div class="filter-option" :class="{ active: query.tradeType === 2 }" @click="query.tradeType = 2">快递邮寄</div>
              <div class="filter-option" :class="{ active: query.tradeType === 3 }" @click="query.tradeType = 3">均可</div>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">价格区间</div>
            <div class="price-range">
              <n-input-number v-model:value="query.minPrice" placeholder="最低" :min="0" size="small" />
              <span>-</span>
              <n-input-number v-model:value="query.maxPrice" placeholder="最高" :min="0" size="small" />
            </div>
            <n-button size="small" block style="margin-top: 8px" @click="search">确认</n-button>
          </div>

          <n-button block @click="resetFilter">重置筛选</n-button>
        </aside>

        <!-- 右侧内容 -->
        <div class="market-content">
          <!-- 搜索条 -->
          <div class="search-bar">
            <n-input
              v-model:value="query.keyword"
              placeholder="搜索商品..."
              clearable
              @keyup.enter="search"
            >
              <template #suffix>
                <n-button type="primary" text @click="search">搜索</n-button>
              </template>
            </n-input>
            <div class="sort-options">
              <span
                v-for="opt in sortOptions"
                :key="opt.value"
                class="sort-opt"
                :class="{ active: query.sortBy === opt.value }"
                @click="query.sortBy = opt.value as any"
              >{{ opt.label }}</span>
            </div>
          </div>

          <!-- 商品数量 -->
          <div class="result-info">共找到 <strong>{{ total }}</strong> 件商品</div>

          <!-- 商品网格 -->
          <div v-if="loading" class="product-grid">
            <n-skeleton v-for="i in 12" :key="i" height="300px" :sharp="false" />
          </div>
          <div v-else-if="products.length" class="product-grid">
            <ProductCard
              v-for="p in products"
              :key="p.id"
              :product="p"
              @click="$router.push(`/market/${p.id}`)"
            />
          </div>
          <n-empty v-else description="暂无符合条件的商品" style="padding: 60px 0" />

          <!-- 分页 -->
          <div v-if="total > query.pageSize!" class="pagination">
            <n-pagination
              v-model:page="query.pageNum"
              :page-count="pages"
              :page-size="query.pageSize"
              show-quick-jumper
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  NInput, NButton, NInputNumber, NSkeleton, NEmpty, NPagination
} from 'naive-ui'
import { productApi } from '@/api/modules/product'
import type { ProductCategory, ProductListVO, ProductQueryDTO } from '@/types'
import { CONDITION_LABELS } from '@/types'
import ProductCard from '@/components/common/ProductCard.vue'

const route = useRoute()

const categories = ref<ProductCategory[]>([])
const products = ref<ProductListVO[]>([])
const loading = ref(false)
const total = ref(0)
const pages = ref(0)

const conditionLabels = CONDITION_LABELS.slice(1) // 去掉空字符串

const sortOptions = [
  { label: '最新', value: 'newest' },
  { label: '价格↑', value: 'price_asc' },
  { label: '价格↓', value: 'price_desc' },
]

const query = reactive<ProductQueryDTO>({
  pageNum: 1,
  pageSize: 12,
  keyword: route.query.keyword as string || undefined,
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
  sortBy: 'newest',
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await productApi.list(query)
    products.value = res.data.list
    total.value = res.data.total
    pages.value = res.data.pages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function search() {
  query.pageNum = 1
  loadProducts()
}

function resetFilter() {
  query.categoryId = undefined
  query.conditionLevel = undefined
  query.tradeType = undefined
  query.minPrice = undefined
  query.maxPrice = undefined
  query.sortBy = 'newest'
  query.pageNum = 1
  loadProducts()
}

// 监听筛选条件变化自动搜索
watch(
  () => [query.categoryId, query.conditionLevel, query.tradeType, query.sortBy, query.pageNum],
  () => loadProducts()
)

onMounted(async () => {
  // 获取分类列表
  const res = await fetch('/api/categories').then(r => r.json())
  categories.value = res.data || []
  loadProducts()
})
</script>

<style scoped>
.market-page { background: var(--color-bg); min-height: calc(100vh - 60px); }

.market-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.filter-panel {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 80px;
}
.filter-section { margin-bottom: 20px; }
.filter-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--color-border);
}
.filter-options { display: flex; flex-direction: column; gap: 4px; }
.filter-option {
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  color: var(--color-text-secondary);
  transition: all 0.15s;
}
.filter-option:hover { background: var(--color-primary-light); color: var(--color-primary); }
.filter-option.active { background: var(--color-primary-light); color: var(--color-primary); font-weight: 600; }

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.market-content { flex: 1; min-width: 0; }

.search-bar {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: 16px;
}
.sort-options { display: flex; gap: 8px; flex-shrink: 0; }
.sort-opt {
  font-size: 13px;
  cursor: pointer;
  color: var(--color-text-secondary);
  padding: 4px 10px;
  border-radius: 4px;
}
.sort-opt:hover, .sort-opt.active { color: var(--color-primary); background: var(--color-primary-light); }

.result-info {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 12px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}
</style>
