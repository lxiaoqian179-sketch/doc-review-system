<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth.js'
import { useAuthStore } from '../store/auth.js'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const authStore = useAuthStore()
const router = useRouter()

async function handleSubmit() {
  errorMessage.value = ''
  try {
    const data = await login(username.value, password.value)
    authStore.setAuth(data.token, data.username, data.role)
    router.push('/')
  } catch (error) {
    console.error('登入失敗', error)
    errorMessage.value = '帳號或密碼錯誤'
  }
}
</script>

<template>
  <div>
    <h2>登入</h2>
    <input v-model="username" placeholder="帳號" />
    <input v-model="password" type="password" placeholder="密碼" />
    <button @click="handleSubmit">登入</button>
    <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  </div>
</template>