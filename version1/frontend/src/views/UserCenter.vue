<template>
  <div class="user-center-container">
    <el-card class="center-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <div class="tabs-container">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="个人信息" name="profile">
            <div class="profile-content">
              <div class="profile-header">
                <div class="avatar-section">
                  <el-avatar :size="100" :src="defaultAvatar">
                    {{ getUserInitial(userInfo.username) }}
                  </el-avatar>
                  <el-button type="primary" size="small" @click="showAvatarUpload = true" class="change-avatar-btn">
                    更换头像
                  </el-button>
                </div>
                <div class="user-info">
                  <h2>{{ userInfo.username }}</h2>
                  <p>{{ userInfo.email || '未设置邮箱' }}</p>
                  <p>注册时间：{{ formatDate(userInfo.registrationTime) }}</p>
                  <p>发布商品：{{ userStats.productCount }} 件</p>
                  <p>交易成功率：{{ calculateSuccessRate() }}%</p>
                </div>
              </div>
              
              <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px" class="profile-form">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="50" show-word-limit />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio label="1">男</el-radio>
                    <el-radio label="2">女</el-radio>
                    <el-radio label="0">保密</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" maxlength="100" show-word-limit />
                </el-form-item>
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号码" maxlength="20" show-word-limit />
                </el-form-item>
                <el-form-item label="个性签名" prop="bio">
                  <el-input
                    v-model="profileForm.bio"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入个性签名"
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitProfile" :loading="submitting">保存修改</el-button>
                  <el-button @click="resetProfile">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="安全设置" name="security">
            <div class="security-content">
              <el-card class="security-item">
                <template #header>
                  <div class="security-item-header">
                    <span>修改密码</span>
                  </div>
                </template>
                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px" class="password-form">
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="submitPassword" :loading="submittingPassword">修改密码</el-button>
                    <el-button @click="resetPassword">重置</el-button>
                  </el-form-item>
                </el-form>
              </el-card>
              
              <el-card class="security-item">
                <template #header>
                  <div class="security-item-header">
                    <span>账户安全提示</span>
                  </div>
                </template>
                <div class="security-tips">
                  <el-alert
                    title="为了您的账户安全，请定期更换密码"
                    type="info"
                    :closable="false"
                    show-icon
                  />
                  <el-alert
                    title="请勿与他人共享您的账户信息"
                    type="warning"
                    :closable="false"
                    show-icon
                  />
                  <el-alert
                    title="在使用完账号后，请及时登出"
                    type="warning"
                    :closable="false"
                    show-icon
                  />
                </div>
              </el-card>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
    
    <!-- 头像上传对话框 -->
    <el-dialog v-model="showAvatarUpload" title="更换头像" width="30%">
      <el-upload
        class="avatar-uploader"
        action=""
        :http-request="uploadAvatar"
        :show-file-list="false"
        :auto-upload="false"
        :on-change="handleAvatarChange"
        accept="image/*"
      >
        <img v-if="previewAvatar" :src="previewAvatar" class="avatar" />
        <i v-else class="el-icon-plus avatar-uploader-icon" />
      </el-upload>
      <div class="dialog-footer">
        <el-button @click="showAvatarUpload = false">取消</el-button>
        <el-button type="primary" @click="confirmAvatarUpload" :loading="uploadingAvatar">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { userAPI } from '../api'

