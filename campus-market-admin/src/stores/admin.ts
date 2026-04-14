import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<any>(JSON.parse(localStorage.getItem('admin_info') || 'null'))

  const isLoggedIn = computed(() => !!token.value && !!adminInfo.value)

  function setAuth(newToken: string, info: any) {
    token.value = newToken
    adminInfo.value = info
    localStorage.setItem('admin_token', newToken)
    localStorage.setItem('admin_info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  return { token, adminInfo, isLoggedIn, setAuth, logout }
})
