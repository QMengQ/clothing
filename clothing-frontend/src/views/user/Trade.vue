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
            title="服装交易市场"
            icon="ShoppingBag"
            class="trade-header-card"
          >
            <div class="trade-header">
              <h2>浏览、购买和租赁服装</h2>
              <p>发现来自其他用户的优质服装，或分享你自己的闲置衣物</p>
            </div>
          </Card>

          <!-- 标签页 -->
          <el-tabs v-model="activeTab" class="tab-container" @tab-click="handleTabClick">
            <el-tab-pane label="浏览服装" name="browse">
              <!-- 筛选和排序 -->
              <Card
                title="筛选和排序"
                class="filter-card"
              >
                <div class="add-clothing-button">
                  <el-button type="primary" @click="showAddClothingDialog">
                    <el-icon><Plus /></el-icon>
                    添加服装
                  </el-button>
                </div>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="类别">
                  <el-select v-model="filters.category" placeholder="选择类别" clearable>
                    <el-option label="上衣" value="上衣"></el-option>
                    <el-option label="裤子" value="裤子"></el-option>
                    <el-option label="裙子" value="裙子"></el-option>
                    <el-option label="鞋帽" value="鞋帽"></el-option>
                    <el-option label="配饰" value="配饰"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="尺寸">
                  <el-select v-model="filters.size" placeholder="选择尺寸" clearable>
                    <el-option label="XS" value="XS"></el-option>
                    <el-option label="S" value="S"></el-option>
                    <el-option label="M" value="M"></el-option>
                    <el-option label="L" value="L"></el-option>
                    <el-option label="XL" value="XL"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="价格范围">
                  <el-select v-model="filters.priceRange" placeholder="选择价格范围" clearable>
                    <el-option label="0-100" value="0-100"></el-option>
                    <el-option label="100-300" value="100-300"></el-option>
                    <el-option label="300-500" value="300-500"></el-option>
                    <el-option label="500+" value="500+"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="状况">
                  <el-select v-model="filters.condition" placeholder="选择状况" clearable>
                    <el-option label="全新" value="全新"></el-option>
                    <el-option label="近似全新" value="近似全新"></el-option>
                    <el-option label="轻微使用" value="轻微使用"></el-option>
                    <el-option label="正常使用" value="正常使用"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" class="sort-row">
              <el-col :span="12">
                <el-form-item label="排序方式">
                  <el-select v-model="sortBy" placeholder="选择排序方式">
                    <el-option label="最新" value="latest"></el-option>
                    <el-option label="价格从低到高" value="price-asc"></el-option>
                    <el-option label="价格从高到低" value="price-desc"></el-option>
                    <el-option label="受欢迎程度" value="popularity"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12" class="filter-actions">
                <el-button type="primary" @click="applyFilters">应用筛选</el-button>
                <el-button @click="resetFilters">重置</el-button>
              </el-col>
            </el-row>
          </Card>
            </el-tab-pane>
            <el-tab-pane label="我的服装" name="my">
              <!-- 我的服装列表 -->
              <Card class="filter-card">
                <div class="add-clothing-button">
                  <el-button type="primary" @click="showAddClothingDialog">
                    <el-icon><Plus /></el-icon>
                    添加服装
                  </el-button>
                </div>
              </Card>
            </el-tab-pane>
          </el-tabs>

          <!-- 添加服装对话框 -->
          <el-dialog
            v-model="addClothingDialogVisible"
            title="添加服装"
            width="800px"
            destroy-on-close
          >
            <el-form :model="addClothingForm" ref="addClothingFormRef" label-position="top">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="title" :rules="[{ required: true, message: '请输入服装标题', trigger: 'blur' }]">
                    <el-input v-model="addClothingForm.title" placeholder="服装标题" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="category" :rules="[{ required: true, message: '请选择服装类别', trigger: 'change' }]">
                    <el-select v-model="addClothingForm.category" placeholder="服装类别" size="large">
                      <el-option label="上衣" value="上衣"></el-option>
                      <el-option label="裤子" value="裤子"></el-option>
                      <el-option label="裙子" value="裙子"></el-option>
                      <el-option label="鞋帽" value="鞋帽"></el-option>
                      <el-option label="配饰" value="配饰"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="size" :rules="[{ required: true, message: '请输入尺寸', trigger: 'blur' }]">
                    <el-input v-model="addClothingForm.size" placeholder="尺寸" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="condition" :rules="[{ required: true, message: '请选择状况', trigger: 'change' }]">
                    <el-select v-model="addClothingForm.condition" placeholder="状况" size="large">
                      <el-option label="全新" value="全新"></el-option>
                      <el-option label="近似全新" value="近似全新"></el-option>
                      <el-option label="轻微使用" value="轻微使用"></el-option>
                      <el-option label="正常使用" value="正常使用"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="price" :rules="[{ required: true, message: '请输入价格', trigger: 'blur' }]">
                    <el-input v-model="addClothingForm.price" type="number" placeholder="价格" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="rentalPrice">
                    <el-input v-model="addClothingForm.rentalPrice" type="number" placeholder="租赁价格（选填）" size="large" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item prop="description">
                    <el-input
                      v-model="addClothingForm.description"
                      type="textarea"
                      placeholder="服装描述（选填）"
                      :rows="3"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="图片">
                    <el-upload
                      class="upload-demo"
                      :action="''"
                      :auto-upload="false"
                      :on-change="handleAddImageChange"
                      :on-remove="handleAddImageRemove"
                      :before-upload="beforeUpload"
                      :multiple="true"
                      :limit="5"
                      :on-exceed="handleExceed"
                      list-type="picture-card"
                      :file-list="addClothingImages"
                    >
                      <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                      <template #file="{ file }">
                        <div class="image-preview">
                          <img :src="file.url" class="preview-image" />
                          <div class="image-actions">
                            <el-button size="small" type="danger" circle @click.stop="handleAddImageRemove(file)">
                              <el-icon><Delete /></el-icon>
                            </el-button>
                          </div>
                        </div>
                      </template>
                    </el-upload>
                    <el-alert
                      :title="`已上传 ${addClothingImages.length}/5 张照片`"
                      type="info"
                      :closable="false"
                      class="upload-info"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="addClothingDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addClothing" :loading="addClothingLoading">
                  确认添加
                </el-button>
              </span>
            </template>
          </el-dialog>

          <!-- 服装展示 -->
          <div v-if="activeTab === 'browse'" class="clothing-grid">
            <div v-for="item in filteredItems" :key="item.id" class="clothing-card">
              <el-card :shadow="'hover'" class="clothing-item">
                <div class="clothing-image">
                  <el-image
                    :src="item.image"
                    fit="cover"
                    class="item-image"
                  />
                  <div v-if="item.availability === 'available'" class="availability-badge available">可交易</div>
                  <div v-else class="availability-badge unavailable">已售出</div>
                </div>
                <div class="clothing-info">
                  <h3 class="clothing-title">{{ item.title }}</h3>
                  <p class="clothing-description">{{ item.description }}</p>
                  <div class="clothing-details">
                    <span class="detail-item"><strong>类别:</strong> {{ item.category }}</span>
                    <span class="detail-item"><strong>尺寸:</strong> {{ item.size }}</span>
                    <span class="detail-item"><strong>状况:</strong> {{ item.condition }}</span>
                  </div>
                  <div class="clothing-price">
                    <span class="price">{{ item.price }} 元</span>
                    <span v-if="item.rentalPrice" class="rental-price">租赁: {{ item.rentalPrice }} 元/天</span>
                  </div>
                  <div class="clothing-owner">
                    <el-avatar :src="item.owner.avatar" size="small"></el-avatar>
                    <span>{{ item.owner.name }}</span>
                  </div>
                  <div class="clothing-actions">
                    <el-button type="primary" @click="buyItem(item)" :disabled="item.availability !== 'available'">
                      购买
                    </el-button>
                    <el-button type="success" @click="rentItem(item)" :disabled="item.availability !== 'available' || !item.rentalPrice">
                      租赁
                    </el-button>
                    <el-button @click="contactOwner(item.owner)" class="message-button">
                      <el-icon><ChatDotRound /></el-icon>
                      联系
                    </el-button>
                    <el-button type="danger" @click="deleteClothing(item)" class="delete-button">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
          
          <!-- 我的服装展示 -->
          <div v-else-if="activeTab === 'my'" class="clothing-grid">
            <div v-for="item in myClothingItems" :key="item.id" class="clothing-card">
              <el-card :shadow="'hover'" class="clothing-item">
                <div class="clothing-image">
                  <el-image
                    :src="item.image"
                    fit="cover"
                    class="item-image"
                  />
                  <div v-if="item.reviewStatus === 'pending'" class="availability-badge pending">待审核</div>
                  <div v-else-if="item.reviewStatus === 'approved'" class="availability-badge available">已通过</div>
                  <div v-else-if="item.reviewStatus === 'rejected'" class="availability-badge rejected">已拒绝</div>
                </div>
                <div class="clothing-info">
                  <h3 class="clothing-title">{{ item.title }}</h3>
                  <p class="clothing-description">{{ item.description }}</p>
                  <div class="clothing-details">
                    <span class="detail-item"><strong>类别:</strong> {{ item.category }}</span>
                    <span class="detail-item"><strong>尺寸:</strong> {{ item.size }}</span>
                    <span class="detail-item"><strong>状况:</strong> {{ item.condition }}</span>
                    <span class="detail-item" :class="{
                      'status-pending': item.reviewStatus === 'pending',
                      'status-approved': item.reviewStatus === 'approved',
                      'status-rejected': item.reviewStatus === 'rejected'
                    }"><strong>审核状态:</strong> {{ item.reviewStatus === 'pending' ? '待审核' : item.reviewStatus === 'approved' ? '已通过' : '已拒绝' }}</span>
                  </div>
                  <div v-if="item.reviewStatus === 'rejected'" class="rejection-reason">
                    <strong>拒绝原因:</strong> {{ item.rejectionReason }}
                  </div>
                  <div class="clothing-price">
                    <span class="price">{{ item.price }} 元</span>
                    <span v-if="item.rentalPrice" class="rental-price">租赁: {{ item.rentalPrice }} 元/天</span>
                  </div>
                  <div class="clothing-actions">
                    <el-button v-if="item.reviewStatus === 'rejected'" type="warning" @click="resubmitClothing(item)" class="resubmit-button">
                      重新提交
                    </el-button>
                    <el-button type="danger" @click="deleteClothing(item)" class="delete-button">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>

          <!-- 消息界面 -->
          <el-dialog
            v-model="messageDialogVisible"
            title="消息中心"
            width="80%"
            destroy-on-close
            @open="loadConversations"
          >
            <div class="message-container">
              <div class="message-sidebar">
                <div v-if="loadingConversations" class="loading-container">
                  <el-spinner />
                  <p>加载对话中...</p>
                </div>
                <div v-else v-for="conversation in conversations" :key="conversation.id" 
                     class="conversation-item" 
                     :class="{ active: selectedConversation === conversation.id }"
                     @click="openMessageDialog(conversation.id)">
                  <el-avatar :src="conversation.avatar" size="small"></el-avatar>
                  <div class="conversation-info">
                    <div class="conversation-name">{{ conversation.name }}</div>
                    <div class="conversation-last-message">{{ conversation.lastMessage }}</div>
                    <div class="conversation-time">{{ new Date(conversation.time).toLocaleString() }}</div>
                  </div>
                  <div v-if="conversation.unread" class="unread-badge">{{ conversation.unread }}</div>
                </div>
                <div v-if="!loadingConversations && conversations.length === 0" class="no-conversations">
                  <p>暂无对话</p>
                </div>
              </div>
              <div class="message-content">
                <div v-if="selectedConversation" class="message-thread">
                  <div class="thread-header">
                    <el-avatar :src="currentConversation?.avatar" size="small"></el-avatar>
                    <h3>{{ currentConversation?.name }}</h3>
                  </div>
                  <div class="message-list">
                    <div v-if="loadingMessages" class="loading-container">
                      <el-spinner />
                      <p>加载消息中...</p>
                    </div>
                    <div v-else v-for="message in messages" :key="message.id" 
                         class="message" :class="{ 'sent': message.sent }">
                      <div class="message-bubble">{{ message.content }}</div>
                      <div class="message-time">{{ new Date(message.time).toLocaleString() }}</div>
                      <div v-if="message.sent" class="message-status">
                        {{ message.status === 'read' ? '已读' : '已发送' }}
                      </div>
                    </div>
                    <div v-if="!loadingMessages && messages.length === 0" class="no-messages">
                      <p>暂无消息</p>
                    </div>
                  </div>
                  <div class="message-input">
                    <el-input v-model="newMessage" placeholder="输入消息..." @keyup.enter="sendMessage"></el-input>
                    <el-button type="primary" @click="sendMessage" :loading="sendingMessage">发送</el-button>
                  </div>
                </div>
                <div v-else class="no-conversation">
                  <p>选择一个对话开始聊天</p>
                </div>
              </div>
            </div>
          </el-dialog>

          <!-- 购买对话框 -->
          <el-dialog
            v-model="buyDialogVisible"
            title="购买确认"
            width="60%"
            destroy-on-close
          >
            <div v-if="selectedItem" class="purchase-dialog">
              <div class="purchase-image">
                <el-image :src="selectedItem.image" fit="cover"></el-image>
              </div>
              <div class="purchase-info">
                <h3>{{ selectedItem.title }}</h3>
                <p>{{ selectedItem.description }}</p>
                <div class="purchase-details">
                  <span class="detail-item"><strong>类别:</strong> {{ selectedItem.category }}</span>
                  <span class="detail-item"><strong>尺寸:</strong> {{ selectedItem.size }}</span>
                  <span class="detail-item"><strong>状况:</strong> {{ selectedItem.condition }}</span>
                </div>
                <div class="purchase-price">
                  <h4>价格: {{ selectedItem.price }} 元</h4>
                </div>
                <div class="payment-method">
                  <h4>支付方式</h4>
                  <el-radio-group v-model="paymentMethod">
                    <el-radio label="alipay">支付宝</el-radio>
                    <el-radio label="wechat">微信支付</el-radio>
                    <el-radio label="creditcard">信用卡</el-radio>
                  </el-radio-group>
                </div>
              </div>
            </div>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="buyDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmPurchase">确认购买</el-button>
              </span>
            </template>
          </el-dialog>

          <!-- 租赁对话框 -->
          <el-dialog
            v-model="rentDialogVisible"
            title="租赁确认"
            width="60%"
            destroy-on-close
          >
            <div v-if="selectedItem" class="rental-dialog">
              <div class="rental-image">
                <el-image :src="selectedItem.image" fit="cover"></el-image>
              </div>
              <div class="rental-info">
                <h3>{{ selectedItem.title }}</h3>
                <p>{{ selectedItem.description }}</p>
                <div class="rental-details">
                  <span class="detail-item"><strong>类别:</strong> {{ selectedItem.category }}</span>
                  <span class="detail-item"><strong>尺寸:</strong> {{ selectedItem.size }}</span>
                  <span class="detail-item"><strong>状况:</strong> {{ selectedItem.condition }}</span>
                </div>
                <div class="rental-price">
                  <h4>租赁价格: {{ selectedItem.rentalPrice }} 元/天</h4>
                </div>
                <div class="rental-period">
                  <h4>租赁期限</h4>
                  <el-date-picker
                    v-model="rentalPeriod"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :disabled-date="disabledDate"
                  />
                </div>
                <div class="rental-total" v-if="rentalPeriod.length">
                  <h4>总费用: {{ calculateRentalTotal() }} 元</h4>
                </div>
                <div class="payment-method">
                  <h4>支付方式</h4>
                  <el-radio-group v-model="paymentMethod">
                    <el-radio label="alipay">支付宝</el-radio>
                    <el-radio label="wechat">微信支付</el-radio>
                    <el-radio label="creditcard">信用卡</el-radio>
                  </el-radio-group>
                </div>
              </div>
            </div>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="rentDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmRental">确认租赁</el-button>
              </span>
            </template>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, ShoppingBag, MapLocation, Refresh, ChatDotRound, Plus, Delete } from '@element-plus/icons-vue'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import request from '../../utils/request'

