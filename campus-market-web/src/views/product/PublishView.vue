<template>
  <div class="publish-page">
    <div class="container page-padding">
      <div class="publish-card">
        <h2 class="page-title">发布闲置</h2>

        <n-form ref="formRef" :model="form" :rules="rules" label-placement="top" label-width="auto">
          <div class="form-row">
            <!-- 标题 -->
            <n-form-item label="商品标题" path="title" style="grid-column: 1 / -1">
              <n-input v-model:value="form.title" placeholder="简洁描述你的商品，例如「九成新 iPhone 13 Pro 128G 远峰蓝」" :maxlength="100" show-count />
            </n-form-item>

            <!-- 价格 -->
            <n-form-item label="售价（元）" path="price">
              <n-input-number v-model:value="form.price" :min="0.01" :max="99999" :precision="2" placeholder="请输入售价" style="width: 100%" />
            </n-form-item>

            <!-- 原价 -->
            <n-form-item label="原价（元，可选）">
              <n-input-number v-model:value="form.originalPrice" :min="0" :precision="2" placeholder="填写原价更有说服力" style="width: 100%" />
            </n-form-item>

            <!-- 分类 -->
            <n-form-item label="商品分类" path="categoryId">
              <n-select v-model:value="form.categoryId" :options="categoryOptions" placeholder="选择分类" />
            </n-form-item>

            <!-- 成色 -->
            <n-form-item label="商品成色" path="conditionLevel">
              <n-select v-model:value="form.conditionLevel" :options="conditionOptions" placeholder="选择成色" />
            </n-form-item>

            <!-- 学校 -->
            <n-form-item label="所在学校" path="schoolId">
              <n-select
                v-model:value="form.schoolId"
                :options="schoolOptions"
                placeholder="选择学校"
                filterable
                @update:value="onSchoolChange"
              />
            </n-form-item>

            <!-- 校区 -->
            <n-form-item label="所在校区">
              <n-select
                v-model:value="form.campusId"
                :options="campusOptions"
                placeholder="选择校区（可选）"
                clearable
              />
            </n-form-item>

            <!-- 交易方式 -->
            <n-form-item label="交易方式" path="tradeType">
              <n-select v-model:value="form.tradeType" :options="tradeTypeOptions" placeholder="选择交易方式" />
            </n-form-item>

            <!-- 交易地点 -->
            <n-form-item label="交易地点（可选）">
              <n-input v-model:value="form.tradeLocation" placeholder="例如：学生活动中心门口" :maxlength="100" />
            </n-form-item>

            <!-- 商品图片 -->
            <n-form-item label="商品图片" path="coverImage" style="grid-column: 1 / -1">
              <div class="upload-area">
                <div class="upload-tip">首张图片为封面，最多上传9张</div>
                <div class="image-list">
                  <div
                    v-for="(img, idx) in uploadedImages"
                    :key="idx"
                    class="image-item"
                  >
                    <img :src="img" class="preview-img" />
                    <div class="image-remove" @click="removeImage(idx)">×</div>
                    <div v-if="idx === 0" class="cover-badge">封面</div>
                  </div>
                  <label v-if="uploadedImages.length < 9" class="upload-btn" for="imageInput">
                    <span>+</span>
                    <span class="upload-text">上传图片</span>
                    <input id="imageInput" type="file" accept="image/*" multiple hidden @change="handleImageUpload" />
                  </label>
                </div>
              </div>
            </n-form-item>

            <!-- 商品描述 -->
            <n-form-item label="商品描述" style="grid-column: 1 / -1">
              <n-input
                v-model:value="form.description"
                type="textarea"
                :rows="5"
                placeholder="详细描述商品状态、购买时间、使用情况等..."
                :maxlength="2000"
                show-count
              />
            </n-form-item>
          </div>

          <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 8px;">
            <n-button @click="$router.back()">取消</n-button>
            <n-button type="primary" :loading="submitting" @click="handleSubmit">
              发布商品
            </n-button>
          </div>
        </n-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  NForm, NFormItem, NInput, NInputNumber, NSelect, NButton, useMessage
} from 'naive-ui'
import { productApi } from '@/api/modules/product'
import { schoolApi } from '@/api/modules/school'

const router = useRouter()
const message = useMessage()

const formRef = ref()
const submitting = ref(false)
const uploadedImages = ref<string[]>([])
const categoryOptions = ref<{ label: string; value: number }[]>([])
const schoolOptions = ref<{ label: string; value: number }[]>([])
const campusOptions = ref<{ label: string; value: number }[]>([])

