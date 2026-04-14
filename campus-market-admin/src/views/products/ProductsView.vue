<template>
  <div class="products-view">
    <h2 class="page-title">商品管理</h2>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <n-input v-model:value="params.keyword" placeholder="搜索商品标题" clearable style="width: 220px" @keyup.enter="load" />
      <n-select v-model:value="params.auditStatus" :options="auditStatusOptions" placeholder="审核状态" clearable style="width: 140px" />
      <n-select v-model:value="params.status" :options="statusOptions" placeholder="商品状态" clearable style="width: 140px" />
      <n-button type="primary" @click="load">搜索</n-button>
      <n-button @click="resetParams">重置</n-button>
    </div>

    <!-- 表格 -->
    <n-data-table
      :columns="columns"
      :data="products"
      :loading="loading"
      :pagination="{ page: params.pageNum, pageSize: params.pageSize, itemCount: total, onChange: (p: number) => { params.pageNum = p; load() } }"
      size="small"
    />

    <!-- 审核弹窗 -->
    <n-modal v-model:show="showAuditModal" title="审核商品" preset="card" style="max-width: 440px">
      <n-form label-placement="top">
        <n-form-item label="审核结果" required>
          <n-radio-group v-model:value="auditForm.auditStatus">
            <n-radio :value="1">通过</n-radio>
            <n-radio :value="2">拒绝</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="auditForm.remark" type="textarea" :rows="3" placeholder="审核备注（拒绝时请说明原因）" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button type="primary" :loading="submitting" @click="submitAudit">确认</n-button>
        <n-button @click="showAuditModal = false" style="margin-left: 8px">取消</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import {
  NInput, NSelect, NButton, NDataTable, NModal, NForm, NFormItem,
  NRadioGroup, NRadio, NTag, useMessage
} from 'naive-ui'
import { productAdminApi } from '@/api'

const message = useMessage()
const products = ref([])
const loading = ref(false)
const total = ref(0)
const showAuditModal = ref(false)
const submitting = ref(false)
const currentProductId = ref<number>(0)
const auditForm = ref({ auditStatus: 1, remark: '' })

const params = ref({
  pageNum: 1,
  pageSize: 15,
  keyword: '',
  status: null,
  auditStatus: null,
})

const statusOptions = [
  { label: '待审核', value: 0 },
  { label: '已上架', value: 1 },
  { label: '已下架', value: 2 },
  { label: '已售出', value: 3 },
  { label: '审核拒绝', value: 4 },
]
const auditStatusOptions = [
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已拒绝', value: 2 },
]

const STATUS_LABELS: Record<number, string> = { 0: '待审核', 1: '已上架', 2: '已下架', 3: '已售出', 4: '审核拒绝' }
const STATUS_TYPES: Record<number, any> = { 0: 'warning', 1: 'success', 2: 'default', 3: 'info', 4: 'error' }

const columns = [
  { title: '封面', key: 'coverImage', width: 60, render: (row: any) => h('img', { src: row.coverImage, style: 'width:48px;height:48px;object-fit:cover;border-radius:6px' }) },
  { title: '标题', key: 'title', ellipsis: { tooltip: true } },
  { title: '价格', key: 'price', width: 80, render: (row: any) => `¥${row.price}` },
  { title: '发布者', key: 'userNickname', width: 100 },
  { title: '状态', key: 'status', width: 90, render: (row: any) => h(NTag, { type: STATUS_TYPES[row.status], size: 'small' }, () => STATUS_LABELS[row.status]) },
  { title: '发布时间', key: 'createdAt', width: 160 },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render: (row: any) => h('div', { style: 'display:flex;gap:6px' }, [
      row.status === 0 ? h(NButton, { size: 'tiny', type: 'primary', onClick: () => openAudit(row.id) }, () => '审核') : null,
      row.status === 1 ? h(NButton, { size: 'tiny', type: 'warning', onClick: () => handleOffShelf(row.id) }, () => '下架') : null,
    ]),
  },
]

async function load() {
  loading.value = true
  try {
    const res: any = await productAdminApi.list(params.value)
    products.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAudit(id: number) {
  currentProductId.value = id
  auditForm.value = { auditStatus: 1, remark: '' }
  showAuditModal.value = true
}

async function submitAudit() {
  submitting.value = true
  try {
    await productAdminApi.audit(currentProductId.value, auditForm.value)
    message.success('审核完成')
    showAuditModal.value = false
    load()
  } catch (e: any) {
    message.error(e.message)
  } finally {
    submitting.value = false
  }
}

async function handleOffShelf(id: number) {
  await productAdminApi.offShelf(id)
  message.success('已下架')
  load()
}

function resetParams() {
  params.value = { pageNum: 1, pageSize: 15, keyword: '', status: null, auditStatus: null }
  load()
}

onMounted(load)
</script>

<style scoped>
.products-view { background: #fff; border-radius: 12px; padding: 20px; }
.page-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap; }
</style>
