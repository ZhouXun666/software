<template>
  <div class="trade-detail-container">
    <div v-if="loading" class="loading">
      <el-spinner type="spinner" />
      <p>加载中...</p>
    </div>
    <el-card v-else-if="trade" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>交易详情</span>
          <el-button type="primary" size="small" @click="goBack" class="back-btn">
            返回列表
          </el-button>
        </div>
      </template>
      
      <!-- 交易状态概览 -->
      <div class="status-overview">
        <el-steps :active="tradeStatusStep" finish-status="success" align-center>
          <el-step title="交易申请" description="买家发起交易申请" />
          <el-step title="卖家确认" description="卖家接受或拒绝交易" />
          <el-step title="交易完成" description="买家确认收货" />
        </el-steps>
        <el-tag :type="getStatusType(trade.status)" class="status-tag">
          {{ getStatusText(trade.status) }}
        </el-tag>
      </div>
      
      <div class="detail-content">
        <!-- 商品信息 -->
        <el-card class="info-section">
          <template #header>
            <div class="section-header">
              <span>商品信息</span>
            </div>
          </template>
          <div class="product-info">
            <img :src="getProductImage(trade.productImages)" class="product-image" />
            <div class="product-details">
              <h3 class="product-name">{{ trade.productName }}</h3>
              <p class="product-price">¥{{ trade.productPrice }}</p>
              <p class="product-category">分类：{{ getCategoryName(trade.productCategory) }}</p>
              <p class="product-description">{{ trade.productDescription }}</p>
            </div>
          </div>
        </el-card>
        
        <!-- 交易方信息 -->
        <div class="trade-parties">
          <el-card class="info-section">
            <template #header>
              <div class="section-header">
                <span>交易方信息</span>
              </div>
            </template>
            <div class="parties-info">
              <div class="party-item">
                <div class="party-label">买家</div>
                <div class="party-content">
                  <el-avatar :size="40" :src="buyerInfo.avatar || defaultAvatar">
                    {{ getUserInitial(buyerInfo.username) }}
                  </el-avatar>
                  <div class="party-details">
                    <div class="party-name">{{ buyerInfo.username }}</div>
                    <div class="party-contact">联系方式：{{ buyerInfo.contact || '未提供' }}</div>
                  </div>
                </div>
              </div>
              <div class="party-item">
                <div class="party-label">卖家</div>
                <div class="party-content">
                  <el-avatar :size="40" :src="sellerInfo.avatar || defaultAvatar">
                    {{ getUserInitial(sellerInfo.username) }}
                  </el-avatar>
                  <div class="party-details">
                    <div class="party-name">{{ sellerInfo.username }}</div>
                    <div class="party-contact">联系方式：{{ sellerInfo.contact || '未提供' }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 交易信息 -->
        <el-card class="info-section">
          <template #header>
            <div class="section-header">
              <span>交易信息</span>
            </div>
          </template>
          <div class="trade-info">
            <div class="info-row">
              <div class="info-label">交易ID：</div>
              <div class="info-value">{{ trade.id }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">创建时间：</div>
              <div class="info-value">{{ formatDate(trade.createTime) }}</div>
            </div>
            <!-- <div class="info-row">
              <div class="info-label">更新时间：</div>
              <div class="info-value">{{ formatDate(trade.updateTime) }}</div>
            </div> -->
            <div v-if="trade.status === 1 || trade.status === 3" class="info-row">
              <div class="info-label">接受时间：</div>
              <div class="info-value">{{ formatDate(trade.acceptTime) }}</div>
            </div>
            <div v-if="trade.status === 2" class="info-row">
              <div class="info-label">拒绝时间：</div>
              <div class="info-value">{{ formatDate(trade.rejectTime) }}</div>
            </div>
            <div v-if="trade.status === 3" class="info-row">
              <div class="info-label">完成时间：</div>
              <div class="info-value">{{ formatDate(trade.completeTime) }}</div>
            </div>
            <div v-if="trade.status === 4" class="info-row">
              <div class="info-label">取消时间：</div>
              <div class="info-value">{{ formatDate(trade.cancelTime) }}</div>
            </div>
          </div>
        </el-card>
        
        <!-- 交易留言
        <el-card class="info-section">
          <template #header>
            <div class="section-header">
              <span>交易留言</span>
            </div>
          </template>
          <div class="messages-section">
            <div v-if="tradeMessages.length > 0" class="messages-list">
              <div v-for="message in tradeMessages" :key="message.id" class="message-item">
                <div class="message-header">
                  <div class="message-sender">{{ message.sender === 'buyer' ? '买家' : '卖家' }}</div>
                  <div class="message-time">{{ formatDate(message.createdTime) }}</div>
                </div>
                <div class="message-content">{{ message.content }}</div>
              </div>
            </div>
            <div v-else class="no-messages">暂无交易留言</div>
            
            <el-form v-if="canSendMessage" :model="messageForm" :rules="messageRules" ref="messageFormRef" class="message-form">
              <el-form-item prop="content">
                <el-input
                  v-model="messageForm.content"
                  type="textarea"
                  :rows="3"
                  placeholder="输入留言内容"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="sendMessage" :loading="sendingMessage">发送留言</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card> -->
        
        <!-- 操作按钮 -->
        <div v-if="actionButtons.length > 0" class="action-buttons">
          <el-button
            v-for="button in actionButtons"
            :key="button.key"
            :type="button.type"
            :size="button.size || 'large'"
            :loading="button.key === 'sending' && sendingAction"
            @click="handleAction(button.action)"
            :class="button.class"
          >
            {{ button.text }}
          </el-button>
        </div>
      </div>
    </el-card>
    <div v-else class="not-found">
      <el-empty description="交易不存在或已删除" />
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
    
    <!-- 确认对话框 -->
    <el-dialog v-model="confirmDialogVisible" :title="confirmDialogTitle" width="30%">
      <div>{{ confirmDialogContent }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="confirmDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmAction" v-if="actionType === 'cancel' || actionType === 'reject'">确认</el-button>
          <el-button type="success" @click="confirmAction" v-else>确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { tradeAPI } from '../api'
import dayjs from 'dayjs';

export default {
  name: 'TradeDetail',
  data() {
    return {
      trade: null,
      loading: false,
      buyerInfo: {},
      sellerInfo: {},
      tradeMessages: [],
      // defaultAvatar: 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png',
      messageForm: {
        content: ''
      },
      messageRules: {
        content: [
          { required: true, message: '请输入留言内容', trigger: 'blur' },
          { max: 500, message: '留言内容不能超过500个字符', trigger: 'blur' }
        ]
      },
      sendingMessage: false,
      sendingAction: false,
      confirmDialogVisible: false,
      confirmDialogTitle: '',
      confirmDialogContent: '',
      actionType: ''
    }
  },
  computed: {
    tradeStatusStep() {
      switch (this.trade?.status) {
        case 0: return 1
        case 1: return 2
        case 2: return 2 // 拒绝也是在第二步
        case 3: return 3
        case 4: return 2 // 取消也是在第二步
        default: return 1
      }
    },
    // canSendMessage() {
    //   // 只有在交易进行中（待确认、已接受）时才能发送留言
    //   return this.trade && (this.trade.status === 0 || this.trade.status === 1)
    // },
    actionButtons() {
      if (!this.trade) return []
      
      const buttons = []
      const currentUserId = JSON.parse(localStorage.getItem('userInfo'))?.id
      
      // 买家操作
      if (currentUserId === this.trade.buyerId) {
        if (this.trade.status === 0) {
          buttons.push({
            key: 'cancel',
            text: '取消交易',
            type: 'danger',
            action: 'cancel'
          })
        }
        if (this.trade.status === 1) {
          buttons.push({
            key: 'confirm',
            text: '确认收货',
            type: 'success',
            action: 'confirm'
          })
        }
      }
      
      // 卖家操作
      if (currentUserId === this.trade.sellerId) {
        if (this.trade.status === 0) {
          buttons.push({
            key: 'accept',
            text: '接受交易',
            type: 'success',
            class: 'mr-2',
            action: 'accept'
          })
          buttons.push({
            key: 'reject',
            text: '拒绝交易',
            type: 'danger',
            action: 'reject'
          })
        }
      }
      
      return buttons
    }
  },
  mounted() {
    this.loadTradeDetail()
  },
  methods: {
    async loadTradeDetail() {
      const tradeId = this.$route.params.id
      this.loading = true
      try {
        const response = await tradeAPI.getTradeById(tradeId)
        this.trade = response
        // 设置买家和卖家信息
        this.buyerInfo = {
          username: response.buyerName,
          contact: response.buyerPhone
          // avatar: response.buyerAvatar,
        }
        this.sellerInfo = {
          username: response.sellerName,
          contact: response.sellerPhone
          // avatar: response.sellerAvatar,
        }
        
        // 加载交易留言
        if (response.messages) {
          this.tradeMessages = response.messages
        }
      } catch (error) {
        this.$message.error('加载交易详情失败')
        this.trade = null
      } finally {
        this.loading = false
      }
    },
    getProductImage(images) {
      if (!images) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const baseURL = 'http://localhost:8080/'
      return `${baseURL}${images}`
    },
    getCategoryName(categoryId) {
      const categoryMap = {
        1: '电子产品',
        2: '图书文具',
        3: '服装鞋帽',
        4: '生活用品',
        5: '其他'
      }
      return categoryMap[categoryId] || '未知'
    },
    formatDate(date) {
      if (!date) return ''
      return dayjs(date).format("YYYY-MM-DD HH:mm")
    },
    getUserInitial(username) {
      return username ? username.charAt(0).toUpperCase() : 'U'
    },
    getStatusType(status) {
      const statusMap = {
        0: 'warning', // 待确认
        1: 'primary', // 已接受
        2: 'info',    // 已拒绝
        3: 'success', // 已完成
        4: 'danger'   // 已取消
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        0: '待确认',
        1: '已接受',
        2: '已拒绝',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    },
    handleAction(action) {
      this.actionType = action
      
      switch (action) {
        case 'cancel':
          this.confirmDialogTitle = '取消交易'
          this.confirmDialogContent = '确定要取消该交易吗？'
          break
        case 'confirm':
          this.confirmDialogTitle = '确认收货'
          this.confirmDialogContent = '请确认您已收到商品，确认后交易将完成。'
          break
        case 'accept':
          this.confirmDialogTitle = '接受交易'
          this.confirmDialogContent = '确定要接受该交易申请吗？'
          break
        case 'reject':
          this.confirmDialogTitle = '拒绝交易'
          this.confirmDialogContent = '确定要拒绝该交易申请吗？'
          break
      }
      
      this.confirmDialogVisible = true
    },
    async confirmAction() {
      this.confirmDialogVisible = false
      this.sendingAction = true
      
      try {
        switch (this.actionType) {
          case 'cancel':
            await tradeAPI.cancelTrade(this.trade.id)
            this.$message.success('交易已取消')
            break
          case 'confirm':
            await tradeAPI.completeTrade(this.trade.id)
            this.$message.success('已确认收货，交易完成')
            break
          case 'accept':
            await tradeAPI.acceptTrade(this.trade.id)
            this.$message.success('已接受交易')
            break
          case 'reject':
            await tradeAPI.rejectTrade(this.trade.id)
            this.$message.success('已拒绝交易')
            break
        }
        // 重新加载交易详情
        this.loadTradeDetail()
      } catch (error) {
        this.$message.error('操作失败，请重试')
      } finally {
        this.sendingAction = false
      }
    },
    // async sendMessage() {
    //   this.$refs.messageFormRef.validate(async (valid) => {
    //     if (valid) {
    //       this.sendingMessage = true
    //       try {
    //         const response = await tradeAPI.sendMessage(this.trade.id, { content: this.messageForm.content })
            
    //         // 添加新留言到列表
    //         this.tradeMessages.push(response)
    //         // 清空表单
    //         this.messageForm.content = ''
    //         this.$refs.messageFormRef.resetFields()
            
    //         this.$message.success('留言发送成功')
    //       } catch (error) {
    //         this.$message.error('留言发送失败，请重试')
    //       } finally {
    //         this.sendingMessage = false
    //       }
    //     }
    //   })
    // },
    goBack() {
      this.$router.push('/user/trades')
    }
  }
}
</script>

<style scoped>
.trade-detail-container {
  padding: 0;
}

.loading,
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 20px;
}

.detail-card {
  max-width: 900px;
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

/* 状态概览 */
.status-overview {
  position: relative;
  padding: 30px 0;
  margin-bottom: 30px;
  border-bottom: 1px solid #EBEEF5;
}

.status-tag {
  position: absolute;
  top: 30px;
  right: 0;
  font-size: 16px;
  padding: 6px 12px;
}

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-section {
  border-radius: 4px;
}

.section-header {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 商品信息 */
.product-info {
  display: flex;
  gap: 20px;
}

.product-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 10px;
}

.product-price {
  font-size: 20px;
  font-weight: 600;
  color: #F56C6C;
  margin-bottom: 10px;
}

.product-category {
  color: #606266;
  margin-bottom: 10px;
}

.product-description {
  color: #606266;
  line-height: 1.6;
  word-break: break-all;
}

/* 交易方信息 */
.parties-info {
  display: flex;
  gap: 40px;
}

.party-item {
  flex: 1;
}

.party-label {
  font-weight: 500;
  margin-bottom: 10px;
  color: #303133;
}

.party-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.party-details {
  flex: 1;
}

.party-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.party-contact {
  color: #606266;
}

/* 交易信息 */
.trade-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-row {
  display: flex;
  gap: 15px;
}

.info-label {
  width: 120px;
  font-weight: 500;
  color: #606266;
}

.info-value {
  flex: 1;
  color: #303133;
}

/* 留言区 */
.messages-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  padding: 15px;
  background-color: #F5F7FA;
  border-radius: 4px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.message-sender {
  font-weight: 500;
  color: #303133;
}

.message-time {
  color: #909399;
  font-size: 12px;
}

.message-content {
  color: #303133;
  line-height: 1.6;
}

.no-messages {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}

.message-form {
  margin-top: 20px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 30px;
  padding: 20px 0;
}

.mr-2 {
  margin-right: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-card {
    max-width: 100%;
  }
  
  .product-info {
    flex-direction: column;
  }
  
  .product-image {
    width: 100%;
    height: auto;
  }
  
  .parties-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .info-row {
    flex-direction: column;
    gap: 5px;
  }
  
  .info-label {
    width: auto;
  }
  
  .status-tag {
    position: static;
    display: block;
    margin-top: 20px;
    text-align: center;
  }
}
</style>