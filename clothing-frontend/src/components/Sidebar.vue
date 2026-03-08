<template>
  <el-aside :width="width" class="sidebar">
    <div class="sidebar-header">
      <el-avatar :size="48" :src="userAvatar" />
      <div class="user-info">
        <h3 class="user-name">{{ username }}</h3>
        <p class="user-role">{{ userRole }}</p>
      </div>
    </div>
    
    <el-menu
      router
      :default-active="activeMenu"
      class="menu"
      background-color="#fff"
      text-color="#606266"
      active-text-color="#409EFF"
      :unique-opened="true"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.index"
        :index="item.index"
      >
        <el-icon>
          <component :is="item.icon" />
        </el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </el-menu>
    
    <div class="sidebar-footer">
      <el-button
        type="danger"
        plain
        @click="logout"
        class="logout-button"
        size="small"
      >
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'

const props = defineProps({
  username: {
    type: String,
    default: '用户'
  },
  userRole: {
    type: String,
    default: '普通用户'
  },
  userAvatar: {
    type: String,
    default: ''
  },
  menuItems: {
    type: Array,
    required: true
  },
  width: {
    type: String,
    default: '240px'
  }
})

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path
})

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
// 样式已经在全局样式中定义
</style>