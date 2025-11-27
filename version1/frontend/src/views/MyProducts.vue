<template>
  <div class="my-products-container">
    <el-card class="products-card">
      <template #header>
        <div class="card-header">
          <span>我的商品</span>
          <el-button type="primary" @click="goToCreate" class="create-btn">
            <el-icon><Plus /></el-icon>
            发布商品
          </el-button>
        </div>
      </template>
      
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称"
          prefix-icon="el-icon-search"
          class="search-input"
          clearable
        />
        <el-select v-model="productStatus" placeholder="商品状态" clearable class="status-select">
          <el-option label="全部" value="" />
          <el-option label="上架" value="1" />
          <el-option label="下架" value="0" />
        </el-select>
        <el-select v-model="productCategory" placeholder="商品分类" clearable class="category-select">
          <el-option label="全部" value="" />
          <el-option label="电子产品" value="1" />
          <el-option label="图书文具" value="2" />
          <el-option label="服装鞋帽" value="3" />
          <el-option label="生活用品" value="4" />
          <el-option label="其他" value="5" />
        </el-select>
      </div>
      
      <el-table
        v-loading="loading"
        :data="filteredProducts"
        style="width: 100%"
        :empty-text="emptyText"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="200">
          <template #default="scope">
            <div class="product-name-wrapper">
              <img :src="getFirstImage(scope.row.images)" class="product-thumbnail" />
              <span class="product-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="scope">{{ getCategoryName(scope.row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="scope">{{formatDate(scope.row.createTime)}}</template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewProduct(scope.row.id)" class="btn-view">
              查看
            </el-button>
            <el-button v-if="scope.row.status != 2" type="warning" size="small" @click="editProduct(scope.row.id)" class="btn-edit">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteProduct(scope.row)" class="btn-delete">
              删除
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
    
    <!-- 确认删除对话框 -->
    <el-dialog v-model="confirmDialogVisible" title="确认删除" width="30%">
      <div>确定要删除该商品吗？删除后将无法恢复。</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="confirmDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import { productAPI } from '../api'

export default {
  name: 'MyProducts',
  components: {
    Plus
  },
  data() {
    return {
      products: [],
      loading: false,
      searchQuery: '',
      productStatus: '',
      productCategory: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      confirmDialogVisible: false,
      deleteProductId: null
    }
  },
  computed: {
    filteredProducts() {
      let filtered = this.products
      
      // 搜索过滤
      if (this.searchQuery) {
        filtered = filtered.filter(product => 
          product.name.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }
      
      // 状态过滤
      if (this.productStatus !== '') {
        filtered = filtered.filter(product => product.status.toString() === this.productStatus)
      }
      
      // 分类过滤
      if (this.productCategory !== '') {
        filtered = filtered.filter(product => product.categoryId.toString() === this.productCategory)
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
      return '暂无商品'
    }
  },
  mounted() {
    this.loadMyProducts()
  },
  watch: {
    searchQuery() {
      this.currentPage = 1
    },
    productStatus() {
      this.currentPage = 1
    },
    productCategory() {
      this.currentPage = 1
    }
  },
  methods: {
    async loadMyProducts() {
      this.loading = true
      try {
        //获得当前用户的id
        const userInfoString = localStorage.getItem('userInfo');
        const userInfo = JSON.parse(userInfoString);
        const id = userInfo.id;
        const response = await productAPI.getUserProducts(id)
        this.products = response.map(product => ({
          ...product,
          createdTime: this.formatDate(product.createTime)
        }))
      } catch (error) {
        this.$message.error('加载商品列表失败')
        this.products = []
      } finally {
        this.loading = false
      }
    },
    getFirstImage(images) {
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
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    },
    goToCreate() {
      this.$router.push('/product/create')
    },
    viewProduct(productId) {
      this.$router.push(`/product/${productId}`)
    },
    editProduct(productId) {
      this.$router.push(`/product/update/${productId}`)
    },
    deleteProduct(product) {
      this.deleteProductId = product.id
      this.confirmDialogVisible = true
    },
    async confirmDelete() {
      this.confirmDialogVisible = false
      try {
        await productAPI.deleteProduct(this.deleteProductId)
        this.products = this.products.filter(product => product.id !== this.deleteProductId)
        this.$message.success('商品删除成功')
      } catch (error) {
        this.$message.error('删除失败，请重试')
      }
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
.my-products-container {
  padding: 0;
}

.products-card {
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

.status-select,
.category-select {
  width: 150px;
}

.product-name-wrapper {
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

.product-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-view {
  margin-right: 5px;
}

.btn-edit {
  margin-right: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .products-card {
    max-width: 100%;
  }
  
  .search-filter {
    flex-direction: column;
  }
  
  .search-input,
  .status-select,
  .category-select {
    width: 100%;
  }
}
</style>