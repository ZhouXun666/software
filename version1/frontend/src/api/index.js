import axios from 'axios'

// 用户相关API
export const userAPI = {
  // 用户注册
  register: (data) => {
    return axios.post('/users/register', data)
  },
  
  // 用户登录
  login: (data) => {
    return axios.post('/users/login', data)
  },
  
  // 获取当前用户信息
  getUserInfo: (id) => {
    return axios.get(`/users/${id}`)
  },
  
  // 更新用户信息
  updateUserInfo: (id,data) => {
    return axios.put(`/users/update/${id}`, data)
  },
  
  // 获取用户列表（管理员）
  getUsers: (params) => {
    return axios.get('/users', { params })
  }
}

// 商品相关API
export const productAPI = {
  // 创建商品
  createProduct: (data) => {
    return axios.post('/products/create', data)
  },
  
  // 获取商品列表（分页）
  getProducts: (params) => {
    return axios.get('/products/list', {params})
  },
  //根据商品类别获得商品列表（分页）
  getProductsByCategory: (params,category) =>{
    return axios.get(`products/category/${category}`,{params})
  },
  // 获取商品详情
  getProductById: (id) => {
    return axios.get(`/products/${id}`)
  },
  
  // 更新商品
  updateProduct: (id, data) => {
    return axios.put(`/products/update/${id}`, data)
  },
  
  // 删除商品
  deleteProduct: (id) => {
    return axios.delete(`/products/delete/${id}`)
  },
  // 获取我的商品
  getUserProducts: (id) => {
    return axios.get(`/products/user/${id}`)
  },
  
  // 搜索商品
  searchProducts: (params) => {
    return axios.get('/products/search', {params})
  }
}

// 交易相关API
export const tradeAPI = {
  // 申请交易
  applyTrade: (data) => {
    return axios.post('/trades/apply', data)
  },
  
  // 接受交易
  acceptTrade: (id) => {
    return axios.post(`/trades/${id}/accept`)
  },
  
  // 拒绝交易
  rejectTrade: (id) => {
    return axios.post(`/trades/${id}/reject`)
  },
  
  // 取消交易
  cancelTrade: (id) => {
    return axios.post(`/trades/${id}/cancel`)
  },
  
  // 完成交易
  completeTrade: (id) => {
    return axios.post(`/trades/${id}/complete`)
  },
  
  // 获取交易详情
  getTradeById: (id) => {
    return axios.get(`/trades/${id}`)
  },
  
  // 获取我作为买家的交易
  getMyBuyTrades: (id) => {
    return axios.get(`/trades/buyer/${id}`)
  },
  
  // 获取我作为卖家的交易
  getMySellTrades: (id) => {
    return axios.get(`/trades/seller/${id}`)
  },
  
  // 获取商品的交易记录
  getProductTrades: (productId) => {
    return axios.get(`/trades/product/${productId}`)
  }
}

// 上传图片API
export const uploadAPI = {
  // 上传图片
  uploadImage: (formData) => {
    return axios.post('/products/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}