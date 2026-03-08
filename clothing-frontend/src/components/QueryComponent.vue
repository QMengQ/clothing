<template>
  <div class="query-component">
    <!-- 搜索框 -->
    <div class="search-container">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索..."
        size="large"
        prefix-icon="Search"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <p>&nbsp;&nbsp;&nbsp;</p>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-container" v-if="filterFields.length > 0">
      <el-collapse v-model="activeFilterNames">
        <el-collapse-item title="高级筛选" name="filter">
          <el-form :model="filterForm" label-position="top" class="filter-form">
            <el-row :gutter="20">
              <el-col
                v-for="field in filterFields"
                :key="field.key"
                :span="field.span || 8"
              >
                <el-form-item :label="field.label">
                  <component
                    :is="getComponentByType(field.type)"
                    v-model="filterForm[field.key]"
                    v-bind="field.props"
                    :clearable="true"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 排序 -->
    <div class="sort-container" v-if="sortOptions.length > 0">
      <el-select
        v-model="sortBy"
        placeholder="排序方式"
        size="large"
        @change="handleSort"
      >
        <el-option
          v-for="option in sortOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 查询条件保存 -->
    <div class="save-query-container" v-if="showSaveQuery">
      <el-dropdown>
        <el-button type="info">
          <el-icon><Star /></el-icon>
          保存查询条件
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="showSaveQueryDialog = true">
              保存当前条件
            </el-dropdown-item>
            <el-dropdown-item divided>
              <template #default>
                <span>已保存的查询：</span>
              </template>
            </el-dropdown-item>
            <el-dropdown-item
              v-for="(savedQuery, index) in savedQueries"
              :key="index"
              @click="loadSavedQuery(savedQuery)"
            >
              {{ savedQuery.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 保存查询条件对话框 -->
    <el-dialog
      v-model="showSaveQueryDialog"
      title="保存查询条件"
      width="400px"
    >
      <el-form :model="saveQueryForm">
        <el-form-item label="查询名称" required>
          <el-input v-model="saveQueryForm.name" placeholder="请输入查询名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSaveQueryDialog = false">取消</el-button>
          <el-button type="primary" @click="saveQuery">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { Search, Refresh, Star, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  // 筛选字段配置
  filterFields: {
    type: Array,
    default: () => []
  },
  // 排序选项
  sortOptions: {
    type: Array,
    default: () => []
  },
  // 是否显示保存查询条件功能
  showSaveQuery: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits([
  'search', // 搜索事件
  'reset', // 重置事件
  'sort', // 排序事件
  'page-change' // 分页事件
])

// 搜索关键词
const searchKeyword = ref('')

// 筛选表单
const filterForm = reactive({})

// 排序字段
const sortBy = ref('')

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 展开的筛选面板
const activeFilterNames = ref(['filter'])

// 保存查询条件对话框
const showSaveQueryDialog = ref(false)

// 保存查询条件表单
const saveQueryForm = reactive({
  name: ''
})

// 已保存的查询条件
const savedQueries = ref([])

// 初始化
const init = () => {
  // 初始化筛选表单
  props.filterFields.forEach(field => {
    filterForm[field.key] = ''
  })
  
  // 加载已保存的查询条件
  loadSavedQueries()
}

// 加载已保存的查询条件
const loadSavedQueries = () => {
  const saved = localStorage.getItem('savedQueries')
  if (saved) {
    savedQueries.value = JSON.parse(saved)
  }
}

// 保存查询条件
const saveQuery = () => {
  if (!saveQueryForm.name.trim()) {
    return
  }
  
  const queryToSave = {
    name: saveQueryForm.name.trim(),
    keyword: searchKeyword.value,
    filters: { ...filterForm },
    sortBy: sortBy.value,
    pagination: { ...pagination }
  }
  
  savedQueries.value.push(queryToSave)
  localStorage.setItem('savedQueries', JSON.stringify(savedQueries.value))
  showSaveQueryDialog.value = false
  saveQueryForm.name = ''
}

// 加载已保存的查询条件
const loadSavedQuery = (query) => {
  searchKeyword.value = query.keyword
  Object.assign(filterForm, query.filters)
  sortBy.value = query.sortBy
  Object.assign(pagination, query.pagination)
  handleSearch()
}

// 处理搜索
const handleSearch = () => {
  console.log('搜索按钮被点击', {
    keyword: searchKeyword.value,
    filters: { ...filterForm },
    sortBy: sortBy.value,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  })
  emit('search', {
    keyword: searchKeyword.value,
    filters: { ...filterForm },
    sortBy: sortBy.value,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  })
}

// 重置查询
const resetQuery = () => {
  searchKeyword.value = ''
  
  // 重置筛选表单
  props.filterFields.forEach(field => {
    filterForm[field.key] = ''
  })
  
  sortBy.value = ''
  pagination.currentPage = 1
  
  emit('reset')
  handleSearch()
}

// 处理排序
const handleSort = () => {
  pagination.currentPage = 1
  emit('sort', sortBy.value)
  handleSearch()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  emit('page-change', pagination)
  handleSearch()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  emit('page-change', pagination)
  handleSearch()
}

// 根据类型获取组件
const getComponentByType = (type) => {
  switch (type) {
    case 'input':
      return 'el-input'
    case 'select':
      return 'el-select'
    case 'date':
      return 'el-date-picker'
    default:
      return 'el-input'
  }
}

// 监听筛选字段变化
watch(() => props.filterFields, () => {
  init()
}, { deep: true, immediate: true })

// 初始化
init()
</script>

<style lang="scss" scoped>
.query-component {
  background-color: #f9fafc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-container {
  margin-bottom: 16px;
  .el-input {
    width: 100%;
    .el-input__wrapper {
      padding-right: 200px; /* 为两个按钮留出足够空间 */
    }
    .el-input__append {
      display: flex;
      gap: 8px;
      padding: 0 8px;
    }
  }
}

.filter-container {
  margin-bottom: 16px;
}

.filter-form {
  margin-top: 16px;
}

.sort-container {
  margin-bottom: 16px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.save-query-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>