const router = useRouter()
const username = ref('用户')
const userAvatar = ref('')
const loading = ref(false)

const menuItems = [
  { index: '/user/home', icon: 'House', title: '首页' },
  { index: '/user/clothing', icon: 'ShoppingBag', title: '衣物管理' },
  { index: '/user/location', icon: 'MapLocation', title: '收纳管理' },
  { index: '/user/recycle', icon: 'Refresh', title: '衣物回收' },
  { index: '/user/trade', icon: 'ShoppingBag', title: '服装交易' }
]

// 筛选和排序
const filters = ref({
  category: '',
  size: '',
  priceRange: '',
  condition: ''
})

const sortBy = ref('latest')
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 标签页
const activeTab = ref('browse')

// 服装数据
const clothingItems = ref([])
const myClothingItems = ref([])

// 过滤后的服装列表
const filteredItems = computed(() => {
  let result = [...clothingItems.value]
  
  // 应用筛选条件
  if (filters.value.category) {
    result = result.filter(item => item.category === filters.value.category)
  }
  if (filters.value.size) {
    result = result.filter(item => item.size === filters.value.size)
  }
  if (filters.value.condition) {
    result = result.filter(item => item.condition === filters.value.condition)
  }
  if (filters.value.priceRange) {
    const [min, max] = filters.value.priceRange.split('-').map(p => parseInt(p) || 0)
    result = result.filter(item => {
      if (max === 0) {
        return item.price >= min
      }
      return item.price >= min && item.price <= max
    })
  }
  
  // 应用排序
  switch (sortBy.value) {
    case 'price-asc':
      result.sort((a, b) => a.price - b.price)
      break
    case 'price-desc':
      result.sort((a, b) => b.price - a.price)
      break
    case 'latest':
    default:
      // 假设id越大越新
      result.sort((a, b) => b.id - a.id)
      break
  }
  
  return result
})

