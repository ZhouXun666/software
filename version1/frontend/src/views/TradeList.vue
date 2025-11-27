<template>
  <div class="trade-list-container">
    <el-card class="trade-card">
      <template #header>
        <div class="card-header">
          <span>交易列表</span>
        </div>
      </template>
      
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索交易编号或商品名称"
          prefix-icon="el-icon-search"
          class="search-input"
          clearable
        />
        <el-select v-model="tradeStatus" placeholder="交易状态" clearable class="status-select">
          <el-option label="全部" value="" />
          <el-option label="待接受" value="1" />
          <el-option label="已接受" value="2" />
          <el-option label="交易中" value="3" />
          <el-option label="已完成" value="4" />
          <el-option label="已取消" value="5" />
          <el-option label="已拒绝" value="6" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          clearable
          class="date-picker"
        />
      </div>
      
      <el-table
        v-loading="loading"
        :data="filteredTrades"
        style="width: 100%"
        :empty-text="emptyText"
      >
        <el-table-column prop="id" label="交易编号" width="120" />
        <el-table-column prop="productName" label="商品信息" min-width="200">
          <template #default="scope">
            <div class="product-info">
              <img :src="getProductImage(scope.row.product)" class="product-thumbnail" />
              <div class="product-details">
                <div class="product-name">{{ scope.row.productName }}</div>
                <div class="product-price">¥{{ scope.row.price }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="buyerName" label="买家" width="120">
          <template #default="scope">
            <span>{{ scope.row.buyerName || '未知买家' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sellerName" label="卖家" width="120">
          <template #default="scope">
            <span>{{ scope.row.sellerName || '未知卖家' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column prop="updatedTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewTrade(scope.row.id)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { tradeAPI, productAPI } from '../api'

export default {
  name: 'TradeList',
  data() {
    return {
      trades: [],
      loading: false,
      searchQuery: '',
      tradeStatus: '',
      dateRange: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      productCache: {}
    }
  },
  computed: {
    filteredTrades() {
      let filtered = this.trades
      
      // 搜索过滤
      if (this.searchQuery) {
        filtered = filtered.filter(trade => 
          trade.id.toString().includes(this.searchQuery) ||
          trade.productName.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }
      
      // 状态过滤
      if (this.tradeStatus !== '') {
        filtered = filtered.filter(trade => trade.status.toString() === this.tradeStatus)
      }
      
      // 日期范围过滤
      if (this.dateRange && this.dateRange.length === 2) {
        const [startDate, endDate] = this.dateRange
        filtered = filtered.filter(trade => {
          const tradeDate = new Date(trade.createdTime)
          return tradeDate >= startDate && tradeDate <= endDate
        })
      }
      
      // 计算总数
      this.total = filtered.length
      
      // 分页
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      return filtered.slice(startIndex, endIndex)
    },
    emptyText() {
      if (this.loading) return '加载中...'
      return '暂无交易记录'
    }
  },
  mounted() {
    this.loadTrades()
  },
  watch: {
    searchQuery() {
      this.currentPage = 1
    },
    tradeStatus() {
      this.currentPage = 1
    },
    dateRange() {
      this.currentPage = 1
    }
  },
  methods: {
    async loadTrades() {
      this.loading = true
      try {
        // 获取所有交易记录
        const response = await tradeAPI.getAllTrades()
        
        // 格式化数据
        this.trades = response.map(async trade => {
          // 获取商品信息
          if (trade.productId && !this.productCache[trade.productId]) {
            try {
              const product = await productAPI.getProduct(trade.productId)
              this.productCache[trade.productId] = product
            } catch (error) {
              console.error('Failed to load product details:', error)
              this.productCache[trade.productId] = { name: '未知商品', images: [] }
            }
          }
          
          const productInfo = this.productCache[trade.productId] || { name: '未知商品', images: [] }
          
          return {
            ...trade,
            productName: productInfo.name,
            product: productInfo,
            createdTime: this.formatDate(trade.createdTime),
            updatedTime: this.formatDate(trade.updatedTime)
          }
        })
        
        // 等待所有异步操作完成
        this.trades = await Promise.all(this.trades)
      } catch (error) {
        this.$message.error('加载交易列表失败')
        this.trades = []
      } finally {
        this.loading = false
      }
    },
    getProductImage(product) {
      if (!product || !product.images) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const imageArray = typeof product.images === 'string' ? JSON.parse(product.images) : product.images
      return imageArray && imageArray.length > 0 ? imageArray[0] : 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
    },
    getStatusType(status) {
      const statusTypes = {
        1: 'info',    // 待接受
        2: 'primary', // 已接受
        3: 'warning', // 交易中
        4: 'success', // 已完成
        5: 'danger',  // 已取消
        6: 'danger'   // 已拒绝
      }
      return statusTypes[status] || 'default'
    },
    getStatusText(status) {
      const statusTexts = {
        1: '待接受',
        2: '已接受',
        3: '交易中',
        4: '已完成',
        5: '已取消',
        6: '已拒绝'
      }
      return statusTexts[status] || '未知'
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    },
    viewTrade(tradeId) {
      this.$router.push(`/trade/${tradeId}`)
    },
    handleSizeChange(size) {
      this.pageSize = size
    },
    handleCurrentChange(current) {
      this.currentPage = current
    }
  }
}
</script>

<style scoped>
.trade-list-container {
  padding: 0;
}

.trade-card {
  max-width: 1200px;
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

.search-filter {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.status-select {
  width: 150px;
}

.date-picker {
  width: 280px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumbnail {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details {
  flex: 1;
  overflow: hidden;
}

.product-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.product-price {
  color: #f56c6c;
  font-size: 14px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .trade-card {
    max-width: 100%;
  }
  
  .search-filter {
    flex-direction: column;
  }
  
  .search-input,
  .status-select,
  .date-picker {
    width: 100%;
  }
}
</style>