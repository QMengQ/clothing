<template>
  <div class="admin-home-container">
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
            title="所有衣物" 
            icon="Collection"
            :badge="list.length"
            class="clothing-list-card"
          >
            <div class="table-container">
              <el-table 
                :data="list" 
                v-loading="loading"
                style="width: 100%"
                stripe
                border
                :empty-text="emptyText"
              >
                <el-table-column prop="image" label="图片" min-width="100">
                  <template #default="scope">
                    <el-image
                      v-if="scope.row.image"
                      :src="scope.row.image"
                      fit="cover"
                      style="width: 80px; height: 80px; border-radius: 4px"
                      :preview-src-list="[scope.row.image]"
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
              </el-table>
            </div>
          </Card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Collection } from '@element-plus/icons-vue'
import request from '../../utils/request'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'

const router = useRouter()
const route = useRoute()
const username = ref('管理员')
const userAvatar = ref('')

const list = ref([])
const loading = ref(false)

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/allClothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/review', icon: 'DataAnalysis', title: '回收审核' }
]

const activeMenu = computed(() => {
  return route.path
})

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无衣物数据'
})

onMounted(() => {
  loadUserData()
  load()
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const load = () => {
  loading.value = true
  request.get('/admin/clothing').then(res => {
    list.value = res.data || []
  }).catch(error => {
    console.error('加载衣物列表失败:', error)
    ElMessage.error('加载衣物列表失败')
    list.value = []
  }).finally(() => {
    loading.value = false
  })
}

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.admin-home-container {
  min-height: 100vh;
}

.table-container {
  margin-top: 16px;
}
</style>