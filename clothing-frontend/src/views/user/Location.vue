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
            title="添加收纳位置"
            icon="Plus"
            class="add-location-card"
          >
            <div class="add-location-form">
              <el-input 
                  v-model="name" 
                  placeholder="收纳位置名称"
                  size="large"
                  :prefix-icon="LocationIcon"
                  :disabled="loading"
                />
              <el-button 
                type="primary" 
                @click="add" 
                style="margin-top:16px"
                size="large"
                :loading="loading"
                :disabled="!name.trim() || loading"
                class="add-button"
              >
                <el-icon class="button-icon"><Plus /></el-icon>
                添加位置
              </el-button>
            </div>
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
            title="收纳位置列表"
            icon="MapLocation"
            :badge="list.length"
            class="location-list-card"
            style="margin-top: 24px;"
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
              <el-table-column prop="name" label="位置名称" min-width="200">
                <template #default="scope">
                  <el-button 
                    type="primary" 
                    text
                    @click="showLocationDetail(scope.row)"
                  >
                    {{ scope.row.name }}
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="100" fixed="right">
                <template #default="scope">
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

          <!-- 收纳位置详细视图 -->
          <el-dialog
            v-model="detailDialogVisible"
            :title="`${selectedLocation?.name || ''} - 衣物详情`"
            width="80%"
            destroy-on-close
          >
            <div class="location-detail">
              <!-- 筛选和排序 -->
              <div class="filter-sort-container">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <el-select
                      v-model="filterType"
                      placeholder="按类型筛选"
                      size="large"
                      clearable
                    >
                      <el-option
                        v-for="type in clothingTypes"
                        :key="type"
                        :label="type"
                        :value="type"
                      />
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <el-select
                      v-model="filterSeason"
                      placeholder="按季节筛选"
                      size="large"
                      clearable
                    >
                      <el-option label="春季" value="春季" />
                      <el-option label="夏季" value="夏季" />
                      <el-option label="秋季" value="秋季" />
                      <el-option label="冬季" value="冬季" />
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <el-select
                      v-model="sortBy"
                      placeholder="排序方式"
                      size="large"
                    >
                      <el-option label="按名称排序" value="name" />
                      <el-option label="按穿着频率排序" value="wearFrequency" />
                      <el-option label="按收纳日期排序" value="storageDate" />
                    </el-select>
                  </el-col>
                </el-row>
              </div>

              <!-- 衣物列表 -->
              <el-table 
                :data="filteredAndSortedClothes"
                v-loading="clothesLoading"
                style="width: 100%; margin-top: 20px"
                stripe
                border
                :empty-text="clothesEmptyText"
              >
                <el-table-column prop="image" label="图片" min-width="100">
                  <template #default="scope">
                    <el-image
                      v-if="scope.row.image"
                      :src="scope.row.image"
                      fit="cover"
                      style="width: 60px; height: 60px; border-radius: 4px"
                      :preview-src-list="[scope.row.image]"
                    />
                    <span v-else>无图片</span>
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="名称" min-width="120" />
                <el-table-column prop="type" label="类型" min-width="100" />
                <el-table-column prop="season" label="季节" min-width="80" />
                <el-table-column label="操作" min-width="100" fixed="right">
                  <template #default="scope">
                    <el-button 
                      type="primary" 
                      @click="moveClothing(scope.row)"
                      size="small"
                    >
                      移动
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-dialog>

          <!-- 移动衣物对话框 -->
          <el-dialog
            v-model="moveDialogVisible"
            title="移动衣物"
            width="50%"
          >
            <div class="move-clothing-form">
              <el-form-item label="当前衣物">
                <span>{{ selectedClothing?.name || '' }}</span>
              </el-form-item>
              <el-form-item label="目标位置" required>
                <el-select
                  v-model="targetLocationId"
                  placeholder="选择目标位置"
                  size="large"
                  style="width: 100%"
                >
                  <el-option
                    v-for="location in list"
                    :key="location.id"
                    :label="location.name"
                    :value="location.id"
                    :disabled="location.id === selectedLocation?.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="moveDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmMove" :loading="moveLoading">确认移动</el-button>
              </span>
            </template>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onActivated, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, MapLocation, Delete, House, ShoppingBag, MapLocation as LocationIcon, Refresh, Message } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { locationQueryService } from '../../utils/queryService'
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

const name = ref('')
const list = ref([])
const loading = ref(false)
const tableLoading = ref(false)

// 详细视图相关
const detailDialogVisible = ref(false)
const selectedLocation = ref(null)
const clothesList = ref([])
const clothesLoading = ref(false)

// 筛选和排序
const filterType = ref('')
const filterSeason = ref('')
const sortBy = ref('name')

// 移动衣物相关
const moveDialogVisible = ref(false)
const selectedClothing = ref(null)
const targetLocationId = ref('')
const moveLoading = ref(false)

// 查询相关
const filterFields = [
  {
    key: 'name',
    label: '位置名称',
    type: 'input',
    props: {
      placeholder: '请输入位置名称'
    }
  }
]