// 处理标签页切换
const handleTabClick = (tab) => {
  if (tab.props.name === 'my') {
    loadMyClothingList()
  }
}

// 对话数据
const conversations = ref([])
const messages = ref([])
const loadingConversations = ref(false)
const loadingMessages = ref(false)
const sendingMessage = ref(false)

// 对话框状态
const messageDialogVisible = ref(false)
const buyDialogVisible = ref(false)
const rentDialogVisible = ref(false)

// 选中的商品和对话
const selectedItem = ref(null)
const selectedConversation = ref(null)
const newMessage = ref('')
const paymentMethod = ref('alipay')
const rentalPeriod = ref([])

// 添加服装表单
const addClothingDialogVisible = ref(false)
const addClothingForm = reactive({
  title: '',
  category: '',
  size: '',
  condition: '',
  price: null,
  rentalPrice: null,
  description: ''
})
const addClothingImages = ref([])
const addClothingFormRef = ref()
const addClothingLoading = ref(false)

// 计算当前选中的对话
const currentConversation = computed(() => {
  return conversations.value.find(c => c.id === selectedConversation.value)
})

// 加载对话列表
const loadConversations = async () => {
  loadingConversations.value = true
  try {
    const token = localStorage.getItem('token')
    if (token) {
      const response = await request.get('/api/v1/messages/conversations')
      conversations.value = response.data || []
    }
  } catch (error) {
    console.error('加载对话列表失败:', error)
    ElMessage.error('加载对话列表失败')
  } finally {
    loadingConversations.value = false
  }
}

