<template>
  <div class="user-home-container">
    <el-container class="user-layout">
      <Sidebar 
        :username="username"
        :user-role="'普通用户'"
        :user-avatar="userAvatar"
        :menu-items="menuItems"
        @logout="logout"
      />

      <el-container class="user-content">
        <el-main class="main-content">
          <!-- 发布回收流程 -->
          <Card 
            title="发布回收" 
            icon="Refresh"
            class="publish-recycle-card"
          >
            <el-steps :active="currentStep" finish-status="success" class="recycle-steps">
              <el-step title="照片上传" />
              <el-step title="地理位置" />
              <el-step title="衣物信息" />
              <el-step title="确认提交" />
            </el-steps>

            <div class="step-content">
              <!-- 步骤1：照片上传 -->
              <div v-if="currentStep === 0" class="step-upload">
                <el-upload
                  class="upload-demo"
                  :action="''"
                  :auto-upload="false"
                  :on-change="handleImageChange"
                  :on-remove="handleImageRemove"
                  :before-upload="beforeUpload"
                  :multiple="true"
                  :limit="5"
                  :on-exceed="handleExceed"
                  list-type="picture-card"
                >
                  <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  <template #file="{ file }">
                    <div class="image-preview">
                      <img :src="file.url" class="preview-image" />
                      <div class="image-actions">
                        <el-button size="small" circle @click.stop="rotateImage(file)">
                          <el-icon><Refresh /></el-icon>
                        </el-button>
                        <el-button size="small" type="danger" circle @click.stop="handleImageRemove(file)">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </div>
                  </template>
                </el-upload>
                <el-alert
                  :title="`已上传 ${images.length}/5 张照片`"
                  type="info"
                  :closable="false"
                  class="upload-info"
                />
              </div>

              <!-- 步骤2：地理位置 -->
              <div v-if="currentStep === 1" class="step-location">
                <el-form-item label="位置信息">
                  <el-button type="primary" @click="getCurrentLocation" :loading="locationLoading">
                    <el-icon><MapLocation /></el-icon>
                    获取当前位置
                  </el-button>
                  <el-input
                    v-model="form.address"
                    placeholder="请输入地址"
                    class="location-input"
                  />
                  <div v-if="form.latitude && form.longitude" class="location-info">
                    <p>纬度: {{ form.latitude }}</p>
                    <p>经度: {{ form.longitude }}</p>
                  </div>
                </el-form-item>
              </div>

              <!-- 步骤3：衣物信息 -->
              <div v-if="currentStep === 2" class="step-clothing-info">
                <el-form :model="form" ref="formRef" label-position="top">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item prop="clothingType" :rules="[{ required: true, message: '请选择衣物类型', trigger: 'change' }]">
                        <el-select v-model="form.clothingType" placeholder="衣物类型" size="large">
                          <el-option label="衬衫" value="衬衫" />
                          <el-option label="裤子" value="裤子" />
                          <el-option label="夹克" value="夹克" />
                          <el-option label="外套" value="外套" />
                          <el-option label="裙子" value="裙子" />
                          <el-option label="其他" value="其他" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="clothingCondition" :rules="[{ required: true, message: '请选择状况', trigger: 'change' }]">
                        <el-select v-model="form.clothingCondition" placeholder="状况" size="large">
                          <el-option label="全新" value="全新" />
                          <el-option label="几乎全新" value="几乎全新" />
                          <el-option label="轻度使用" value="轻度使用" />
                          <el-option label="磨损较重" value="磨损较重" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item prop="size" :rules="[{ required: true, message: '请输入尺码', trigger: 'blur' }]">
                        <el-input v-model="form.size" placeholder="尺码" size="large" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="brand">
                        <el-input v-model="form.brand" placeholder="品牌（选填）" size="large" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item prop="material">
                        <el-input v-model="form.material" placeholder="材质（选填）" size="large" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="qualityRating" :rules="[{ required: true, message: '请选择质量评级', trigger: 'change' }]">
                        <el-rate v-model="form.qualityRating" :max="5" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="24">
                      <el-form-item prop="notes">
                        <el-input
                          v-model="form.notes"
                          type="textarea"
                          placeholder="备注或特殊特征（选填）"
                          :rows="3"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item prop="recycleType" :rules="[{ required: true, message: '请选择回收类型', trigger: 'change' }]">
                        <el-select v-model="form.recycleType" placeholder="回收类型" size="large">
                          <el-option label="交易" value="交易" />
                          <el-option label="回收" value="回收" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="price">
                        <el-input v-model="form.price" type="number" placeholder="预估价值（选填）" size="large" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>

              <!-- 步骤4：确认提交 -->
              <div v-if="currentStep === 3" class="step-confirm">
                <el-card class="confirm-card">
                  <template #header>
                    <div class="card-header">
                      <span>回收信息确认</span>
                    </div>
                  </template>
                  <div class="confirm-content">
                    <div class="confirm-item">
                      <span class="label">衣物类型:</span>
                      <span class="value">{{ form.clothingType }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">状况:</span>
                      <span class="value">{{ form.clothingCondition }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">尺码:</span>
                      <span class="value">{{ form.size }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">品牌:</span>
                      <span class="value">{{ form.brand || '未填写' }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">材质:</span>
                      <span class="value">{{ form.material || '未填写' }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">质量评级:</span>
                      <span class="value">{{ '★'.repeat(form.qualityRating) }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">回收类型:</span>
                      <span class="value">{{ form.recycleType }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">地址:</span>
                      <span class="value">{{ form.address || '未填写' }}</span>
                    </div>
                    <div class="confirm-item">
                      <span class="label">照片数量:</span>
                      <span class="value">{{ images.length }} 张</span>
                    </div>
                  </div>
                </el-card>
              </div>

              <!-- 步骤导航 -->
              <div class="step-navigation">
                <el-button 
                  v-if="currentStep > 0" 
                  @click="prevStep"
                  :disabled="loading"
                >
                  上一步
                </el-button>
                <el-button 
                  v-if="currentStep < 3" 
                  type="primary" 
                  @click="nextStep"
                  :disabled="!canProceed || loading"
                >
                  下一步
                </el-button>
                <el-button 
                  v-if="currentStep === 3" 
                  type="primary" 
                  @click="submitRecycle"
                  :loading="loading"
                >
                  确认提交
                </el-button>
              </div>
            </div>
          </Card>

          <!-- 查询组件 -->
          <Card 
            title="高级搜索" 
            icon="Search"
            class="search-card"
          >
            <el-form :model="searchForm" label-width="100px" class="search-form">
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="衣物类型">
                    <el-select v-model="searchForm.clothingType" placeholder="请选择衣物类型">
                      <el-option label="衬衫" value="衬衫" />
                      <el-option label="裤子" value="裤子" />
                      <el-option label="夹克" value="夹克" />
                      <el-option label="外套" value="外套" />
                      <el-option label="裙子" value="裙子" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="回收类型">
                    <el-select v-model="searchForm.recycleType" placeholder="请选择回收类型">
                      <el-option label="交易" value="交易" />
                      <el-option label="回收" value="回收" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="请选择状态">
                      <el-option label="待审核" value="待审核" />
                      <el-option label="待处理" value="待处理" />
                      <el-option label="处理中" value="处理中" />
                      <el-option label="已完成" value="已完成" />
                      <el-option label="已取消" value="已取消" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="时间范围">
                    <el-date-picker
                      v-model="searchForm.dateRange"
                      type="daterange"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="关键词">
                    <el-input v-model="searchForm.keyword" placeholder="请输入关键词" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24" class="search-actions">
                  <el-button type="primary" @click="handleSearch">
                    搜索
                  </el-button>
                  <el-button @click="resetSearch">
                    重置
                  </el-button>
                  <el-button @click="exportExcel">
                    导出Excel
                  </el-button>
                </el-col>
              </el-row>
            </el-form>
          </Card>

          <!-- 回收列表 -->
          <Card 
            title="回收列表" 
            icon="List"
            :badge="list.length"
            class="recycle-list-card"
          >
            <el-table 
              :data="list" 
              v-loading="tableLoading"
              style="width: 100%"
              stripe
              border
              :empty-text="emptyText"
            >
              <el-table-column prop="clothingType" label="衣物类型" min-width="120" />
              <el-table-column prop="clothingCondition" label="状况" min-width="120" />
              <el-table-column prop="recycleType" label="回收类型" min-width="120" />
              <el-table-column label="图片" min-width="100">
                <template #default="scope">
                  <div v-if="scope.row.imageUrls" class="image-list">
                    <el-image
                      v-for="(url, index) in scope.row.imageUrls.split(',')"
                      :key="index"
                      :src="`http://localhost:8080/uploads/${url}`"
                      :preview-src-list="[`http://localhost:8080/uploads/${url}`]"
                      style="width: 50px; height: 50px; margin-right: 5px;"
                    />
                  </div>
                  <span v-else>无</span>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" min-width="150" />
              <el-table-column label="操作" min-width="200">
                <template #default="scope">
                  <el-button size="small" @click="editRecycle(scope.row)">
                    编辑
                  </el-button>
                  <el-button size="small" type="primary" @click="moveToTrade(scope.row)">
                    转到交易
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteRecycle(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-if="total > 0"
              class="pagination"
              :current-page="pagination.currentPage"
              :page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </Card>
        </el-main>
      </el-container>
    </el-container>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑回收记录"
      width="800px"
    >
      <el-form :model="editForm" ref="editFormRef" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="clothingType" :rules="[{ required: true, message: '请选择衣物类型', trigger: 'change' }]">
              <el-select v-model="editForm.clothingType" placeholder="衣物类型" size="large">
                <el-option label="衬衫" value="衬衫" />
                <el-option label="裤子" value="裤子" />
                <el-option label="夹克" value="夹克" />
                <el-option label="外套" value="外套" />
                <el-option label="裙子" value="裙子" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="clothingCondition" :rules="[{ required: true, message: '请选择状况', trigger: 'change' }]">
              <el-select v-model="editForm.clothingCondition" placeholder="状况" size="large">
                <el-option label="全新" value="全新" />
                <el-option label="几乎全新" value="几乎全新" />
                <el-option label="轻度使用" value="轻度使用" />
                <el-option label="磨损较重" value="磨损较重" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="size" :rules="[{ required: true, message: '请输入尺码', trigger: 'blur' }]">
              <el-input v-model="editForm.size" placeholder="尺码" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="brand">
              <el-input v-model="editForm.brand" placeholder="品牌（选填）" size="large" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="material">
              <el-input v-model="editForm.material" placeholder="材质（选填）" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="qualityRating" :rules="[{ required: true, message: '请选择质量评级', trigger: 'change' }]">
              <el-rate v-model="editForm.qualityRating" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item prop="notes">
              <el-input
                v-model="editForm.notes"
                type="textarea"
                placeholder="备注或特殊特征（选填）"
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="recycleType" :rules="[{ required: true, message: '请选择回收类型', trigger: 'change' }]">
              <el-select v-model="editForm.recycleType" placeholder="回收类型" size="large">
                <el-option label="交易" value="交易" />
                <el-option label="回收" value="回收" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="price">
              <el-input v-model="editForm.price" type="number" placeholder="预估价值（选填）" size="large" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="位置信息">
              <el-button type="primary" @click="getEditLocation" :loading="editLocationLoading">
                <el-icon><MapLocation /></el-icon>
                获取当前位置
              </el-button>
              <el-input
                v-model="editForm.address"
                placeholder="请输入地址"
                class="location-input"
              />
              <div v-if="editForm.latitude && editForm.longitude" class="location-info">
                <p>纬度: {{ editForm.latitude }}</p>
                <p>经度: {{ editForm.longitude }}</p>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="照片">
              <el-upload
                class="upload-demo"
                :action="''"
                :auto-upload="false"
                :on-change="handleEditImageChange"
                :on-remove="handleEditImageRemove"
                :before-upload="beforeUpload"
                :multiple="true"
                :limit="5"
                :on-exceed="handleExceed"
                list-type="picture-card"
                :file-list="editImages"
              >
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                <template #file="{ file }">
                  <div class="image-preview">
                    <img :src="file.url" class="preview-image" />
                    <div class="image-actions">
                      <el-button size="small" type="danger" circle @click.stop="handleEditImageRemove(file)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </template>
              </el-upload>
              <el-alert
                :title="`已上传 ${editImages.length}/5 张照片`"
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
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="editLoading">
            保存修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Goods, Document, ArrowRight, List, House, ShoppingBag, MapLocation, Plus, Delete, Message } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { recycleQueryService } from '../../utils/queryService'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'

const router = useRouter()
const route = useRoute()
const username = ref('用户')
const userAvatar = ref('')

// 表单数据
const form = reactive({
  clothingType: '',
  clothingCondition: '',
  size: '',
  brand: '',
  material: '',
  notes: '',
  qualityRating: 0,
  recycleType: '',
  price: null,
  address: '',
  latitude: null,
  longitude: null
})

// 编辑表单数据
const editForm = reactive({
  clothingType: '',
  clothingCondition: '',
  size: '',
  brand: '',
  material: '',
  notes: '',
  qualityRating: 0,
  recycleType: '',
  price: null,
  address: '',
  latitude: null,
  longitude: null
})

// 状态
const currentStep = ref(0)
const images = ref([])
const editImages = ref([])
const loading = ref(false)
const editLoading = ref(false)
const tableLoading = ref(false)
const locationLoading = ref(false)
const editLocationLoading = ref(false)
const formRef = ref()
const editFormRef = ref()
const dialogVisible = ref(false)
const currentEditId = ref(null)
const list = ref([])
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  clothingType: '',
  recycleType: '',
  status: '',
  dateRange: null,
  keyword: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20
})

// 空文本
const emptyText = ref('暂无回收记录')

// 步骤验证
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0: // 照片上传
      return images.value.length > 0
    case 1: // 地理位置
      return form.address || (form.latitude && form.longitude)
    case 2: // 衣物信息
      return form.clothingType && form.clothingCondition && form.size && form.recycleType && form.qualityRating > 0
    default:
      return true
  }
})

// 菜单
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

// 加载用户数据
const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '用户'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
}

// 加载回收列表
const load = async () => {
  try {
    tableLoading.value = true
    const response = await request.get('/recycle/my')
    list.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载回收列表失败:', error)
    ElMessage.error('加载回收列表失败')
    list.value = []
  } finally {
    tableLoading.value = false
  }
}

// 步骤导航
const nextStep = () => {
  if (canProceed.value) {
    currentStep.value++
    saveProgress()
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 图片处理
const handleImageChange = (file, fileList) => {
  images.value = fileList
  saveProgress()
}

const handleImageRemove = (file, fileList) => {
  images.value = fileList || images.value.filter(img => img.uid !== file.uid)
  saveProgress()
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

const rotateImage = (file) => {
  // 这里可以实现图片旋转功能
  ElMessage.info('图片旋转功能开发中')
}

// 地理位置
const getCurrentLocation = () => {
  locationLoading.value = true
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        form.latitude = position.coords.latitude
        form.longitude = position.coords.longitude
        
        // 使用用户提供的高德地图逆地理编码API获取详细地址
        const url = `https://restapi.amap.com/v3/geocode/regeo?location=${form.longitude},${form.latitude}&key=be262c006216c542747fce766130cee3`;
        
        fetch(url)
          .then(response => response.json())
          .then(data => {
            if (data.status === '1' && data.regeocode) {
              const addressComponent = data.regeocode.addressComponent;
              let detailedAddress = '';
              
              // 构建详细地址
              if (addressComponent.province) detailedAddress += addressComponent.province;
              if (addressComponent.city) detailedAddress += addressComponent.city;
              if (addressComponent.district) detailedAddress += addressComponent.district;
              if (addressComponent.township) detailedAddress += addressComponent.township;
              if (addressComponent.streetNumber && addressComponent.streetNumber.street) detailedAddress += addressComponent.streetNumber.street;
              if (addressComponent.streetNumber && addressComponent.streetNumber.number) detailedAddress += addressComponent.streetNumber.number;
              
              if (detailedAddress) {
                form.address = detailedAddress;
                ElMessage.success('获取位置成功');
              } else {
                form.address = `纬度: ${form.latitude}, 经度: ${form.longitude}`;
                ElMessage.warning('获取详细地址失败，仅显示经纬度');
              }
            } else {
              form.address = `纬度: ${form.latitude}, 经度: ${form.longitude}`;
              ElMessage.warning('获取详细地址失败，仅显示经纬度');
            }
            locationLoading.value = false;
          })
          .catch(error => {
            console.error('获取详细地址失败:', error);
            form.address = `纬度: ${form.latitude}, 经度: ${form.longitude}`;
            ElMessage.warning('获取详细地址失败，仅显示经纬度');
            locationLoading.value = false;
          });
      },
      (error) => {
        console.error('获取位置失败:', error)
        ElMessage.error('获取位置失败，请手动输入地址')
        locationLoading.value = false
      }
    )
  } else {
    ElMessage.error('浏览器不支持地理位置功能')
    locationLoading.value = false
  }
}

// 提交回收
const submitRecycle = () => {
  loading.value = true
  const formData = new FormData()
  formData.append('item', JSON.stringify(form))
  images.value.forEach((image, index) => {
    if (image.raw) {
      formData.append('images', image.raw)
    }
  })

  request.post('/recycle/publish', formData).then(() => {
    ElMessage.success('发布成功')
    resetForm()
    load()
  }).catch(error => {
    console.error('发布回收失败:', error)
    ElMessage.error('发布回收失败')
  }).finally(() => {
    loading.value = false
  })
}

// 提交编辑
const submitEdit = () => {
  editLoading.value = true
  const formData = new FormData()
  // 排除不需要编辑的字段
  const editData = {
    clothingType: editForm.clothingType,
    clothingCondition: editForm.clothingCondition,
    size: editForm.size,
    brand: editForm.brand,
    material: editForm.material,
    notes: editForm.notes,
    qualityRating: editForm.qualityRating,
    recycleType: editForm.recycleType,
    price: editForm.price,
    address: editForm.address,
    latitude: editForm.latitude,
    longitude: editForm.longitude
  }
  formData.append('item', JSON.stringify(editData))
  editImages.value.forEach((image, index) => {
    if (image.raw) {
      formData.append('images', image.raw)
    }
  })

  request.put(`/recycle/update/${currentEditId.value}`, formData).then(() => {
    ElMessage.success('编辑成功')
    dialogVisible.value = false
    load()
  }).catch(error => {
    console.error('编辑失败:', error)
    ElMessage.error('编辑失败')
  }).finally(() => {
    editLoading.value = false
  })
}

// 编辑图片处理
const handleEditImageChange = (file, fileList) => {
  editImages.value = fileList
}

const handleEditImageRemove = (file, fileList) => {
  editImages.value = fileList || editImages.value.filter(img => img.uid !== file.uid)
}

// 编辑获取位置
const getEditLocation = () => {
  editLocationLoading.value = true
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        editForm.latitude = position.coords.latitude
        editForm.longitude = position.coords.longitude
        
        // 使用用户提供的高德地图逆地理编码API获取详细地址
        const url = `https://restapi.amap.com/v3/geocode/regeo?location=${editForm.longitude},${editForm.latitude}&key=be262c006216c542747fce766130cee3`;
        
        fetch(url)
          .then(response => response.json())
          .then(data => {
            if (data.status === '1' && data.regeocode) {
              const addressComponent = data.regeocode.addressComponent;
              let detailedAddress = '';
              
              // 构建详细地址
              if (addressComponent.province) detailedAddress += addressComponent.province;
              if (addressComponent.city) detailedAddress += addressComponent.city;
              if (addressComponent.district) detailedAddress += addressComponent.district;
              if (addressComponent.township) detailedAddress += addressComponent.township;
              if (addressComponent.streetNumber && addressComponent.streetNumber.street) detailedAddress += addressComponent.streetNumber.street;
              if (addressComponent.streetNumber && addressComponent.streetNumber.number) detailedAddress += addressComponent.streetNumber.number;
              
              if (detailedAddress) {
                editForm.address = detailedAddress;
                ElMessage.success('获取位置成功');
              } else {
                editForm.address = `纬度: ${editForm.latitude}, 经度: ${editForm.longitude}`;
                ElMessage.warning('获取详细地址失败，仅显示经纬度');
              }
            } else {
              editForm.address = `纬度: ${editForm.latitude}, 经度: ${editForm.longitude}`;
              ElMessage.warning('获取详细地址失败，仅显示经纬度');
            }
            editLocationLoading.value = false;
          })
          .catch(error => {
            console.error('获取详细地址失败:', error);
            editForm.address = `纬度: ${editForm.latitude}, 经度: ${editForm.longitude}`;
            ElMessage.warning('获取详细地址失败，仅显示经纬度');
            editLocationLoading.value = false;
          });
      },
      (error) => {
        console.error('获取位置失败:', error)
        ElMessage.error('获取位置失败，请手动输入地址')
        editLocationLoading.value = false
      }
    )
  } else {
    ElMessage.error('浏览器不支持地理位置功能')
    editLocationLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  tableLoading.value = true
  const params = {
    clothingType: searchForm.clothingType,
    recycleType: searchForm.recycleType,
    status: searchForm.status,
    keyword: searchForm.keyword
  }
  
  if (searchForm.dateRange) {
    params.startDate = searchForm.dateRange[0]
    params.endDate = searchForm.dateRange[1]
  }

  request.get('/recycle/search', { params }).then(response => {
    list.value = Array.isArray(response.data) ? response.data : []
    total.value = list.value.length
    emptyText.value = list.value.length === 0 ? '暂无符合条件的回收记录' : '暂无回收记录'
  }).catch(error => {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
    list.value = []
  }).finally(() => {
    tableLoading.value = false
  })
}

