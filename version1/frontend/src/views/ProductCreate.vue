<template>
  <div class="product-create-container">
    <el-card class="create-card">
      <template #header>
        <div class="card-header">发布二手商品</div>
      </template>
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="100px" class="create-form">
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
            <img :src="dialogImageUrl" alt="预览图片" style="width: 100%" />
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
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting" size="large">发布商品</el-button>
          <el-button @click="resetForm" size="large">重置</el-button>
          <el-button @click="goBack" size="large">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import { productAPI, uploadAPI } from '../api'

export default {
  name: 'ProductCreate',
  components: {
    Plus
  },
  data() {
    return {
      productForm: {
        name: '',
        categoryId: '',
        price: '',
        description: '',
        images:[]
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
      submitting: false
    }
  },
  methods: {
    handleFileChange(file, fileList) {
      this.fileList = fileList
      // 更新商品图片数组
      this.productForm.images = fileList.map(file => {
        // 如果是已上传的文件，使用url；否则使用本地预览
        return file.url || URL.createObjectURL(file.raw)
      })
    },
    handleExceed(files, fileList) {
      this.$message.warning(`最多只能上传5张图片，已忽略多余${files.length}张图片`)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url || URL.createObjectURL(file.raw)
      this.dialogVisible = true
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
      // 更新商品图片数组
      this.productForm.images = fileList.map(file => file.url)
    },
    async submitForm() {
      this.$refs.productFormRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            const imagesformData = new FormData();
            imagesformData.append('file',this.fileList[0].raw)
            const imageUrl = (await uploadAPI.uploadImage(imagesformData))
            // 构建表单数据
            const formData = new FormData()
            formData.append('name', this.productForm.name)
            formData.append('description', this.productForm.description)
            formData.append('price', this.productForm.price)
            formData.append('images', imageUrl)
            formData.append('categoryId', this.productForm.categoryId)
            
            // 上传图片（在实际环境中，这里应该先上传图片，然后将图片URL保存）
            // 由于是演示环境，我们直接使用本地预览URL
            //获取用户id
            const userInfoString = localStorage.getItem('userInfo');
            const userInfo = JSON.parse(userInfoString);
            const userId = userInfo.id; 
            formData.append('userId',userId)
            // 调用API发布商品
            await productAPI.createProduct(formData)
            
            this.$message.success('商品发布成功')
            // 跳转到我的商品页面
            this.$router.push('/user/products')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '商品发布失败，请重试')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    resetForm() {
      this.$refs.productFormRef.resetFields()
      this.fileList = []
      this.productForm.images = []
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.product-create-container {
  padding: 0;
}

.create-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.create-form {
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
  .create-card {
    max-width: 100%;
  }
  
  .el-form {
    width: 80px;
  }
}
</style>