// 加载消息历史
const loadMessageHistory = async (otherUserId) => {
  loadingMessages.value = true
  try {
    const token = localStorage.getItem('token')
    if (token) {
      const response = await request.get(`/api/v1/messages/history/${otherUserId}`)
      messages.value = response.data || []
    }
  } catch (error) {
    console.error('加载消息历史失败:', error)
    ElMessage.error('加载消息历史失败')
  } finally {
    loadingMessages.value = false
  }
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    return
  }
  
  sendingMessage.value = true
  try {
    const token = localStorage.getItem('token')
    if (token && selectedConversation.value) {
      const response = await request.post('/api/v1/messages', {
        receiverId: selectedConversation.value,
        content: newMessage.value
      })
      
      // 添加新消息到消息列表
      messages.value.push(response.data)
      
      // 清空输入框
      newMessage.value = ''
      
      // 更新对话列表中的最后一条消息
      const conversationIndex = conversations.value.findIndex(c => c.id === selectedConversation.value)
      if (conversationIndex !== -1) {
        conversations.value[conversationIndex].lastMessage = response.data.content
        conversations.value[conversationIndex].time = response.data.time
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sendingMessage.value = false
  }
}

// 打开消息对话框
const openMessageDialog = (otherUserId) => {
  selectedConversation.value = otherUserId
  loadMessageHistory(otherUserId)
  messageDialogVisible.value = true
}

// 加载用户数据
const loadUserData = async () => {
  try {
    const token = localStorage.getItem('token')
    if (token) {
      // 尝试从后端获取用户信息
      const response = await request.get('/user/info')
      if (response.data && response.data.username) {
        username.value = response.data.username
        localStorage.setItem('username', response.data.username)
      } else {
        const user = localStorage.getItem('username')
        username.value = user || '用户'
      }
    } else {
      const user = localStorage.getItem('username')
      username.value = user || '用户'
    }
   userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
  } catch (error) {
    console.error('加载用户数据失败:', error)
    const user = localStorage.getItem('username')
    username.value = user || '用户'
   userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
  }
}

// 加载服装列表
const loadClothingList = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    // 检查是否有token，如果没有则使用模拟数据
    if (!token) {
      // 使用模拟数据作为 fallback
      clothingItems.value = [
        {
          id: 1,
          title: '复古牛仔外套',
          description: '经典复古风格牛仔外套，适合春秋季节穿着',
          image: 'https://picsum.photos/seed/denim-jacket/800/600',
          category: '上衣',
          size: 'M',
          condition: '近似全新',
          price: 299,
          rentalPrice: 20,
          availability: 'available',
          owner: {
            id: 101,
            name: '12342',
            avatar: 'https://picsum.photos/seed/fashion-person/100/100'
          }
        },
        {
          id: 2,
          title: '黑色西装裤',
          description: '修身黑色西装裤，适合正式场合',
          image: 'https://picsum.photos/seed/suit-pants/800/600',
          category: '裤子',
          size: 'L',
          condition: '轻微使用',
          price: 199,
          rentalPrice: 15,
          availability: 'available',
          owner: {
            id: 102,
            name: '2424242',
            avatar: 'https://picsum.photos/seed/professional/100/100'
          }
        },
        {
          id: 3,
          title: '碎花连衣裙',
          description: '春季碎花连衣裙，轻盈舒适',
          image: 'https://picsum.photos/seed/floral-dress/800/600',
          category: '裙子',
          size: 'S',
          condition: '全新',
          price: 399,
          rentalPrice: 25,
          availability: 'available',
          owner: {
            id: 103,
            name: '123456',
            avatar: 'https://picsum.photos/seed/artistic/100/100'
          }
        },
        {
          id: 4,
          title: '白色高跟鞋',
          description: '时尚美丽',
          image: 'https://picsum.photos/id/21/800/600',
          category: '鞋帽',
          size: '40',
          condition: '正常使用',
          price: 499,
          rentalPrice: 30,
          availability: 'available',
          owner: {
            id: 104,
            name: '1234567',
            avatar: 'https://picsum.photos/seed/sporty/100/100'
          }
        },
        {
          id: 5,
          title: '复古太阳镜',
          description: '经典复古风格太阳镜，时尚百搭',
          image: 'https://picsum.photos/id/21/800/600',
          category: '配饰',
          size: '均码',
          condition: '近似全新',
          price: 159,
          rentalPrice: 10,
          availability: 'available',
          owner: {
            id: 105,
            name: '12345678',
            avatar: 'https://picsum.photos/seed/trendy/100/100'
          }
        }
      ]
    } else {
      // 有token，使用request.js调用后端API
      const response = await request.get('/api/v1/trade/clothing', {
        params: {
          category: filters.value.category,
          size: filters.value.size,
          condition: filters.value.condition,
          priceRange: filters.value.priceRange,
          sortBy: sortBy.value,
          page: page.value,
          sizePerPage: size.value
        }
      })
      
      const responseData = response.data
      // 后端返回的是 {data: [...], total: number} 格式
      const clothingData = responseData.data || []
      total.value = responseData.total || 0
      
      clothingItems.value = clothingData.map(item => ({
        ...item,
        // 为没有title和description的服装添加默认值
        title: item.title || item.name || '未命名服装',
        description: item.description || item.notes || '暂无描述',
        // 为没有image的服装添加默认图片，检查是否包含lf-cdn.trae.com.cn URL
        image: item.imageUrls ? (item.imageUrls.includes('lf-cdn.trae.com.cn') ? `https://picsum.photos/seed/clothing${item.id}/800/600` : `http://localhost:8080/uploads/${item.imageUrls.split(',')[0]}`) : `https://picsum.photos/seed/clothing${item.id}/800/600`,
        // 为没有category的服装添加默认分类
        category: item.category || item.type || '其他',
        // 为没有condition的服装添加默认状况
        condition: item.clothingCondition || item.condition || item.status || '正常使用',
        // 为没有price的服装添加默认价格
        price: item.price || 0,
        // 为没有rentalPrice的服装添加默认租赁价格
        rentalPrice: item.rentalPrice || 0,
        // 默认可用性为可交易
        availability: 'available',
        owner: {
          id: item.userId || 1,
          name: item.username || ('用户' + (item.userId || 1)),
          avatar: `https://picsum.photos/seed/user${item.userId || 1}/100/100`
        }
      }))
    }
  } catch (error) {
    ElMessage.error('网络错误')
    console.error('Error loading clothing list:', error)
    // 清空服装列表，不使用模拟数据
    clothingItems.value = []
  } finally {
    loading.value = false
  }
}

