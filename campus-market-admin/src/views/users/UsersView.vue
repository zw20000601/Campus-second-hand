<template>
  <div class="users-view">
    <h2 class="page-title">用户管理</h2>
    <div class="filter-bar">
      <n-input v-model:value="params.keyword" placeholder="搜索用户名/昵称" clearable style="width: 220px" @keyup.enter="load" />
      <n-select v-model:value="params.status" :options="statusOptions" placeholder="账号状态" clearable style="width: 140px" />
      <n-button type="primary" @click="load">搜索</n-button>
    </div>
    <n-data-table :columns="columns" :data="users" :loading="loading" size="small"
      :pagination="{ page: params.pageNum, pageSize: params.pageSize, itemCount: total, onChange: (p: number) => { params.pageNum = p; load() } }" />
  </div>
</template>

<script setup lang="ts">
import { ref, h, onMounted } from 'vue'
import { NInput, NSelect, NButton, NDataTable, NTag, useMessage } from 'naive-ui'
import { userAdminApi } from '@/api'

const message = useMessage()
const users = ref([])
const loading = ref(false)
const total = ref(0)
const params = ref({ pageNum: 1, pageSize: 15, keyword: '', status: null })
const statusOptions = [{ label: '正常', value: 1 }, { label: '已封禁', value: 0 }]

const columns = [
  { title: 'ID', key: 'id', width: 70 },
  { title: '用户名', key: 'username', width: 120 },
  { title: '昵称', key: 'nickname', width: 120 },
  { title: '学校', key: 'schoolName', width: 150 },
  {
    title: '状态', key: 'status', width: 90,
    render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'error', size: 'small' }, () => row.status === 1 ? '正常' : '已封禁')
  },
  { title: '注册时间', key: 'createdAt', width: 160 },
  {
    title: '操作', key: 'actions', width: 100,
    render: (row: any) => h(NButton, {
      size: 'tiny',
      type: row.status === 1 ? 'error' : 'success',
      onClick: () => toggleStatus(row)
    }, () => row.status === 1 ? '封禁' : '解封')
  },
]

async function load() {
  loading.value = true
  try {
    const res: any = await userAdminApi.list(params.value)
    users.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await userAdminApi.updateStatus(row.id, newStatus)
  message.success(newStatus === 1 ? '已解封' : '已封禁')
  load()
}

onMounted(load)
</script>

<style scoped>
.users-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