const sortOptions = [
  { label: '按名称排序', value: 'name' },
  { label: '按创建时间排序', value: 'createTime' }
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

// 衣物类型列表
const clothingTypes = ref(['上衣', '裤子', '裙子', '外套', '内衣', '鞋子', '配饰'])

const emptyText = computed(() => {
  return tableLoading.value ? '加载中...' : '暂无收纳位置数据'
})

const clothesEmptyText = computed(() => {
  return clothesLoading.value ? '加载中...' : '该位置暂无衣物'
})

// 筛选和排序后的衣物列表
const filteredAndSortedClothes = computed(() => {
  let result = [...clothesList.value]
  
  // 按类型筛选
  if (filterType.value) {
    result = result.filter(item => item.type === filterType.value)
  }
  
  // 按季节筛选
  if (filterSeason.value) {
    result = result.filter(item => item.season === filterSeason.value)
  }
  
  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'wearFrequency':
        return (b.wearFrequency || 0) - (a.wearFrequency || 0)
      case 'storageDate':
        return new Date(b.storageDate || 0) - new Date(a.storageDate || 0)
      default:
        return 0
    }
  })
  
  return result
})

onMounted(() => {
  loadUserData()
  load()
})

// 当页面被激活时重新加载数据
onActivated(() => {
  load()
  if (detailDialogVisible.value && selectedLocation.value) {
    loadClothesByLocation(selectedLocation.value.id)
  }
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
      res = await locationQueryService.query({
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
      const response = await request.get('/location')
      list.value = Array.isArray(response.data) ? response.data : []
      pagination.total = list.value.length
    }
  } catch (error) {
    console.error('加载位置失败:', error)
    ElMessage.error('加载位置失败')
    list.value = []
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

const add = () => {
  if (!name.value.trim()) {
    ElMessage.warning('请输入收纳位置名称')
    return
  }
  
  loading.value = true
  request.post('/location', { name: name.value.trim() }).then(() => {
    ElMessage.success('添加成功')
    name.value = ''
    load()
  }).catch(error => {
    console.error('添加位置失败:', error)
    ElMessage.error('添加位置失败')
  }).finally(() => {
    loading.value = false
  })
}

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个收纳位置吗？此操作不可撤销。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete('/location/' + id)
    ElMessage.success('删除成功')
    load()
    // 如果删除的是当前查看的位置，关闭详情对话框
    if (selectedLocation.value && selectedLocation.value.id === id) {
      detailDialogVisible.value = false
      selectedLocation.value = null
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 显示收纳位置详细视图
const showLocationDetail = (location) => {
  selectedLocation.value = location
  detailDialogVisible.value = true
  loadClothesByLocation(location.id)
}

// 加载指定位置的衣物
const loadClothesByLocation = (locationId) => {
  clothesLoading.value = true
  request.get('/clothing/my').then(res => {
    // 从所有衣物中筛选出属于当前位置的衣物
    const allClothes = res.data || []
    clothesList.value = allClothes.filter(item => item.location === selectedLocation.value.name)
  }).catch(error => {
    console.error('加载衣物失败:', error)
    ElMessage.error('加载衣物失败')
    clothesList.value = []
  }).finally(() => {
    clothesLoading.value = false
  })
}

// 移动衣物
const moveClothing = (clothing) => {
  selectedClothing.value = clothing
  moveDialogVisible.value = true
  targetLocationId.value = ''
}

// 确认移动衣物
const confirmMove = async () => {
  if (!targetLocationId.value) {
    ElMessage.warning('请选择目标位置')
    return
  }
  
  // 找到目标位置的名称
  const targetLocation = list.value.find(location => location.id === targetLocationId.value)
  if (!targetLocation) {
    ElMessage.error('目标位置不存在')
    return
  }
  
  moveLoading.value = true
  try {
    // 尝试使用POST方法更新衣物
    await request.post(`/clothing/${selectedClothing.value.id}/update`, {
      location: targetLocation.name
    })
    ElMessage.success('移动成功')
    moveDialogVisible.value = false
    // 重新加载当前位置的衣物
    if (selectedLocation.value) {
      loadClothesByLocation(selectedLocation.value.id)
    }
  } catch (error) {
    console.error('移动衣物失败:', error)
    ElMessage.error('移动衣物失败')
  } finally {
    moveLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.add-location-card,
.location-list-card {
  margin-bottom: 24px;
}

.add-location-form {
  margin-top: 16px;
}

.button-icon {
  margin-right: 8px;
}

.filter-sort-container {
  background-color: #f9fafc;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.location-detail {
  width: 100%;
}

.move-clothing-form {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .add-location-form {
    margin-top: 12px;
  }
  
  .el-button {
    width: 100%;
  }
  
  .filter-sort-container {
    padding: 12px;
  }
  
  .el-row {
    .el-col {
      &:span {
        flex: 100%;
        max-width: 100%;
        margin-bottom: 12px;
      }
    }
  }
}
</style>