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
            title="添加衣物"
            icon="Plus"
            class="add-clothing-card"
          >
            <el-form 
              :model="form" 
              ref="formRef"
              label-position="top"
              class="form"
            >
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item prop="name" :rules="[{ required: true, message: '请输入衣物名称', trigger: 'blur' }]">
                    <el-input v-model="form.name" placeholder="衣物名称" size="large"/>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="type" :rules="[{ required: true, message: '请输入类型', trigger: 'blur' }]">
                    <el-input v-model="form.type" placeholder="类型" size="large"/>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="size" :rules="[{ required: true, message: '请输入尺码', trigger: 'blur' }]">
                    <el-input v-model="form.size" placeholder="尺码" size="large"/>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item prop="season" :rules="[{ required: true, message: '请选择季节', trigger: 'change' }]">
                    <el-select v-model="form.season" placeholder="季节" size="large">
                      <el-option label="春季" value="春季"></el-option>
                      <el-option label="夏季" value="夏季"></el-option>
                      <el-option label="秋季" value="秋季"></el-option>
                      <el-option label="冬季" value="冬季"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
                    <el-select v-model="form.status" placeholder="状态" size="large">
                      <el-option label="全新" value="全新"></el-option>
                      <el-option label="良好" value="良好"></el-option>
                      <el-option label="一般" value="一般"></el-option>
                      <el-option label="较差" value="较差"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="location" :rules="[{ required: true, message: '请输入位置', trigger: 'blur' }]">
                    <el-input v-model="form.location" placeholder="位置" size="large"/>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="purchaseDate" :rules="[{ required: true, message: '请选择购买时间', trigger: 'change' }]">
                    <el-date-picker 
                      v-model="form.purchaseDate" 
                      type="date" 
                      placeholder="购买时间"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      size="large"
                    >
                    </el-date-picker>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="lastWearDate" :rules="[{ required: false }]">
                    <el-date-picker 
                      v-model="form.lastWearDate" 
                      type="date" 
                      placeholder="最后一次穿戴时间(可选)"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      size="large"
                    >
                    </el-date-picker>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="24">
                  <el-form-item label="衣物图片" prop="image">
                    <el-upload
                      class="upload-demo"
                      :action="''"
                      :auto-upload="false"
                      :on-change="handleImageChange"
                      :on-remove="handleImageRemove"
                      :before-upload="beforeUpload"
                      :limit="1"
                      :on-exceed="handleExceed"
                      list-type="picture-card"
                      :file-list="fileList"
                    >
                      <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                      <template #file="{ file }">
                        <div class="image-preview">
                          <img :src="file.url" class="preview-image" />
                          <div class="image-actions">
                            <el-button size="small" type="danger" circle @click.stop="handleImageRemove(file)">
                              <el-icon><Delete /></el-icon>
                            </el-button>
                          </div>
                        </div>
                      </template>
                    </el-upload>
                    <template #tip>
                      <div class="el-upload__tip">
                        请上传JPG、PNG、GIF格式的图片，大小不超过2MB
                      </div>
                    </template>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row>
                <el-col :span="24" class="form-actions">
                  <el-button 
                    type="primary" 
                    @click="add" 
                    :loading="loading"
                    size="large"
                    class="add-button"
                  >
                    <el-icon class="button-icon"><Plus /></el-icon>
                    添加衣物
                  </el-button>
                </el-col>
              </el-row>
            </el-form>
          </Card>

          <!-- 查询组件 -->
          <QueryComponent
            :filter-fields="filterFields"
            :sort-options="sortOptions"
            @search="handleSearch"
            @reset="handleReset"
            @sort="handleSort"
            @page-change="handlePageChange"
          />

          <Card
            title="衣物列表"
            icon="Collection"
            :badge="list.length"
            class="clothing-list-card"
          >
            <el-table 
              :data="list" 
              v-loading="tableLoading"
              style="width: 100%"
              stripe
              border
              :empty-text="emptyText"
              class="table"
            >
              <el-table-column prop="image" label="图片" min-width="100">
                <template #default="scope">
                  <el-image
                    v-if="scope.row.image"
                    :src="scope.row.image.includes('http') ? scope.row.image : `http://localhost:8080/uploads/${scope.row.image}`"
                    fit="cover"
                    style="width: 80px; height: 80px; border-radius: 4px"
                    :preview-src-list="[scope.row.image.includes('http') ? scope.row.image : `http://localhost:8080/uploads/${scope.row.image}`]"
                  />
                  <span v-else>无图片</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="名称" min-width="120" />
              <el-table-column prop="type" label="类型" min-width="100" />
              <el-table-column prop="size" label="尺码" min-width="80" />
              <el-table-column prop="season" label="季节" min-width="80" />
              <el-table-column prop="status" label="状态" min-width="80" />
              <el-table-column prop="location" label="位置" min-width="120" />
              <el-table-column prop="purchaseDate" label="购买时间" min-width="120" />
              <el-table-column prop="lastWearDate" label="最后穿戴" min-width="120" />
              <el-table-column label="操作" min-width="200" fixed="right">
                <template #default="scope">
                  <el-button 
                    type="primary" 
                    @click="edit(scope.row)" 
                    size="small"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    type="primary" 
                    @click="moveToRecycle(scope.row)" 
                    size="small"
                  >
                    转到回收
                  </el-button>
                  <el-button 
                    type="danger" 
                    @click="del(scope.row.id)" 
                    size="small"
                    circle
                    :icon="Delete"
                    title="删除"
                  />
                </template>
              </el-table-column>
            </el-table>
          </Card>
        </el-main>
      </el-container>
    </el-container>

    <!-- 编辑衣物对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑衣物"
      width="80%"
      destroy-on-close
    >
      <el-form 
        :model="editForm" 
        ref="editFormRef"
        label-position="top"
        class="form"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item prop="name" :rules="[{ required: true, message: '请输入衣物名称', trigger: 'blur' }]">
              <el-input v-model="editForm.name" placeholder="衣物名称" size="large"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="type" :rules="[{ required: true, message: '请输入类型', trigger: 'blur' }]">
              <el-input v-model="editForm.type" placeholder="类型" size="large"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="size" :rules="[{ required: true, message: '请输入尺码', trigger: 'blur' }]">
              <el-input v-model="editForm.size" placeholder="尺码" size="large"/>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item prop="season" :rules="[{ required: true, message: '请选择季节', trigger: 'change' }]">
              <el-select v-model="editForm.season" placeholder="季节" size="large">
                <el-option label="春季" value="春季"></el-option>
                <el-option label="夏季" value="夏季"></el-option>
                <el-option label="秋季" value="秋季"></el-option>
                <el-option label="冬季" value="冬季"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
              <el-select v-model="editForm.status" placeholder="状态" size="large">
                <el-option label="全新" value="全新"></el-option>
                <el-option label="良好" value="良好"></el-option>
                <el-option label="一般" value="一般"></el-option>
                <el-option label="较差" value="较差"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="location" :rules="[{ required: true, message: '请输入位置', trigger: 'blur' }]">
              <el-input v-model="editForm.location" placeholder="位置" size="large"/>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="purchaseDate" :rules="[{ required: true, message: '请选择购买时间', trigger: 'change' }]">
              <el-date-picker 
                v-model="editForm.purchaseDate" 
                type="date" 
                placeholder="购买时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                size="large"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="lastWearDate" :rules="[{ required: false }]">
              <el-date-picker 
                v-model="editForm.lastWearDate" 
                type="date" 
                placeholder="最后一次穿戴时间(可选)"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                size="large"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="衣物图片" prop="image">
              <el-upload
                class="upload-demo"
                :action="''"
                :auto-upload="false"
                :on-change="handleEditImageChange"
                :on-remove="handleEditImageRemove"
                :before-upload="beforeUpload"
                :limit="1"
                :on-exceed="handleExceed"
                list-type="picture-card"
                :file-list="editFileList"
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
              <div class="el-upload__tip" style="margin-top: 8px">
                请上传JPG、PNG、GIF格式的图片，大小不超过2MB
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmEdit" :loading="editLoading">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, computed, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Collection, Delete, House, ShoppingBag, MapLocation, Refresh, Message } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { clothingQueryService } from '../../utils/queryService'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'
import QueryComponent from '../../components/QueryComponent.vue'

