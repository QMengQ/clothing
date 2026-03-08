<template>
  <div class="app-container">
    <el-config-provider :locale="zhCn">
      <el-header class="app-header">
        <div class="header-content">
          <el-avatar :size="40" :src="logoUrl" />
          <h1 class="app-title">智能衣物管理系统</h1>
          <div class="header-actions">
            <el-dropdown v-if="isLoggedIn">
              <span class="user-info">
                <el-avatar :size="32" :src="userAvatar" />
                <span class="username">{{ username }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>

      <el-footer class="app-footer">
        <p>© 2026 智能衣物管理系统. 保留所有权利.</p>
      </el-footer>
    </el-config-provider>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const isLoggedIn = ref(false)
const username = ref('')
const userAvatar = ref('')
const logoUrl = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20clothing%20management%20system%20logo%2C%20minimalist%2C%20blue%20color%20scheme&image_size=square'

onMounted(() => {
  checkLoginStatus()
})

const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('username')
  if (token) {
    isLoggedIn.value = true
    username.value = user || '用户'
    userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
  }
}

const handleProfile = () => {
  // 跳转到个人中心
  router.push('/user/profile')
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

  <style lang="scss">
@import './styles/global.scss';

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #fff;
  box-shadow: $box-shadow;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: $transition;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.app-title {
  font-size: 20px;
  font-weight: 600;
  color: $primary-color;
  margin: 0 0 0 16px;
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: $border-radius;
  transition: $transition;
  
  &:hover {
    background-color: $bg-color;
  }
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: $text-color-secondary;
}

.app-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 24px auto;
  padding: 0 24px;
}

.app-footer {
  background-color: #fff;
  padding: 20px 0;
  text-align: center;
  color: $text-color-light;
  font-size: 12px;
  margin-top: auto;
  border-top: 1px solid $border-color;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .app-title {
    font-size: 16px;
  }
  
  .app-main {
    padding: 0 16px;
    margin: 16px auto;
  }
  
  .username {
    display: none;
  }
}
</style>