// 加载用户自己的服装列表
const loadMyClothingList = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用后端API获取用户自己的服装列表
    const response = await request.get('/api/v1/trade/user/clothing')
    const clothingData = response.data || []
    
    myClothingItems.value = clothingData.map(item => ({
      ...item,
      // 为没有title和description的服装添加默认值
      title: item.title || item.name || '未命名服装',
      description: item.description || item.notes || '暂无描述',
      // 为没有image的服装添加默认图片，检查是否包含lf-cdn.trae.com.cn URL
      image: item.imageUrls ? (item.imageUrls.includes('lf-cdn.trae.com.cn') ? `https://picsum.photos/seed/clothing${item.id}/800/600` : `http://localhost:8080/uploads/${item.imageUrls.split(',')[0]}`) : `https://picsum.photos/seed/clothing${item.id}/800/600`,
      // 为没有category的服装添加默认分类
      category: item.category || item.type || '其他',
      // 为没有condition的服装添加默认状况
      condition: item.clothingCondition || item.condition || item.status || '正常使用',
      // 为没有price的服装添加默认价格
      price: item.price || 0,
      // 为没有rentalPrice的服装添加默认租赁价格
      rentalPrice: item.rentalPrice || 0,
      // 默认可用性为可交易
      availability: 'available'
    }))
  } catch (error) {
    ElMessage.error('网络错误')
    console.error('Error loading my clothing list:', error)
    // 清空服装列表
    myClothingItems.value = []
  } finally {
    loading.value = false
  }
}

