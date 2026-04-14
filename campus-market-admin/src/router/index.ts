import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes: [
    { path: '/login', name: 'admin-login', component: () => import('@/views/auth/LoginView.vue') },
    {
      path: '/',
      component: () => import('@/components/layout/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },
        { path: 'products', name: 'products', component: () => import('@/views/products/ProductsView.vue') },
        { path: 'users', name: 'users', component: () => import('@/views/users/UsersView.vue') },
        { path: 'reports', name: 'reports', component: () => import('@/views/reports/ReportsView.vue') },
        { path: 'categories', name: 'categories', component: () => import('@/views/categories/CategoriesView.vue') },
        { path: 'schools', name: 'schools', component: () => import('@/views/schools/SchoolsView.vue') },
        { path: 'notices', name: 'notices', component: () => import('@/views/notices/NoticesView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const adminStore = useAdminStore()
    if (!adminStore.isLoggedIn) {
      return { name: 'admin-login' }
    }
  }
})

export default router