// 重置搜索
const resetSearch = () => {
  searchForm.clothingType = ''
  searchForm.recycleType = ''
  searchForm.status = ''
  searchForm.dateRange = null
  searchForm.keyword = ''
  load()
}

// 导出Excel
const exportExcel = () => {
  const params = {
    clothingType: searchForm.clothingType,
    recycleType: searchForm.recycleType,
    status: searchForm.status,
    keyword: searchForm.keyword
  }
  
  if (searchForm.dateRange) {
    params.startDate = searchForm.dateRange[0]
    params.endDate = searchForm.dateRange[1]
  }

  // 构建导出URL
  let url = '/recycle/export?'
  Object.keys(params).forEach(key => {
    if (params[key]) {
      url += `${key}=${encodeURIComponent(params[key])}&`
    }
  })
  url = url.slice(0, -1)

  // 打开新窗口下载
  window.open(url, '_blank')
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 这里可以根据需要重新加载数据
}

const handleCurrentChange = (current) => {
  pagination.currentPage = current
  // 这里可以根据需要重新加载数据
}

// 编辑回收
const editRecycle = (item) => {
  // 打开编辑对话框
  dialogVisible.value = true
  // 复制当前项的数据到编辑表单
  Object.assign(editForm, item)
  // 处理图片
  editImages.value = []
  if (item.imageUrls) {
    const imageUrls = item.imageUrls.split(',')
    imageUrls.forEach(url => {
      editImages.value.push({
        url: `http://localhost:8080/uploads/${url}`,
        name: url
      })
    })
  }
  // 保存当前编辑的ID
  currentEditId.value = item.id
}

