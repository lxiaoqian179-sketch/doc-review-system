import axios from 'axios'
import { useAuthStore } from '../store/auth.js'

const API_BASE_URL = 'http://localhost:8080/api'

export async function getMyDocuments() {
  const authStore = useAuthStore()
  const response = await axios.get(`${API_BASE_URL}/documents`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function uploadDocument(file, title, description, category) {
  const authStore = useAuthStore()
  const formData = new FormData()
  formData.append('file', file)
  formData.append('title', title)
  if (description) formData.append('description', description)
  if (category) formData.append('category', category)

  const response = await axios.post(`${API_BASE_URL}/documents/upload`, formData, {
    headers: {
      Authorization: `Bearer ${authStore.token}`,
      'Content-Type': 'multipart/form-data'
    }
  })
  return response.data
}

export async function getPendingDocuments() {
  const authStore = useAuthStore()
  const response = await axios.get(`${API_BASE_URL}/reviews/pending`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function approveDocument(docId) {
  const authStore = useAuthStore()
  const response = await axios.post(`${API_BASE_URL}/reviews/${docId}/approve`, null, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function rejectDocument(docId, comment) {
  const authStore = useAuthStore()
  const response = await axios.post(`${API_BASE_URL}/reviews/${docId}/reject`, { comment }, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function getStatsOverview() {
  const authStore = useAuthStore()
  const response = await axios.get(`${API_BASE_URL}/stats/overview`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function getStatsKeywords() {
  const authStore = useAuthStore()
  const response = await axios.get(`${API_BASE_URL}/stats/keywords`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}

export async function getStatsTrend() {
  const authStore = useAuthStore()
  const response = await axios.get(`${API_BASE_URL}/stats/trend`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  return response.data
}