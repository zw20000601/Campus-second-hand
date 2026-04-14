<template>
  <div class="categories-view">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <n-button type="primary" @click="openAdd">+ 新增分类</n-button>
    </div>
    <n-data-table :columns="columns" :data="categories" :loading="loading" size="small" />

    <n-modal v-model:show="showModal" :title="editId ? '编辑分类' : '新增分类'" preset="card" style="max-width: 400px">
      <n-form ref="formRef" :model="form" label-placement="top">
        <n-form-item label="分类名称" required>
          <n-input v-model:value="form.name" placeholder="分类名称" />
        </n-form-item>
        <n-form-item label="图标（Emoji）">
          <n-input v-model:value="form.icon" placeholder="如 📱" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" @click="handleSave">保存</n-button>
        <n-button @click="showModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, h, onMounted } from 'vue'
import { NButton, NDataTable, NModal, NForm, NFormItem, NInput, NInputNumber, useMessage, useDialog } from 'naive-ui'
import { categoryAdminApi } from '@/api'

const message = useMessage()
const dialog = useDialog()
const categories = ref([])
const loading = ref(false)
const showModal = ref(false)
const editId = ref<number | null>(null)
const form = ref({ name: '', icon: '', sort: 0, status: 1 })

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '图标', key: 'icon', width: 60, render: (row: any) => h('span', { style: 'font-size:20px' }, row.icon) },
  { title: '分类名称', key: 'name' },
  { title: '排序', key: 'sort', width: 80 },
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
  const res: any = await categoryAdminApi.list()
  categories.value = res.data
  loading.value = false
}

function openAdd() {
  editId.value = null
  form.value = { name: '', icon: '', sort: 0, status: 1 }
  showModal.value = true
}

function openEdit(row: any) {
  editId.value = row.id
  form.value = { name: row.name, icon: row.icon, sort: row.sort, status: row.status }
  showModal.value = true
}

async function handleSave() {
  if (editId.value) {
    await categoryAdminApi.update(editId.value, form.value)
  } else {
    await categoryAdminApi.add(form.value)
  }
  message.success('保存成功')
  showModal.value = false
  load()
}

function handleDelete(id: number) {
  dialog.warning({
    title: '确认删除', content: '确认删除该分类？',
    positiveText: '删除',
    onPositiveClick: async () => {
      await categoryAdminApi.delete(id)
      message.success('已删除')
      load()
    }
  })
}

onMounted(load)
</script>

<style scoped>
.categories-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 18px; font-weight: 700; }
</style>