const router = useRouter()
const route = useRoute()
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

const form = reactive({
  name: '',
  type: '',
  size: '',
  season: '',
  status: '',
  location: '',
  purchaseDate: '',
  lastWearDate: '',
  image: ''
})

const list = ref([])
const loading = ref(false)
const tableLoading = ref(false)
const formRef = ref()
const fileList = ref([])
const imageFile = ref(null)

// 编辑相关
const editDialogVisible = ref(false)
const editForm = reactive({
  id: '',
  name: '',
  type: '',
  size: '',
  season: '',
  status: '',
  location: '',
  purchaseDate: '',
  lastWearDate: '',
  image: ''
})
const editFormRef = ref()
const editFileList = ref([])
const editImageFile = ref(null)
const editLoading = ref(false)

// 查询相关
const filterFields = [
  {
    key: 'type',
    label: '类型',
    type: 'input',
    props: {
      placeholder: '请输入类型'
    }
  },
  {
    key: 'season',
    label: '季节',
    type: 'select',
    props: {
      placeholder: '请选择季节',
      options: [
        { label: '春季', value: '春季' },
        { label: '夏季', value: '夏季' },
        { label: '秋季', value: '秋季' },
        { label: '冬季', value: '冬季' }
      ]
    }
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    props: {
      placeholder: '请选择状态',
      options: [
        { label: '全新', value: '全新' },
        { label: '良好', value: '良好' },
        { label: '一般', value: '一般' },
        { label: '较差', value: '较差' }
      ]
    }
  },
  {
    key: 'location',
    label: '位置',
    type: 'input',
    props: {
      placeholder: '请输入位置'
    }
  },
  {
    key: 'purchaseDateStart',
    label: '购买时间开始',
    type: 'date',
    props: {
      type: 'date',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: '选择开始日期'
    }
  },
  {
    key: 'purchaseDateEnd',
    label: '购买时间结束',
    type: 'date',
    props: {
      type: 'date',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: '选择结束日期'
    }
  }
]

