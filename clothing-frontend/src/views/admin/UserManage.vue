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
            :badge="userList.length"
            class="user-list-card"
          >
            <div class="table-container">
              <el-table 
                :data="userList" 
                v-loading="loading"
                style="width: 100%"
                stripe
                border
                :empty-text="emptyText"
              >
                <el-table-column prop="id" label="ID" min-width="80" />
                <el-table-column prop="username" label="用户名" min-width="120" />
                <el-table-column prop="role" label="角色" min-width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'">
                      {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" min-width="120">
                  <template #default="scope">
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="deleteUser(scope.row)"
                      :disabled="scope.row.role === 'ADMIN'"
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </template>
                </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Delete } from '@element-plus/icons-vue'
import request from '../../utils/request'
import Sidebar from '../../components/Sidebar.vue'
import Card from '../../components/Card.vue'

const router = useRouter()
const route = useRoute()
const username = ref('管理员')
const userAvatar = ref('')

const userList = ref([])
const loading = ref(false)

const menuItems = [
  { index: '/admin/home', icon: 'House', title: '首页' },
  { index: '/admin/users', icon: 'User', title: '用户管理' },
  { index: '/admin/allClothing', icon: 'ShoppingBag', title: '衣物总览' },
  { index: '/admin/review', icon: 'View', title: '服装审核' },
  { index: '/admin/reviewRecycle', icon: 'DataAnalysis', title: '回收审核' }
]

const activeMenu = computed(() => {
  return route.path
})

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无用户数据'
})

onMounted(() => {
  loadUserData()
  loadUsers()
})

const loadUserData = () => {
  const user = localStorage.getItem('username')
  username.value = user || '管理员'
  userAvatar.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=admin%20avatar%2C%20minimalist%2C%20professional%20look&image_size=square`
}

const loadUsers = () => {
  loading.value = true
  request.get('/admin/users').then(res => {
    userList.value = res.data || []
  }).catch(error => {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
    userList.value = []
  }).finally(() => {
    loading.value = false
  })
}

const deleteUser = (user) => {
  if (user.role === 'ADMIN') {
    ElMessage.warning('不能删除管理员用户')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除用户 ${user.username} 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    loading.value = true
    request.delete(`/admin/user/${user.id}`).then(() => {
      ElMessage.success('用户删除成功')
      loadUsers()
    }).catch(error => {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {
    // 取消删除
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

.table-container {
  margin-top: 16px;
}
</style>