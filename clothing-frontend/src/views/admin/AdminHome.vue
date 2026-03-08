<template>
  <div class="admin-home-container">
    <el-container class="admin-layout">
      <Sidebar 
        :username="username"
        :user-role="'系统管理员'"
        :user-avatar="userAvatar"
        :menu-items="menuItems"
        @logout="logout"
      />

      <el-container class="admin-content">
        <el-main class="main-content">
          <Card 
            title="管理员后台"
            class="welcome-card"
          >
            <div class="welcome-content">
              <el-result
                icon="success"
                title="登录成功"
                sub-title="欢迎回来，管理员"
              >
                <template #extra>
                  <div class="stats-container">
                    <el-statistic
                      v-for="stat in stats"
                      :key="stat.label"
                      class="stat-item"
                      :title="stat.label"
                      :value="stat.value"
                      :value-style="{ color: '#409EFF' }"
                    />
                  </div>
                </template>
              </el-result>
            </div>
          </Card>
          
          <el-row :gutter="20" class="quick-actions">
            <el-col :span="6">
              <Card class="action-card" @click="navigateTo('/admin/users')">
                <div class="action-content">
                  <el-icon class="action-icon"><User /></el-icon>
                  <h4>用户管理</h4>
                  <p>管理系统用户</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card class="action-card" @click="navigateTo('/admin/clothing')">
                <div class="action-content">
                  <el-icon class="action-icon"><ShoppingBag /></el-icon>
                  <h4>衣物总览</h4>
                  <p>查看所有衣物</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card class="action-card" @click="navigateTo('/admin/stats')">
                <div class="action-content">
                  <el-icon class="action-icon"><DataAnalysis /></el-icon>
                  <h4>系统统计</h4>
                  <p>查看系统数据统计</p>
                </div>
              </Card>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ShoppingBag, DataAnalysis } from '@element-plus/icons-vue'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'

const router = useRouter()
const route = useRoute()
const username = ref('管理员')
const userAvatar = ref('')

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/clothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/stats', icon: 'DataAnalysis', title: '系统统计' }
]

const activeMenu = computed(() => {
  return route.path
})

const stats = ref([
  { label: '总用户数', value: 0 },
  { label: '总衣物数', value: 0 },
  { label: '收纳位置', value: 0 },
  { label: '回收衣物', value: 0 }
])

onMounted(() => {
  loadUserData()
  loadStats()
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const loadStats = () => {
  // 这里可以从API加载实际数据
  // 暂时使用模拟数据
}

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.welcome-card {
  margin-bottom: 24px;
}

.welcome-content {
  padding: 20px 0;
}

.stats-container {
  display: flex;
  gap: 24px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.stat-item {
  flex: 1;
  min-width: 120px;
  padding: 16px;
  background-color: #f9fafc;
  border-radius: 8px;
  text-align: center;
}

.quick-actions {
  margin-top: 24px;
}

.action-card {
  height: 160px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    border-color: #409EFF;
  }
}

.action-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  
  .action-icon {
    font-size: 32px;
    color: #409EFF;
    margin-bottom: 12px;
  }
  
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
  }
  
  p {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .stat-item {
    width: 100%;
  }
}
</style>