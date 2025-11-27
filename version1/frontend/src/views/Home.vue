<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="toHome">
          <h1>校园二手交易</h1>
        </div>
        <div class="search-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            class="search-input"
            :clearable="isClearable"
          >
            <!-- <template #append>
              <el-button @click="handleSearch" :icon="Search"></el-button>
            </template> -->
          </el-input>
        </div>
        <div class="nav-right">
          <el-button v-if="!isLogin" type="text" @click="toLogin">登录</el-button>
          <el-button v-if="!isLogin" type="primary" @click="toRegister">注册</el-button>
          <template v-else>
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" :src="userInfo?.avatar || ''">{{ userInfo?.username?.charAt(0) }}</el-avatar>
                <span class="user-name">{{ userInfo?.username }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="info">个人中心</el-dropdown-item>
                  <el-dropdown-item command="products">我的商品</el-dropdown-item>
                  <el-dropdown-item command="trades">我的交易</el-dropdown-item>
                  <el-dropdown-item command="create" type="primary">发布商品</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 内容区域 -->
    <el-main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 校园二手商品交易系统 - 版权所有</p>
        <p>让闲置物品流动起来，共享美好校园生活</p>
      </div>
    </el-footer>
  </div>
</template>

<script>
import { ArrowDown, Search } from '@element-plus/icons-vue'

export default {
  name: 'HomeLayout',
  components: {
    ArrowDown,
    Search
  },
  data() {
    return {
      searchKeyword: '',
      isClearable:true,
      isLogin: false,
      userInfo: null
    }
  },
  mounted() {
    this.checkLoginStatus()
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      const userInfoStr = localStorage.getItem('userInfo')
      if (token && userInfoStr) {
        this.isLogin = true
        try{
          this.userInfo = JSON.parse(userInfoStr)
        }catch (error){
          console.error('解析用户数据失败:', error);
        }
      }
    },
    handleCommand(command) {
      switch (command) {
        case 'info':
          this.$router.push('/user/info')
          break
        case 'products':
          this.$router.push('/user/products')
          break
        case 'trades':
          this.$router.push('/user/trades')
          break
        case 'create':
          this.$router.push('/product/create')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        this.isLogin = false
        this.userInfo = null
        this.$message.success('退出成功')
        this.$router.push('/')
      }).catch(() => {
        // 取消操作
      })
    },
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push({ path: '/', query: { keyword: this.searchKeyword } })
      }
    },
    toHome() {
      this.$router.push('/')
    },
    toLogin() {
      this.$router.push('/login')
    },
    toRegister() {
      this.$router.push('/register')
    }
  },
  watch: {
    // 监听路由变化，更新登录状态
    '$route'(to, from) {
      this.checkLoginStatus()
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo h1 {
  font-size: 24px;
  color: #667eea;
  margin: 0;
  cursor: pointer;
}

.search-container {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.user-name {
  font-size: 14px;
}

.main {
  flex: 1;
  max-width: 1200px;
  margin: 20px auto;
  width: 100%;
  padding: 0 20px;
}

.footer {
  background-color: #f5f7fa;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #606266;
}

.footer-content p {
  margin: 5px 0;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>