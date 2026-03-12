<template>
  <div class="review-clothing-container">
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
            title="服装审核管理"
            class="page-header"
          >
            <div class="header-content">
              <h2>待审核服装列表</h2>
              <p>审核通过或拒绝用户提交的服装商品</p>
            </div>
          </Card>

          <!-- 筛选和搜索 -->
          <Card class="filter-card">
            <el-form :inline="true" class="filter-form">
              <el-form-item label="搜索">
                <el-input 
                  v-model="searchQuery" 
                  placeholder="输入服装标题或ID" 
                  clearable
                  @keyup.enter="loadPendingClothing"
                >
                  <template #append>
                    <el-button @click="loadPendingClothing"><el-icon><Search /></el-icon></el-button>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadPendingClothing">
                  <el-icon><Refresh /></el-icon>
                  刷新列表
                </el-button>
              </el-form-item>
            </el-form>
          </Card>

          <!-- 服装列表 -->
          <Card class="clothing-list-card">
            <template #header>
              <div class="card-header">
                <span>待审核服装 ({{ total }})</span>
                <el-pagination
                  v-model:current-page="page"
                  v-model:page-size="sizePerPage"
                  :page-sizes="[10, 20, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="total"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </template>

            <div v-if="loading" class="loading-container">
              <el-spinner size="large" />
              <p>加载中...</p>
            </div>

            <div v-else-if="clothingList.length === 0" class="empty-container">
              <el-empty description="暂无待审核服装" />
            </div>

            <div v-else class="clothing-grid">
              <el-card 
                v-for="item in clothingList" 
                :key="item.id" 
                class="clothing-item"
                shadow="hover"
              >
                <div class="clothing-image">
                  <el-image 
                    :src="getImageUrl(item.imageUrls)" 
                    fit="cover"
                    class="item-image"
                  />
                </div>
                <div class="clothing-info">
                  <h3 class="clothing-title">{{ item.title }}</h3>
                  <div class="clothing-meta">
                    <span class="meta-item"><strong>ID:</strong> {{ item.id }}</span>
                    <span class="meta-item"><strong>用户:</strong> {{ item.username }}</span>
                    <span class="meta-item"><strong>类别:</strong> {{ item.category }}</span>
                    <span class="meta-item"><strong>尺寸:</strong> {{ item.size }}</span>
                    <span class="meta-item"><strong>状况:</strong> {{ item.clothingCondition }}</span>
                    <span class="meta-item"><strong>价格:</strong> ¥{{ item.price }}</span>
                    <span v-if="item.rentalPrice" class="meta-item"><strong>租赁价:</strong> ¥{{ item.rentalPrice }}/天</span>
                  </div>
                  <div class="clothing-description">{{ item.description }}</div>
                  <div class="clothing-actions">
                    <el-button type="primary" @click="approveClothing(item)">
                      <el-icon><Check /></el-icon>
                      批准
                    </el-button>
                    <el-button type="danger" @click="showRejectDialog(item)">
                      <el-icon><CircleClose /></el-icon>
                      拒绝
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </Card>
        </el-main>
      </el-container>
    </el-container>

    <!-- 拒绝对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝服装"
      width="500px"
      destroy-on-close
    >
      <el-form :model="rejectForm" ref="rejectFormRef" label-position="top">
        <el-form-item 
          prop="rejectionReason" 
          label="拒绝原因"
          :rules="[{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]"
        >
          <el-input
            v-model="rejectForm.rejectionReason"
            type="textarea"
            placeholder="请输入拒绝原因"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="rejectClothing" :loading="processingReject">
            确认拒绝
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Check, CircleClose } from '@element-plus/icons-vue'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import request from '../../utils/request'

const router = useRouter()
const username = ref('管理员')
const userAvatar = ref('')

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/allClothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/review', icon: 'View', title: '回收审核' }
]

// 分页和筛选
const page = ref(1)
const sizePerPage = ref(10)
const total = ref(0)
const searchQuery = ref('')
const loading = ref(false)
const clothingList = ref([])

// 拒绝对话框
const rejectDialogVisible = ref(false)
const processingReject = ref(false)
const rejectForm = ref({ rejectionReason: '' })
const rejectFormRef = ref(null)
const selectedItem = ref(null)

onMounted(() => {
  loadUserData()
  loadPendingClothing()
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const loadPendingClothing = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/v1/trade/admin/clothing/pending', {
      params: {
        page: page.value,
        sizePerPage: sizePerPage.value
      }
    })
    
    const data = response.data
    clothingList.value = data.data || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载待审核服装失败:', error)
    ElMessage.error('加载待审核服装失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (newSize) => {
  sizePerPage.value = newSize
  page.value = 1
  loadPendingClothing()
}

const handleCurrentChange = (newPage) => {
  page.value = newPage
  loadPendingClothing()
}

const getImageUrl = (imageUrls) => {
  if (!imageUrls) {
    return `https://picsum.photos/seed/clothing${Date.now()}/400/300`
  }
  
  const urls = imageUrls.split(',')
  if (urls.length > 0) {
    const firstUrl = urls[0]
    if (firstUrl.startsWith('http')) {
      return firstUrl
    } else {
      return `http://localhost:8080/uploads/${firstUrl}`
    }
  }
  
  return `https://picsum.photos/seed/clothing${Date.now()}/400/300`
}

const approveClothing = async (item) => {
  try {
    await request.put(`/api/v1/trade/admin/clothing/${item.id}/approve`)
    ElMessage.success('服装已批准')
    loadPendingClothing()
  } catch (error) {
    console.error('批准服装失败:', error)
    ElMessage.error('批准服装失败')
  }
}

const showRejectDialog = (item) => {
  selectedItem.value = item
  rejectForm.value = { rejectionReason: '' }
  rejectDialogVisible.value = true
}

const rejectClothing = async () => {
  if (!selectedItem.value) {
    return
  }
  
  processingReject.value = true
  try {
    await request.put(`/api/v1/trade/admin/clothing/${selectedItem.value.id}/reject`, null, {
      params: {
        rejectionReason: rejectForm.value.rejectionReason
      }
    })
    
    ElMessage.success('服装已拒绝')
    rejectDialogVisible.value = false
    loadPendingClothing()
  } catch (error) {
    console.error('拒绝服装失败:', error)
    ElMessage.error('拒绝服装失败')
  } finally {
    processingReject.value = false
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

.filter-card {
  margin-bottom: 24px;
}

.filter-form {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.clothing-list-card {
  margin-bottom: 40px;
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

.clothing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.clothing-item {
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.clothing-image {
  position: relative;
  margin-bottom: 16px;
  
  .item-image {
    width: 100%;
    height: 200px;
    border-radius: 8px;
  }
}

.clothing-info {
  .clothing-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 12px;
    line-height: 1.4;
  }
  
  .clothing-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 12px;
    
    .meta-item {
      font-size: 12px;
      color: #606266;
      background-color: #f9fafc;
      padding: 4px 8px;
      border-radius: 4px;
    }
  }
  
  .clothing-description {
    font-size: 14px;
    color: #606266;
    margin-bottom: 16px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .clothing-actions {
    display: flex;
    gap: 8px;
    
    .el-button {
      flex: 1;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .clothing-grid {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
