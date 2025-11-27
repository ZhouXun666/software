<template>
  <div class="product-detail-container">
    <div v-if="loading" class="loading">
      <el-spinner type="spinner" />
      <p>加载中...</p>
    </div>
    <div v-else-if="product" class="product-detail">
      <!-- 商品图片和信息 -->
      <div class="product-main">
        <div class="product-images">
          <!-- <el-carousel :interval="4000" type="card" height="400px" indicator-position="outside">
            <el-carousel-item v-for="(image, index) in getImages(product.images)" :key="index">
              <img :src="image" :alt="product.name + '-' + (index + 1)" />
            </el-carousel-item>
          </el-carousel> -->
            <img :src="getImages(product.images)" class="product-image" />
        </div>
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <div class="product-price">¥{{ product.price }}</div>
          <div class="product-meta">
            <div class="meta-item">
              <span class="meta-label">分类：</span>
              <span class="meta-value">{{ getCategoryName(product.categoryId) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">发布时间：</span>
              <span class="meta-value">{{ formatDate(product.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">浏览次数：</span>
              <span class="meta-value">{{ product.views || 0 }}</span>
            </div>
          </div>
          <div class="product-seller">
            <el-avatar :size="40" :src="product.userAvatar || ''">{{ product.username?.charAt(0) }}</el-avatar>
            <div class="seller-info">
              <div class="seller-name">{{ product.username || '用户' }}</div>
              <el-button type="primary" size="small" @click="contactSeller">联系卖家</el-button>
              <div class="seller-phone">{{ product.userphone || '未知'}}</div>
            </div>
          </div>
          <div class="product-actions">
            <el-button
              v-if="canBuy"
              type="primary"
              size="large"
              @click="handleTrade"
              :loading="trading"
              class="action-btn"
            >
              申请交易
            </el-button>
            <el-button
              v-else-if="isMyProduct && product.status != 2"
              type="success"
              size="large"
              @click="editProduct"
              class="action-btn"
            >
              编辑商品
            </el-button>
            <el-button
              v-if="isMyProduct"
              type="danger"
              size="large"
              @click="deleteProduct"
              class="action-btn"
            >
              删除商品
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品描述 -->
      <el-card class="description-card">
        <template #header>
          <div class="card-header">商品描述</div>
        </template>
        <div class="product-description">{{ product.description }}</div>
      </el-card>

      <!-- 交易记录（仅卖家可见） -->
      <el-card v-if="isMyProduct" class="trades-card">
        <template #header>
          <div class="card-header">交易记录</div>
        </template>
        <el-table :data="productTrades" style="width: 100%">
          <el-table-column prop="buyerName" label="买家" width="120" />
          <el-table-column prop="message" label="留言" />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="180" :formatter="formatDateTime"/>
          <el-table-column label="操作" width="200" v-if="showActions">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="primary" size="small" @click="acceptTrade(row.id)">接受</el-button>
              <el-button v-if="row.status === 0" type="danger" size="small" @click="rejectTrade(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="productTrades.length === 0" class="empty-message">暂无交易记录</div>
      </el-card>
    </div>
    <div v-else class="not-found">
      <el-empty description="商品不存在或已下架" />
      <el-button type="primary" @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script>
import { productAPI, tradeAPI } from '../api'
import dayjs from 'dayjs';

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: null,
      productTrades: [],
      loading: false,
      trading: false,
      currentUser: null
    }
  },
  computed: {
    canBuy() {
      return this.currentUser && !this.isMyProduct && this.product?.status === 1
    },
    isMyProduct() {
      return this.currentUser && this.product && this.product.userId === this.currentUser.id
    },
    showActions() {
      return this.isMyProduct
    }
  },
  mounted() {
    this.currentUser = JSON.parse(localStorage.getItem('userInfo'))
    this.loadProductDetail()
  },
  methods: {
    async loadProductDetail() {
      const productId = this.$route.params.id
      this.loading = true
      try {
        const response = await productAPI.getProductById(productId)
        this.product = response
        
        // 如果是卖家，加载交易记录
        if (this.isMyProduct) {
          await this.loadProductTrades()
        }
      } catch (error) {
        this.$message.error('加载商品详情失败')
        this.product = null
      } finally {
        this.loading = false
      }
    },
    async loadProductTrades() {
      try {
        const response = await tradeAPI.getProductTrades(this.product.id)
        this.productTrades = response
      } catch (error) {
        console.error('加载交易记录失败', error)
      }
    },
    getImages(images) {
      if (!images) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const baseURL = 'http://localhost:8080/'
      return `${baseURL}${images}`
    },
    getCategoryName(categoryId) {
      const categoryMap = {
        '1': '电子产品',
        '2': '图书文具',
        '3': '服装鞋帽',
        '4': '生活用品',
        '5': '其他'
      }
      return categoryMap[categoryId] || '未分类'
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    },
    formatDateTime(row,column,dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return dayjs(date).format("YYYY-MM-DD HH:mm")
    },
    contactSeller() {
      // 这里可以实现联系卖家的逻辑，比如打开聊天窗口
      this.$message.info('请通过下面电话或线下方式联系卖家')
    },
    async handleTrade() {
      this.$prompt('请输入交易留言（选填）', '申请交易', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入您的交易留言...',
        inputPattern: /^.{0,200}$/,
        inputErrorMessage: '留言最多200个字符'
      }).then(async ({ value }) => {
        this.trading = true
        try {
          const requestData = {
            productId: this.product.id,
            buyerId: this.currentUser.id,
            message: value || ''
          }
          const response = await tradeAPI.applyTrade(requestData)
          console.log(response)
          this.$message.success('交易申请已提交，请等待卖家确认')
          // 跳转到交易列表
          this.$router.push('/user/trades')
        } catch (error) {
          this.$message.error(error.response?.data?.message || '申请交易失败')
        } finally {
          this.trading = false
        }
      }).catch(() => {
        // 取消操作
      })
    },
    editProduct() {
      this.$router.push(`/product/update/${this.product.id}`)
    },
    async deleteProduct() {
      this.$confirm('确定要删除这个商品吗？删除后不可恢复。', '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        danger: true
      }).then(async () => {
        try {
          await productAPI.deleteProduct(this.product.id)
          this.$message.success('商品删除成功')
          this.$router.push('/user/products')
        } catch (error) {
          this.$message.error('删除失败')
        }
      }).catch(() => {
        // 取消删除
      })
    },
    async acceptTrade(tradeId) {
      this.$confirm('确定要接受这个交易吗？接受后商品将自动下架。', '接受交易', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'primary'
      }).then(async () => {
        try {
          await tradeAPI.acceptTrade(tradeId)
          this.$message.success('交易已接受')
          await this.loadProductTrades()
          // 重新加载商品信息以更新状态
          await this.loadProductDetail()
        } catch (error) {
          this.$message.error('操作失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    async rejectTrade(tradeId) {
      this.$confirm('确定要拒绝这个交易吗？', '拒绝交易', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await tradeAPI.rejectTrade(tradeId)
          this.$message.success('交易已拒绝')
          await this.loadProductTrades()
        } catch (error) {
          this.$message.error('操作失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    getStatusType(status) {
      switch (status) {
        case 0: return 'info'
        case 1: return 'success'
        case 2: return 'danger'
        case 3: return 'primary'
        case 4: return 'warning'
        default: return 'default'
      }
    },
    goBack() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.product-detail-container {
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

.product-detail {
  padding-bottom: 40px;
}

.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.product-images {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #303133;
  line-height: 1.4;
}

.product-price {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
  margin: 20px 0;
}

.product-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.meta-item {
  display: flex;
  gap: 10px;
}

.meta-label {
  color: #606266;
  font-size: 14px;
  width: 80px;
}

.meta-value {
  color: #303133;
  font-size: 14px;
  flex: 1;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.seller-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seller-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.product-actions {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

.action-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

.description-card,
.trades-card {
  margin-bottom: 30px;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.product-description {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}

.empty-message {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

@media (max-width: 768px) {
  .product-main {
    grid-template-columns: 1fr;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .product-title {
    font-size: 20px;
  }
  
  .product-price {
    font-size: 28px;
  }
}
</style>