const sortOptions = [
  { label: '按名称排序', value: 'name' },
  { label: '按购买时间排序', value: 'purchaseDate' },
  { label: '按最后穿戴时间排序', value: 'lastWearDate' }
]

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const queryParams = reactive({
  keyword: '',
  filters: {},
  sortBy: ''
})

const emptyText = computed(() => {
  return tableLoading.value ? '加载中...' : '暂无衣物数据'
})

onMounted(() => {
  loadUserData()
  load()
})

// 当页面被激活时重新加载数据
onActivated(() => {
  load()
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '用户'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square`
}

const load = async (isQuery = false) => {
  try {
    tableLoading.value = true
    let res
    if (isQuery) {
      // 使用查询服务执行分页查询
      res = await clothingQueryService.query({
        keyword: queryParams.keyword,
        filters: queryParams.filters,
        sortBy: queryParams.sortBy,
        page: pagination.currentPage,
        pageSize: pagination.pageSize
      })
      // 处理查询服务返回的数据
      list.value = Array.isArray(res.data) ? res.data : []
      pagination.total = res.total || list.value.length
    } else {
      // 加载所有数据（用于初始加载）
      const response = await request.get('/clothing/my')
      list.value = Array.isArray(response.data) ? response.data : []
      pagination.total = list.value.length
    }
  } catch (error) {
    console.error('加载衣物列表失败:', error)
    ElMessage.error('加载衣物列表失败: ' + (error.response?.data?.message || error.message))
    list.value = [] // 确保列表为空数组而不是undefined
  } finally {
    tableLoading.value = false
  }
}

// 处理搜索
const handleSearch = (params) => {
  queryParams.keyword = params.keyword
  queryParams.filters = params.filters
  queryParams.sortBy = params.sortBy
  pagination.currentPage = params.page
  pagination.pageSize = params.pageSize
  load(true)
}

// 处理重置
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.filters = {}
  queryParams.sortBy = ''
  pagination.currentPage = 1
  load()
}

// 处理排序
const handleSort = (sortBy) => {
  queryParams.sortBy = sortBy
  load(true)
}

// 处理分页变化
const handlePageChange = (pageInfo) => {
  pagination.currentPage = pageInfo.currentPage
  pagination.pageSize = pageInfo.pageSize
  load(true)
}

const handleImageChange = (file, fileList) => {
  fileList.value = fileList
  // 直接使用file.raw，不需要检查status
  if (file.raw) {
    imageFile.value = file.raw
    // 为了预览，我们可以创建一个临时URL
    if (typeof URL !== 'undefined') {
      file.url = URL.createObjectURL(file.raw)
    }
  }
}

const handleImageRemove = (file, fileList) => {
  imageFile.value = null
  fileList.value = fileList || []
}

const beforeUpload = (file) => {
  // 验证文件类型
  const isImage = /\.(jpg|jpeg|png|gif)$/i.test(file.name)
  if (!isImage) {
    ElMessage.error('只能上传JPG、PNG、GIF格式的图片')
    return false
  }
  
  // 验证文件大小
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  
  return true
}

const formatFileSize = (size) => {
  if (!size) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const add = async (retryCount = 0) => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  // 验证必填字段
  if (!form.name.trim()) {
    ElMessage.warning('请输入衣物名称')
    return
  }
  if (!form.type.trim()) {
    ElMessage.warning('请输入类型')
    return
  }
  if (!form.size.trim()) {
    ElMessage.warning('请输入尺码')
    return
  }
  if (!form.location.trim()) {
    ElMessage.warning('请输入位置')
    return
  }
  
  // 为测试添加默认值
  if (!form.season) {
    form.season = '春季'
  }
  if (!form.status) {
    form.status = '良好'
  }
  if (!form.purchaseDate) {
    form.purchaseDate = new Date().toISOString().split('T')[0]
  }

  try {
    loading.value = true
    const formData = new FormData()
    
    formData.append('name', form.name.trim())
    formData.append('type', form.type.trim())
    formData.append('size', form.size.trim())
    formData.append('season', form.season)
    formData.append('status', form.status)
    formData.append('location', form.location.trim())
    formData.append('purchaseDate', form.purchaseDate)
    
    // 只有当穿戴时间有值时才添加
    if (form.lastWearDate) {
      formData.append('lastWearDate', form.lastWearDate)
    }
    
    // 添加图片文件
    if (imageFile.value) {
      formData.append('image', imageFile.value)
    }

    await request.post('/clothing/add', formData)
    ElMessage.success('添加成功')
    
    // 清空表单
    Object.keys(form).forEach(key => {
      form[key] = ''
    })
    
    // 清空文件列表
    fileList.value = []
    imageFile.value = null
    
    // 重新加载列表
    await load()
  } catch (error) {
    console.error('添加衣物失败:', error)
    
    // 失败重试机制
    if (retryCount < 2) {
      const { value: retry } = await ElMessageBox.confirm(
        `添加失败：${error.response?.data?.message || error.message}\n是否重试？`,
        '上传失败',
        {
          confirmButtonText: '重试',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      if (retry) {
        await add(retryCount + 1)
        return
      }
    } else {
      ElMessage.error('添加失败：' + (error.response?.data?.message || error.message))
    }
  } finally {
    loading.value = false
  }
}

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这件衣物吗？此操作不可撤销。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete('/clothing/' + id)
    ElMessage.success('删除成功')
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }
}

// 编辑衣物
const edit = (clothing) => {
  console.log('edit函数被调用', clothing)
  // 填充表单数据
  editForm.id = clothing.id
  editForm.name = clothing.name
  editForm.type = clothing.type
  editForm.size = clothing.size
  editForm.season = clothing.season
  editForm.status = clothing.status
  editForm.location = clothing.location
  editForm.purchaseDate = clothing.purchaseDate
  editForm.lastWearDate = clothing.lastWearDate
  editForm.image = clothing.image
  
  // 重置文件列表
  editFileList.value = []
  editImageFile.value = null
  
  // 如果有图片，添加到文件列表
  if (clothing.image) {
    const imageUrl = clothing.image.includes('http') ? clothing.image : `http://localhost:8080/uploads/${clothing.image}`
    editFileList.value = [{
      name: '当前图片',
      url: imageUrl,
      uid: Date.now().toString()
    }]
  }
  
  // 打开编辑对话框
  editDialogVisible.value = true
  console.log('editDialogVisible.value:', editDialogVisible.value)
}

