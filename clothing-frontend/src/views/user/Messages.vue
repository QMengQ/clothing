<template>
  <div class="messages-container">
    <Sidebar 
      :username="username" 
      :user-role="userRole" 
      :user-avatar="userAvatar" 
      :menu-items="menuItems" 
    />
    
    <div class="messages-content">
      <div class="messages-header">
        <h2 class="page-title">消息中心</h2>
        <el-input
          v-model="searchQuery"
          placeholder="搜索会话"
          prefix-icon="el-icon-search"
          class="search-input"
        />
      </div>
      
      <div class="messages-body">
        <div class="conversations-list">
          <div 
            v-for="conversation in filteredConversations" 
            :key="conversation.id"
            class="conversation-item"
            :class="{ 'unread': conversation.unreadCount > 0 }"
            @click="selectConversation(conversation)"
          >
            <div class="conversation-avatar">
              <el-avatar :size="48" :src="`https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`">
                {{ getConversationAvatar(conversation) }}
              </el-avatar>
            </div>
            <div class="conversation-info">
              <div class="conversation-header">
                <h3 class="conversation-name">{{ getConversationName(conversation) }}</h3>
                <span class="conversation-time">{{ formatTime(conversation.lastMessageTime) }}</span>
              </div>
              <p class="conversation-last-message">{{ conversation.lastMessage }}</p>
            </div>
            <div v-if="conversation.unreadCount > 0" class="unread-badge">
              {{ conversation.unreadCount }}
            </div>
          </div>
          
          <div v-if="filteredConversations.length === 0" class="empty-state">
            <el-empty description="暂无消息" />
          </div>
        </div>
        
        <div class="message-thread" v-if="selectedConversation">
          <div class="thread-header">
            <div class="thread-info">
              <el-avatar :size="36" :src="`https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`">
                {{ getConversationAvatar(selectedConversation) }}
              </el-avatar>
              <h3 class="thread-name">{{ getConversationName(selectedConversation) }}</h3>
            </div>
            <el-button type="primary" size="small" @click="markAsRead(selectedConversation)">
              标记已读
            </el-button>
          </div>
          
          <div class="thread-messages">
            <div 
              v-for="message in selectedConversation.messages" 
              :key="message.id"
              class="message-item"
              :class="{ 'own-message': message.senderId === userId }"
            >
              <div class="message-content">
                <p>{{ message.content }}</p>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
            </div>
          </div>
          
          <div class="thread-input">
            <el-input
              v-model="newMessage"
              placeholder="输入消息..."
              @keyup.enter="sendMessage"
            />
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </div>
        </div>
        
        <div class="no-conversation" v-else>
          <el-empty description="请选择一个会话" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElEmpty } from 'element-plus'
import { House, ShoppingBag, MapLocation, Refresh, Message, ShoppingBag as ShoppingBagIcon, Warning } from '@element-plus/icons-vue'
import Sidebar from '../../components/Sidebar.vue'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()

// 用户信息
const username = ref('用户')
const userRole = ref('普通用户')
const userAvatar = ref('')
const userId = ref(localStorage.getItem('userId'))

// 消息数据
const conversations = ref([])
const selectedConversation = ref(null)
const newMessage = ref('')
const searchQuery = ref('')

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

// 过滤后的会话列表
const filteredConversations = computed(() => {
  if (!searchQuery.value) {
    return conversations.value
  }
  return conversations.value.filter(conversation => {
    const name = getConversationName(conversation).toLowerCase()
    const lastMessage = conversation.lastMessage.toLowerCase()
    return name.includes(searchQuery.value.toLowerCase()) || lastMessage.includes(searchQuery.value.toLowerCase())
  })
})

// 加载用户数据
const loadUserData = () => {
  const user = localStorage.getItem('username')
  const role = localStorage.getItem('role')
  if (user) {
    username.value = user
    userRole.value = role === 'ADMIN' ? '管理员' : '普通用户'
    userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
  }
}

// 加载会话数据
const loadConversations = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.get('/api/v1/messages/conversations')
    // 转换后端返回的数据结构为前端期望的结构
    conversations.value = response.data.map(conv => ({
      id: conv.id,
      name: conv.name,
      avatar: conv.avatar,
      lastMessage: conv.lastMessage,
      lastMessageTime: conv.time,
      unreadCount: conv.unread,
      participants: [
        { id: userId.value, name: username.value },
        { id: conv.id, name: conv.name }
      ]
    }))
  } catch (error) {
    console.error('加载会话列表失败:', error)
    ElMessage.error('加载会话列表失败')
    conversations.value = []
  }
}