// 删除回收
const deleteRecycle = (id) => {
  ElMessageBox.confirm('确定要删除这个回收项吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/recycle/delete/${id}`).then(() => {
      ElMessage.success('删除成功')
      load()
    }).catch(error => {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    })
  }).catch(() => {
    // 取消删除
  })
}

// 保存进度
const saveProgress = () => {
  localStorage.setItem('recycleProgress', JSON.stringify({
    step: currentStep.value,
    form: form,
    images: images.value.map(img => img.name)
  }))
}

// 加载进度
const loadProgress = () => {
  const saved = localStorage.getItem('recycleProgress')
  if (saved) {
    try {
      const progress = JSON.parse(saved)
      currentStep.value = progress.step
      Object.assign(form, progress.form)
      // 注意：这里只恢复了图片名称，实际文件需要重新选择
    } catch (e) {
      console.error('加载进度失败:', e)
    }
  }
}

// 重置表单
const resetForm = () => {
  currentStep.value = 0
  Object.keys(form).forEach(key => {
    form[key] = ''
  })
  form.qualityRating = 0
  form.price = null
  form.latitude = null
  form.longitude = null
  images.value = []
  localStorage.removeItem('recycleProgress')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '待处理': 'warning',
    '处理中': 'info',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return statusMap[status] || 'default'
}

