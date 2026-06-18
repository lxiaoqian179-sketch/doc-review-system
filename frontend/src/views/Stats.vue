<script setup>
import { ref, onMounted } from 'vue'
import { Pie, Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title, Tooltip, Legend,
  ArcElement, CategoryScale, LinearScale, BarElement
} from 'chart.js'
import { getStatsOverview, getStatsKeywords, getStatsTrend } from '../api/document.js'

ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale, LinearScale, BarElement)

const isLoading = ref(true)
const errorMessage = ref('')

const overviewData = ref(null)
const keywordsData = ref(null)
const trendData = ref(null)

onMounted(async () => {
  try {
    const [overview, keywords, trend] = await Promise.all([
      getStatsOverview(),
      getStatsKeywords(),
      getStatsTrend()
    ])

    overviewData.value = {
      labels: ['待審核', '已核准', '已退回'],
      datasets: [{
        data: [overview.pending, overview.approved, overview.rejected],
        backgroundColor: ['#facc15', '#4ade80', '#f87171']
      }]
    }

    keywordsData.value = {
      labels: keywords.map(k => k.word),
      datasets: [{
        label: '出現次數',
        data: keywords.map(k => k.count),
        backgroundColor: '#60a5fa'
      }]
    }

    trendData.value = {
      labels: trend.map(t => t.date),
      datasets: [{
        label: '每日上傳數',
        data: trend.map(t => t.count),
        backgroundColor: '#a78bfa'
      }]
    }
  } catch (error) {
    console.error('取得統計資料失敗', error)
    errorMessage.value = '無法取得統計資料，請確認你有 ADMIN 權限'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div>
    <h2>統計儀表板</h2>

    <p v-if="isLoading">載入中...</p>
    <p v-else-if="errorMessage" style="color: red">{{ errorMessage }}</p>

    <div v-else>
      <h3>文件狀態總覽</h3>
      <div style="max-width: 400px">
        <Pie :data="overviewData" />
      </div>

      <h3>標題關鍵字 Top 20</h3>
      <div style="max-width: 600px">
        <Bar :data="keywordsData" />
      </div>

      <h3>近 30 天上傳趨勢</h3>
      <div style="max-width: 600px">
        <Bar :data="trendData" />
      </div>
    </div>
  </div>
</template>