<template>
  <div class="user-update-container">
    <el-card class="update-card">
      <template #header>
        <div class="card-header">
          <span>编辑个人信息</span>
        </div>
      </template>
      
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px" class="update-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled placeholder="用户名不可修改" />
        </el-form-item>
        
        <el-form-item label="昵称" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入昵称" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
            <el-radio label="3">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" type="email" placeholder="请输入邮箱" maxlength="100" show-word-limit />
        </el-form-item>
        
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号码" maxlength="20" show-word-limit />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { userAPI } from '../api'

export default {
  name: 'UserUpdate',
  data() {
    return {
      userForm: {
        username: '',
        name: '',
        gender: '0',
        email: '',
        phone: '',
      },
      userRules: {
        name: [
          { required: false, max: 50, message: '昵称不能超过50个字符', trigger: 'blur' }
        ],
        email: [
          { required: false, type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: false, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        bio: [
          { required: false, max: 200, message: '个性签名不能超过200个字符', trigger: 'blur' }
        ]
      },
      submitting: false
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        const response = await userAPI.getUserInfo(id)
        this.userForm = {
          username: response.username,
          name: response.name || '',
          gender: response.gender?.toString() || '0',
          email: response.email || '',
          phone: response.phone || '',
        }
      } catch (error) {
        this.$message.error('加载用户信息失败')
      }
    },
    async submitForm() {
      this.$refs.userFormRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            //获得当前用户的id
            const userInfoString = localStorage.getItem('userInfo');
            const userInfo = JSON.parse(userInfoString);
            const id = userInfo.id;
            await userAPI.updateUserInfo(id,this.userForm)
            this.$message.success('个人信息更新成功')
            this.$router.push('/user/info')
          } catch (error) {
            this.$message.error('更新失败，请重试')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    resetForm() {
      this.$refs.userFormRef.resetFields()
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.user-update-container {
  padding: 0;
}

.update-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.update-form {
  padding-top: 20px;
}

@media (max-width: 768px) {
  .update-card {
    max-width: 100%;
  }
  
  .el-form {
    width: 80px;
  }
}
</style>