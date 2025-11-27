<template>
  <div class="user-trades-container">
    <el-card class="trades-card">
      <template #header>
        <div class="card-header">
          <span>我的交易</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="我发起的交易" name="buyer">
          <div class="trade-content">
            <div class="search-filter">
              <el-input
                v-model="buyerSearchQuery"
                placeholder="搜索商品名称"
                prefix-icon="el-icon-search"
                class="search-input"
                clearable
              />
              <el-select v-model="buyerTradeStatus" placeholder="选择交易状态" clearable class="status-select">
                <el-option label="全部" value="" />
                <el-option label="待确认" value="0" />
                <el-option label="已接受" value="1" />
                <el-option label="已拒绝" value="2" />
                <el-option label="已完成" value="3" />
                <el-option label="已取消" value="4" />
              </el-select>
              <el-date-picker
                v-model="buyerDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="date-picker"
              />
            </div>
            
            <el-table
              v-loading="buyerLoading"
              :data="filteredBuyerTrades"
              style="width: 100%"
              :empty-text="buyerEmptyText"
              @row-click="viewTradeDetail"
            >
              <el-table-column prop="id" label="交易ID" width="100" />
              <el-table-column prop="productName" label="商品信息" min-width="200">
                <template #default="scope">
                  <div class="product-info-wrapper">
                    <img :src="getProductImage(scope.row.productImages)" class="product-image" />
                    <div class="product-details">
                      <div class="product-name">{{ scope.row.productName }}</div>
                      <div class="product-price">¥{{ scope.row.productPrice }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="sellerName" label="卖家" width="120">
                <template #default="scope">
                  <el-tooltip :content="scope.row.sellerContact || '暂无联系方式'">
                    <div class="user-name">{{ scope.row.sellerName }}</div>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="交易状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdTime" label="申请时间" width="180" />
              <el-table-column prop="updatedTime" label="更新时间" width="180" />
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click.stop="viewTradeDetail(scope.row)" class="btn-view">
                    详情
                  </el-button>
                  <el-button type="danger" size="small" @click.stop="cancelTrade(scope.row)" v-if="scope.row.status === 0" class="btn-cancel">
                    取消
                  </el-button>
                  <el-button type="success" size="small" @click.stop="confirmReceive(scope.row)" v-if="scope.row.status === 1" class="btn-confirm">
                    确认收货
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="buyerCurrentPage"
                v-model:page-size="buyerPageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="buyerTotal"
                @size-change="handleBuyerSizeChange"
                @current-change="handleBuyerCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我收到的交易" name="seller">
          <div class="trade-content">
            <div class="search-filter">
              <el-input
                v-model="sellerSearchQuery"
                placeholder="搜索商品名称"
                prefix-icon="el-icon-search"
                class="search-input"
                clearable
              />
              <el-select v-model="sellerTradeStatus" placeholder="选择交易状态" clearable class="status-select">
                <el-option label="全部" value="" />
                <el-option label="待确认" value="0" />
                <el-option label="已接受" value="1" />
                <el-option label="已拒绝" value="2" />
                <el-option label="已完成" value="3" />
                <el-option label="已取消" value="4" />
              </el-select>
              <el-date-picker
                v-model="sellerDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="date-picker"
              />
            </div>
            
            <el-table
              v-loading="sellerLoading"
              :data="filteredSellerTrades"
              style="width: 100%"
              :empty-text="sellerEmptyText"
              @row-click="viewTradeDetail"
            >
              <el-table-column prop="id" label="交易ID" width="100" />
              <el-table-column prop="productName" label="商品信息" min-width="300">
                <template #default="scope">
                  <div class="product-info-wrapper">
                    <img :src="getProductImage(scope.row.productImages)" class="product-image" />
                    <div class="product-details">
                      <div class="product-name">{{ scope.row.productName }}</div>
                      <div class="product-price">¥{{ scope.row.productPrice }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="buyerName" label="买家" width="120">
                <template #default="scope">
                  <el-tooltip :content="scope.row.buyerContact || '暂无联系方式'">
                    <div class="user-name">{{ scope.row.buyerName }}</div>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="交易状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdTime" label="申请时间" width="180" />
              <el-table-column prop="updatedTime" label="更新时间" width="180" />
              <el-table-column label="操作" width="300" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click.stop="viewTradeDetail(scope.row)" class="btn-view">
                    详情
                  </el-button>
                  <el-button type="success" size="small" @click.stop="acceptTrade(scope.row)" v-if="scope.row.status === 0" class="btn-accept">
                    接受
                  </el-button>
                  <el-button type="danger" size="small" @click.stop="rejectTrade(scope.row)" v-if="scope.row.status === 0" class="btn-reject">
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="sellerCurrentPage"
                v-model:page-size="sellerPageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="sellerTotal"
                @size-change="handleSellerSizeChange"
                @current-change="handleSellerCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
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
  name: 'UserTrades',
  data() {
    return {
      activeTab: 'buyer',
      // 买家交易数据
      buyerTrades: [],
      buyerLoading: false,
      buyerSearchQuery: '',
      buyerTradeStatus: '',
      buyerDateRange: null,
      buyerCurrentPage: 1,
      buyerPageSize: 10,
      buyerTotal: 0,
      // 卖家交易数据
      sellerTrades: [],
      sellerLoading: false,
      sellerSearchQuery: '',
      sellerTradeStatus: '',
      sellerDateRange: null,
      sellerCurrentPage: 1,
      sellerPageSize: 10,
      sellerTotal: 0,
      // 确认对话框
      confirmDialogVisible: false,
      confirmDialogTitle: '',
      confirmDialogContent: '',
      actionTrade: null,
      actionType: ''
    }
  },
  computed: {
    filteredBuyerTrades() {
      let filtered = this.buyerTrades
      
      // 搜索过滤
      if (this.buyerSearchQuery) {
        filtered = filtered.filter(trade => 
          trade.productName.toLowerCase().includes(this.buyerSearchQuery.toLowerCase())
        )
      }
      
      // 状态过滤
      if (this.buyerTradeStatus !== '') {
        filtered = filtered.filter(trade => trade.status.toString() === this.buyerTradeStatus)
      }
      
      // 日期过滤
      if (this.buyerDateRange && this.buyerDateRange.length === 2) {
        filtered = filtered.filter(trade => {
          const tradeDate = new Date(trade.createdTime)
          const startDate = new Date(this.buyerDateRange[0])
          const endDate = new Date(this.buyerDateRange[1])
          return tradeDate >= startDate && tradeDate <= endDate
        })
      }
      
      // 分页
      this.buyerTotal = filtered.length
      const startIndex = (this.buyerCurrentPage - 1) * this.buyerPageSize
      const endIndex = startIndex + this.buyerPageSize
      return filtered.slice(startIndex, endIndex)
    },
    filteredSellerTrades() {
      let filtered = this.sellerTrades
      
      // 搜索过滤
      if (this.sellerSearchQuery) {
        filtered = filtered.filter(trade => 
          trade.productName.toLowerCase().includes(this.sellerSearchQuery.toLowerCase())
        )
      }
      
      // 状态过滤
      if (this.sellerTradeStatus !== '') {
        filtered = filtered.filter(trade => trade.status.toString() === this.sellerTradeStatus)
      }
      
      // 日期过滤
      if (this.sellerDateRange && this.sellerDateRange.length === 2) {
        filtered = filtered.filter(trade => {
          const tradeDate = new Date(trade.createdTime)
          const startDate = new Date(this.sellerDateRange[0])
          const endDate = new Date(this.sellerDateRange[1])
          return tradeDate >= startDate && tradeDate <= endDate
        })
      }
      
      // 分页
      this.sellerTotal = filtered.length
      const startIndex = (this.sellerCurrentPage - 1) * this.sellerPageSize
      const endIndex = startIndex + this.sellerPageSize
      return filtered.slice(startIndex, endIndex)
    },
    buyerEmptyText() {
      if (this.buyerLoading) return '加载中...'
      return '暂无发起的交易'
    },
    sellerEmptyText() {
      if (this.sellerLoading) return '加载中...'
      return '暂无收到的交易'
    }
  },
  mounted() {
    this.loadBuyerTrades()
    this.loadSellerTrades()
  },
  watch: {
    // 买家筛选条件监听
    buyerSearchQuery() {
      this.buyerCurrentPage = 1
    },
    buyerTradeStatus() {
      this.buyerCurrentPage = 1
    },
    buyerDateRange() {
      this.buyerCurrentPage = 1
    },
    // 卖家筛选条件监听
    sellerSearchQuery() {
      this.sellerCurrentPage = 1
    },
    sellerTradeStatus() {
      this.sellerCurrentPage = 1
    },
    sellerDateRange() {
      this.sellerCurrentPage = 1
    }
  },
  methods: {
    handleTabClick() {
      // 切换标签页时可以添加逻辑
    },
    async loadBuyerTrades() {
      this.buyerLoading = true
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        const response = await tradeAPI.getMyBuyTrades(id)
        this.buyerTrades = response.map(trade => ({
          ...trade,
          createdTime: dayjs(trade.createTime).format("YYYY-MM-DD HH:mm"),
          updatedTime: dayjs(trade.updateTime).format("YYYY-MM-DD HH:mm")
        }))
      } catch (error) {
        this.$message.error('加载交易记录失败')
        this.buyerTrades = []
      } finally {
        this.buyerLoading = false
      }
    },
    async loadSellerTrades() {
      this.sellerLoading = true
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        const response = await tradeAPI.getMySellTrades(id)
        this.sellerTrades = response.map(trade => ({
          ...trade,
          createdTime: dayjs(trade.createTime).format("YYYY-MM-DD HH:mm"),
          updatedTime: dayjs(trade.updateTime).format("YYYY-MM-DD HH:mm")
        }))
      } catch (error) {
        this.$message.error('加载交易记录失败')
        this.sellerTrades = []
      } finally {
        this.sellerLoading = false
      }
    },
    getProductImage(productImages) {
      if (!productImages) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const baseURL = 'http://localhost:8080/'
      return `${baseURL}${productImages}`
    },
    // formatDate(dateString) {
    //   if (!dateString) return ''
    //   const date = new Date(dateString)
    //   return date.toLocaleString('zh-CN')
    // },
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
    viewTradeDetail(trade) {
      this.$router.push(`/trade/detail/${trade.id}`)
    },
    // 买家操作
    cancelTrade(trade) {
      this.actionTrade = trade
      this.actionType = 'cancel'
      this.confirmDialogTitle = '取消交易'
      this.confirmDialogContent = '确定要取消该交易吗？'
      this.confirmDialogVisible = true
    },
    confirmReceive(trade) {
      this.actionTrade = trade
      this.actionType = 'confirm'
      this.confirmDialogTitle = '确认收货'
      this.confirmDialogContent = '请确认您已收到商品，确认后交易将完成。'
      this.confirmDialogVisible = true
    },
    // 卖家操作
    acceptTrade(trade) {
      this.actionTrade = trade
      this.actionType = 'accept'
      this.confirmDialogTitle = '接受交易'
      this.confirmDialogContent = '确定要接受该交易申请吗？'
      this.confirmDialogVisible = true
    },
    rejectTrade(trade) {
      this.actionTrade = trade
      this.actionType = 'reject'
      this.confirmDialogTitle = '拒绝交易'
      this.confirmDialogContent = '确定要拒绝该交易申请吗？'
      this.confirmDialogVisible = true
    },
    async confirmAction() {
      this.confirmDialogVisible = false
      
      try {
        switch (this.actionType) {
          case 'cancel':
            await tradeAPI.cancelTrade(this.actionTrade.id)
            this.$message.success('交易已取消')
            this.loadBuyerTrades()
            break
          case 'confirm':
            await tradeAPI.confirmReceive(this.actionTrade.id)
            this.$message.success('已确认收货，交易完成')
            this.loadBuyerTrades()
            break
          case 'accept':
            await tradeAPI.acceptTrade(this.actionTrade.id)
            this.$message.success('已接受交易')
            this.loadSellerTrades()
            break
          case 'reject':
            await tradeAPI.rejectTrade(this.actionTrade.id)
            this.$message.success('已拒绝交易')
            this.loadSellerTrades()
            break
        }
      } catch (error) {
        this.$message.error('操作失败，请重试')
      }
    },
    // 分页处理
    handleBuyerSizeChange(size) {
      this.buyerPageSize = size
    },
    handleBuyerCurrentChange(current) {
      this.buyerCurrentPage = current
    },
    handleSellerSizeChange(size) {
      this.sellerPageSize = size
    },
    handleSellerCurrentChange(current) {
      this.sellerCurrentPage = current
    }
  }
}
</script>

<style scoped>
.user-trades-container {
  padding: 0;
}

.trades-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.trade-content {
  padding: 20px 0;
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
  width: 300px;
}

/* 表格样式 */
.product-info-wrapper {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  color: #F56C6C;
  font-weight: 600;
}

.user-name {
  cursor: pointer;
}

.user-name:hover {
  color: #409EFF;
}

/* 按钮样式 */
.btn-view {
  margin-right: 5px;
}

.btn-accept {
  margin-right: 5px;
}

.btn-reject {
  margin-right: 5px;
}

.btn-cancel {
  margin-right: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .trades-card {
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
  
  .pagination-container {
    justify-content: center;
  }
}
</style>