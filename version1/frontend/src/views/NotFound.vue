<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="number">404</div>
      <div class="title">页面不存在</div>
      <div class="description">您访问的页面可能已被删除、移动或暂时不可用</div>
      <div class="actions">
        <el-button type="primary" @click="goToHome" size="large">返回首页</el-button>
        <el-button @click="goBack" size="large">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goToHome() {
      this.$router.push('/')
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.number {
  font-size: 100px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 15px;
}

.description {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
  line-height: 1.6;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

@media (max-width: 768px) {
  .not-found-content {
    padding: 30px 20px;
    margin: 0 20px;
  }
  
  .number {
    font-size: 80px;
  }
  
  .title {
    font-size: 20px;
  }
  
  .description {
    font-size: 14px;
  }
  
  .actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>