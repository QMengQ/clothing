<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <el-avatar :size="64" :src="logoUrl" />
        <h2 class="login-title">智能衣物管理系统</h2>
        <p class="login-subtitle">欢迎回来，请登录您的账号</p>
      </div>
      
      <el-form
        :model="form"
        :rules="rules"
        ref="loginFormRef"
        class="login-form"
        autocomplete="on"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            autocomplete="username"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            size="large"
            autocomplete="current-password"
          />
        </el-form-item>
        
        <el-form-item class="login-actions">
          <el-button
            type="primary"
            :loading="loading"
            @click="login"
            size="large"
            class="login-button"
            :disabled="loading"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <el-link @click="goRegister" type="primary" class="register-link">
            没有账号？立即注册
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElLink, ElAvatar } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const logoUrl = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20clothing%20management%20system%20logo%2C%20minimalist%2C%20blue%20color%20scheme&image_size=square'

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ]
}

const goRegister = () => {
  router.push('/register')
}

const login = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/login', form)
        if (res.data.token) {
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('role', res.data.role)
          localStorage.setItem('username', res.data.username || form.username)
          
          ElMessage.success('登录成功')
          
          if (res.data.role === 'ADMIN') {
            router.push('/admin/home')
          } else {
            router.push('/user/home')
          }
        } else {
          ElMessage.error('登录失败，请检查账号密码')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试')
        console.error('Login error:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409EFF 0%, #667eea 100%);
  padding: 20px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: var(--border-radius, 12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  padding: 32px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  
  .login-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--primary-color, #409EFF);
    margin: 16px 0 8px;
  }
  
  .login-subtitle {
    font-size: 14px;
    color: var(--info-color, #909399);
    margin: 0;
  }
}

.login-form {
  .login-actions {
    margin-top: 24px;
  }
  
  .login-button {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
  }
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  
  .register-link {
    font-size: 14px;
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form-wrapper {
    padding: 24px;
  }
  
  .login-header {
    margin-bottom: 24px;
    
    .login-title {
      font-size: 20px;
    }
  }
}
</style>