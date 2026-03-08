<template>
  <div class="stats-container">
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
            title="系统数据统计" 
            icon="DataAnalysis"
          >
            <el-row :gutter="20" class="stats-row">
              <el-col :span="8">
                <Card class="stat-card">
                  <div class="stat-content">
                    <h3>用户总数</h3>
                    <p class="stat-value">{{ stats.userCount || 0 }}</p>
                  </div>
                </Card>
              </el-col>
              <el-col :span="8">
                <Card class="stat-card">
                  <div class="stat-content">
                    <h3>衣物总数</h3>
                    <p class="stat-value">{{ stats.clothingCount || 0 }}</p>
                  </div>
                </Card>
              </el-col>
              <el-col :span="8">
                <Card class="stat-card">
                  <div class="stat-content">
                    <h3>回收总数</h3>
                    <p class="stat-value">{{ stats.recycleCount || 0 }}</p>
                  </div>
                </Card>
              </el-col>
            </el-row>

            <Card class="chart-card" style="margin-top: 24px;">
              <div ref="typeChart" style="height:400px"></div>
            </Card>

            <Card class="chart-card" style="margin-top: 24px;">
              <div ref="recycleChart" style="height:400px"></div>
            </Card>
          </Card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import * as echarts from 'echarts'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'

const router = useRouter()
const route = useRoute()
const username = ref('管理员')
const userAvatar = ref('')
const stats = ref({})
const typeChart = ref(null)
const recycleChart = ref(null)

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/clothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/stats', icon: 'DataAnalysis', title: '系统统计' }
]

onMounted(async () => {
  loadUserData()
  
  try {
    const s = await request.get('/admin/stats')
    stats.value = s.data

    // 衣物类型统计
    const typeRes = await request.get('/admin/typeStats')

    const typeInstance = echarts.init(typeChart.value)

    typeInstance.setOption({
      title: { text: '衣物类型分布' },
      tooltip: {},
      series: [
        {
          type: 'pie',
          data: Object.entries(typeRes.data).map(([key, value]) => ({
            name: key,
            value: value
          }))
        }
      ]
    })

    // 回收状态统计
    const recycleRes = await request.get('/admin/recycleStats')

    const recycleInstance = echarts.init(recycleChart.value)

    recycleInstance.setOption({
      title: { text: '回收状态统计' },
      xAxis: {
        type: 'category',
        data: Object.keys(recycleRes.data)
      },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: Object.values(recycleRes.data)
        }
      ]
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.stats-container {
  min-height: 100vh;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  text-align: center;
}

.stat-content h3 {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin: 0 0 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
  margin: 0;
}

.chart-card {
  padding: 20px;
}
</style>