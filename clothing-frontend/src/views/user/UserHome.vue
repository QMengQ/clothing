<template>
  <div class="container">
    <el-container class="layout">
      <Sidebar
        :username="username"
        :user-role="'普通用户'"
        :user-avatar="userAvatar"
        :menu-items="menuItems"
      />

      <el-container class="content">
        <el-main class="main-content">
          <Card
            title="欢迎使用智能衣物管理系统"
            class="welcome-card"
          >
            <div class="welcome-content">
              <el-result
                icon="success"
                title="登录成功"
                sub-title="您可以开始管理您的衣物了"
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
          
          <el-row :gutter="20" class="charts-container">
            <el-col :span="12">
              <Card title="衣物状态分布" class="chart-card">
                <div id="pieChart" class="chart"></div>
              </Card>
            </el-col>
            <el-col :span="12">
              <Card title="衣物数据统计" class="chart-card">
                <div id="barChart" class="chart"></div>
              </Card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="quick-actions">
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/clothing')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><ShoppingBag /></el-icon>
                  <h4>衣物管理</h4>
                  <p>添加和管理您的衣物</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/location')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><MapLocation /></el-icon>
                  <h4>收纳管理</h4>
                  <p>管理衣物的存放位置</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/recycle')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><Refresh /></el-icon>
                  <h4>衣物回收</h4>
                  <p>处理不需要的衣物</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/trade')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><ShoppingBag /></el-icon>
                  <h4>服装交易</h4>
                  <p>买卖和租赁服装</p>
                </div>
              </Card>
            </el-col>
          </el-row>
          <el-row :gutter="20" class="quick-actions" style="margin-top: 20px;">
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/orders')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><ShoppingBag /></el-icon>
                  <h4>订单管理</h4>
                  <p>查看和管理您的订单</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/messages')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><Message /></el-icon>
                  <h4>消息中心</h4>
                  <p>查看和发送消息</p>
                </div>
              </Card>
            </el-col>
            <el-col :span="6">
              <Card
                class="action-card"
                @click="navigateTo('/user/idle-alerts')"
              >
                <div class="action-content">
                  <el-icon class="action-icon"><Warning /></el-icon>
                  <h4>闲置预警</h4>
                  <p>查看闲置衣物提醒</p>
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
import { ref, onMounted, onActivated, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, ShoppingBag, MapLocation, Refresh, Message, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import request from '../../utils/request'

const router = useRouter()
const username = ref('用户')
const userAvatar = ref('')

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

const stats = ref([
  { label: '总衣物数', value: 0 },
  { label: '已收纳', value: 0 },
  { label: '待处理', value: 0 },
  { label: '回收数', value: 0 }
])

onMounted(() => {
  loadUserData()
  loadStats()
  // 延迟初始化图表，确保DOM元素已渲染
  setTimeout(() => {
    initCharts()
    // 添加窗口大小变化监听器
    window.addEventListener('resize', handleResize)
  }, 100)
})

// 当页面被激活时重新加载数据
onActivated(() => {
  loadStats()
  // 确保图表大小正确
  setTimeout(() => {
    handleResize()
  }, 100)
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '用户'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
}

const pieChart = ref(null)
const barChart = ref(null)

const initCharts = () => {
  // 初始化饼图
  pieChart.value = echarts.init(document.getElementById('pieChart'))
  // 初始化柱状图
  barChart.value = echarts.init(document.getElementById('barChart'))
  updateCharts()
}

const updateCharts = () => {
  if (!pieChart.value || !barChart.value) return

  // 准备饼图数据
  const pieData = [
    { value: stats.value.find(s => s.label === '已收纳').value, name: '已收纳' },
    { value: stats.value.find(s => s.label === '待处理').value, name: '待处理' },
    { value: stats.value.find(s => s.label === '回收数').value, name: '已回收' }
  ]

  // 饼图配置
  const pieOption = {
    title: {
      text: '衣物状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['已收纳', '待处理', '已回收']
    },
    series: [
      {
        name: '衣物状态',
        type: 'pie',
        radius: '50%',
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)'
        }
      }
    ],
    color: ['#409EFF', '#67C23A', '#E6A23C']
  }

  // 柱状图配置
  const barOption = {
    title: {
      text: '衣物数据统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: stats.value.map(s => s.label),
      axisLabel: {
        rotate: 0
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: stats.value.map(s => s.value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: '#409EFF'
            },
            {
              offset: 1,
              color: '#66B1FF'
            }
          ])
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }

  // 更新图表
  pieChart.value.setOption(pieOption)
  barChart.value.setOption(barOption)
}

const handleResize = () => {
  if (pieChart.value) pieChart.value.resize()
  if (barChart.value) barChart.value.resize()
}

const loadStats = async () => {
  try {
    // 尝试从衣物列表计算统计数据
    const clothingRes = await request.get('/clothing/my')
    let total = 0
    let stored = 0
    let pending = 0
    let recycled = 0
    
    if (Array.isArray(clothingRes.data)) {
      const clothingList = clothingRes.data
      total = clothingList.length
      stored = clothingList.filter(item => item.location).length
    }
    
    // 从回收API获取回收项数据
    try {
      const recycleRes = await request.get('/recycle/my')
      if (Array.isArray(recycleRes.data)) {
        const recycleList = recycleRes.data
        // 待处理：状态为"待审核"的回收项
        pending = recycleList.filter(item => item.status === '待审核').length
        // 回收数：状态为"已批准"的回收项
        recycled = recycleList.filter(item => item.status === '已批准').length
      }
    } catch (recycleError) {
      console.error('加载回收列表失败:', recycleError)
    }
    
    stats.value = [
      { label: '总衣物数', value: total },
      { label: '已收纳', value: stored },
      { label: '待处理', value: pending },
      { label: '回收数', value: recycled }
    ]
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用默认值
    stats.value = [
      { label: '总衣物数', value: 0 },
      { label: '已收纳', value: 0 },
      { label: '待处理', value: 0 },
      { label: '回收数', value: 0 }
    ]
  }
  // 更新图表
  updateCharts()
}

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.welcome-card {
  margin-bottom: 24px;
  
  .welcome-content {
    padding: 20px 0;
  }
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

.charts-container {
  margin-top: 24px;
}

.chart-card {
  height: 300px;
}

.chart {
  width: 100%;
  height: 250px;
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
  
  .charts-container {
    .el-col {
      :span {
        flex: 100%;
        max-width: 100%;
        margin-bottom: 20px;
      }
    }
  }
  
  .chart-card {
    height: 250px;
  }
  
  .chart {
    height: 200px;
  }
  
  .quick-actions {
    .el-col {
      :span {
        flex: 100%;
        max-width: 100%;
      }
    }
  }
}
</style>