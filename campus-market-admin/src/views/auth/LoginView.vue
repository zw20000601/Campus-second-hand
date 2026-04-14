<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <span class="logo">🏫</span>
        <h1>校园二手 管理后台</h1>
      </div>
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="管理员账号" path="username">
          <n-input v-model:value="form.username" placeholder="请输入账号" size="large" @keyup.enter="handleLogin" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="请输入密码" size="large" @keyup.enter="handleLogin" />
        </n-form-item>
      </n-form>
      <n-button type="primary" block size="large" :loading="loading" style="border-radius: 10px" @click="handleLogin">
        登录
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'
import { adminApi } from '@/api'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const message = useMessage()
const loading = ref(false)
const formRef = ref()
const form = ref({ username: 'admin', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号' }],
  password: [{ required: true, message: '请输入密码' }],
}

async function handleLogin() {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res: any = await adminApi.login(form.value)
    adminStore.setAuth(res.data.token, {
      adminId: res.data.adminId,
      username: res.data.username,
      nickname: res.data.nickname,
      role: res.data.role,
      isSuperAdmin: res.data.isSuperAdmin,
    })
    message.success('登录成功')
    router.push({ name: 'dashboard' })
  } catch (err: any) {
    message.error(err.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0CBFB5, #089B91);
}
.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px 36px;
  width: 380px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.login-header { text-align: center; margin-bottom: 28px; }
.logo { font-size: 48px; display: block; margin-bottom: 8px; }
.login-header h1 { font-size: 18px; font-weight: 700; color: #0CBFB5; }
</style>
