# 前端对接规范

> **文档版本**: v1.0
> **最后更新**: 2025-11-20
> **适用框架**: Vue 2/3, React, Angular

---

## 📑 目录

- [1. 快速开始](#1-快速开始)
- [2. 认证流程](#2-认证流程)
  - [2.1 登录流程](#21-登录流程)
  - [2.2 Token管理](#22-token管理)
  - [2.3 Token刷新](#23-token刷新)
  - [2.4 登出流程](#24-登出流程)
- [3. Axios配置](#3-axios配置)
  - [3.1 基础配置](#31-基础配置)
  - [3.2 请求拦截器](#32-请求拦截器)
  - [3.3 响应拦截器](#33-响应拦截器)
  - [3.4 完整配置示例](#34-完整配置示例)
- [4. Fetch配置](#4-fetch配置)
  - [4.1 基础封装](#41-基础封装)
  - [4.2 完整示例](#42-完整示例)
- [5. 统一错误处理](#5-统一错误处理)
  - [5.1 HTTP错误处理](#51-http错误处理)
  - [5.2 业务错误处理](#52-业务错误处理)
  - [5.3 网络错误处理](#53-网络错误处理)
- [6. 路由守卫](#6-路由守卫)
  - [6.1 Vue Router守卫](#61-vue-router守卫)
  - [6.2 React Router守卫](#62-react-router守卫)
- [7. 状态管理](#7-状态管理)
  - [7.1 Vuex用户状态](#71-vuex用户状态)
  - [7.2 React Context用户状态](#72-react-context用户状态)
- [8. 常见问题](#8-常见问题)
- [9. 最佳实践](#9-最佳实践)

---

## 1. 快速开始

### 1.1 环境要求

```json
{
  "node": ">=14.0.0",
  "npm": ">=6.0.0"
}
```

### 1.2 安装依赖

```bash
# 使用Axios
npm install axios

# 使用Day.js处理日期
npm install dayjs

# 可选：使用Element UI（Vue）
npm install element-ui

# 可选：使用Ant Design（React）
npm install antd
```

### 1.3 基础配置

创建 `src/config/api.js`：

```javascript
// API基础配置
export const API_CONFIG = {
  // 基础URL - 根据环境变量配置
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8082',

  // 请求超时时间（毫秒）
  timeout: 30000,

  // Token存储的key
  tokenKey: 'token',

  // Token在请求头中的字段名
  tokenHeader: 'Authorization',

  // Token前缀
  tokenPrefix: 'Bearer '
};
```

创建 `.env.development`：

```bash
# 开发环境
VUE_APP_API_BASE_URL=http://localhost:8082
```

创建 `.env.production`：

```bash
# 生产环境
VUE_APP_API_BASE_URL=https://api.yourschool.com
```

---

## 2. 认证流程

### 2.1 登录流程

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  用户    │─────>│  前端    │─────>│  后端    │
│ 输入账号  │      │ 发送请求  │      │ 验证账号  │
└──────────┘      └──────────┘      └──────────┘
                        │                  │
                        │<─────────────────┤
                        │   返回JWT Token   │
                        │                  │
                        ▼                  │
                  ┌──────────┐            │
                  │ 存储Token │            │
                  │ localStorage│          │
                  └──────────┘            │
                        │                  │
                        ▼                  │
                  ┌──────────┐            │
                  │ 跳转首页  │            │
                  └──────────┘            │
```

**完整登录代码示例**

```javascript
// src/api/auth.js
import axios from '@/utils/request';

/**
 * 用户登录
 * @param {string} identifier - 用户名/手机号/邮箱
 * @param {string} password - 密码
 * @returns {Promise}
 */
export function login(identifier, password) {
  return axios.post('/api/user/login', {
    identifier,
    password
  });
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return axios.post('/api/user/logout');
}

// src/views/Login.vue
<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="rules">
      <el-form-item prop="identifier">
        <el-input
          v-model="loginForm.identifier"
          placeholder="请输入用户名/手机号/邮箱">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码">
        </el-input>
      </el-form-item>
      <el-button
        type="primary"
        :loading="loading"
        @click="handleLogin">
        登录
      </el-button>
    </el-form>
  </div>
</template>

<script>
import { login } from '@/api/auth';

export default {
  data() {
    return {
      loginForm: {
        identifier: '',
        password: ''
      },
      rules: {
        identifier: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) return;

        this.loading = true;
        try {
          const response = await login(
            this.loginForm.identifier,
            this.loginForm.password
          );

          if (response.data.code === 200) {
            // 保存Token
            localStorage.setItem('token', response.data.data);

            // 提示成功
            this.$message.success('登录成功');

            // 跳转首页
            this.$router.push('/dashboard');
          } else {
            this.$message.error(response.data.msg);
          }
        } catch (error) {
          this.$message.error('登录失败，请稍后重试');
        } finally {
          this.loading = false;
        }
      });
    }
  }
};
</script>
```

### 2.2 Token管理

**Token工具类**

```javascript
// src/utils/auth.js

const TOKEN_KEY = 'token';

/**
 * 获取Token
 * @returns {string|null}
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/**
 * 设置Token
 * @param {string} token
 */
export function setToken(token) {
  return localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 移除Token
 */
export function removeToken() {
  return localStorage.removeItem(TOKEN_KEY);
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!getToken();
}
```

**使用示例**

```javascript
import { getToken, setToken, removeToken, isLoggedIn } from '@/utils/auth';

// 登录成功后保存Token
setToken('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...');

// 获取Token用于API请求
const token = getToken();

// 登出时清除Token
removeToken();

// 检查登录状态
if (isLoggedIn()) {
  console.log('用户已登录');
} else {
  console.log('用户未登录');
}
```

### 2.3 Token刷新

> **注意**: 当前系统暂未实现Token自动刷新机制。Token默认有效期为24小时。

**建议的Token刷新策略（待后端实现）**

```javascript
// src/utils/request.js

let isRefreshing = false;  // 是否正在刷新Token
let requests = [];  // 缓存的请求队列

/**
 * 刷新Token
 */
async function refreshToken() {
  try {
    const response = await axios.post('/api/user/refresh-token');
    const newToken = response.data.data;
    setToken(newToken);
    return newToken;
  } catch (error) {
    // 刷新失败，跳转到登录页
    removeToken();
    window.location.href = '/login';
    return Promise.reject(error);
  }
}

// 响应拦截器中处理Token过期
axios.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // Token过期（401错误）
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // 正在刷新Token，将请求加入队列
        return new Promise((resolve) => {
          requests.push((token) => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`;
            resolve(axios(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const newToken = await refreshToken();

        // 重新发送队列中的请求
        requests.forEach(cb => cb(newToken));
        requests = [];

        // 重新发送当前请求
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
```

### 2.4 登出流程

```javascript
// src/api/auth.js
import axios from '@/utils/request';
import { removeToken } from '@/utils/auth';

/**
 * 用户登出
 */
export async function logout() {
  try {
    // 调用后端登出接口，将Token加入黑名单
    await axios.post('/api/user/logout');
  } catch (error) {
    console.error('登出接口调用失败:', error);
  } finally {
    // 无论接口成功与否，都清除本地Token
    removeToken();

    // 清除其他用户数据（如Vuex状态）
    // store.dispatch('user/logout');

    // 跳转到登录页
    window.location.href = '/login';
  }
}

// 使用示例
<template>
  <el-dropdown @command="handleCommand">
    <span class="user-info">
      {{ username }}
      <i class="el-icon-arrow-down"></i>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="profile">个人中心</el-dropdown-item>
      <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
import { logout } from '@/api/auth';

export default {
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          await logout();
        });
      }
    }
  }
};
</script>
```

---

## 3. Axios配置

### 3.1 基础配置

```javascript
// src/utils/request.js
import axios from 'axios';
import { API_CONFIG } from '@/config/api';
import { getToken } from '@/utils/auth';

// 创建Axios实例
const service = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: API_CONFIG.timeout
});

export default service;
```

### 3.2 请求拦截器

```javascript
// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加Token到请求头
    const token = getToken();
    if (token) {
      config.headers[API_CONFIG.tokenHeader] = `${API_CONFIG.tokenPrefix}${token}`;
    }

    // 如果是FormData，设置正确的Content-Type
    if (config.data instanceof FormData) {
      config.headers['Content-Type'] = 'multipart/form-data';
    }

    // 打印请求日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log('Request:', config.method.toUpperCase(), config.url, config.data || config.params);
    }

    return config;
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);
```

### 3.3 响应拦截器

```javascript
import { Message } from 'element-ui';
import { removeToken } from '@/utils/auth';

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 打印响应日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log('Response:', response.config.url, response.data);
    }

    const { code, msg, data } = response.data;

    // 业务成功
    if (code === 200) {
      return response;
    }

    // 业务失败 - 统一错误提示
    Message.error(msg || '操作失败');
    return Promise.reject(new Error(msg || 'Error'));
  },
  error => {
    console.error('Response Error:', error);

    // HTTP错误处理
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          Message.error('未登录或登录已过期，请重新登录');
          removeToken();
          // 跳转到登录页
          setTimeout(() => {
            window.location.href = '/login';
          }, 1500);
          break;

        case 403:
          Message.error('没有权限访问该资源');
          break;

        case 404:
          Message.error('请求的资源不存在');
          break;

        case 500:
          Message.error(data.msg || '服务器内部错误');
          break;

        default:
          Message.error(data.msg || '请求失败');
      }
    } else if (error.request) {
      // 请求已发送但未收到响应
      Message.error('网络错误，请检查您的网络连接');
    } else {
      // 请求配置出错
      Message.error('请求配置错误');
    }

    return Promise.reject(error);
  }
);
```

### 3.4 完整配置示例

```javascript
// src/utils/request.js
import axios from 'axios';
import { Message, Loading } from 'element-ui';
import { getToken, removeToken } from '@/utils/auth';
import { API_CONFIG } from '@/config/api';

// 创建Axios实例
const service = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: API_CONFIG.timeout
});

// Loading实例
let loadingInstance = null;

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加Token
    const token = getToken();
    if (token) {
      config.headers[API_CONFIG.tokenHeader] = `${API_CONFIG.tokenPrefix}${token}`;
    }

    // 显示Loading（如果配置了showLoading）
    if (config.showLoading !== false) {
      loadingInstance = Loading.service({
        lock: true,
        text: '加载中...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
    }

    // 开发环境打印日志
    if (process.env.NODE_ENV === 'development') {
      console.log(`[Request] ${config.method.toUpperCase()} ${config.url}`, {
        params: config.params,
        data: config.data
      });
    }

    return config;
  },
  error => {
    // 关闭Loading
    if (loadingInstance) {
      loadingInstance.close();
    }

    console.error('[Request Error]', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 关闭Loading
    if (loadingInstance) {
      loadingInstance.close();
    }

    const { code, msg, data } = response.data;

    // 开发环境打印日志
    if (process.env.NODE_ENV === 'development') {
      console.log(`[Response] ${response.config.url}`, response.data);
    }

    // 业务成功
    if (code === 200) {
      return response;
    }

    // 业务失败
    Message.error(msg || '操作失败');
    return Promise.reject(new Error(msg || 'Error'));
  },
  error => {
    // 关闭Loading
    if (loadingInstance) {
      loadingInstance.close();
    }

    console.error('[Response Error]', error);

    // HTTP错误处理
    if (error.response) {
      const { status, data } = error.response;

      const errorMessages = {
        401: '未登录或登录已过期，请重新登录',
        403: '没有权限访问该资源',
        404: '请求的资源不存在',
        500: data?.msg || '服务器内部错误'
      };

      const message = errorMessages[status] || data?.msg || '请求失败';
      Message.error(message);

      // 401错误跳转到登录页
      if (status === 401) {
        removeToken();
        setTimeout(() => {
          window.location.href = '/login';
        }, 1500);
      }
    } else if (error.request) {
      Message.error('网络错误，请检查您的网络连接');
    } else {
      Message.error('请求配置错误');
    }

    return Promise.reject(error);
  }
);

export default service;
```

---

## 4. Fetch配置

### 4.1 基础封装

```javascript
// src/utils/fetch.js
import { getToken, removeToken } from '@/utils/auth';
import { API_CONFIG } from '@/config/api';

/**
 * 统一的Fetch请求封装
 * @param {string} url - 请求URL
 * @param {object} options - 请求配置
 * @returns {Promise}
 */
async function request(url, options = {}) {
  // 默认配置
  const defaultOptions = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  };

  // 合并配置
  const config = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  };

  // 添加Token
  const token = getToken();
  if (token) {
    config.headers[API_CONFIG.tokenHeader] = `${API_CONFIG.tokenPrefix}${token}`;
  }

  // 处理FormData
  if (config.body instanceof FormData) {
    delete config.headers['Content-Type'];
  }

  // 拼接完整URL
  const fullUrl = url.startsWith('http')
    ? url
    : `${API_CONFIG.baseURL}${url}`;

  try {
    const response = await fetch(fullUrl, config);

    // HTTP错误处理
    if (!response.ok) {
      await handleHttpError(response);
      throw new Error(`HTTP Error: ${response.status}`);
    }

    const data = await response.json();

    // 业务错误处理
    if (data.code !== 200) {
      handleBusinessError(data);
      throw new Error(data.msg || 'Business Error');
    }

    return data;
  } catch (error) {
    console.error('Request failed:', error);
    throw error;
  }
}

/**
 * 处理HTTP错误
 */
async function handleHttpError(response) {
  const { status } = response;

  const errorMessages = {
    401: '未登录或登录已过期',
    403: '没有权限访问',
    404: '请求的资源不存在',
    500: '服务器内部错误'
  };

  const message = errorMessages[status] || '请求失败';
  alert(message);

  if (status === 401) {
    removeToken();
    window.location.href = '/login';
  }
}

/**
 * 处理业务错误
 */
function handleBusinessError(data) {
  alert(data.msg || '操作失败');
}

/**
 * GET请求
 */
export function get(url, params = {}) {
  const queryString = new URLSearchParams(params).toString();
  const fullUrl = queryString ? `${url}?${queryString}` : url;

  return request(fullUrl, {
    method: 'GET'
  });
}

/**
 * POST请求
 */
export function post(url, data = {}) {
  return request(url, {
    method: 'POST',
    body: JSON.stringify(data)
  });
}

/**
 * PUT请求
 */
export function put(url, data = {}) {
  return request(url, {
    method: 'PUT',
    body: JSON.stringify(data)
  });
}

/**
 * DELETE请求
 */
export function del(url) {
  return request(url, {
    method: 'DELETE'
  });
}

/**
 * 文件上传
 */
export function upload(url, formData) {
  return request(url, {
    method: 'POST',
    body: formData
  });
}
```

### 4.2 完整示例

```javascript
// src/api/user.js
import { get, post, put, del } from '@/utils/fetch';

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return get('/api/users', params);
}

/**
 * 添加用户
 */
export function addUser(data) {
  return post('/api/users', data);
}

/**
 * 修改用户
 */
export function updateUser(id, data) {
  return put(`/api/users/${id}`, data);
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return del(`/api/users/${id}`);
}

// 使用示例
import { getUserList, addUser } from '@/api/user';

// 查询用户列表
async function loadUsers() {
  try {
    const response = await getUserList({ page: 1, size: 10 });
    console.log('用户列表:', response.data);
  } catch (error) {
    console.error('查询失败:', error);
  }
}

// 添加用户
async function createUser() {
  try {
    const response = await addUser({
      username: 'teacher001',
      password: 'Pass@123',
      realName: '张老师',
      userType: 2
    });
    console.log('用户创建成功:', response.data);
  } catch (error) {
    console.error('创建失败:', error);
  }
}
```

---

## 5. 统一错误处理

### 5.1 HTTP错误处理

```javascript
// src/utils/errorHandler.js

/**
 * HTTP错误处理器
 */
export function handleHttpError(error) {
  if (!error.response) {
    // 网络错误
    return {
      type: 'network',
      message: '网络错误，请检查您的网络连接'
    };
  }

  const { status, data } = error.response;

  const errorMap = {
    400: {
      type: 'client',
      message: data?.msg || '请求参数错误'
    },
    401: {
      type: 'auth',
      message: '未登录或登录已过期',
      action: () => {
        removeToken();
        window.location.href = '/login';
      }
    },
    403: {
      type: 'permission',
      message: '没有权限访问该资源'
    },
    404: {
      type: 'notFound',
      message: '请求的资源不存在'
    },
    500: {
      type: 'server',
      message: data?.msg || '服务器内部错误'
    }
  };

  const errorInfo = errorMap[status] || {
    type: 'unknown',
    message: data?.msg || '请求失败'
  };

  // 执行错误动作（如跳转登录页）
  if (errorInfo.action) {
    errorInfo.action();
  }

  return errorInfo;
}

// 使用示例
import { handleHttpError } from '@/utils/errorHandler';

try {
  const response = await axios.get('/api/users');
} catch (error) {
  const errorInfo = handleHttpError(error);
  console.log('错误类型:', errorInfo.type);
  console.log('错误信息:', errorInfo.message);

  // 显示错误提示
  Message.error(errorInfo.message);
}
```

### 5.2 业务错误处理

```javascript
/**
 * 业务错误处理器
 */
export function handleBusinessError(response) {
  const { code, msg } = response.data;

  if (code === 200) {
    return { success: true, data: response.data.data };
  }

  // 业务错误码映射
  const errorMap = {
    400: '请求参数错误',
    401: '未授权',
    403: '无权限',
    404: '资源不存在',
    500: '服务器错误'
  };

  const message = msg || errorMap[code] || '操作失败';

  return {
    success: false,
    code,
    message
  };
}

// 统一响应处理函数
export async function handleResponse(promise) {
  try {
    const response = await promise;
    const result = handleBusinessError(response);

    if (!result.success) {
      Message.error(result.message);
      return null;
    }

    return result.data;
  } catch (error) {
    const errorInfo = handleHttpError(error);
    Message.error(errorInfo.message);
    return null;
  }
}

// 使用示例
import { handleResponse } from '@/utils/errorHandler';
import { getUserList } from '@/api/user';

async function loadUsers() {
  const data = await handleResponse(getUserList({ page: 1, size: 10 }));

  if (data) {
    console.log('用户列表:', data);
    this.users = data.records;
    this.total = data.total;
  }
}
```

### 5.3 网络错误处理

```javascript
/**
 * 网络错误检测
 */
export function isNetworkError(error) {
  return (
    !error.response &&
    error.request &&
    error.message === 'Network Error'
  );
}

/**
 * 超时错误检测
 */
export function isTimeoutError(error) {
  return error.code === 'ECONNABORTED' && error.message.includes('timeout');
}

/**
 * 网络错误处理
 */
export function handleNetworkError(error) {
  if (isNetworkError(error)) {
    Message.error('网络连接失败，请检查您的网络');
    return true;
  }

  if (isTimeoutError(error)) {
    Message.error('请求超时，请稍后重试');
    return true;
  }

  return false;
}

// 在响应拦截器中使用
axios.interceptors.response.use(
  response => response,
  error => {
    // 先处理网络错误
    if (handleNetworkError(error)) {
      return Promise.reject(error);
    }

    // 再处理HTTP错误
    handleHttpError(error);
    return Promise.reject(error);
  }
);
```

---

## 6. 路由守卫

### 6.1 Vue Router守卫

```javascript
// src/router/index.js
import Vue from 'vue';
import VueRouter from 'vue-router';
import { isLoggedIn } from '@/utils/auth';

Vue.use(VueRouter);

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'UserList',
    component: () => import('@/views/User/List.vue'),
    meta: {
      requiresAuth: true,
      roles: ['super_admin', 'admin']  // 需要的角色
    }
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next();
    return;
  }

  // 检查是否登录
  if (!isLoggedIn()) {
    // 未登录，跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath }  // 保存原路径，登录后跳回
    });
    return;
  }

  // 检查角色权限（如果需要）
  if (to.meta.roles) {
    const userRole = localStorage.getItem('userRole');
    if (!to.meta.roles.includes(userRole)) {
      // 无权限
      Message.error('您没有权限访问该页面');
      next('/403');
      return;
    }
  }

  // 已登录且有权限，放行
  next();
});

// 全局后置钩子
router.afterEach((to, from) => {
  // 设置页面标题
  document.title = to.meta.title || '小学教务管理系统';

  // 页面滚动到顶部
  window.scrollTo(0, 0);
});

export default router;
```

**登录后跳转回原页面**

```javascript
// src/views/Login.vue
export default {
  methods: {
    async handleLogin() {
      try {
        const response = await login(this.form.identifier, this.form.password);

        if (response.data.code === 200) {
          setToken(response.data.data);

          // 获取重定向地址
          const redirect = this.$route.query.redirect || '/dashboard';

          // 跳转
          this.$router.push(redirect);
        }
      } catch (error) {
        this.$message.error('登录失败');
      }
    }
  }
};
```

### 6.2 React Router守卫

```jsx
// src/components/PrivateRoute.jsx
import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { isLoggedIn } from '@/utils/auth';

/**
 * 私有路由组件 - 需要登录才能访问
 */
export function PrivateRoute({ component: Component, ...rest }) {
  return (
    <Route
      {...rest}
      render={props =>
        isLoggedIn() ? (
          <Component {...props} />
        ) : (
          <Redirect
            to={{
              pathname: '/login',
              state: { from: props.location }
            }}
          />
        )
      }
    />
  );
}

// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { PrivateRoute } from '@/components/PrivateRoute';
import Login from '@/pages/Login';
import Dashboard from '@/pages/Dashboard';
import UserList from '@/pages/User/List';

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/login" component={Login} />
        <PrivateRoute exact path="/dashboard" component={Dashboard} />
        <PrivateRoute exact path="/users" component={UserList} />
        <Redirect from="/" to="/dashboard" />
      </Switch>
    </Router>
  );
}

export default App;
```

---

## 7. 状态管理

### 7.1 Vuex用户状态

```javascript
// src/store/modules/user.js
import { login as apiLogin, logout as apiLogout } from '@/api/auth';
import { getToken, setToken, removeToken } from '@/utils/auth';

const state = {
  token: getToken(),
  userInfo: null,
  roles: []
};

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token;
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo;
  },
  SET_ROLES(state, roles) {
    state.roles = roles;
  }
};

const actions = {
  /**
   * 用户登录
   */
  async login({ commit }, { identifier, password }) {
    try {
      const response = await apiLogin(identifier, password);

      if (response.data.code === 200) {
        const token = response.data.data;
        commit('SET_TOKEN', token);
        setToken(token);
        return { success: true };
      } else {
        return { success: false, message: response.data.msg };
      }
    } catch (error) {
      return { success: false, message: '登录失败' };
    }
  },

  /**
   * 获取用户信息
   */
  async getUserInfo({ commit }) {
    try {
      // 调用获取用户信息接口（需要后端实现）
      // const response = await getUserInfo();
      // const userInfo = response.data.data;

      // 模拟数据
      const userInfo = {
        id: 1,
        username: 'admin',
        realName: '管理员',
        roles: ['super_admin']
      };

      commit('SET_USER_INFO', userInfo);
      commit('SET_ROLES', userInfo.roles);

      return userInfo;
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return null;
    }
  },

  /**
   * 用户登出
   */
  async logout({ commit }) {
    try {
      await apiLogout();
    } catch (error) {
      console.error('登出接口调用失败:', error);
    } finally {
      commit('SET_TOKEN', '');
      commit('SET_USER_INFO', null);
      commit('SET_ROLES', []);
      removeToken();
    }
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions
};

// src/store/index.js
import Vue from 'vue';
import Vuex from 'vuex';
import user from './modules/user';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    user
  }
});

// 使用示例
// src/views/Login.vue
export default {
  methods: {
    async handleLogin() {
      const result = await this.$store.dispatch('user/login', {
        identifier: this.form.identifier,
        password: this.form.password
      });

      if (result.success) {
        // 获取用户信息
        await this.$store.dispatch('user/getUserInfo');

        // 跳转首页
        this.$router.push('/dashboard');
      } else {
        this.$message.error(result.message);
      }
    }
  }
};
```

### 7.2 React Context用户状态

```jsx
// src/contexts/AuthContext.jsx
import React, { createContext, useState, useContext, useEffect } from 'react';
import { getToken, setToken as saveToken, removeToken } from '@/utils/auth';
import { login as apiLogin, logout as apiLogout } from '@/api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getToken());
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 初始化时获取用户信息
  useEffect(() => {
    if (token) {
      // 调用获取用户信息接口
      // getUserInfo().then(userInfo => setUser(userInfo));
      setLoading(false);
    } else {
      setLoading(false);
    }
  }, [token]);

  /**
   * 登录
   */
  const login = async (identifier, password) => {
    try {
      const response = await apiLogin(identifier, password);

      if (response.data.code === 200) {
        const newToken = response.data.data;
        setToken(newToken);
        saveToken(newToken);
        return { success: true };
      } else {
        return { success: false, message: response.data.msg };
      }
    } catch (error) {
      return { success: false, message: '登录失败' };
    }
  };

  /**
   * 登出
   */
  const logout = async () => {
    try {
      await apiLogout();
    } catch (error) {
      console.error('登出失败:', error);
    } finally {
      setToken(null);
      setUser(null);
      removeToken();
    }
  };

  /**
   * 检查是否已登录
   */
  const isAuthenticated = () => {
    return !!token;
  };

  const value = {
    token,
    user,
    loading,
    login,
    logout,
    isAuthenticated
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * useAuth Hook
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}

// src/App.jsx
import { AuthProvider } from '@/contexts/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        {/* 路由配置 */}
      </Router>
    </AuthProvider>
  );
}

// 使用示例
// src/pages/Login.jsx
import { useAuth } from '@/contexts/AuthContext';

function Login() {
  const { login } = useAuth();
  const [form, setForm] = useState({ identifier: '', password: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();

    const result = await login(form.identifier, form.password);

    if (result.success) {
      // 跳转首页
      history.push('/dashboard');
    } else {
      alert(result.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* 表单内容 */}
    </form>
  );
}
```

---

## 8. 常见问题

### Q1: Token过期后如何处理？

**A**: 当前系统Token有效期为24小时。Token过期后会返回401错误，前端应：
1. 清除本地Token
2. 跳转到登录页
3. 提示用户重新登录

```javascript
// 在Axios响应拦截器中处理
if (error.response?.status === 401) {
  removeToken();
  Message.error('登录已过期，请重新登录');
  setTimeout(() => {
    window.location.href = '/login';
  }, 1500);
}
```

### Q2: 如何上传文件？

**A**: 使用FormData格式上传：

```javascript
const formData = new FormData();
formData.append('avatarFile', file);
formData.append('realName', '张三');

await axios.put(`/api/users/${userId}`, formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
});
```

### Q3: 跨域问题如何解决？

**A**: 开发环境配置代理：

```javascript
// vue.config.js
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
};
```

### Q4: 如何处理分页？

**A**: 参考[数据字典-分页参数规范](./DATA_DICTIONARY.md#5-分页参数规范)

### Q5: 日期格式如何统一？

**A**: 使用Day.js库处理日期：

```javascript
import dayjs from 'dayjs';

// 格式化
dayjs().format('YYYY-MM-DD HH:mm:ss');

// 解析
dayjs('2024-11-20 10:30:00');
```

---

## 9. 最佳实践

### 9.1 API接口管理

**推荐目录结构**
```
src/api/
├── auth.js          # 认证相关
├── user.js          # 用户管理
├── teacher.js       # 教师管理
├── student.js       # 学生管理
├── class.js         # 班级管理
└── index.js         # 统一导出
```

**统一导出**
```javascript
// src/api/index.js
export * from './auth';
export * from './user';
export * from './teacher';
export * from './student';
export * from './class';

// 使用
import { login, getUserList, getTeacherList } from '@/api';
```

### 9.2 常量管理

```javascript
// src/constants/index.js
export const USER_TYPE = {
  ADMIN: 1,
  TEACHER: 2,
  STUDENT: 3,
  PARENT: 4
};

export const STATUS = {
  DISABLED: 0,
  ENABLED: 1
};

// 使用
import { USER_TYPE, STATUS } from '@/constants';
```

### 9.3 环境变量配置

```bash
# .env.development
VUE_APP_API_BASE_URL=http://localhost:8082
VUE_APP_TITLE=小学教务管理系统（开发）

# .env.production
VUE_APP_API_BASE_URL=https://api.yourschool.com
VUE_APP_TITLE=小学教务管理系统
```

### 9.4 错误边界

```jsx
// React错误边界
class ErrorBoundary extends React.Component {
  state = { hasError: false };

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <h1>出错了，请刷新页面重试</h1>;
    }

    return this.props.children;
  }
}

// 使用
<ErrorBoundary>
  <App />
</ErrorBoundary>
```

### 9.5 性能优化

**1. 使用防抖节流**
```javascript
import { debounce } from 'lodash';

// 搜索框防抖
methods: {
  handleSearch: debounce(function(keyword) {
    this.loadData({ keyword });
  }, 500)
}
```

**2. 路由懒加载**
```javascript
const UserList = () => import('@/views/User/List.vue');
```

**3. 图片懒加载**
```vue
<img v-lazy="imageUrl" alt="">
```

---

## 📝 总结

本文档提供了完整的前端对接规范，包括：
- ✅ 认证流程和Token管理
- ✅ Axios/Fetch配置和拦截器
- ✅ 统一错误处理
- ✅ 路由守卫
- ✅ 状态管理
- ✅ 最佳实践

建议前端开发人员：
1. 严格遵循本文档的规范
2. 使用提供的代码模板
3. 统一错误处理方式
4. 做好代码注释和文档

---

> 💡 **提示**: 如有疑问或需要补充，请联系后端团队或项目负责人。