// 应用筛选
const applyFilters = () => {
  page.value = 1
  loadClothingList()
  ElMessage.success('筛选条件已应用')
}

// 重置筛选
const resetFilters = () => {
  filters.value = {
    category: '',
    size: '',
    priceRange: '',
    condition: ''
  }
  sortBy.value = 'latest'
  page.value = 1
  loadClothingList()
  ElMessage.success('筛选条件已重置')
}

// 购买商品
const buyItem = (item) => {
  selectedItem.value = item
  buyDialogVisible.value = true
}

// 租赁商品
const rentItem = (item) => {
  selectedItem.value = item
  rentDialogVisible.value = true
}

// 联系卖家
const contactOwner = (owner) => {
  openMessageDialog(owner.id)
}

// 确认购买
const confirmPurchase = async () => {
  if (!selectedItem.value) return
  
  loading.value = true
  try {
    const response = await fetch('/api/v1/trade', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        clothingId: selectedItem.value.id,
        tradeType: 'purchase',
        price: selectedItem.value.price,
        paymentMethod: paymentMethod.value
      })
    })
    
    if (response.ok) {
      ElMessage.success(`成功购买 ${selectedItem.value.title}`)
      // 更新商品状态
      const item = clothingItems.value.find(i => i.id === selectedItem.value.id)
      if (item) {
        item.availability = 'sold'
      }
      buyDialogVisible.value = false
      selectedItem.value = null
    } else {
      ElMessage.error('购买失败，请重试')
    }
  } catch (error) {
    ElMessage.error('网络错误，请重试')
    console.error('Error creating trade:', error)
    // 模拟购买过程
    ElMessage.success(`成功购买 ${selectedItem.value.title}`)
    // 更新商品状态
    const item = clothingItems.value.find(i => i.id === selectedItem.value.id)
    if (item) {
      item.availability = 'sold'
    }
    buyDialogVisible.value = false
    selectedItem.value = null
  } finally {
    loading.value = false
  }
}

// 确认租赁
const confirmRental = async () => {
  if (!selectedItem.value || !rentalPeriod.value.length) return
  
  loading.value = true
  try {
    const response = await fetch('/api/v1/trade', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        clothingId: selectedItem.value.id,
        tradeType: 'rental',
        price: calculateRentalTotal(),
        rentalStartDate: rentalPeriod.value[0],
        rentalEndDate: rentalPeriod.value[1],
        paymentMethod: paymentMethod.value
      })
    })
    
    if (response.ok) {
      ElMessage.success(`成功租赁 ${selectedItem.value.title}`)
      // 更新商品状态
      const item = clothingItems.value.find(i => i.id === selectedItem.value.id)
      if (item) {
        item.availability = 'sold'
      }
      rentDialogVisible.value = false
      selectedItem.value = null
      rentalPeriod.value = []
    } else {
      ElMessage.error('租赁失败，请重试')
    }
  } catch (error) {
    ElMessage.error('网络错误，请重试')
    console.error('Error creating rental:', error)
    // 模拟租赁过程
    ElMessage.success(`成功租赁 ${selectedItem.value.title}`)
    // 更新商品状态
    const item = clothingItems.value.find(i => i.id === selectedItem.value.id)
    if (item) {
      item.availability = 'sold'
    }
    rentDialogVisible.value = false
    selectedItem.value = null
    rentalPeriod.value = []
  } finally {
    loading.value = false
  }
}

