<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo">
        <span class="logo-icon">🏫</span>
        <h1>校园二手</h1>
        <p>创建你的账号</p>
      </div>

      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="4-20位字母/数字/下划线" size="large" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            show-password-on="click"
            placeholder="6-20位密码"
            size="large"
          />
        </n-form-item>
        <n-form-item label="昵称（可选）">
          <n-input v-model:value="form.nickname" placeholder="你的昵称" size="large" />
        </n-form-item>
        <n-form-item label="所在学校（可选）">
          <n-select
            v-model:value="form.schoolId"
            :options="schoolOptions"
            placeholder="选择学校"
            size="large"
            clearable
            filterable
            @update:value="onSchoolChange"
          />
        </n-form-item>
        <n-form-item v-if="campusOptions.length" label="所在校区（可选）">
          <n-select
            v-model:value="form.campusId"
            :options="campusOptions"
            placeholder="选择校区"
            size="large"
            clearable
          />
        </n-form-item>
      </n-form>

      <n-button
        type="primary"
        block
        size="large"
        :loading="loading"
        style="margin-top: 8px; border-radius: 10px;"
        @click="handleRegister"
      >
        注册
      </n-button>

      <div class="auth-footer">
        已有账号？<RouterLink to="/login" class="link">立即登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, NSelect, useMessage } from 'naive-ui'
import { authApi } from '@/api/modules/auth'
import { schoolApi } from '@/api/modules/school'
import { useUserStore } from '@/stores/user'
import type { School, Campus } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const formRef = ref()
const schools = ref<School[]>([])
const campuses = ref<Campus[]>([])
const form = ref({
  username: '',
  password: '',
  nickname: '',
  schoolId: null as number | null,
  campusId: null as number | null,
})

const schoolOptions = ref<{ label: string; value: number }[]>([])
const campusOptions = ref<{ label: string; value: number }[]>([])

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '4-20位字母、数字或下划线' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '密码长度6-20位' },
  ],
}

async function onSchoolChange(schoolId: number) {
  form.value.campusId = null
  campusOptions.value = []
  if (schoolId) {
    const res = await schoolApi.listCampuses(schoolId)
    campusOptions.value = res.data.map(c => ({ label: c.name, value: c.id }))
  }
}

async function handleRegister() {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await authApi.register({
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.nickname || undefined,
      schoolId: form.value.schoolId || undefined,
      campusId: form.value.campusId || undefined,
    })
    userStore.setAuth(res.data.token, res.data.user)
    message.success('注册成功！')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (err: any) {
    message.error(err.message || '注册失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const res = await schoolApi.listSchools()
  schoolOptions.value = res.data.map(s => ({ label: s.name, value: s.id }))
})
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
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.auth-logo {
  text-align: center;
  margin-bottom: 28px;
}
.logo-icon { font-size: 48px; display: block; margin-bottom: 8px; }
.auth-logo h1 { font-size: 24px; font-weight: 800; color: var(--color-primary); margin-bottom: 4px; }
.auth-logo p { color: var(--color-text-secondary); font-size: 14px; }
.auth-footer { text-align: center; margin-top: 20px; font-size: 14px; color: var(--color-text-secondary); }
.link { color: var(--color-primary); text-decoration: none; font-weight: 500; }
</style>
