<template>
  <div class="idle-alerts-container">
    <el-container class="user-layout">
      <Sidebar 
        :username="username"
        :user-role="userRole"
        :user-avatar="userAvatar"
        :menu-items="menuItems"
        @logout="logout"
      />

      <el-container class="user-content">
        <el-main class="main-content">
          <Card 
            title="闲置衣物预警"
            class="page-header"
          >
            <div class="header-content">
              <h2>闲置衣物预警管理</h2>
              <p>查看和处理超过六个月未穿着的衣物</p>
            </div>
          </Card>

          <!-- 统计卡片 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :span="8">
              <Card class="stat-card">
                <div class="stat-content">
                  <h3 class="stat-title">总衣物数</h3>
                  <p class="stat-value">{{ stats.totalClothing }}</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="8">
              <Card class="stat-card warning-card">
                <div class="stat-content">
                  <h3 class="stat-title">闲置衣物数</h3>
                  <p class="stat-value">{{ stats.idleClothing }}</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="8">
              <Card class="stat-card">
                <div class="stat-content">
                  <h3 class="stat-title">闲置率</h3>
                  <p class="stat-value">{{ idleRate }}%</p>
                </div>
              </Card>
            </el-col>
          </el-row>

          <!-- 分类统计图表 -->
          <Card class="chart-card">
            <template #header>
              <span>闲置衣物分类统计</span>
            </template>
            <div ref="categoryChartRef" class="chart-container"></div>
          </Card>

          <!-- 预警列表 -->
          <Card class="alerts-card">
            <template #header>
              <div class="card-header">
                <span>待处理预警 ({{ pendingAlerts.length }})</span>
                <el-button type="primary" @click="scanIdleClothing">
                  <el-icon><Refresh /></el-icon>
                  扫描闲置衣物
                </el-button>
              </div>
            </template>

            <div v-if="loading" class="loading-container">
              <el-spinner size="large" />
              <p>加载中...</p>
            </div>

            <div v-else-if="pendingAlerts.length === 0" class="empty-container">
              <el-empty description="暂无闲置衣物预警" />
            </div>

            <div v-else class="alerts-list">
              <el-card 
                v-for="alert in pendingAlerts" 
                :key="alert.id" 
                class="alert-item"
                shadow="hover"
              >
                <div class="alert-content">
                  <div class="alert-header">
                    <h3 class="alert-title">{{ alert.clothingName }}</h3>
                    <span class="alert-badge warning-badge">闲置 {{ alert.idleDays }} 天</span>
                  </div>
                  <div class="alert-meta">
                    <span class="meta-item"><strong>类别:</strong> {{ alert.clothingCategory }}</span>
                    <span class="meta-item"><strong>最后穿着:</strong> {{ formatDate(alert.lastWearDate) }}</span>
                    <span v-if="alert.exceptionRule" class="meta-item"><strong>例外规则:</strong> {{ alert.exceptionRule }}</span>
                  </div>
                  <div class="alert-actions">
                    <el-button type="primary" @click="viewClothing(alert.clothingId)">
                      <el-icon><View /></el-icon>
                      查看详情
                    </el-button>
                    <el-button type="success" @click="markAsProcessed(alert.id)">
                      <el-icon><Check /></el-icon>
                      标记已处理
                    </el-button>
                    <el-button type="info" @click="markAsDismissed(alert.id)">
                      <el-icon><CircleClose /></el-icon>
                      忽略
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </Card>

          <!-- 历史预警 -->
          <Card class="history-card">
            <template #header>
              <span>历史预警</span>
            </template>

            <div v-if="historyAlerts.length === 0" class="empty-container">
              <el-empty description="暂无历史预警" />
            </div>

            <div v-else class="history-list">
              <el-card 
                v-for="alert in historyAlerts" 
                :key="alert.id" 
                class="history-item"
                shadow="hover"
              >
                <div class="alert-content">
                  <div class="alert-header">
                    <h3 class="alert-title">{{ alert.clothingName }}</h3>
                    <span :class="['alert-badge', alert.status === 'processed' ? 'success-badge' : 'info-badge']">
                      {{ alert.status === 'processed' ? '已处理' : '已忽略' }}
                    </span>
                  </div>
                  <div class="alert-meta">
                    <span class="meta-item"><strong>类别:</strong> {{ alert.clothingCategory }}</span>
                    <span class="meta-item"><strong>最后穿着:</strong> {{ formatDate(alert.lastWearDate) }}</span>
                    <span class="meta-item"><strong>预警日期:</strong> {{ formatDate(alert.alertDate) }}</span>
                  </div>
                  <div class="alert-actions">
                    <el-button type="primary" @click="viewClothing(alert.clothingId)">
                      <el-icon><View /></el-icon>
                      查看详情
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </Card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, View, Check, CircleClose, House, ShoppingBag, MapLocation, Message, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import request from '../../utils/request'

const router = useRouter()
const username = ref('用户')
const userRole = ref('普通用户')
const userAvatar = ref('')

// 图表引用
const categoryChartRef = ref(null)
let categoryChart = null