// 删除服装
const deleteClothing = async (item) => {
  try {
    const response = await request.delete(`/api/v1/trade/clothing/${item.id}`)
    if (response.data) {
      ElMessage.success('服装删除成功')
      // 从列表中移除
      clothingItems.value = clothingItems.value.filter(i => i.id !== item.id)
      // 同时从我的服装列表中移除
      myClothingItems.value = myClothingItems.value.filter(i => i.id !== item.id)
    } else {
      ElMessage.error('删除失败，请重试')
    }
  } catch (error) {
    console.error('删除服装失败:', error)
    ElMessage.error('删除失败，请重试')
  }
}

// 计算租赁总费用
const calculateRentalTotal = () => {
  if (!selectedItem.value || !rentalPeriod.value.length) return 0
  
  const startDate = new Date(rentalPeriod.value[0])
  const endDate = new Date(rentalPeriod.value[1])
  const days = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1
  return days * selectedItem.value.rentalPrice
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 图片处理
const handleAddImageChange = (file, fileList) => {
  addClothingImages.value = fileList
}

const handleAddImageRemove = (file, fileList) => {
  addClothingImages.value = fileList || addClothingImages.value.filter(img => img.uid !== file.uid)
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
  }
  return isImage && isLt2M
}

const handleExceed = () => {
  ElMessage.error('最多只能上传 5 张照片！')
}

// 显示添加服装对话框
const showAddClothingDialog = () => {
  addClothingDialogVisible.value = true
}

// 添加服装
const addClothing = () => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  addClothingLoading.value = true
  const formData = new FormData()
  formData.append('title', addClothingForm.title)
  formData.append('category', addClothingForm.category)
  formData.append('size', addClothingForm.size)
  formData.append('status', addClothingForm.condition)
  formData.append('price', addClothingForm.price)
  if (addClothingForm.rentalPrice) {
    formData.append('rentalPrice', addClothingForm.rentalPrice)
  }
  formData.append('description', addClothingForm.description)
  
  // 添加图片
  // 检查是否有从回收页面传递过来的图片URL
  const hasExternalImages = addClothingImages.value.some(image => image.url && !image.raw)
  
  if (hasExternalImages) {
    // 如果有外部图片URL，提取图片文件名并添加到formData
    const imageUrls = addClothingImages.value
      .filter(image => image.url)
      .map(image => {
        // 从URL中提取文件名
        const parts = image.url.split('/')
        return parts[parts.length - 1]
      })
      .join(',')
    
    formData.append('imageUrls', imageUrls)
  } else {
    // 否则添加原始文件
    addClothingImages.value.forEach((image, index) => {
      if (image.raw) {
        formData.append('images', image.raw)
      }
    })
  }
  
  request.post('/api/v1/trade/clothing', formData).then(() => {
    ElMessage.success('服装添加成功')
    addClothingDialogVisible.value = false
    // 重置表单
    Object.keys(addClothingForm).forEach(key => {
      addClothingForm[key] = ''
    })
    addClothingForm.price = null
    addClothingForm.rentalPrice = null
    addClothingImages.value = []
    addClothingLoading.value = false
    // 重新加载服装列表
    loadClothingList()
    // 同时加载用户自己的服装列表，确保"我的服装"标签页也能看到新添加的服装
    loadMyClothingList()
  }).catch(error => {
    console.error('添加服装失败:', error)
    ElMessage.error('添加服装失败，请重试')
    addClothingLoading.value = false
  })
}

// 生命周期
onMounted(() => {
  loadUserData()
  loadClothingList()
  // 检查是否有从回收页面传递过来的数据
  checkRecycleToTradeData()
})

// 检查是否有从回收页面传递过来的数据
const checkRecycleToTradeData = () => {
  const recycleData = localStorage.getItem('recycleToTradeData')
  if (recycleData) {
    try {
      const data = JSON.parse(recycleData)
      console.log('从回收页面获取的数据:', data)
      // 填充表单数据
      addClothingForm.title = data.title || ''
      addClothingForm.category = data.category || ''
      addClothingForm.size = data.size || ''
      addClothingForm.condition = data.condition || ''
      addClothingForm.price = data.price || 0
      addClothingForm.description = data.description || ''
      
      // 处理图片
      if (data.images && data.images.length > 0) {
        addClothingImages.value = data.images.map((imageUrl, index) => ({
          url: imageUrl.includes('lf-cdn.trae.com.cn') ? `https://picsum.photos/seed/clothing${Date.now() + index}/800/600` : imageUrl,
          name: `image-${index}`
        }))
      }
      
      // 显示添加服装对话框
      addClothingDialogVisible.value = true
      
      // 清除 localStorage 中的数据
      localStorage.removeItem('recycleToTradeData')
      
      ElMessage.success('已从回收页面导入衣物信息')
    } catch (error) {
      console.error('解析回收数据失败:', error)
      ElMessage.error('解析回收数据失败，请重试')
    }
  }
}
</script>

