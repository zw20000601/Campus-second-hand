<template>
  <div class="campuses-view">
    <div class="page-header">
      <h2 class="page-title">校区管理</h2>
      <n-button type="primary" :disabled="!selectedSchoolId" @click="openAdd">+ 新增校区</n-button>
    </div>

    <!-- 学校选择 -->
    <div class="school-select-bar">
      <span class="label">选择学校：</span>
      <n-select
        v-model:value="selectedSchoolId"
        :options="schoolOptions"
        placeholder="请选择学校"
        style="width: 260px"
        @update:value="loadCampuses"
      />
    </div>

    <n-data-table
      :columns="columns"
      :data="campuses"
      :loading="loading"
      size="small"
    />

    <n-modal v-model:show="showModal" :title="editId ? '编辑校区' : '新增校区'" preset="card" style="max-width: 440px">
      <n-form label-placement="top">
        <n-form-item label="校区名称" required>
          <n-input v-model:value="form.name" placeholder="校区名称" />
        </n-form-item>
        <n-form-item label="地址">
          <n-input v-model:value="form.address" placeholder="详细地址" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" :loading="submitting" @click="handleSave">保存</n-button>
        <n-button @click="showModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, h, onMounted } from 'vue'
import { NButton, NDataTable, NModal, NForm, NFormItem, NInput, NInputNumber, NSelect, useMessage } from 'naive-ui'
import { schoolAdminApi, campusAdminApi } from '@/api'

const message = useMessage()
const schools = ref<any[]>([])
const campuses = ref<any[]>([])
const loading = ref(false)
const submitting = ref(false)
const selectedSchoolId = ref<number | null>(null)
const showModal = ref(false)
const editId = ref<number | null>(null)
const form = ref({ name: '', address: '', sort: 0 })

const schoolOptions = ref<{ label: string; value: number }[]>([])

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '校区名称', key: 'name' },
  { title: '地址', key: 'address', ellipsis: { tooltip: true } },
  { title: '排序', key: 'sort', width: 70 },
  {
    title: '操作', key: 'actions', width: 120,
    render: (row: any) => h('div', { style: 'display:flex;gap:6px' }, [
      h(NButton, { size: 'tiny', onClick: () => openEdit(row) }, () => '编辑'),
      h(NButton, { size: 'tiny', type: 'error', ghost: true, onClick: () => handleDelete(row.id) }, () => '删除'),
    ])
  },
]

async function loadSchools() {
  const res: any = await schoolAdminApi.list({ pageNum: 1, pageSize: 100 })
  schools.value = res.data.list
  schoolOptions.value = schools.value.map((s: any) => ({ label: s.name, value: s.id }))
  if (schoolOptions.value.length > 0 && !selectedSchoolId.value) {
    selectedSchoolId.value = schoolOptions.value[0].value
    await loadCampuses(selectedSchoolId.value)
  }
}

async function loadCampuses(schoolId: number) {
  if (!schoolId) return
  loading.value = true
  try {
    const res: any = await campusAdminApi.list(schoolId)
    campuses.value = res.data
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editId.value = null
  form.value = { name: '', address: '', sort: 0 }
  showModal.value = true
}

function openEdit(row: any) {
  editId.value = row.id
  form.value = { name: row.name, address: row.address || '', sort: row.sort }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.name.trim()) { message.warning('校区名称不能为空'); return }
  submitting.value = true
  try {
    if (editId.value) {
      await campusAdminApi.update(editId.value, form.value)
    } else {
      await campusAdminApi.add({ ...form.value, schoolId: selectedSchoolId.value })
    }
    message.success('保存成功')
    showModal.value = false
    loadCampuses(selectedSchoolId.value!)
  } catch (e: any) {
    message.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  await campusAdminApi.delete(id)
  message.success('已删除')
  loadCampuses(selectedSchoolId.value!)
}

onMounted(loadSchools)
</script>

<style scoped>
.campuses-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 18px; font-weight: 700; }
.school-select-bar { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.label { color: #555; font-size: 14px; }
</style>
