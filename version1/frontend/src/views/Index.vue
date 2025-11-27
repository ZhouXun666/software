<template>
  <div class="index-container">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <div class="filter-content">
        <el-input 
          v-model="searchKeyword"
          placeholder="搜索商品名称"
          prefix-icon="el-icon-search"
          class="search-input"
          :clearable="isClearable"
        />
        <el-select v-model="selectedCategory" placeholder="选择分类" clearable>
          <el-option label="全部分类" value="" />
          <el-option label="电子产品" value="1" />
          <el-option label="图书文具" value="2" />
          <el-option label="服装鞋帽" value="3" />
          <el-option label="生活用品" value="4" />
          <el-option label="其他" value="5" />
        </el-select>
        <el-button type="primary" @click="handleFilter" size="default">筛选</el-button>
        <el-button @click="resetFilter" size="default">重置</el-button>
        <div class="sort-container">
          <span>排序：</span>
          <el-radio-group v-model="sortBy" @change="handleSort">
            <el-radio-button label="0">默认</el-radio-button>
            <el-radio-button label="1">价格从低到高</el-radio-button>
            <el-radio-button label="2">价格从高到低</el-radio-button>
            <el-radio-button label="3">最新发布</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <!-- 商品列表 -->
    <div class="product-list">
      <el-card
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="goToDetail(product.id)"
      >
        <div class="product-image">
          <img :src="getMainImage(product.images)" :alt="product.name" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-description">{{ product.description }}</p>
          <div class="product-meta">
            <span class="product-price">¥{{ product.price }}</span>
            <span class="product-date">{{ formatDate(product.createTime) }}</span>
          </div>
          <div class="product-user">
            <el-avatar :size="20" :src="product.userAvatar || ''">{{ product.username?.charAt(0) }}</el-avatar>
            <span>{{ product.username || '用户' }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[3, 6, 10]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 加载中 -->
    <el-dialog v-model="loading" title="" width="30%" :show-close="false">
      <div class="loading-container">
        <el-spinner type="spinner" />
        <p>加载中...</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { productAPI } from '../api'

export default {
  name: 'ProductIndex',
  data() {
    return {
      searchKeyword: '',
      isClearable:true,
      products: [],
      total: 0,
      currentPage: 1,
      pageSize: 3,
      selectedCategory: '',
      //'0'表示默认按照id升序排列，'1'表示价格从低到高升序排列，'2'表示价格从高到低降序排列，'3'表示创建时间从新到旧降序排列
      sortBy: '0',
      loading: false
    }
  },
  mounted() {
    this.loadProducts()
    // 监听路由参数变化
    this.$watch(
      () => this.$route.query,
      (newQuery) => {
        if (newQuery.keyword) {
          // 处理搜索参数
          this.searchKeyword = newQuery.keyword
          this.loadProducts()
        }
      },
      { immediate: true }
    )
  },
  methods: {
    async loadProducts() {
      this.loading = true
      try {
        let response
        const params = {
          page: this.currentPage-1,
          size: this.pageSize,
          sort: this.sortBy
        }
        if(this.searchKeyword){
          params.searchKeyword = this.searchKeyword
          this.selectedCategory = ""
          response = await productAPI.searchProducts(params)
        }else if(this.selectedCategory){
          response = await productAPI.getProductsByCategory(params,this.selectedCategory)
        }else{
          response = await productAPI.getProducts(params)
        }
        this.products = response.content|| []
        this.total = response.totalElements
      } catch (error) {
        this.$message.error('加载商品失败')
      } finally {
        this.loading = false
      }
    },
    handleFilter() {
      this.currentPage = 1
      this.loadProducts()
    },
    resetFilter() {
      this.selectedCategory = ''
      this.searchKeyword = ''
      this.sortBy = "0"
      this.currentPage = 1
      this.loadProducts()
    },
    handleSort() {
      this.currentPage = 1
      this.loadProducts()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadProducts()
    },
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage
      this.loadProducts()
    },
    goToDetail(productId) {
      this.$router.push(`/product/${productId}`)
    },
    getMainImage(images) {
      if (!images) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const baseURL = 'http://localhost:8080/'
      return `${baseURL}${images}`
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) {
        return '今天'
      } else if (days === 1) {
        return '昨天'
      } else if (days < 7) {
        return `${days}天前`
      } else {
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
      }
    }
  }
}
</script>

<style scoped>
.index-container {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-content .el-select {
  width: 200px;
}

.sort-container {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
  margin-bottom: 12px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px 0;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.product-date {
  font-size: 12px;
  color: #909399;
}

.product-user {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.loading-container p {
  margin-top: 16px;
  color: #606266;
}

@media (max-width: 768px) {
  .product-list {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  
  .filter-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-content .el-select {
    width: 100%;
  }
  
  .sort-container {
    margin-left: 0;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>