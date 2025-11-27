<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-title">
          <h2>校园二手商品交易系统</h2>
          <p>用户注册</p>
        </div>
      </template>
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入真实姓名" prefix-icon="el-icon-user-solid" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="el-icon-phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" type="email" placeholder="请输入邮箱" prefix-icon="el-icon-message" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" class="register-btn">注册</el-button>
          <el-button type="text" @click="goToLogin">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { userAPI } from '../api'

export default {
  name: 'Register',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
        phone: '',
        email: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validatePassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    // 验证两次密码是否一致
    validatePassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    async handleRegister() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const { confirmPassword, ...formData } = this.registerForm
            await userAPI.register(formData)
            this.$message.success('注册成功，请登录')
            this.$router.push('/login')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '注册失败，请重试')
          } finally {
            this.loading = false
          }
        }
      })
    },
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
}

.register-title h2 {
  color: #303133;
  font-size: 24px;
  margin-bottom: 10px;
}

.register-title p {
  color: #606266;
  font-size: 16px;
}

.register-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.el-form {
  padding: 0 20px 20px;
}
</style>