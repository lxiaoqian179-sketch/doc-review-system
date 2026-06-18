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

// Navigation Guard（導航守衛）：每次切換路由前都會先跑這段
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 這個頁面不需要登入（例如登入頁本身），直接放行
  if (!to.meta.requiresAuth) {
    // 如果已經登入了又想去登入頁，直接導回首頁
    if (token) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 需要登入但沒有 token，導回登入頁
  if (!token) {
    next('/login')
    return
  }

  // 需要特定角色
  if (to.meta.roles && !to.meta.roles.includes(role)) {
    alert(`此頁面需要 ${to.meta.roles.join(' 或 ')} 權限`)
    next('/')
    return
  }

  // 全部通過，放行
  next()
})

export default router