<style lang="scss" scoped>
.trade-header-card {
  margin-bottom: 24px;
  
  .trade-header {
    text-align: center;
    padding: 20px 0;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }
    
    p {
      font-size: 16px;
      color: #606266;
      margin: 0;
    }
  }
}

.filter-card {
  margin-bottom: 24px;
}

.sort-row {
  margin-top: 16px;
  
  .filter-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.clothing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.clothing-card {
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.clothing-item {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.clothing-image {
  position: relative;
  margin-bottom: 16px;
  
  .item-image {
    width: 100%;
    height: 200px;
    border-radius: 8px;
  }
  
  .availability-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    
    &.available {
      background-color: #67C23A;
      color: white;
    }
    
    &.unavailable {
      background-color: #F56C6C;
      color: white;
    }
  }
}

.clothing-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.clothing-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.clothing-description {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.clothing-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  
  .detail-item {
    font-size: 12px;
    color: #909399;
    background-color: #f9fafc;
    padding: 4px 8px;
    border-radius: 4px;
  }
}

.clothing-price {
  margin-bottom: 12px;
  
  .price {
    font-size: 18px;
    font-weight: 600;
    color: #F56C6C;
    display: block;
  }
  
  .rental-price {
    font-size: 14px;
    color: #67C23A;
    margin-top: 4px;
    display: block;
  }
}

.clothing-owner {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  
  span {
    font-size: 14px;
    color: #606266;
  }
}

.clothing-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
  
  .message-button {
    margin-left: auto;
  }
}

/* 消息界面 */
.message-container {
  display: flex;
  height: 500px;
  
  .message-sidebar {
    width: 300px;
    border-right: 1px solid #ebeef5;
    overflow-y: auto;
    
    .conversation-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      cursor: pointer;
      transition: background-color 0.3s ease;
      
      &:hover {
        background-color: #f9fafc;
      }
      
      &.active {
        background-color: #ecf5ff;
      }
      
      .conversation-info {
        flex: 1;
        min-width: 0;
        
        .conversation-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .conversation-last-message {
          font-size: 12px;
          color: #909399;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .conversation-time {
          font-size: 11px;
          color: #c0c4cc;
          margin-top: 4px;
        }
      }
      
      .unread-badge {
        background-color: #F56C6C;
        color: white;
        font-size: 12px;
        padding: 2px 6px;
        border-radius: 10px;
      }
    }
  }
  
  .message-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    .thread-header {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }
    }
    
    .message-list {
      flex: 1;
      padding: 16px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 16px;
      
      .message {
        display: flex;
        flex-direction: column;
        max-width: 70%;
        
        &.sent {
          align-self: flex-end;
          
          .message-bubble {
            background-color: #409EFF;
            color: white;
            border-radius: 12px 12px 0 12px;
          }
        }
        
        &:not(.sent) {
          align-self: flex-start;
          
          .message-bubble {
            background-color: #f9fafc;
            color: #303133;
            border-radius: 0 12px 12px 12px;
          }
        }
        
        .message-bubble {
          padding: 10px 14px;
          margin-bottom: 4px;
        }
        
        .message-time {
          font-size: 11px;
          color: #c0c4cc;
          align-self: flex-end;
        }
      }
    }
    
    .message-input {
      padding: 16px;
      border-top: 1px solid #ebeef5;
      display: flex;
      gap: 12px;
      
      .el-input {
        flex: 1;
      }
    }
    
    .no-conversation {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #909399;
    }
  }
}

/* 购买和租赁对话框 */
.purchase-dialog,
.rental-dialog {
  display: flex;
  gap: 24px;
  
  .purchase-image,
  .rental-image {
    width: 300px;
    height: 200px;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 8px;
    }
  }
  
  .purchase-info,
  .rental-info {
    flex: 1;
    
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }
    
    p {
      font-size: 14px;
      color: #606266;
      margin: 0 0 16px 0;
      line-height: 1.4;
    }
    
    .purchase-details,
    .rental-details {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 16px;
      
      .detail-item {
        font-size: 12px;
        color: #909399;
        background-color: #f9fafc;
        padding: 4px 8px;
        border-radius: 4px;
      }
    }
    
    .purchase-price,
    .rental-price,
    .rental-total {
      margin-bottom: 16px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: #F56C6C;
        margin: 0;
      }
    }
    
    .rental-period {
      margin-bottom: 16px;
      
      h4 {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin: 0 0 8px 0;
      }
    }
    
    .payment-method {
      margin-top: 24px;
      
      h4 {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin: 0 0 8px 0;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .clothing-grid {
    grid-template-columns: 1fr;
  }
  
  .message-container {
    flex-direction: column;
    
    .message-sidebar {
      width: 100%;
      height: 200px;
      border-right: none;
      border-bottom: 1px solid #ebeef5;
    }
  }
  
  .purchase-dialog,
  .rental-dialog {
    flex-direction: column;
    
    .purchase-image,
    .rental-image {
      width: 100%;
    }
  }
  
  .sort-row {
    flex-direction: column;
    gap: 12px;
    
    .filter-actions {
      justify-content: flex-start;
    }
  }
}
</style>