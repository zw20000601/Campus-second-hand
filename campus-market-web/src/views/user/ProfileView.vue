<template>
  <div class="profile-page">
    <div class="page-header"><h2>个人资料</h2></div>

    <n-form ref="formRef" :model="form" label-placement="top">
      <!-- 头像 -->
      <div class="avatar-section">
        <n-avatar :src="form.avatar" round :size="80" />
        <label class="change-avatar" for="avatarInput">
          更换头像
          <input id="avatarInput" type="file" accept="image/*" hidden @change="handleAvatarUpload" />
        </label>
      </div>

      <n-form-item label="昵称">
        <n-input v-model:value="form.nickname" placeholder="你的昵称" :maxlength="20" />
      </n-form-item>
      <n-form-item label="性别">
        <n-radio-group v-model:value="form.gender">
          <n-radio :value="0">保密</n-radio>
          <n-radio :value="1">男</n-radio>
          <n-radio :value="2">女</n-radio>
        </n-radio-group>
      </n-form-item>
      <n-form-item label="个人简介">
        <n-input v-model:value="form.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." :maxlength="100" />
      </n-form-item>
      <n-form-item label="所在学校">
        <n-select v-model:value="form.schoolId" :options="schoolOptions" placeholder="选择学校" filterable clearable @update:value="onSchoolChange" />
      </n-form-item>
      <n-form-item label="所在校区">
        <n-select v-model:value="form.campusId" :options="campusOptions" placeholder="选择校区" clearable />
      </n-form-item>

      <n-button type="primary" :loading="saving" @click="handleSave">保存修改</n-button>
    </n-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NForm, NFormItem, NInput, NButton, NAvatar, NSelect, NRadioGroup, NRadio, useMessage } from 'naive-ui'
import { userApi } from '@/api/modules/user'
import { schoolApi } from '@/api/modules/school'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const message = useMessage()

const saving = ref(false)
const form = ref({
  nickname: '',
  gender: 0,
  bio: '',
  schoolId: null as number | null,
  campusId: null as number | null,
  avatar: '',
})
const schoolOptions = ref<{ label: string; value: number }[]>([])
const campusOptions = ref<{ label: string; value: number }[]>([])

async function onSchoolChange(schoolId: number) {
  form.value.campusId = null
  campusOptions.value = []
  if (schoolId) {
    const res = await schoolApi.listCampuses(schoolId)
    campusOptions.value = res.data.map(c => ({ label: c.name, value: c.id }))
  }
}

async function handleAvatarUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    // 1. 上传图片文件，获取 URL
    const uploadRes = await userApi.uploadAvatarFile(formData)
    const avatarUrl = uploadRes.data
    // 2. 将 URL 持久化到用户记录
    const userRes = await userApi.updateAvatarUrl(avatarUrl)
    form.value.avatar = avatarUrl
    userStore.updateUser(userRes.data)
    message.success('头像更新成功')
  } catch (err: any) {
    message.error(err.message || '头像上传失败')
  }
}

async function handleSave() {
  saving.value = true
  try {
    const res = await userApi.updateProfile({
      nickname: form.value.nickname,
      gender: form.value.gender,
      bio: form.value.bio,
      schoolId: form.value.schoolId || undefined,
      campusId: form.value.campusId || undefined,
    } as any)
    userStore.updateUser(res.data)
    message.success('保存成功')
  } catch (err: any) {
    message.error(err.message)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const user = userStore.user
  if (user) {
    form.value.nickname = user.nickname || ''
    form.value.gender = user.gender || 0
    form.value.bio = user.bio || ''
    form.value.schoolId = user.schoolId || null
    form.value.campusId = user.campusId || null
    form.value.avatar = user.avatar || ''
  }
  const [schoolRes] = await Promise.all([schoolApi.listSchools()])
  schoolOptions.value = schoolRes.data.map(s => ({ label: s.name, value: s.id }))
  if (form.value.schoolId) {
    await onSchoolChange(form.value.schoolId)
  }
})
</script>

<style scoped>
.profile-page { background: #fff; border-radius: var(--radius-md); padding: 24px; box-shadow: var(--shadow-sm); }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 18px; font-weight: 700; }
.avatar-section { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.change-avatar { font-size: 13px; color: var(--color-primary); cursor: pointer; text-decoration: underline; }
</style>