// 选择会话
const selectConversation = async (conversation) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.get(`/api/v1/messages/history/${conversation.id}`)
    // 转换后端返回的消息结构为前端期望的结构
    conversation.messages = response.data.map(msg => ({
      id: msg.id,
      content: msg.content,
      timestamp: msg.time,
      senderId: msg.sent ? userId.value : conversation.id
    }))
    selectedConversation.value = conversation
    
    // 标记为已读
    conversation.unreadCount = 0
    await request.put(`/api/v1/messages/read/${conversation.id}`)
    
    // 选择会话后，滚动到底部
    setTimeout(() => {
      const messagesContainer = document.querySelector('.thread-messages')
      if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight
      }
    }, 100)
  } catch (error) {
    console.error('加载会话消息失败:', error)
    ElMessage.error('加载会话消息失败')
    selectedConversation.value = conversation
  }
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedConversation.value) return
  
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const response = await request.post('/api/v1/messages', {
      receiverId: selectedConversation.value.id,
      content: newMessage.value
    })

    const message = {
      id: response.data.id,
      content: response.data.content,
      timestamp: response.data.time,
      senderId: userId.value
    }
    selectedConversation.value.messages.push(message)
    selectedConversation.value.lastMessage = message.content
    selectedConversation.value.lastMessageTime = message.timestamp
    
    newMessage.value = ''
    
    // 发送消息后，滚动到底部
    setTimeout(() => {
      const messagesContainer = document.querySelector('.thread-messages')
      if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight
      }
    }, 100)
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

// 标记为已读
const markAsRead = async (conversation) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    await request.put(`/api/v1/messages/read/${conversation.id}`)
    conversation.unreadCount = 0
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

// 获取会话名称
const getConversationName = (conversation) => {
  const otherParticipant = conversation.participants.find(p => p.id !== userId.value)
  return otherParticipant ? otherParticipant.name : '未知用户'
}

// 获取会话头像
const getConversationAvatar = (conversation) => {
  const otherParticipant = conversation.participants.find(p => p.id !== userId.value)
  return otherParticipant ? otherParticipant.name.charAt(0) : '?'
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserData()
  loadConversations()
})
</script>

<style lang="scss" scoped>
.messages-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.messages-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.messages-header {
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

.messages-body {
  display: flex;
  gap: 24px;
  min-height: 600px;
  
  .conversations-list {
    width: 320px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;
    
    .conversation-item {
      display: flex;
      align-items: center;
      padding: 16px;
      cursor: pointer;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      &.unread {
        background-color: #ecf5ff;
      }
      
      .conversation-avatar {
        margin-right: 12px;
      }
      
      .conversation-info {
        flex: 1;
        min-width: 0;
        
        .conversation-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 4px;
          
          .conversation-name {
            font-size: 16px;
            font-weight: 500;
            color: #303133;
            margin: 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          
          .conversation-time {
            font-size: 12px;
            color: #909399;
          }
        }
        
        .conversation-last-message {
          font-size: 14px;
          color: #606266;
          margin: 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
      
      .unread-badge {
        background-color: #f56c6c;
        color: #fff;
        font-size: 12px;
        font-weight: 500;
        min-width: 20px;
        height: 20px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0 6px;
      }
    }
    
    .empty-state {
      padding: 48px 0;
    }
  }
  
  .message-thread {
    flex: 1;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    min-height: 600px;
    max-height: 700px;
    
    .thread-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #e4e7ed;
      
      .thread-info {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .thread-name {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin: 0;
        }
      }
    }
    
    .thread-messages {
      flex: 1;
      padding: 16px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 16px;
      max-height: 500px;
      
      .message-item {
        max-width: 70%;
        
        &.own-message {
          align-self: flex-end;
          
          .message-content {
            background-color: #409eff;
            color: #fff;
            border-radius: 12px 12px 0 12px;
          }
        }
        
        &:not(.own-message) {
          align-self: flex-start;
          
          .message-content {
            background-color: #f9fafc;
            color: #303133;
            border-radius: 0 12px 12px 12px;
          }
        }
        
        .message-content {
          padding: 10px 14px;
          margin-bottom: 4px;
          word-wrap: break-word;
          word-break: break-all;
          white-space: normal;
          max-width: 100%;
          box-sizing: border-box;
          
          p {
            margin: 0 0 4px 0;
            word-wrap: break-word;
          }
          
          .message-time {
            font-size: 11px;
            opacity: 0.7;
            align-self: flex-end;
          }
        }
      }
    }
    
    .thread-input {
      padding: 16px;
      border-top: 1px solid #e4e7ed;
      display: flex;
      gap: 12px;
      align-items: center;
      height: 80px;
      box-sizing: border-box;
      
      :deep(.el-input) {
        flex: 1;
        height: 40px;
      }
      
      :deep(.el-button) {
        height: 40px;
      }
    }
  }
  
  .no-conversation {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

@media (max-width: 768px) {
  .messages-body {
    flex-direction: column;
    
    .conversations-list {
      width: 100%;
      max-height: 300px;
    }
    
    .message-thread {
      min-height: 400px;
      max-height: 500px;
    }
  }
}
</style>