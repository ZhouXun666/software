<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-title">
          <h2>校园二手商品交易系统</h2>
          <p>用户登录</p>
        </div>
      </template>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-btn">登录</el-button>
          <el-button type="text" @click="goToRegister">去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { userAPI } from '../api'

export default {
  name: 'UserLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const response = await userAPI.login(this.loginForm)
            // 保存token和用户信息
            localStorage.setItem('token', response.token)
            localStorage.setItem('userInfo', JSON.stringify(response.user))
            console.log(response.user)
            // 显示成功消息
            this.$message.success('登录成功')
            // 跳转到首页
            this.$router.push('/')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
          } finally {
            this.loading = false
          }
        }
      })
    },
    goToRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
}

.login-title h2 {
  color: #303133;
  font-size: 24px;
  margin-bottom: 10px;
}

.login-title p {
  color: #606266;
  font-size: 16px;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.el-form {
  padding: 0 20px 20px;
}
</style>