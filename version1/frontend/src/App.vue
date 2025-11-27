<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
  </div>
</template>

<script>
export default {
  name: 'App',
  provide() {
    return {
      reload: this.reload
    }
  },
  data() {
    return {
      isRouterAlive: true
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false
      this.$nextTick(() => {
        this.isRouterAlive = true
      })
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color: #333;
  background-color: #f5f5f5;
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* 通用样式 */
.el-card {
  margin-bottom: 20px;
}

.el-button {
  margin-right: 10px;
}

/* 页面容器 */
.page-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>