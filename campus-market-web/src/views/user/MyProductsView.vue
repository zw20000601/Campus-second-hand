<template>
  <div class="my-products">
    <div class="page-header">
      <h2>我的发布</h2>
      <RouterLink to="/publish">
        <n-button type="primary" size="small">+ 发布闲置</n-button>
      </RouterLink>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <n-select
        v-model:value="statusFilter"
        :options="statusOptions"
        placeholder="全部状态"
        clearable
        size="small"
        style="width: 140px"
        @update:value="loadProducts"
      />
    </div>

    <div v-if="loading" class="loading-wrap">
      <n-spin />
    </div>
    <div v-else-if="products.length" class="product-list">
      <div v-for="p in products" :key="p.id" class="product-row">
        <img :src="p.coverImage" :alt="p.title" class="product-img" @click="$router.push(`/market/${p.id}`)" />
        <div class="product-info">
          <div class="product-title" @click="$router.push(`/market/${p.id}`)">{{ p.title }}</div>
          <div class="product-price">¥{{ p.price }}</div>
          <div class="product-meta">
            <n-tag :type="statusTagType(p.status)" size="small">{{ statusLabel(p.status) }}</n-tag>
            <span class="meta-item">👁 {{ p.viewCount }} 浏览</span>
            <span class="meta-item">{{ p.createdAt }}</span>
          </div>
        </div>
        <div class="product-actions">
          <n-button v-if="p.status === 1" size="small" @click="handleOffShelf(p.id)">下架</n-button>
          <n-button v-if="p.status === 1" size="small" type="success" @click="handleSold(p.id)">标记售出</n-button>
          <n-button size="small" type="error" ghost @click="handleDelete(p.id)">删除</n-button>
        </div>
      </div>
    </div>
    <n-empty v-else description="还没有发布过商品" style="padding: 60px 0" />

    <div v-if="total > pageSize" class="pagination">
      <n-pagination v-model:page="pageNum" :page-count="pages" :page-size="pageSize" @update:page="loadProducts" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NSpin, NTag, NButton, NSelect, NEmpty, NPagination, useMessage, useDialog } from 'naive-ui'
import { productApi } from '@/api/modules/product'
import type { ProductListVO } from '@/types'

const message = useMessage()
const dialog = useDialog()

const products = ref<ProductListVO[]>([])
const loading = ref(false)
const statusFilter = ref<number | null>(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const pages = ref(0)

const statusOptions = [
  { label: '待审核', value: 0 },
  { label: '已上架', value: 1 },
  { label: '已下架', value: 2 },
  { label: '已售出', value: 3 },
  { label: '审核拒绝', value: 4 },
]

function statusLabel(status: number) {
  return statusOptions.find(o => o.value === status)?.label || '未知'
}
function statusTagType(status: number) {
  const map: Record<number, any> = { 0: 'warning', 1: 'success', 2: 'default', 3: 'info', 4: 'error' }
  return map[status] || 'default'
}

async function loadProducts() {
  loading.value = true
  try {
    const res = await productApi.myProducts({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value ?? undefined,
    })
    products.value = res.data.list
    total.value = res.data.total
    pages.value = res.data.pages
  } finally {
    loading.value = false
  }
}

async function handleOffShelf(id: number) {
  await productApi.offShelf(id)
  message.success('已下架')
  loadProducts()
}

async function handleSold(id: number) {
  await productApi.markSold(id)
  message.success('已标记为售出')
  loadProducts()
}

function handleDelete(id: number) {
  dialog.warning({
    title: '确认删除',
    content: '删除后不可恢复，确认删除该商品吗？',
    positiveText: '确认删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await productApi.delete(id)
      message.success('已删除')
      loadProducts()
    },
  })
}

onMounted(loadProducts)
</script>

<style scoped>
.my-products { background: #fff; border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { font-size: 18px; font-weight: 700; }
.filter-bar { margin-bottom: 16px; }

.product-list { display: flex; flex-direction: column; gap: 12px; }
.product-row {
  display: flex; align-items: center; gap: 16px;
  padding: 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-sm); transition: box-shadow 0.2s;
}
.product-row:hover { box-shadow: var(--shadow-sm); }
.product-img { width: 72px; height: 72px; object-fit: cover; border-radius: 6px; cursor: pointer; flex-shrink: 0; }
.product-info { flex: 1; min-width: 0; }
.product-title { font-size: 14px; font-weight: 500; cursor: pointer; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-title:hover { color: var(--color-primary); }
.product-price { font-size: 16px; font-weight: 700; color: #FF4B6E; margin-bottom: 6px; }
.product-meta { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.meta-item { font-size: 12px; color: var(--color-text-secondary); }
.product-actions { display: flex; flex-direction: column; gap: 6px; flex-shrink: 0; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
.loading-wrap { display: flex; justify-content: center; padding: 40px; }
</style>