const conditionOptions = [
  { label: '全新', value: 1 },
  { label: '几乎全新（9成新以上）', value: 2 },
  { label: '轻微使用（7-9成新）', value: 3 },
  { label: '明显使用（5-7成新）', value: 4 },
  { label: '有瑕疵（需说明）', value: 5 },
]
const tradeTypeOptions = [
  { label: '线下交易', value: 1 },
  { label: '快递邮寄', value: 2 },
  { label: '均可', value: 3 },
]

const form = ref({
  title: '',
  description: '',
  price: null as number | null,
  originalPrice: null as number | null,
  categoryId: null as number | null,
  schoolId: null as number | null,
  campusId: null as number | null,
  conditionLevel: null as number | null,
  tradeType: null as number | null,
  tradeLocation: '',
  coverImage: '',
  images: [] as string[],
})

const rules = {
  title: [{ required: true, message: '请填写商品标题' }],
  price: [{ required: true, type: 'number', message: '请填写售价', validator: (_: any, v: any) => v != null && v > 0 }],
  categoryId: [{ required: true, type: 'number', message: '请选择分类' }],
  conditionLevel: [{ required: true, type: 'number', message: '请选择成色' }],
  tradeType: [{ required: true, type: 'number', message: '请选择交易方式' }],
  schoolId: [{ required: true, type: 'number', message: '请选择学校' }],
  coverImage: [{ required: true, message: '请上传至少一张图片' }],
}

async function onSchoolChange(schoolId: number) {
  form.value.campusId = null
  campusOptions.value = []
  if (schoolId) {
    const res = await schoolApi.listCampuses(schoolId)
    campusOptions.value = res.data.map(c => ({ label: c.name, value: c.id }))
  }
}

async function handleImageUpload(e: Event) {
  const files = (e.target as HTMLInputElement).files
  if (!files) return

  for (const file of Array.from(files)) {
    if (uploadedImages.value.length >= 9) break
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await productApi.uploadImage(formData)
      uploadedImages.value.push(res.data)
      if (uploadedImages.value.length === 1) {
        form.value.coverImage = res.data
      }
    } catch (err: any) {
      message.error(`图片上传失败: ${err.message}`)
    }
  }
}

function removeImage(idx: number) {
  uploadedImages.value.splice(idx, 1)
  form.value.coverImage = uploadedImages.value[0] || ''
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitting.value = true
    await productApi.publish({
      title: form.value.title,
      description: form.value.description,
      price: form.value.price!,
      originalPrice: form.value.originalPrice || undefined,
      categoryId: form.value.categoryId!,
      schoolId: form.value.schoolId!,
      campusId: form.value.campusId || undefined,
      conditionLevel: form.value.conditionLevel!,
      tradeType: form.value.tradeType!,
      tradeLocation: form.value.tradeLocation || undefined,
      coverImage: form.value.coverImage,
      images: uploadedImages.value,
    })
    message.success('发布成功！商品正在审核中...')
    router.push('/my/products')
  } catch (err: any) {
    message.error(err.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const [catRes, schoolRes] = await Promise.all([
    fetch('/api/categories').then(r => r.json()),
    schoolApi.listSchools(),
  ])
  categoryOptions.value = (catRes.data || []).map((c: any) => ({ label: `${c.icon} ${c.name}`, value: c.id }))
  schoolOptions.value = schoolRes.data.map(s => ({ label: s.name, value: s.id }))
})
</script>

<style scoped>
.publish-page { background: var(--color-bg); min-height: calc(100vh - 60px); }
.publish-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 32px 40px;
  box-shadow: var(--shadow-sm);
  max-width: 860px;
  margin: 0 auto;
}
.page-title { font-size: 22px; font-weight: 700; margin-bottom: 28px; color: var(--color-text); }

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}

.upload-area {}
.upload-tip { font-size: 12px; color: var(--color-text-secondary); margin-bottom: 10px; }
.image-list { display: flex; flex-wrap: wrap; gap: 10px; }
.image-item { position: relative; width: 90px; height: 90px; border-radius: 8px; overflow: hidden; }
.preview-img { width: 100%; height: 100%; object-fit: cover; }
.image-remove {
  position: absolute; top: 2px; right: 2px;
  width: 20px; height: 20px;
  background: rgba(0,0,0,0.5);
  color: #fff; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; cursor: pointer;
}
.cover-badge {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(12,191,181,0.8);
  color: #fff; font-size: 11px;
  text-align: center; padding: 2px 0;
}
.upload-btn {
  width: 90px; height: 90px;
  border: 2px dashed var(--color-border);
  border-radius: 8px;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 4px; cursor: pointer;
  transition: border-color 0.2s;
  color: var(--color-text-secondary);
  font-size: 24px;
}
.upload-btn:hover { border-color: var(--color-primary); color: var(--color-primary); }
.upload-text { font-size: 12px; }
</style>