const menuItems = [
  { index: '/user/home', icon: 'House', title: '首页' },
  { index: '/user/clothing', icon: 'ShoppingBag', title: '衣物管理' },
  { index: '/user/location', icon: 'MapLocation', title: '收纳管理' },
  { index: '/user/recycle', icon: 'Refresh', title: '衣物回收' },
  { index: '/user/trade', icon: 'ShoppingBag', title: '服装交易' },
  { index: '/user/orders', icon: 'ShoppingBag', title: '订单管理' },
  { index: '/user/messages', icon: 'Message', title: '消息中心' },
  { index: '/user/idle-alerts', icon: 'Warning', title: '闲置预警' }
]

// 预警数据
const loading = ref(false)
const pendingAlerts = ref([])
const historyAlerts = ref([])
const stats = ref({ totalClothing: 0, idleClothing: 0, categoryStats: [] })

// 计算闲置率
const idleRate = computed(() => {
  if (stats.value.totalClothing === 0) return 0
  return Math.round((stats.value.idleClothing / stats.value.totalClothing) * 100)
})

onMounted(() => {
  loadUserData()
  loadAlerts()
  loadStats()
  // 初始化图表
  setTimeout(() => {
    initCategoryChart()
  }, 100)
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  const role = localStorage.getItem('role')
  username.value = user || '用户'
  userRole.value = role === 'ADMIN' ? '系统管理员' : '普通用户'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
}

const loadAlerts = async () => {
  loading.value = true
  try {
    // 获取所有预警
    const response = await request.get('/api/v1/idle-alerts/all')
    const allAlerts = response.data || []
    
    // 分离待处理和历史预警
    pendingAlerts.value = allAlerts.filter(alert => alert.status === 'pending')
    historyAlerts.value = allAlerts.filter(alert => alert.status !== 'pending')
  } catch (error) {
    console.error('加载预警失败:', error)
    ElMessage.error('加载预警失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await request.get('/api/v1/idle-alerts/stats')
    stats.value = response.data || { totalClothing: 0, idleClothing: 0, categoryStats: [] }
    // 更新图表
    updateCategoryChart()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const scanIdleClothing = async () => {
  try {
    await request.post('/api/v1/idle-alerts/scan')
    ElMessage.success('扫描完成')
    // 重新加载数据
    loadAlerts()
    loadStats()
  } catch (error) {
    console.error('扫描失败:', error)
    ElMessage.error('扫描失败')
  }
}

const markAsProcessed = async (alertId) => {
  try {
    await request.put(`/api/v1/idle-alerts/${alertId}/process`)
    ElMessage.success('预警已标记为已处理')
    loadAlerts()
    loadStats()
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

const markAsDismissed = async (alertId) => {
  try {
    await request.put(`/api/v1/idle-alerts/${alertId}/dismiss`)
    ElMessage.success('预警已标记为已忽略')
    loadAlerts()
    loadStats()
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

const viewClothing = (clothingId) => {
  // 跳转到衣物管理页面
  ElMessage.info('请在衣物管理页面查看该衣物详情')
  router.push('/user/clothing')
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const initCategoryChart = () => {
  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
    updateCategoryChart()
    
    // 监听窗口大小变化
    window.addEventListener('resize', handleResize)
  }
}

const updateCategoryChart = () => {
  if (!categoryChart) return
  
  const categoryData = stats.value.categoryStats || []
  const categories = categoryData.map(item => item.category)
  const values = categoryData.map(item => item.count)
  
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
        name: '闲置衣物',
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
  
  categoryChart.setOption(option)
}

const handleResize = () => {
  if (categoryChart) {
    categoryChart.resize()
  }
}

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 24px;
  
  .header-content {
    text-align: center;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px;
    }
    
    p {
      font-size: 16px;
      color: #606266;
      margin: 0;
    }
  }
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
  
  &.warning-card {
    border-left: 4px solid #E6A23C;
  }
  
  .stat-content {
    text-align: center;
    
    .stat-title {
      font-size: 16px;
      color: #606266;
      margin: 0 0 8px;
    }
    
    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
}

.chart-card {
  margin-bottom: 24px;
  height: 350px;
  
  .chart-container {
    width: 100%;
    height: 300px;
  }
}

.alerts-card,
.history-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.loading-container {
  gap: 16px;
  
  p {
    color: #606266;
    margin: 0;
  }
}

.alerts-list,
.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.alert-item,
.history-item {
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
}

.alert-content {
  .alert-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    .alert-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
    
    .alert-badge {
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 600;
      
      &.warning-badge {
        background-color: #FDF6EC;
        color: #E6A23C;
      }
      
      &.success-badge {
        background-color: #F0F9EB;
        color: #67C23A;
      }
      
      &.info-badge {
        background-color: #ECF5FF;
        color: #409EFF;
      }
    }
  }
  
  .alert-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;
    
    .meta-item {
      font-size: 14px;
      color: #606266;
      background-color: #f9fafc;
      padding: 4px 8px;
      border-radius: 4px;
    }
  }
  
  .alert-actions {
    display: flex;
    gap: 8px;
    
    .el-button {
      flex: 1;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row {
    flex-direction: column;
    gap: 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .alert-actions {
    flex-direction: column;
  }
}
</style>