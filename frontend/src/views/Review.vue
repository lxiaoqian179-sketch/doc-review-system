<script setup>
import { ref, onMounted } from 'vue'
import { getPendingDocuments, approveDocument, rejectDocument } from '../api/document.js'

const documents = ref([])
const isLoading = ref(true)
const errorMessage = ref('')

async function loadPending() {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const data = await getPendingDocuments()
    documents.value = data.content
  } catch (error) {
    console.error('取得待審清單失敗', error)
    errorMessage.value = '無法取得待審清單'
  } finally {
    isLoading.value = false
  }
}

async function handleApprove(docId) {
  try {
    await approveDocument(docId)
    await loadPending()
  } catch (error) {
    console.error('核准失敗', error)
    alert('核准失敗')
  }
}

async function handleReject(docId) {
  const comment = prompt('請輸入退回原因：')
  if (!comment) return

  try {
    await rejectDocument(docId, comment)
    await loadPending()
  } catch (error) {
    console.error('退回失敗', error)
    alert('退回失敗')
  }
}

onMounted(loadPending)
</script>

<template>
  <div>
    <h2>待審核文件</h2>

    <p v-if="isLoading">載入中...</p>
    <p v-else-if="errorMessage" style="color: red">{{ errorMessage }}</p>
    <p v-else-if="documents.length === 0">目前沒有待審核的文件</p>

    <table v-else border="1" cellpadding="8">
      <thead>
        <tr>
          <th>標題</th>
          <th>上傳者</th>
          <th>分類</th>
          <th>上傳時間</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="doc in documents" :key="doc.id">
          <td>{{ doc.title }}</td>
          <td>{{ doc.uploaderUsername }}</td>
          <td>{{ doc.category }}</td>
          <td>{{ doc.createdAt }}</td>
          <td>
            <button @click="handleApprove(doc.id)">核准</button>
            <button @click="handleReject(doc.id)">退回</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>