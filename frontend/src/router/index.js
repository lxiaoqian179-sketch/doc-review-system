import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Documents from '../views/Documents.vue'
import Upload from '../views/Upload.vue'
import Review from '../views/Review.vue'
import Stats from '../views/Stats.vue'

const routes = [
  {
    path: '/login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/documents',
    component: Documents,
    meta: { requiresAuth: true }
  },
  {
    path: '/upload',
    component: Upload,
    meta: { requiresAuth: true }
  },
  {
    path: '/review',
    component: Review,
    meta: { requiresAuth: true, roles: ['REVIEWER', 'ADMIN'] }
  },
  {
    path: '/stats',
    component: Stats,
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 不需要登入的頁面（例如登入頁）
  if (!to.meta.requiresAuth) {
    // 已登入又想去登入頁，導回首頁
    if (token) return '/'
    return true
  }

  // 需要登入但沒有 token
  if (!token) return '/login'

  // 需要特定角色但角色不符
  if (to.meta.roles && !to.meta.roles.includes(role)) {
    alert(`此頁面需要 ${to.meta.roles.join(' 或 ')} 權限`)
    return '/'
  }

  // 全部通過，放行
  return true
})

export default router