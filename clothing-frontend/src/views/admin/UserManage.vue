<template>
  <div class="user-manage-container">
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
            title="用户管理" 
            icon="User"
          >
            <el-table 
              :data="list" 
              v-loading="loading"
              style="width: 100%"
              stripe
              border
              :empty-text="emptyText"
            >
              <el-table-column prop="username" label="用户名" min-width="120" />
              <el-table-column prop="role" label="角色" min-width="100" />
              <el-table-column label="操作" min-width="100">
                <template #default="scope">
                  <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
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
  { index: '/admin/clothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/stats', icon: 'DataAnalysis', title: '系统统计' }
]

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无用户数据'
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
  request.get('/admin/users').then(res => {
    list.value = res.data || []
  }).catch(error => {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
    list.value = []
  }).finally(() => {
    loading.value = false
  })
}

const del = (id) => {
  request.delete('/admin/user/' + id).then(() => {
    ElMessage.success('删除成功')
    load()
  }).catch(error => {
    console.error('删除用户失败:', error)
    ElMessage.error('删除用户失败')
  })
}

const logout = () => {
  localStorage.clear()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.user-manage-container {
  min-height: 100vh;
}
</style>