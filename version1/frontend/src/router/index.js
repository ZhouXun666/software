import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Home = () => import('../views/Home.vue')
const Index = () => import('../views/Index.vue')
const ProductDetail = () => import('../views/ProductDetail.vue')
const ProductCreate = () => import('../views/ProductCreate.vue')
const ProductUpdate = () => import('../views/ProductUpdate.vue')
const UserCenter = () => import('../views/UserCenter.vue')
const UserTrades = () => import('../views/UserTrades.vue')
const TradeDetail = () => import('../views/TradeDetail.vue')
const NotFound = () => import('../views/NotFound.vue')
const UserUpdate = () => import('../views/UserUpdate.vue')
const MyProducts = () => import('../views/MyProducts.vue')
const TradeList = () => import('../views/TradeList.vue')
const UserInfo = () => import('../views/UserInfo.vue')

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Index',
        component: Index
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: ProductDetail
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/user',
    name: 'User',
    component: Home,
    redirect: '/user/center',
    meta: { requiresAuth: true },
    children: [
      { path: 'center', name: 'UserCenter', component: UserCenter },
      { path: 'info', name: 'UserInfo', component: UserInfo },
      { path: 'update', name: 'UserUpdate', component: UserUpdate },
      { path: 'products', name: 'MyProducts', component: MyProducts },
      { path: 'trades', name: 'UserTrades', component: UserTrades }
    ]
  },
  {
    path: '/trade-list',
    name: 'TradeList',
    component: Home,
    meta: { requiresAuth: true },
    children: [
      { path: '', component: TradeList }
    ]
  },
  {
    path: '/product',
    name: 'Product',
    component: Home,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'create',
        name: 'ProductCreate',
        component: ProductCreate
      },
      {
        path: 'update/:id',
        name: 'ProductUpdate',
        component: ProductUpdate
      }
    ]
  },
  // 新增交易详情路由
  {
    path: '/trade/detail/:id',
    name: 'TradeDetail',
    component: TradeDetail,
    meta: { requiresAuth: true }
  },
  // 新增404页面路由
  {
    path: '/404',
    name: 'NotFound',
    component: NotFound,
    meta: { requiresAuth: false }
  },
  // 捕获所有未匹配的路由，重定向到404页面
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 处理登录验证
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  // 如果需要登录且没有token，则跳转到登录页
  if (requiresAuth && !token) {
    next({ name: 'Login' })
  } else if (requiresAuth && !userInfo) {
    // 如果有token但没有用户信息，尝试获取用户信息
    try {
      // 这里可以添加获取用户信息的逻辑
      next()
    } catch (error) {
      localStorage.removeItem('token')

    }
  } else {
    next()
  }
})

export default router