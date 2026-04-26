<template>
  <div class="orders-container">
    <Sidebar 
      :username="username" 
      :user-role="userRole" 
      :user-avatar="userAvatar" 
      :menu-items="menuItems" 
    />
    
    <div class="orders-content">
      <div class="orders-header">
        <h2 class="page-title">订单管理</h2>
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter="loadOrders"
        />
      </div>
      
      <div class="orders-body">
        <div class="filter-section">
          <el-radio-group v-model="activeStatus" @change="loadOrders">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="pending">待付款</el-radio-button>
            <el-radio-button label="paid">已付款</el-radio-button>
            <el-radio-button label="completed">已完成</el-radio-button>
            <el-radio-button label="cancelled">已取消</el-radio-button>
          </el-radio-group>
        </div>
        
        <div class="orders-list">
          <el-card 
            v-for="order in orders" 
            :key="order.id"
            class="order-card"
          >
            <template #header>
              <div class="order-header">
                <span class="order-id">订单号: {{ order.id }}</span>
                <span 
                  class="order-status" 
                  :class="`status-${order.status}`"
                >
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </template>
            
            <div class="order-content">
              <div class="order-info">
                <div class="clothing-info">
                  <div class="clothing-image" v-if="order.clothing.image">
                    <el-image :src="order.clothing.image" fit="cover" :preview-src-list="[order.clothing.image]"></el-image>
                  </div>
                  <div class="clothing-details">
                    <h3 class="clothing-title">{{ order.clothing.title }}</h3>
                    <p class="clothing-description">{{ order.clothing.description }}</p>
                    <div class="clothing-meta">
                      <span class="meta-item">类别: {{ order.clothing.category }}</span>
                      <span class="meta-item">尺寸: {{ order.clothing.size }}</span>
                      <span class="meta-item">状况: {{ order.clothing.condition }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="order-details">
                  <div class="detail-item">
                    <span class="label">交易类型:</span>
                    <span class="value">{{ order.tradeType === 'purchase' ? '购买' : '租赁' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">价格:</span>
                    <span class="value price">{{ order.price }} 元</span>
                  </div>
                  <div class="detail-item" v-if="order.tradeType === 'rental'">
                    <span class="label">租赁期限:</span>
                    <span class="value">{{ formatDate(order.rentalStartDate) }} 至 {{ formatDate(order.rentalEndDate) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">支付方式:</span>
                    <span class="value">{{ getPaymentMethodText(order.paymentMethod) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">创建时间:</span>
                    <span class="value">{{ formatDate(order.createTime) }}</span>
                  </div>
                </div>
              </div>
              
              <div class="order-address">
                <h4 class="address-title">收货地址</h4>
                <p class="address-content">
                  {{ order.address.recipient }} {{ order.address.phone }}<br>
                  {{ order.address.province }}{{ order.address.city }}{{ order.address.district }}{{ order.address.detailAddress }}
                </p>
              </div>
         
              
              <div class="order-actions">
          <el-button 
            v-if="order.status === 'pending' && String(order.buyerId) === String(userId)"
            type="primary" 
            @click="payOrder(order)"
          >
            立即支付
          </el-button>
          <el-button 
            v-if="(order.status === 'pending' || order.status === 'paid') && String(order.buyerId) === String(userId)"
            type="warning" 
            @click="cancelOrder(order)"
          >
            取消订单
          </el-button>
          <el-button 
            v-if="order.status === 'paid' && String(order.sellerId) === String(userId)"
            type="success" 
            @click="shipOrder(order)"
          >
            发货
          </el-button>
          <el-button 
            v-if="order.status === 'shipped' && String(order.buyerId) === String(userId)"
            type="primary" 
            @click="confirmReceipt(order)"
          >
            确认收货
          </el-button>
          <el-button 
            type="info" 
            @click="viewOrderDetails(order)"
          >
            查看详情
          </el-button>
        </div>
            </div>
          </el-card>
          
          <div v-if="loading" class="loading-container">
            <el-icon :size="30" class="is-loading">
              <Refresh />
            </el-icon>
            <p>加载订单中...</p>
          </div>
          
          <div v-if="!loading && orders.length === 0" class="empty-state">
            <el-empty description="暂无订单" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-section">
          <h3>订单信息</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">订单号:</span>
              <span class="value">{{ selectedOrder.id }}</span>
            </div>
            <div class="detail-item">
              <span class="label">订单状态:</span>
              <span 
                class="value status-badge" 
                :class="`status-${selectedOrder.status}`"
              >
                {{ getStatusText(selectedOrder.status) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="label">交易类型:</span>
              <span class="value">{{ selectedOrder.tradeType === 'purchase' ? '购买' : '租赁' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">价格:</span>
              <span class="value price">{{ selectedOrder.price }} 元</span>
            </div>
            <div class="detail-item" v-if="selectedOrder.tradeType === 'rental'">
              <span class="label">租赁期限:</span>
              <span class="value">{{ formatDate(selectedOrder.rentalStartDate) }} 至 {{ formatDate(selectedOrder.rentalEndDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">支付方式:</span>
              <span class="value">{{ getPaymentMethodText(selectedOrder.paymentMethod) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDate(selectedOrder.createTime) }}</span>
            </div>
            <div class="detail-item" v-if="selectedOrder.paymentTime">
              <span class="label">支付时间:</span>
              <span class="value">{{ formatDate(selectedOrder.paymentTime) }}</span>
            </div>
            <div class="detail-item" v-if="selectedOrder.shippingTime">
              <span class="label">发货时间:</span>
              <span class="value">{{ formatDate(selectedOrder.shippingTime) }}</span>
            </div>
            <div class="detail-item" v-if="selectedOrder.completionTime">
              <span class="label">完成时间:</span>
              <span class="value">{{ formatDate(selectedOrder.completionTime) }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>商品信息</h3>
          <div class="clothing-info">
            <div class="clothing-image" v-if="selectedOrder.clothing.image">
              <el-image :src="selectedOrder.clothing.image" fit="cover" :preview-src-list="[selectedOrder.clothing.image]"></el-image>
            </div>
            <div class="clothing-details">
              <h4 class="clothing-title">{{ selectedOrder.clothing.title }}</h4>
              <p class="clothing-description">{{ selectedOrder.clothing.description }}</p>
              <div class="clothing-meta">
                <span class="meta-item">类别: {{ selectedOrder.clothing.category }}</span>
                <span class="meta-item">尺寸: {{ selectedOrder.clothing.size }}</span>
                <span class="meta-item">状况: {{ selectedOrder.clothing.condition }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>收货地址</h3>
          <div class="address-info">
            <p class="address-content">
              {{ selectedOrder.address.recipient }} {{ selectedOrder.address.phone }}<br>
              {{ selectedOrder.address.province }}{{ selectedOrder.address.city }}{{ selectedOrder.address.district }}{{ selectedOrder.address.detailAddress }}
            </p>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedOrder.logistics && selectedOrder.logistics.length > 0">
          <h3>物流信息</h3>
          <div class="logistics-list">
            <div 
              v-for="(log, index) in selectedOrder.logistics" 
              :key="index"
              class="logistics-item"
              :class="{ 'latest': index === 0 }"
            >
              <div class="logistics-time">{{ formatDate(log.time) }}</div>
              <div class="logistics-content">{{ log.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 支付对话框 -->
    <PaymentDialog
      v-model:visible="paymentDialogVisible"
      :order="currentOrder"
      @payment-success="handlePaymentSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElEmpty } from 'element-plus'
import { House, ShoppingBag, MapLocation, Refresh, ShoppingBag as ShoppingBagIcon, Message, Warning } from '@element-plus/icons-vue'
import Sidebar from '../../components/Sidebar.vue'
import PaymentDialog from '../../components/PaymentDialog.vue'
import request from '../../utils/request'

const router = useRouter()

// 用户信息
const username = ref('用户')
const userRole = ref('普通用户')
const userAvatar = ref('')
const userId = ref(null)

// 订单数据
const orders = ref([])
const loading = ref(false)
const activeStatus = ref('all')
const searchQuery = ref('')

// 订单详情
const orderDetailDialogVisible = ref(false)
const selectedOrder = ref(null)

// 支付对话框
const paymentDialogVisible = ref(false)
const currentOrder = ref(null)

// 菜单
const menuItems = [
  { index: '/user/home', icon: 'House', title: '首页' },
  { index: '/user/clothing', icon: 'ShoppingBag', title: '衣物管理' },
  { index: '/user/location', icon: 'MapLocation', title: '收纳管理' },
  { index: '/user/recycle', icon: 'Refresh', title: '衣物回收' },
  { index: '/user/trade', icon: 'ShoppingBag', title: '服装交易' },
  { index: '/user/orders', icon: 'ShoppingBagIcon', title: '订单管理' },
  { index: '/user/messages', icon: 'Message', title: '消息中心' },
  { index: '/user/idle-alerts', icon: 'Warning', title: '闲置预警' }
]

// 加载用户数据
const loadUserData = () => {
  const user = localStorage.getItem('username')
  const role = localStorage.getItem('role')
  const id = localStorage.getItem('userId')
  if (user) {
    username.value = user
    userRole.value = role === 'ADMIN' ? '管理员' : '普通用户'
    userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
  }
  if (id) {
    userId.value = parseInt(id)
  }
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.get('/api/v1/orders', {
      params: {
        status: activeStatus.value === 'all' ? '' : activeStatus.value,
        search: searchQuery.value
      }
    })

    const orderData = response.data || []
    console.log('订单数据:', orderData)
    console.log('当前用户ID:', userId.value)
    
    orders.value = orderData.map(order => ({
      ...order,
      clothing: order.clothing ? {
        ...order.clothing,
        image: order.clothing.image ,
        title: order.clothing.title ,
        description: order.clothing.description ,
        category: order.clothing.category ,
        condition: order.clothing.condition
      } : {

      }
    }))
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
    orders.value = []
  } finally {
    loading.value = false
  }
}

// 支付订单
const payOrder = (order) => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  currentOrder.value = order
  paymentDialogVisible.value = true
}

// 支付成功回调
const handlePaymentSuccess = () => {
  paymentDialogVisible.value = false
  currentOrder.value = null
  loadOrders()
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.put(`/api/v1/orders/${order.id}/cancel`)
    if (response.status === 200) {
      ElMessage.success('取消订单成功')
      loadOrders()
    } else {
      ElMessage.error('取消订单失败，请重试')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    ElMessage.error('取消订单失败，请重试')
  }
}

// 确认收货
const confirmReceipt = async (order) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.put(`/api/v1/orders/${order.id}/receive`)
    if (response.status === 200) {
      ElMessage.success('确认收货成功')
      loadOrders()
    } else {
      ElMessage.error('确认收货失败，请重试')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('确认收货失败，请重试')
  }
}

// 卖家发货
const shipOrder = async (order) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.put(`/api/v1/orders/${order.id}/ship`)
    if (response.status === 200) {
      ElMessage.success('发货成功')
      loadOrders()
    } else {
      ElMessage.error('发货失败，请重试')
    }
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败，请重试')
  }
}

// 查看订单详情
const viewOrderDetails = (order) => {
  selectedOrder.value = order
  orderDetailDialogVisible.value = true
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'paid': '已付款',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'creditcard': '信用卡'
  }
  return methodMap[method] || method
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserData()
  loadOrders()
})
</script>

<style lang="scss" scoped>
.orders-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.orders-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
  
  .search-input {
    width: 300px;
  }
}

.orders-body {
  .filter-section {
    margin-bottom: 24px;
  }
  
  .orders-list {
    .order-card {
      margin-bottom: 16px;
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .order-id {
          font-size: 14px;
          color: #606266;
        }
        
        .order-status {
          font-size: 14px;
          font-weight: 500;
          padding: 2px 8px;
          border-radius: 10px;
          
          &.status-pending {
            background-color: #fdf6ec;
            color: #e6a23c;
          }
          
          &.status-paid {
            background-color: #ecf5ff;
            color: #409eff;
          }
          
          &.status-shipped {
            background-color: #f0f9eb;
            color: #67c23a;
          }
          
          &.status-completed {
            background-color: #f0f0f0;
            color: #909399;
          }
          
          &.status-cancelled {
            background-color: #fef0f0;
            color: #f56c6c;
          }
        }
      }
      
      .order-content {
        .order-info {
          display: flex;
          margin-bottom: 16px;
          
          .clothing-info {
            flex: 1;
            display: flex;
            gap: 16px;
            
            .clothing-image {
              width: 100px;
              height: 100px;
              flex-shrink: 0;
              
              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                border-radius: 4px;
              }
            }
            
            .clothing-details {
              flex: 1;
              
              .clothing-title {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
                margin: 0 0 8px 0;
              }
              
              .clothing-description {
                font-size: 14px;
                color: #606266;
                margin: 0 0 8px 0;
                line-height: 1.4;
              }
              
              .clothing-meta {
                display: flex;
                gap: 16px;
                
                .meta-item {
                  font-size: 12px;
                  color: #909399;
                }
              }
            }
          }
          
          .order-details {
            width: 200px;
            
            .detail-item {
              display: flex;
              justify-content: space-between;
              margin-bottom: 8px;
              
              .label {
                font-size: 12px;
                color: #909399;
              }
              
              .value {
                font-size: 12px;
                color: #606266;
              }
              
              .value.price {
                font-size: 16px;
                font-weight: 500;
                color: #f56c6c;
              }
            }
          }
        }
        
        .order-address {
          margin-bottom: 16px;
          padding: 12px;
          background-color: #f5f7fa;
          border-radius: 4px;
          
          .address-title {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            margin: 0 0 8px 0;
          }
          
          .address-content {
            font-size: 12px;
            color: #606266;
            margin: 0;
            line-height: 1.4;
          }
        }
        
        .order-actions {
          display: flex;
          gap: 8px;
          justify-content: flex-end;
        }
      }
    }
    
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px 0;
      
      .is-loading {
        animation: spin 1s linear infinite;
      }
      
      p {
        margin-top: 16px;
        font-size: 14px;
        color: #606266;
      }
    }
    
    @keyframes spin {
      from {
        transform: rotate(0deg);
      }
      to {
        transform: rotate(360deg);
      }
    }
    
    .empty-state {
      padding: 48px 0;
    }
  }
}

.order-detail {
  .detail-section {
    margin-bottom: 24px;
    
    h3 {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin: 0 0 16px 0;
      padding-bottom: 8px;
      border-bottom: 1px solid #e4e7ed;
    }
  }
  
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    
    .detail-item {
      display: flex;
      justify-content: space-between;
      
      .label {
        font-size: 14px;
        color: #909399;
      }
      
      .value {
        font-size: 14px;
        color: #606266;
      }
      
      .value.price {
        font-size: 16px;
        font-weight: 500;
        color: #f56c6c;
      }
      
      .value.status-badge {
        padding: 2px 8px;
        border-radius: 10px;
        
        &.status-pending {
          background-color: #fdf6ec;
          color: #e6a23c;
        }
        
        &.status-paid {
          background-color: #ecf5ff;
          color: #409eff;
        }
        
        &.status-shipped {
          background-color: #f0f9eb;
          color: #67c23a;
        }
        
        &.status-completed {
          background-color: #f0f0f0;
          color: #909399;
        }
        
        &.status-cancelled {
          background-color: #fef0f0;
          color: #f56c6c;
        }
      }
    }
  }
  
  .clothing-info {
    display: flex;
    gap: 16px;
    
    .clothing-image {
      width: 150px;
      height: 150px;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 4px;
      }
    }
    
    .clothing-details {
      flex: 1;
      
      .clothing-title {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin: 0 0 8px 0;
      }
      
      .clothing-description {
        font-size: 14px;
        color: #606266;
        margin: 0 0 8px 0;
        line-height: 1.4;
      }
      
      .clothing-meta {
        display: flex;
        gap: 16px;
        
        .meta-item {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
  
  .address-info {
    padding: 12px;
    background-color: #f5f7fa;
    border-radius: 4px;
    
    .address-content {
      font-size: 14px;
      color: #606266;
      margin: 0;
      line-height: 1.4;
    }
  }
  
  .logistics-list {
    .logistics-item {
      display: flex;
      margin-bottom: 16px;
      
      &.latest {
        .logistics-time {
          font-weight: 500;
          color: #303133;
        }
        
        .logistics-content {
          font-weight: 500;
          color: #303133;
        }
      }
      
      .logistics-time {
        width: 120px;
        font-size: 12px;
        color: #909399;
      }
      
      .logistics-content {
        flex: 1;
        font-size: 14px;
        color: #606266;
        line-height: 1.4;
      }
    }
  }
}

@media (max-width: 768px) {
  .orders-content {
    padding: 16px;
  }
  
  .orders-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    
    .search-input {
      width: 100%;
    }
  }
  
  .orders-body {
    .orders-list {
      .order-card {
        .order-content {
          .order-info {
            flex-direction: column;
            
            .order-details {
              width: 100%;
              margin-top: 16px;
            }
          }
        }
      }
    }
  }
  
  .order-detail {
    .detail-grid {
      grid-template-columns: 1fr;
    }
    
    .clothing-info {
      flex-direction: column;
      
      .clothing-image {
        width: 100%;
        height: 200px;
        margin-right: 0;
        margin-bottom: 16px;
      }
    }
  }
}
</style>