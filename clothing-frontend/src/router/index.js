import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserHome from '../views/user/UserHome.vue'
import AdminHome from '../views/admin/AdminHome.vue'
import Clothing from '../views/user/Clothing.vue'
import Location from '../views/user/Location.vue'
import Recycle from '../views/user/Recycle.vue'
import Trade from '../views/user/Trade.vue'
import AllClothing from '../views/admin/AllClothing.vue'
import ReviewClothing from '../views/admin/ReviewClothing.vue'
import UserManage from '../views/admin/UserManage.vue'


const routes = [
  { path: '/', component: Login },
  { path: '/register', component: Register },
  { path: '/user/home', component: UserHome },
  { path: '/admin/home', component: AdminHome },
  { path: '/user/clothing', component: Clothing },
  { path: '/user/location', component: Location },
  { path: '/user/recycle', component: Recycle },
  { path: '/user/trade', component: Trade },
  { path: '/admin/allClothing', component: AllClothing },
  { path: '/admin/review', component: ReviewClothing },
  { path: '/admin/users', component: UserManage },
  
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {

  const token = localStorage.getItem("token")
  const role = localStorage.getItem("role")

  if (to.path === '/'|| to.path === '/register') {
    next()
    return
  }

  if (!token) {
    next('/')
    return
  }

  if (to.path.startsWith('/admin') && role !== 'ADMIN') {
    next('/user/home')
    return
  }

  next()
})

export default router