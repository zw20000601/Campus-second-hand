<template>
  <div class="product-card" @click="$emit('click')">
    <!-- 封面图 -->
    <div class="cover-wrap">
      <img
        :src="product.coverImage || placeholderImage"
        :alt="product.title"
        class="cover"
        loading="lazy"
        @error="handleImgError"
      />
      <!-- 状态标记 -->
      <div v-if="product.status === 3" class="status-badge sold">已售出</div>
      <div v-else-if="product.status === 2" class="status-badge off-shelf">已下架</div>
    </div>

    <!-- 信息 -->
    <div class="info">
      <div class="title text-truncate-2">{{ product.title }}</div>
      <div class="price-row">
        <span class="price"><span class="price-unit">¥</span>{{ product.price }}</span>
        <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
          ¥{{ product.originalPrice }}
        </span>
      </div>
      <div class="meta">
        <span class="condition-tag" :class="`condition-${product.conditionLevel}`">
          {{ product.conditionDesc }}
        </span>
        <span v-if="product.campusName || product.schoolName" class="location">
          📍 {{ product.campusName || product.schoolName }}
        </span>
      </div>
      <div class="footer">
        <div class="user-info">
          <img
            :src="product.userAvatar || defaultAvatar"
            :alt="product.userNickname"
            class="user-avatar"
            @error="handleAvatarError"
          />
          <span class="user-name">{{ product.userNickname || '匿名用户' }}</span>
        </div>
        <div class="stats">
          <span>👁 {{ product.viewCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProductListVO } from '@/types'

defineProps<{ product: ProductListVO }>()
defineEmits<{ click: [] }>()

const placeholderImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyMDAgMjAwIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI0YwRjBGMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LXNpemU9IjQwIj7wn5S6PC90ZXh0Pjwvc3ZnPg=='
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0OCA0OCI+PGNpcmNsZSBjeD0iMjQiIGN5PSIyNCIgcj0iMjQiIGZpbGw9IiMwQ0JGQjUiLz48cGF0aCBkPSJNMjQgMjZjLTUgMC05LTQtOS05czQtOSA5LTkgOSA0IDkgOS00IDkgLTkgOXoiIGZpbGw9IndoaXRlIi8+PC9zdmc+'

function handleImgError(e: Event) {
  (e.target as HTMLImageElement).src = placeholderImage
}
function handleAvatarError(e: Event) {
  (e.target as HTMLImageElement).src = defaultAvatar
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.cover-wrap {
  position: relative;
  overflow: hidden;
}
.cover {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  background: #F5F5F5;
  transition: transform 0.3s ease;
}
.product-card:hover .cover {
  transform: scale(1.04);
}

.status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}
.status-badge.sold { background: rgba(0,0,0,0.5); color: #fff; }
.status-badge.off-shelf { background: rgba(255,140,66,0.85); color: #fff; }

.info {
  padding: 10px 12px 12px;
}
.title {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 8px;
  line-height: 1.5;
  min-height: 40px;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 6px;
}
.price {
  font-size: 18px;
  font-weight: 700;
  color: #FF4B6E;
}
.price-unit { font-size: 12px; font-weight: 400; }
.original-price {
  font-size: 12px;
  color: #BBBBBB;
  text-decoration: line-through;
}
.meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.condition-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}
.condition-1 { background: #E6F9F8; color: #0CBFB5; }
.condition-2 { background: #EBF5FF; color: #3B82F6; }
.condition-3 { background: #FFF4E6; color: #FF8C42; }
.condition-4 { background: #FFF0F0; color: #FF4B6E; }
.condition-5 { background: #F0F0F0; color: #888; }

.location {
  font-size: 11px;
  color: var(--color-text-secondary);
}
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
}
.user-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
}
.user-name {
  font-size: 11px;
  color: var(--color-text-secondary);
  max-width: 70px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.stats {
  font-size: 11px;
  color: var(--color-text-secondary);
}
</style>