// 生命周期
onMounted(() => {
  loadUserData()
  load()
  loadProgress()
  // 检查是否有从衣物管理页面传递过来的数据
  checkClothingToRecycle()
})

// 检查是否有从衣物管理页面传递过来的数据
const checkClothingToRecycle = () => {
  const savedData = localStorage.getItem('clothingToRecycleData')
  if (savedData) {
    try {
      const recycleData = JSON.parse(savedData)
      // 填充表单数据
      Object.assign(form, recycleData)
      // 如果有图片，处理图片
      if (recycleData.image) {
        const imageUrl = recycleData.image.includes('http') ? 
                         recycleData.image : 
                         `http://localhost:8080/uploads/${recycleData.image}`
        // 跳转到步骤1（照片上传），让用户可以选择是否重新上传照片
        currentStep.value = 1
        ElMessage.info('已导入衣物信息，请继续填写完整')
      } else {
        currentStep.value = 1
        ElMessage.info('已导入衣物信息，请继续填写完整')
      }
      // 清除 localStorage 中的数据
      localStorage.removeItem('clothingToRecycleData')
      // 保存进度
      saveProgress()
    } catch (e) {
      console.error('解析回收数据失败:', e)
      localStorage.removeItem('clothingToRecycleData')
    }
  }
}

