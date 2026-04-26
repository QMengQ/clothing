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
          
          <!-- 图表统计区域 -->
          <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
              <Card title="衣物类型分布" class="chart-card">
                <div ref="typeChartRef" class="chart-container"></div>
              </Card>
            </el-col>
            <el-col :span="12">
              <Card title="审核状态分布" class="chart-card">
                <div ref="recycleChartRef" class="chart-container"></div>
              </Card>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" class="quick-actions">
            <el-col :span="8">
              <Card class="action-card" @click="navigateTo('/admin/users')">
                <div class="action-content">
                  <el-icon class="action-icon"><User /></el-icon>
                  <h4>用户管理</h4>
                  <p>管理系统用户</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="8">
              <Card class="action-card" @click="navigateTo('/admin/allClothing')">
                <div class="action-content">
                  <el-icon class="action-icon"><ShoppingBag /></el-icon>
                  <h4>衣物总览</h4>
                  <p>查看所有衣物</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="8">
              <Card class="action-card" @click="navigateTo('/admin/review')">
                <div class="action-content">
                  <el-icon class="action-icon"><DataAnalysis /></el-icon>
                  <h4>回收审核</h4>
                  <p>进行回收审核</p>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ShoppingBag, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()
const username = ref('管理员')
const userAvatar = ref('')

// 图表引用
const typeChartRef = ref(null)
const recycleChartRef = ref(null)
let typeChart = null
let recycleChart = null

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/allClothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/review', icon: 'View', title: '服装审核' },
  { index: '/admin/reviewRecycle', icon: 'DataAnalysis', title: '回收审核' }
]

const activeMenu = computed(() => {
  return route.path
})

const stats = ref([
  { label: '总用户数', value: 0 },
  { label: '总衣物数', value: 0 },
  { label: '回收衣物', value: 0 }
])

onMounted(() => {
  loadUserData()
  loadStats()
  // 初始化图表
  setTimeout(() => {
    initCharts()
  }, 100)
})

onUnmounted(() => {
  // 销毁图表
  if (typeChart) {
    typeChart.dispose()
  }
  if (recycleChart) {
    recycleChart.dispose()
  }
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const loadStats = () => {
  // 从API加载实际数据
  request.get('/admin/stats').then(res => {
    const data = res.data || {}
    stats.value = [
      { label: '总用户数', value: data.userCount || 0 },
      { label: '总衣物数', value: data.clothingCount || 0 },
      { label: '衣物审核', value: data.recycleCount || 0 }
    ]
  }).catch(error => {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  })
}

const initCharts = () => {
  // 初始化衣物类型分布图表
  if (typeChartRef.value) {
    typeChart = echarts.init(typeChartRef.value)
    loadTypeChartData()
  }
  
  // 初始化回收状态分布图表
  if (recycleChartRef.value) {
    recycleChart = echarts.init(recycleChartRef.value)
    loadRecycleChartData()
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

const loadTypeChartData = () => {
  // 从API加载衣物类型分布数据
  request.get('/admin/typeStats').then(res => {
    const typeData = res.data || {}
    const categories = Object.keys(typeData)
    const values = Object.values(typeData)
    
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '衣物类型',
          type: 'pie',
          radius: '60%',
          data: categories.map((category, index) => ({
            name: category,
            value: values[index]
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    typeChart.setOption(option)
  }).catch(error => {
    console.error('加载衣物类型数据失败:', error)
    // 使用模拟数据
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '衣物类型',
          type: 'pie',
          radius: '60%',
          data: [
            { name: '上衣', value: 10 },
            { name: '裤子', value: 8 },
            { name: '裙子', value: 5 },
            { name: '鞋帽', value: 3 },
            { name: '配饰', value: 4 }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    typeChart.setOption(option)
  })
}

const loadRecycleChartData = () => {
  // 从API加载回收状态分布数据
  request.get('/admin/recycleStats').then(res => {
    const recycleData = res.data || {}
    
    // 只保留待审核状态的数据
    const pendingCount = recycleData['pending'] || 0
    
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '审核状态',
          type: 'pie',
          radius: '60%',
          data: [
            { name: '待审核', value: pendingCount }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    recycleChart.setOption(option)
  }).catch(error => {
    console.error('加载回收状态数据失败:', error)
    // 使用模拟数据
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '审核状态',
          type: 'pie',
          radius: '60%',
          data: [
            { name: '待审核', value: 2 }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    recycleChart.setOption(option)
  })
}

const handleResize = () => {
  if (typeChart) {
    typeChart.resize()
  }
  if (recycleChart) {
    recycleChart.resize()
  }
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

.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 350px;
}

.chart-container {
  width: 100%;
  height: 300px;
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