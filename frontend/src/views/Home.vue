<script setup>
import { useAuthStore } from '../store/auth.js'

const authStore = useAuthStore()
</script>

<template>
  <div>
    <h2>首頁</h2>
    <p>歡迎，{{ authStore.username }}！你的角色是 {{ authStore.role }}</p>
  </div>
</template>