// 处理编辑时的图片上传
const handleEditImageChange = (file, fileList) => {
  editFileList.value = fileList
  if (file.raw) {
    editImageFile.value = file.raw
    // 为了预览，创建临时URL
    if (typeof URL !== 'undefined') {
      file.url = URL.createObjectURL(file.raw)
    }
  }
}

// 处理编辑时的图片移除
const handleEditImageRemove = (file, fileList) => {
  editImageFile.value = null
  editFileList.value = fileList || []
  editForm.image = ''
}

// 处理超出限制
const handleExceed = () => {
  ElMessage.error('最多只能上传 1 张照片！')
}

// 确认修改
const confirmEdit = async () => {
  // 验证表单
  if (!editForm.name.trim()) {
    ElMessage.warning('请输入衣物名称')
    return
  }
  if (!editForm.type.trim()) {
    ElMessage.warning('请输入类型')
    return
  }
  if (!editForm.size.trim()) {
    ElMessage.warning('请输入尺码')
    return
  }
  if (!editForm.location.trim()) {
    ElMessage.warning('请输入位置')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要修改这件衣物的信息吗？', '修改确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    editLoading.value = true
    
    // 准备FormData
    const formData = new FormData()
    formData.append('id', editForm.id)
    formData.append('name', editForm.name.trim())
    formData.append('type', editForm.type.trim())
    formData.append('size', editForm.size.trim())
    formData.append('season', editForm.season)
    formData.append('status', editForm.status)
    formData.append('location', editForm.location.trim())
    formData.append('purchaseDate', editForm.purchaseDate)
    
    if (editForm.lastWearDate) {
      formData.append('lastWearDate', editForm.lastWearDate)
    }
    
    if (editImageFile.value) {
      formData.append('image', editImageFile.value)
    }

    // 使用新的更新API
    await request.post('/clothing/update', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    
    // 重置文件列表
    editFileList.value = []
    editImageFile.value = null
    
    // 重新加载列表
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改衣物失败:', error)
      ElMessage.error('修改失败：' + (error.response?.data?.message || error.message))
    }
  } finally {
    editLoading.value = false
  }
}

