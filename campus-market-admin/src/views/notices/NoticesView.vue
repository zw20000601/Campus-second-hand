<template>
  <div class="notices-view">
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <n-button type="primary" @click="openAdd">+ 新增公告</n-button>
    </div>

    <n-data-table :columns="columns" :data="notices" :loading="loading" size="small"
      :pagination="{ page: params.pageNum, pageSize: 15, itemCount: total, onChange: (p: number) => { params.pageNum = p; load() } }" />

    <n-modal v-model:show="showModal" :title="editId ? '编辑公告' : '新增公告'" preset="card" style="max-width: 600px">
      <n-form label-placement="top">
        <n-form-item label="公告标题" required>
          <n-input v-model:value="form.title" placeholder="公告标题" />
        </n-form-item>
        <n-form-item label="公告类型">
          <n-select v-model:value="form.type" :options="[{ label: '通知', value: 1 }, { label: '活动', value: 2 }, { label: '规则', value: 3 }]" />
        </n-form-item>
        <n-form-item label="是否置顶">
          <n-switch v-model:value="isTop" />
        </n-form-item>
        <n-form-item label="公告内容" required>
          <n-input v-model:value="form.content" type="textarea" :rows="8" placeholder="公告内容..." />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" @click="handleSave">发布</n-button>
        <n-button @click="showModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, h, computed, onMounted } from 'vue'
import { NButton, NDataTable, NModal, NForm, NFormItem, NInput, NSelect, NSwitch, NTag, useMessage } from 'naive-ui'
import { noticeAdminApi } from '@/api'

const message = useMessage()
const notices = ref([])
const loading = ref(false)
const total = ref(0)
const params = ref({ pageNum: 1 })
const showModal = ref(false)
const editId = ref<number | null>(null)
const isTop = ref(false)
const form = ref({ title: '', content: '', type: 1, isTop: 0, status: 1 })

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '标题', key: 'title', ellipsis: { tooltip: true } },
  { title: '类型', key: 'type', width: 80, render: (row: any) => ['', '通知', '活动', '规则'][row.type] || '-' },
  { title: '置顶', key: 'isTop', width: 70, render: (row: any) => row.isTop ? h(NTag, { type: 'warning', size: 'small' }, () => '置顶') : '-' },
  { title: '阅读', key: 'viewCount', width: 70 },
  { title: '发布时间', key: 'createdAt', width: 160 },
  {
    title: '操作', key: 'actions', width: 120,
    render: (row: any) => h('div', { style: 'display:flex;gap:6px' }, [
      h(NButton, { size: 'tiny', onClick: () => openEdit(row) }, () => '编辑'),
      h(NButton, { size: 'tiny', type: 'error', ghost: true, onClick: () => handleDelete(row.id) }, () => '删除'),
    ])
  },
]

async function load() {
  loading.value = true
  const res: any = await noticeAdminApi.list(params.value)
  notices.value = res.data.list
  total.value = res.data.total
  loading.value = false
}

function openAdd() {
  editId.value = null
  form.value = { title: '', content: '', type: 1, isTop: 0, status: 1 }
  isTop.value = false
  showModal.value = true
}

function openEdit(row: any) {
  editId.value = row.id
  form.value = { title: row.title, content: row.content, type: row.type, isTop: row.isTop, status: row.status }
  isTop.value = row.isTop === 1
  showModal.value = true
}

async function handleSave() {
  form.value.isTop = isTop.value ? 1 : 0
  editId.value ? await noticeAdminApi.update(editId.value, form.value) : await noticeAdminApi.add(form.value)
  message.success('保存成功')
  showModal.value = false
  load()
}

async function handleDelete(id: number) {
  await noticeAdminApi.delete(id)
  message.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.notices-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 18px; font-weight: 700; }
</style>
