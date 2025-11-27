<template>
  <div class="user-info-container">
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="goToEdit" class="edit-btn">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </template>
      
      <div class="user-profile" v-if="userInfo">
        <div class="avatar-section">
          <el-avatar
            :size="120"
            :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
            class="user-avatar"
          >
            {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
          </el-avatar>
          <div class="user-name">{{ userInfo.username }}</div>
          <div class="user-role">{{ userRole }}</div>
        </div>
        
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <el-descriptions border :column="{ xs: 1, sm: 2 }">
            <el-descriptions-item label="用户ID">{{ userInfo.id || '-' }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userInfo.name || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ userGender }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="手机号码">{{ userInfo.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(userInfo.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ formatDate(userInfo.updateTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="stats-section">
          <h3 class="section-title">账户统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ userStats.totalProducts || 0 }}</div>
              <div class="stat-label">发布商品</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStats.activeProducts || 0 }}</div>
              <div class="stat-label">在售商品</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStats.initiatedTrades || 0 }}</div>
              <div class="stat-label">发起交易</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStats.receivedTrades || 0 }}</div>
              <div class="stat-label">收到交易</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStats.completedTrades || 0 }}</div>
              <div class="stat-label">完成交易</div>
            </div>
          </div>
        </div>
        
        <div class="recent-activities">
          <h3 class="section-title">最近活动</h3>
          <div v-if="recentActivities.length > 0" class="activities-list">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <el-icon v-if="activity.type === 'product'">
                  <Goods />
                </el-icon>
                <el-icon v-else-if="activity.type === 'trade'">
                  <Transaction />
                </el-icon>
                <el-icon v-else>
                  <User />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ activity.description }}</div>
                <div class="activity-time">{{ formatRelativeTime(activity.time) }}</div>
              </div>
            </div>
          </div>
          <div v-else class="no-activities">暂无活动记录</div>
        </div>
      </div>
      
      <div v-else class="loading-state">
        <el-skeleton animated :rows="6" class="user-skeleton" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { Edit, User, Goods } from '@element-plus/icons-vue'
import Transaction from '../components/Transaction.vue'
import { userAPI, productAPI, tradeAPI } from '../api'

export default {
  name: 'UserInfo',
  components: {
    Edit,
    User,
    Goods,
    Transaction
  },
  data() {
    return {
      userInfo: null,
      userStats: {
        totalProducts: 0,
        activeProducts: 0,
        initiatedTrades: 0,
        receivedTrades: 0,
        completedTrades: 0
      },
      recentActivities: [],
      loading: true
    }
  },
  computed: {
    userRole() {
      if (!this.userInfo) return ''
      return this.userInfo.isAdmin ? '管理员' : '普通用户'
    },
    userGender() {
      if (!this.userInfo || !this.userInfo.gender) return '未知'
      const genderMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密'
      }
      return genderMap[this.userInfo.gender] || '未知'
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      this.loading = true
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        // 加载用户信息
        const userInfoResponse = await userAPI.getUserInfo(id)
        this.userInfo = userInfoResponse
        
        // 加载用户统计数据
        await this.loadUserStats()
        
        // 加载最近活动
        await this.loadRecentActivities()
      } catch (error) {
        this.$message.error('加载用户信息失败')
        console.error('Failed to load user info:', error)
      } finally {
        this.loading = false
      }
    },
    async loadUserStats() {
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        // 获取用户商品统计
        const products = await productAPI.getUserProducts(id)
        this.userStats.totalProducts = products.length
        this.userStats.activeProducts = products.filter(p => p.status === 1).length
        
        // 获取用户交易统计
        const initiatedTrades = await tradeAPI.getMyBuyTrades(id)
        const receivedTrades = await tradeAPI.getMySellTrades(id)
        
        this.userStats.initiatedTrades = initiatedTrades.length
        this.userStats.receivedTrades = receivedTrades.length
        
        // 计算完成交易数
        const completedInitiated = initiatedTrades.filter(t => t.status === 4).length
        const completedReceived = receivedTrades.filter(t => t.status === 4).length
        this.userStats.completedTrades = completedInitiated + completedReceived
      } catch (error) {
        console.error('Failed to load user stats:', error)
      }
    },
    async loadRecentActivities() {
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        const activities = []
        // 获取最近发布的商品
        const products = await productAPI.getUserProducts(id)
        const recentProducts = products.slice(0, 3).map(product => ({
          id: `product-${product.id}`,
          type: 'product',
          description: `发布了商品《${product.name}》`,
          time: product.createdTime
        }))
        activities.push(...recentProducts)
        
        // 获取最近的交易
        const trades = await tradeAPI.getMyBuyTrades()
        const recentTrades = trades.slice(0, 3).map(trade => ({
          id: `trade-${trade.id}`,
          type: 'trade',
          description: `发起了交易 #${trade.id}`,
          time: trade.createdTime
        }))
        activities.push(...recentTrades)
        
        // 按时间排序
        activities.sort((a, b) => new Date(b.time) - new Date(a.time))
        
        this.recentActivities = activities.slice(0, 5)
      } catch (error) {
        console.error('Failed to load recent activities:', error)
        this.recentActivities = []
      }
    },
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    },
    formatRelativeTime(dateString) {
      if (!dateString) return ''
      const now = new Date()
      const date = new Date(dateString)
      const diff = now - date
      
      const minute = 60 * 1000
      const hour = minute * 60
      const day = hour * 24
      const week = day * 7
      const month = day * 30
      
      if (diff < minute) {
        return '刚刚'
      } else if (diff < hour) {
        return `${Math.floor(diff / minute)}分钟前`
      } else if (diff < day) {
        return `${Math.floor(diff / hour)}小时前`
      } else if (diff < week) {
        return `${Math.floor(diff / day)}天前`
      } else if (diff < month) {
        return `${Math.floor(diff / week)}周前`
      } else {
        return `${Math.floor(diff / month)}个月前`
      }
    },
    goToEdit() {
      this.$router.push('/user/update')
    }
  }
}
</script>

<style scoped>
.user-info-container {
  padding: 0;
}

.user-info-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  width: 100%;
}

.user-avatar {
  margin-bottom: 15px;
}

.user-name {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 8px;
}

.user-role {
  color: #606266;
}

.info-section,
.stats-section,
.recent-activities {
  width: 100%;
  margin-bottom: 30px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #303133;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  background: #e9ecef;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.activity-icon {
  color: #409eff;
  font-size: 20px;
  margin-top: 2px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin-bottom: 5px;
  color: #303133;
}

.activity-time {
  color: #909399;
  font-size: 12px;
}

.no-activities {
  text-align: center;
  padding: 30px;
  color: #909399;
  background: #f8f9fa;
  border-radius: 8px;
}

.user-skeleton {
  max-width: 800px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .user-info-card {
    max-width: 100%;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>