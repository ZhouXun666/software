# 校园二手商品交易系统

## 项目简介
这是一个基于Vue + Java Spring Boot开发的校园二手商品交易系统，实现了用户管理、商品管理、交易管理等核心功能。

## 技术栈

### 前端
- Vue 3
- Vue Router
- Axios
- Element Plus

### 后端
- Java 8+
- Spring Boot 2.x
- Spring Data JPA
- MySQL

## 项目结构

### 前端
```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── assets/             # 项目资源文件
│   ├── components/         # 组件
│   ├── views/              # 页面视图
│   ├── router/             # 路由配置
│   ├── api/                # API请求
│   ├── utils/              # 工具函数
│   ├── App.vue             # 应用根组件
│   └── main.js             # 入口文件
├── package.json
└── vue.config.js
```

### 后端
```
backend/
├── src/main/java/com/school/secondhand/
│   ├── user/               # 用户管理模块
│   ├── product/            # 商品管理模块
│   ├── trade/              # 交易管理模块
│   ├── config/             # 配置类
│   └── SecondHandApplication.java  # 应用启动类
├── src/main/resources/
│   ├── application.properties  # 应用配置
│   └── static/             # 静态资源
└── pom.xml                 # Maven配置
```

## 系统功能

### 1. 用户管理子系统
- 用户注册
- 用户登录
- 用户信息管理
- JWT认证

### 2. 商品管理子系统
- 创建商品
- 修改商品
- 浏览商品
- 搜索商品
- 商品分类管理

### 3. 交易管理子系统
- 申请交易
- 接受交易
- 拒绝交易
- 取消交易
- 完成交易
- 查询交易记录

## 环境要求

### 前端
- Node.js 14+
- npm 6+

### 后端
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

## 数据库配置
1. 创建MySQL数据库 `secondhand_db`
2. 配置数据库连接信息（在 `backend/src/main/resources/application.properties`）
   - 用户名：root
   - 密码：root
   - 端口：3306

## 运行步骤

### 后端运行
1. 进入后端目录：`cd backend`
2. 编译项目：`mvn clean install`
3. 启动应用：`mvn spring-boot:run`

### 前端运行
1. 进入前端目录：`cd frontend`
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run serve`

## 访问系统
- 前端访问地址：http://localhost:8081
- 后端API地址：http://localhost:8080/api

## 注意事项
1. 确保MySQL服务已启动
2. 确保数据库已创建
3. 前端需要在后端启动后再启动
4. 图片上传功能需要创建 `upload` 目录

## 功能说明

### 用户相关
- 注册：POST /api/users/register
- 登录：POST /api/users/login
- 获取用户信息：GET /api/users/me
- 更新用户信息：PUT /api/users/me

### 商品相关
- 创建商品：POST /api/products
- 修改商品：PUT /api/products/{id}
- 获取商品列表：GET /api/products
- 获取商品详情：GET /api/products/{id}
- 搜索商品：GET /api/products/search

### 交易相关
- 申请交易：POST /api/trades/apply
- 接受交易：POST /api/trades/{id}/accept
- 拒绝交易：POST /api/trades/{id}/reject
- 取消交易：POST /api/trades/{id}/cancel
- 完成交易：POST /api/trades/{id}/complete
- 获取交易列表：GET /api/trades