<script setup>
import { ref, onMounted } from 'vue'
import { getMyDocuments } from '../api/document.js'

const documents = ref([])
const isLoading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    documents.value = await getMyDocuments()
  } catch (error) {
    console.error('取得文件列表失敗', error)
    errorMessage.value = '無法取得文件列表'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div>
    <h2>我的文件</h2>

    <p v-if="isLoading">載入中...</p>
    <p v-else-if="errorMessage" style="color: red">{{ errorMessage }}</p>

    <table v-else border="1" cellpadding="8">
      <thead>
        <tr>
          <th>標題</th>
          <th>分類</th>
          <th>狀態</th>
          <th>上傳時間</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="doc in documents" :key="doc.id">
          <td>{{ doc.title }}</td>
          <td>{{ doc.category }}</td>
          <td>{{ doc.status }}</td>
          <td>{{ doc.createdAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>