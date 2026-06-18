import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Documents from '../views/Documents.vue'
import Upload from '../views/Upload.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/', component: Home },
  { path: '/documents', component: Documents },
  { path: '/upload', component: Upload }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router