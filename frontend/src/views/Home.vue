<script setup>
import { useAuthStore } from '../store/auth.js'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div>
    <h2>首頁</h2>
    <p>歡迎，{{ authStore.username }}！你的角色是 {{ authStore.role }}</p>
    <button @click="handleLogout">登出</button>
  </div>
</template>