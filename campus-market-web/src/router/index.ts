import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/home/HomeView.vue') },
        { path: 'market', name: 'market', component: () => import('@/views/product/MarketView.vue') },
        { path: 'market/:id', name: 'product-detail', component: () => import('@/views/product/ProductDetailView.vue') },
        {
          path: 'publish',
          name: 'publish',
          component: () => import('@/views/product/PublishView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'my',
          component: () => import('@/views/user/MyLayout.vue'),
          meta: { requiresAuth: true },
          children: [
            { path: '', name: 'my', redirect: '/my/products' },
            { path: 'products', name: 'my-products', component: () => import('@/views/user/MyProductsView.vue') },
            { path: 'favorites', name: 'my-favorites', component: () => import('@/views/user/MyFavoritesView.vue') },
            { path: 'profile', name: 'my-profile', component: () => import('@/views/user/ProfileView.vue') },
          ],
        },
      ],
    },
    { path: '/login', name: 'login', component: () => import('@/views/auth/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/auth/RegisterView.vue') },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

// 路由守卫
router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      return { name: 'login', query: { redirect: to.fullPath } }
    }
  }
})

export default router
