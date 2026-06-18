<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { uploadDocument } from '../api/document.js'

const title = ref('')
const description = ref('')
const category = ref('')
const selectedFile = ref(null)
const errorMessage = ref('')
const isUploading = ref(false)
const router = useRouter()

function handleFileChange(event) {
  selectedFile.value = event.target.files[0]
}

async function handleUpload() {
  errorMessage.value = ''

  if (!selectedFile.value) {
    errorMessage.value = '請選擇檔案'
    return
  }
  if (!title.value) {
    errorMessage.value = '請輸入標題'
    return
  }

  isUploading.value = true
  try {
    await uploadDocument(selectedFile.value, title.value, description.value, category.value)
    alert('上傳成功！')
    router.push('/documents')
  } catch (error) {
    console.error('上傳失敗', error)
    errorMessage.value = '上傳失敗，請稍後再試'
  } finally {
    isUploading.value = false
  }
}
</script>

<template>
  <div>
    <h2>上傳文件</h2>

    <div>
      <label>選擇檔案：</label>
      <input type="file" @change="handleFileChange" />
    </div>

    <div>
      <label>標題：</label>
      <input v-model="title" placeholder="文件標題" />
    </div>

    <div>
      <label>描述：</label>
      <input v-model="description" placeholder="文件描述（選填）" />
    </div>

    <div>
      <label>分類：</label>
      <input v-model="category" placeholder="文件分類（選填）" />
    </div>

    <button @click="handleUpload" :disabled="isUploading">
      {{ isUploading ? '上傳中...' : '上傳' }}
    </button>

    <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  </div>
</template>