<template>
  <div class="my-favorites">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>
    <div v-if="loading" class="loading-wrap"><n-spin /></div>
    <div v-else-if="products.length" class="product-grid">
      <div v-for="p in products" :key="p.id" class="card-wrap">
        <ProductCard
          :product="p"
          @click="$router.push(`/market/${p.id}`)"
        />
        <n-button
          class="remove-btn"
          size="tiny"
          type="error"
          ghost
          @click.stop="handleRemove(p.id)"
        >取消收藏</n-button>
      </div>
    </div>
    <n-empty v-else description="还没有收藏任何商品" style="padding: 60px 0" />
    <div v-if="total > pageSize" class="pagination">
      <n-pagination v-model:page="pageNum" :page-count="pages" :page-size="pageSize" @update:page="loadFavorites" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NSpin, NEmpty, NPagination, NButton, useMessage } from 'naive-ui'
import { favoriteApi } from '@/api/modules/favorite'
import type { ProductListVO } from '@/types'
import ProductCard from '@/components/common/ProductCard.vue'

const message = useMessage()

const products = ref<ProductListVO[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const pages = ref(0)

async function loadFavorites() {
  loading.value = true
  try {
    const res = await favoriteApi.myFavorites({ pageNum: pageNum.value, pageSize: pageSize.value })
    products.value = res.data.list
    total.value = res.data.total
    pages.value = res.data.pages
  } finally {
    loading.value = false
  }
}

async function handleRemove(productId: number) {
  try {
    await favoriteApi.remove(productId)
    message.success('已取消收藏')
    // 若当前页只剩 1 条且不是第一页，先回退页码再刷新
    if (products.value.length === 1 && pageNum.value > 1) {
      pageNum.value--
    }
    loadFavorites()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

onMounted(loadFavorites)
</script>

<style scoped>
.my-favorites { background: #fff; border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 18px; font-weight: 700; }
.loading-wrap { display: flex; justify-content: center; padding: 40px; }
.product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }

.card-wrap {
  position: relative;
}
.remove-btn {
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.15s;
}
.card-wrap:hover .remove-btn {
  opacity: 1;
}

.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