// 转到回收页面
const moveToRecycle = (item) => {
  // 准备要传递到回收页面的数据
  const recycleData = {
    clothingType: item.type,
    clothingCondition: item.status,
    size: item.size,
    brand: '',
    material: '',
    notes: `从衣物管理导入：${item.name}`,
    qualityRating: item.status === '全新' ? 5 : 
                     item.status === '良好' ? 4 : 
                     item.status === '一般' ? 3 : 2,
    recycleType: '回收',
    price: 0,
    address: item.location || '',
    latitude: null,
    longitude: null,
    image: item.image
  }
  
  // 存储到 localStorage 中，以便在回收页面获取
  localStorage.setItem('clothingToRecycleData', JSON.stringify(recycleData))
  
  // 跳转到回收页面
  router.push('/user/recycle')
  
  // 显示提示信息
  ElMessage.success('已准备将衣物转移到回收页面')
}
</script>

<style lang="scss" scoped>
.add-clothing-card,
.clothing-list-card {
  margin-bottom: 24px;
}

.upload-file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background-color: #f9fafc;
  border-radius: 8px;
  margin-top: 8px;
  
  .file-name {
    flex: 1;
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .file-size {
    font-size: 12px;
    color: #909399;
  }
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .el-row {
    .el-col {
      &:span {
        flex: 100%;
        max-width: 100%;
      }
    }
  }
}

@media (max-width: 768px) {
  .form {
    .el-form-item {
      margin-bottom: 12px;
    }
  }
  
  .form-actions {
    margin-top: 16px;
  }
  
  .add-button {
    width: 100%;
  }
  
  .upload-file-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    
    .file-name {
      width: 100%;
    }
  }
}
</style>