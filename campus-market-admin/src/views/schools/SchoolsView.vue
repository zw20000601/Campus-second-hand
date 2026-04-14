<template>
  <div class="schools-view">
    <div class="page-header">
      <h2 class="page-title">学校管理</h2>
      <n-button v-if="isSuperAdmin" type="primary" @click="openAdd">+ 新增学校</n-button>
    </div>

    <n-data-table :columns="columns" :data="schools" :loading="loading" size="small"
      :pagination="{ page: 1, pageSize: 15, itemCount: total }" />

    <n-modal v-model:show="showModal" :title="editId ? '编辑学校' : '新增学校'" preset="card" style="max-width: 440px">
      <n-form label-placement="top">
        <n-form-item label="学校名称" required>
          <n-input v-model:value="form.name" placeholder="学校名称" />
        </n-form-item>
        <n-form-item label="省份">
          <n-input v-model:value="form.province" placeholder="省份" />
        </n-form-item>
        <n-form-item label="城市">
          <n-input v-model:value="form.city" placeholder="城市" />
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
import { ref, computed, h, onMounted } from 'vue'
import { NButton, NDataTable, NModal, NForm, NFormItem, NInput, NInputNumber, useMessage } from 'naive-ui'
import { schoolAdminApi } from '@/api'
import { useAdminStore } from '@/stores/admin'

const message = useMessage()
const adminStore = useAdminStore()
const isSuperAdmin = computed(() => adminStore.adminInfo?.isSuperAdmin === true)
const schools = ref([])
const loading = ref(false)
const total = ref(0)
const showModal = ref(false)
const editId = ref<number | null>(null)
const form = ref({ name: '', province: '', city: '', sort: 0 })

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '学校名称', key: 'name' },
  { title: '省份', key: 'province', width: 100 },
  { title: '城市', key: 'city', width: 100 },
  { title: '排序', key: 'sort', width: 70 },
  {
    title: '操作', key: 'actions', width: 120,
    render: (row: any) => isSuperAdmin.value
      ? h('div', { style: 'display:flex;gap:6px' }, [
          h(NButton, { size: 'tiny', onClick: () => openEdit(row) }, () => '编辑'),
          h(NButton, { size: 'tiny', type: 'error', ghost: true, onClick: () => handleDelete(row.id) }, () => '删除'),
        ])
      : null,
  },
]

async function load() {
  loading.value = true
  const res: any = await schoolAdminApi.list({ pageNum: 1, pageSize: 100 })
  schools.value = res.data.list
  total.value = res.data.total
  loading.value = false
}

function openAdd() { editId.value = null; form.value = { name: '', province: '', city: '', sort: 0 }; showModal.value = true }
function openEdit(row: any) { editId.value = row.id; form.value = { name: row.name, province: row.province, city: row.city, sort: row.sort }; showModal.value = true }

async function handleSave() {
  try {
    editId.value ? await schoolAdminApi.update(editId.value, form.value) : await schoolAdminApi.add(form.value)
    message.success('保存成功')
    showModal.value = false
    load()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

async function handleDelete(id: number) {
  try {
    await schoolAdminApi.delete(id)
    message.success('已删除')
    load()
  } catch (e: any) {
    message.error(e.message || '删除失败')
  }
}

onMounted(load)
</script>

<style scoped>
.schools-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 18px; font-weight: 700; }
</style>