export default {
  name: 'UserCenter',
  data() {
    return {
      activeTab: 'profile',
      userInfo: {
        username: '',
        email: '',
        registrationTime: '',
        avatar: ''
      },
      userStats: {
        productCount: 0,
        tradeSuccessCount: 0,
        tradeTotalCount: 0
      },
      defaultAvatar: 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png',
      profileForm: {
        nickname: '',
        gender: '0',
        email: '',
        phone: '',
        bio: ''
      },
      profileRules: {
        nickname: [
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
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      showAvatarUpload: false,
      previewAvatar: '',
      avatarFile: null,
      submitting: false,
      submittingPassword: false,
      uploadingAvatar: false
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadUserStats()
  },
  methods: {
    async loadUserInfo() {
      try {
        const response = await userAPI.getUserInfo()
        this.userInfo = response
        // 填充表单数据
        this.profileForm = {
          nickname: response.nickname || '',
          gender: response.gender?.toString() || '0',
          email: response.email || '',
          phone: response.phone || '',
          bio: response.bio || ''
        }
      } catch (error) {
        this.$message.error('加载用户信息失败')
      }
    },
    async loadUserStats() {
      try {
        const response = await userAPI.getUserStats()
        this.userStats = response
      } catch (error) {
        // 使用默认统计数据
        this.userStats = {
          productCount: 0,
          tradeSuccessCount: 0,
          tradeTotalCount: 0
        }
      }
    },
    handleTabClick(tab) {
      // 可以根据需要添加tab切换逻辑
    },
    getUserInitial(username) {
      return username ? username.charAt(0).toUpperCase() : 'U'
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    },
    calculateSuccessRate() {
      if (this.userStats.tradeTotalCount === 0) return 0
      return Math.round((this.userStats.tradeSuccessCount / this.userStats.tradeTotalCount) * 100)
    },
    async submitProfile() {
      this.$refs.profileFormRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            await userAPI.updateUserInfo(this.profileForm)
            this.$message.success('个人信息更新成功')
            // 重新加载用户信息
            this.loadUserInfo()
          } catch (error) {
            this.$message.error('更新失败，请重试')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    resetProfile() {
      this.$refs.profileFormRef.resetFields()
    },
    async submitPassword() {
      this.$refs.passwordFormRef.validate(async (valid) => {
        if (valid) {
          this.submittingPassword = true
          try {
            await userAPI.changePassword(this.passwordForm)
            this.$message.success('密码修改成功，请重新登录')
            this.$router.push('/login')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '密码修改失败，请检查原密码是否正确')
          } finally {
            this.submittingPassword = false
          }
        }
      })
    },
    resetPassword() {
      this.$refs.passwordFormRef.resetFields()
    },
    handleAvatarChange(file) {
      this.avatarFile = file.raw
      this.previewAvatar = URL.createObjectURL(file.raw)
    },
    async uploadAvatar() {
      // 这个方法被覆盖，实际上传在confirmAvatarUpload中处理
    },
    async confirmAvatarUpload() {
      if (!this.avatarFile) {
        this.$message.warning('请选择要上传的头像')
        return
      }
      
      this.uploadingAvatar = true
      try {
        const formData = new FormData()
        formData.append('avatar', this.avatarFile)
        
        const response = await userAPI.uploadAvatar(formData)
        
        this.$message.success('头像上传成功')
        this.showAvatarUpload = false
        // 更新用户信息
        this.userInfo.avatar = response.avatarUrl
      } catch (error) {
        this.$message.error('头像上传失败，请重试')
      } finally {
        this.uploadingAvatar = false
      }
    }
  }
}
</script>

<style scoped>
.user-center-container {
  padding: 0;
}

.center-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.tabs-container {
  margin-top: 20px;
}

/* 个人信息样式 */
.profile-content {
  padding: 20px 0;
}

.profile-header {
  display: flex;
  align-items: center;
  padding-bottom: 30px;
  border-bottom: 1px solid #EBEEF5;
  margin-bottom: 30px;
}

.avatar-section {
  position: relative;
  margin-right: 40px;
}

.change-avatar-btn {
  position: absolute;
  bottom: -25px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
}

.user-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 500;
}

.user-info p {
  margin: 5px 0;
  color: #606266;
}

.profile-form {
  max-width: 600px;
}

/* 安全设置样式 */
.security-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  border-radius: 4px;
}

.security-item-header {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.password-form {
  max-width: 600px;
}

.security-tips {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 头像上传样式 */
.avatar-uploader {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.avatar {
  width: 150px;
  height: 150px;
  object-fit: cover;
}

.avatar-uploader-icon {
  width: 150px;
  height: 150px;
  line-height: 150px;
  font-size: 40px;
  color: #C0C4CC;
  border: 1px dashed #DCDFE6;
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .center-card {
    max-width: 100%;
  }
  
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .avatar-section {
    margin-right: 0;
    margin-bottom: 30px;
  }
  
  .profile-form,
  .password-form {
    max-width: 100%;
  }
  
  .el-form {
    width: 80px;
  }
}
</style>