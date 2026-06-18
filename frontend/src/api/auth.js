import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export async function login(username, password) {
  const response = await axios.post(`${API_BASE_URL}/auth/login`, {
    username,
    password
  })
  return response.data
}