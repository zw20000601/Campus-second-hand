<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- Logo -->
      <div class="auth-logo">
        <span class="logo-icon">🏫</span>
        <h1>校园二手</h1>
        <p>登录你的账号</p>
      </div>

      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="form.username"
            placeholder="请输入用户名"
            size="large"
            @keyup.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            show-password-on="click"
            placeholder="请输入密码"
            size="large"
            @keyup.enter="handleLogin"
          />
        </n-form-item>
      </n-form>

      <n-button
        type="primary"
        block
        size="large"
        :loading="loading"
        style="margin-top: 8px; border-radius: 10px;"
        @click="handleLogin"
      >
        登录
      </n-button>

      <div class="auth-footer">
        还没有账号？<RouterLink to="/register" class="link">立即注册</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'
import { authApi } from '@/api/modules/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const formRef = ref()
const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await authApi.login(form.value)
    userStore.setAuth(res.data.token, res.data.user)
    message.success('登录成功！')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (err: any) {
    message.error(err.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0CBFB5 0%, #089B91 100%);
  padding: 20px;
}
.auth-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 36px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.auth-logo {
  text-align: center;
  margin-bottom: 32px;
}
.logo-icon { font-size: 48px; display: block; margin-bottom: 8px; }
.auth-logo h1 {
  font-size: 24px;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 4px;
}
.auth-logo p { color: var(--color-text-secondary); font-size: 14px; }
.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
}
.link { color: var(--color-primary); text-decoration: none; font-weight: 500; }
</style>
