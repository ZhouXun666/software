<template>
  <div class="product-update-container">
    <div v-if="loading" class="loading">
      <el-spinner type="spinner" />
      <p>加载中...</p>
    </div>
    <el-card v-else-if="product" class="update-card">
      <template #header>
        <div class="card-header">编辑商品</div>
      </template>
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="100px" class="update-form">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="productForm.categoryId" placeholder="请选择商品分类">
            <el-option label="电子产品" value="1" />
            <el-option label="图书文具" value="2" />
            <el-option label="服装鞋帽" value="3" />
            <el-option label="生活用品" value="4" />
            <el-option label="其他" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input v-model.number="productForm.price" placeholder="请输入商品价格" prefix-icon="el-icon-price-tag">
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="5"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-exceed="handleExceed"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                请上传商品图片，最多5张，单张不超过10MB
              </div>
            </template>
          </el-upload>
          <el-dialog v-model="dialogVisible" title="预览" width="800px">
            <img :src= "dialogImageUrl" alt="预览图片" style="width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="6"
            placeholder="请详细描述商品信息，如：品牌、规格、使用时间、成色等"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio label="1">上架</el-radio>
            <el-radio label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting" size="large">保存修改</el-button>
          <el-button @click="resetForm" size="large">重置</el-button>
          <el-button @click="goBack" size="large">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div v-else class="not-found">
      <el-empty description="商品不存在或已删除" />
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import { productAPI, uploadAPI} from '../api'

export default {
  name: 'ProductUpdate',
  components: {
    Plus
  },
  data() {
    return {
      product: null,
      productForm: {
        name: '',
        categoryId: '',
        price: '',
        description: '',
        images: '',
        status: '1'
      },
      productRules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' },
          { min: 2, max: 100, message: '商品名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择商品分类', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' },
          { min: 10, max: 2000, message: '商品描述长度在 10 到 2000 个字符', trigger: 'blur' }
        ],
        images: [
          { required: true, message: '请至少上传一张商品图片', trigger: 'change' }
        ]
      },
      fileList: [],
      dialogVisible: false,
      dialogImageUrl: '',
      loading: false,
      submitting: false
    }
  },
  mounted() {
    this.loadProductDetail()
  },
  methods: {
    async loadProductDetail() {
      const productId = this.$route.params.id
      this.loading = true
      try {
        const response = await productAPI.getProductById(productId)
        this.product = response
        // 填充表单数据
        this.productForm = {
          name: response.name,
          categoryId: response.categoryId,
          price: response.price,
          description: response.description,
          images: this.getImages(response.images),
          status: response.status.toString()
        }
        // 初始化文件列表
        this.initFileList(this.productForm.images)
      } catch (error) {
        this.$message.error('加载商品信息失败')
        this.product = null
      } finally {
        this.loading = false
      }
    },
    getImages(images) {
      if (!images) return 'https://img.alicdn.com/imgextra/i4/O1CN01q0UcHt1h1sVt0jA8O_!!6000000004122-2-tps-112-112.png'
      const baseURL = 'http://localhost:8080/'
      return `${baseURL}${images}`
    },
    initFileList(imageUrl) {
      this.fileList = [
        {
          uid: Date.now(),
          url: imageUrl,
          status: "success"
        }
      ];
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList
      // 更新商品图片数组
      this.productForm.images = file => {return file.url || URL.createObjectURL(file.raw)}
    },
    handleExceed(files, fileList) {
      this.$message.warning(`最多只能上传5张图片，已忽略多余${files.length}张图片`)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url || URL.createObjectURL(file.raw)
      this.dialogVisible = true
      console.log(this.dialogImageUrl)
    },
    async submitForm() {
      this.$refs.productFormRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            const imagesUrl = "src" + this.productForm.images.split("src")[1]
            const formData = {
              name: this.productForm.name,
              categoryId: this.productForm.categoryId,
              price: this.productForm.price,
              description: this.productForm.description,
              images: imagesUrl,
              status: parseInt(this.productForm.status)
            }
            await productAPI.updateProduct(this.product.id, formData)
            
            this.$message.success('商品更新成功')
            this.$router.push('/user/products')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '更新失败，请重试')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    resetForm() {
      this.$refs.productFormRef.resetFields()
      // 重新初始化文件列表
      this.initFileList(this.getImages(this.product.images))
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.product-update-container {
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

.update-card {
  max-width: 800px;
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

.upload-demo .el-upload-list {
  margin-bottom: 16px;
}

.upload-demo .el-upload {
  margin-bottom: 16px;
}

.el-upload__tip {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
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