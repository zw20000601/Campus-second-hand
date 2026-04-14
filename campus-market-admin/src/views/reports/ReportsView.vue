<template>
  <div class="reports-view">
    <h2 class="page-title">举报管理</h2>
    <div class="filter-bar">
      <n-select v-model:value="params.status" :options="statusOptions" placeholder="处理状态" clearable style="width: 140px" @update:value="load" />
      <n-button type="primary" @click="load">刷新</n-button>
    </div>
    <n-data-table :columns="columns" :data="reports" :loading="loading" size="small"
      :pagination="{ page: params.pageNum, pageSize: 15, itemCount: total, onChange: (p: number) => { params.pageNum = p; load() } }" />

    <!-- 处理弹窗 -->
    <n-modal v-model:show="showModal" title="处理举报" preset="card" style="max-width: 400px">
      <n-form label-placement="top">
        <n-form-item label="处理结果" required>
          <n-select v-model:value="handleForm.status" :options="[{ label: '已处理', value: 1 }, { label: '忽略', value: 2 }]" />
        </n-form-item>
        <n-form-item label="处理备注">
          <n-input v-model:value="handleForm.remark" type="textarea" :rows="3" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" @click="submitHandle">确认</n-button>
        <n-button @click="showModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, h, onMounted } from 'vue'
import { NSelect, NButton, NDataTable, NTag, NModal, NForm, NFormItem, NInput, useMessage } from 'naive-ui'
import { reportAdminApi } from '@/api'

const message = useMessage()
const reports = ref([])
const loading = ref(false)
const total = ref(0)
const params = ref({ pageNum: 1, status: null as number | null })
const showModal = ref(false)
const currentId = ref(0)
const handleForm = ref({ status: 1, remark: '' })

const statusOptions = [{ label: '待处理', value: 0 }, { label: '已处理', value: 1 }, { label: '已忽略', value: 2 }]
const STATUS_MAP: Record<number, any> = { 0: { label: '待处理', type: 'warning' }, 1: { label: '已处理', type: 'success' }, 2: { label: '已忽略', type: 'default' } }
const REASON_MAP: Record<number, string> = { 1: '虚假信息', 2: '商品违规', 3: '价格欺诈', 4: '重复发布', 5: '其他' }

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '商品ID', key: 'productId', width: 80 },
  { title: '举报原因', key: 'reason', width: 100, render: (row: any) => REASON_MAP[row.reason] || '-' },
  { title: '说明', key: 'description', ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', width: 90, render: (row: any) => h(NTag, { type: STATUS_MAP[row.status]?.type, size: 'small' }, () => STATUS_MAP[row.status]?.label) },
  { title: '举报时间', key: 'createdAt', width: 160 },
  {
    title: '操作', key: 'actions', width: 80,
    render: (row: any) => row.status === 0 ? h(NButton, { size: 'tiny', type: 'primary', onClick: () => openHandle(row.id) }, () => '处理') : null
  },
]

async function load() {
  loading.value = true
  try {
    const res: any = await reportAdminApi.list(params.value)
    reports.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openHandle(id: number) {
  currentId.value = id
  handleForm.value = { status: 1, remark: '' }
  showModal.value = true
}

async function submitHandle() {
  await reportAdminApi.handle(currentId.value, handleForm.value)
  message.success('处理成功')
  showModal.value = false
  load()
}

onMounted(load)
</script>

<style scoped>
.reports-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