// 监听表单变化，自动保存进度
watch(form, saveProgress, { deep: true })

// 转到交易页面
const moveToTrade = (item) => {
  // 准备要传递到交易页面的数据
  const tradeData = {
    title: `${item.clothingType} - ${item.clothingCondition}`,
    category: item.clothingType === '衬衫' || item.clothingType === '夹克' || item.clothingType === '外套' ? '上衣' : 
              item.clothingType === '裤子' ? '裤子' : 
              item.clothingType === '裙子' ? '裙子' : '其他',
    size: item.size,
    condition: item.clothingCondition,
    price: item.price || 0,
    description: item.notes || '',
    images: item.imageUrls ? item.imageUrls.split(',').map(url => `http://localhost:8080/uploads/${url}`) : []
  }
  
  // 存储到 localStorage 中，以便在交易页面获取
  localStorage.setItem('recycleToTradeData', JSON.stringify(tradeData))
  
  // 跳转到交易页面
  router.push('/user/trade')
  
  // 显示提示信息
  ElMessage.success('已准备将衣物转移到交易页面')
}
</script>

<style lang="scss" scoped>
.publish-recycle-card,
.recycle-list-card {
  margin-bottom: 24px;
}

.recycle-steps {
  margin-bottom: 30px;
}

.step-content {
  margin-top: 20px;
}

.upload-demo {
  margin-bottom: 20px;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: flex-end;
  padding: 5px;
}

.upload-info {
  margin-top: 10px;
}

.location-input {
  margin-top: 10px;
  width: 100%;
}

.location-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.step-clothing-info {
  max-width: 800px;
  margin: 0 auto;
}

.confirm-card {
  margin-top: 20px;
}

.confirm-content {
  margin-top: 20px;
}

.confirm-item {
  display: flex;
  margin-bottom: 10px;
}

.confirm-item .label {
  width: 100px;
  font-weight: bold;
}

.confirm-item .value {
  flex: 1;
}

.step-navigation {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .step-content {
    padding: 0 10px;
  }
  
  .el-steps {
    font-size: 12px;
  }
  
  .step-navigation {
    flex-direction: column;
  }
  
  .step-navigation button {
    width: 100%;
  }
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.image-list .el-image {
  margin-bottom: 5px;
}
</style>