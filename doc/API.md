# 小学教务管理系统 - API接口文档（前端版）

> **文档版本**: v1.0
> **最后更新**: 2025-11-20
> **基础URL**: `http://localhost:8082`

---

## 📖 快速导航

- [1. 认证模块](#1-认证模块)
  - [1.1 用户登录](#11-用户登录)
  - [1.2 用户登出](#12-用户登出)
- [2. 用户管理](#2-用户管理)
  - [2.1 查询用户列表](#21-查询用户列表)
  - [2.2 添加用户](#22-添加用户)
  - [2.3 修改用户](#23-修改用户)
  - [2.4 删除用户](#24-删除用户)
  - [2.5 查询用户详情](#25-查询用户详情)
  - [2.6 分配角色](#26-分配角色)
  - [2.7 查询用户角色](#27-查询用户角色)
- [3. 菜单管理](#3-菜单管理)
  - [3.1 获取菜单树](#31-获取菜单树)
  - [3.2 获取菜单列表](#32-获取菜单列表)
  - [3.3 获取菜单详情](#33-获取菜单详情)
  - [3.4 创建菜单](#34-创建菜单)
  - [3.5 更新菜单](#35-更新菜单)
  - [3.6 删除菜单](#36-删除菜单)
- [4. 教师管理](#4-教师管理)
  - [4.1 查询教师列表](#41-查询教师列表)
  - [4.2 添加教师](#42-添加教师)
  - [4.3 修改教师](#43-修改教师)
  - [4.4 删除教师](#44-删除教师)
  - [4.5 查询教师详情](#45-查询教师详情)
- [5. 学生管理](#5-学生管理)
  - [5.1 查询学生列表](#51-查询学生列表)
  - [5.2 添加学生](#52-添加学生)
  - [5.3 修改学生](#53-修改学生)
  - [5.4 删除学生](#54-删除学生)
- [6. 班级管理](#6-班级管理)
  - [6.1 查询班级列表](#61-查询班级列表)
  - [6.2 添加班级](#62-添加班级)
  - [6.3 修改班级](#63-修改班级)
  - [6.4 删除班级](#64-删除班级)
- [7. 年级管理](#7-年级管理)
  - [7.1 查询年级列表](#71-查询年级列表)
- [8. 请假管理](#8-请假管理)
  - [8.1 提交请假申请](#81-提交请假申请)
  - [8.2 查询请假详情](#82-查询请假详情)
  - [8.3 我的请假记录](#83-我的请假记录)
  - [8.4 撤回请假](#84-撤回请假)
  - [8.5 待审批列表](#85-待审批列表)
- [9. 调课管理](#9-调课管理)
  - [9.1 提交调课申请](#91-提交调课申请)
  - [9.2 查询调课详情](#92-查询调课详情)
  - [9.3 我的调课记录](#93-我的调课记录)
- [10. 换课管理](#10-换课管理)
  - [10.1 提交换课申请](#101-提交换课申请)
  - [10.2 确认换课](#102-确认换课)
  - [10.3 我的换课记录](#103-我的换课记录)
- [11. 调班管理](#11-调班管理)
  - [11.1 提交调班申请](#111-提交调班申请)
  - [11.2 查询调班详情](#112-查询调班详情)
  - [11.3 我的调班记录](#113-我的调班记录)

---

## 1. 认证模块

### 1.1 用户登录

**接口说明**
用户登录验证，成功后返回JWT Token

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/user/login`
- **是否需要认证**: 否

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| identifier | String | 是 | 用户名/手机号/邮箱 | admin |
| password | String | 是 | 密码 | admin123 |

**请求示例**
```json
{
  "identifier": "admin",
  "password": "admin123"
}
```

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  // JWT Token令牌，需保存用于后续请求
}
```

**前端调用示例 (Axios)**
```javascript
import axios from 'axios';

// 用户登录
async function login(identifier, password) {
  try {
    const response = await axios.post('/api/user/login', {
      identifier,
      password
    });

    if (response.data.code === 200) {
      // 保存token到localStorage
      localStorage.setItem('token', response.data.data);
      return { success: true, token: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('登录失败:', error);
    return { success: false, message: '网络错误，请稍后重试' };
  }
}

// 使用示例
login('admin', 'admin123').then(result => {
  if (result.success) {
    console.log('登录成功！Token:', result.token);
    // 跳转到首页
    window.location.href = '/dashboard';
  } else {
    alert(result.message);
  }
});
```

**前端调用示例 (Fetch)**
```javascript
// 用户登录
async function login(identifier, password) {
  try {
    const response = await fetch('/api/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ identifier, password })
    });

    const data = await response.json();

    if (data.code === 200) {
      // 保存token
      localStorage.setItem('token', data.data);
      return { success: true, token: data.data };
    } else {
      return { success: false, message: data.msg };
    }
  } catch (error) {
    console.error('登录失败:', error);
    return { success: false, message: '网络错误' };
  }
}
```

**错误码说明**

| 错误码 | 说明 |
|--------|------|
| 400 | 用户名或密码不能为空 |
| 401 | 用户名或密码错误 |
| 403 | 账号已被禁用 |
| 500 | 服务器内部错误 |

---

### 1.2 用户登出

**接口说明**
用户登出，将Token加入黑名单

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/user/logout`
- **是否需要认证**: 是

**请求头**
```
Authorization: Bearer <token>
```

**请求参数**
无

**响应示例**
```json
{
  "code": 200,           // 状态码：200表示成功
  "msg": "登出成功"       // 消息：操作结果描述
}
```

**前端调用示例 (Axios)**
```javascript
// 用户登出
async function logout() {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.post('/api/user/logout', {}, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      // 清除本地token
      localStorage.removeItem('token');
      // 跳转到登录页
      window.location.href = '/login';
      return { success: true };
    }
  } catch (error) {
    console.error('登出失败:', error);
    // 即使登出失败，也清除本地token
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
```

---

## 2. 用户管理

### 2.1 查询用户列表

**接口说明**
查询用户列表，支持条件筛选和分页

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/users`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| username | String | 否 | 用户名（模糊查询） | admin |
| realName | String | 否 | 真实姓名（模糊查询） | 张三 |
| userType | Integer | 否 | 用户类型：1-管理员 2-教师 3-学生 4-家长 | 2 |
| status | Integer | 否 | 状态：0-禁用 1-启用 | 1 |
| phone | String | 否 | 手机号（模糊查询） | 138 |
| email | String | 否 | 邮箱（模糊查询） | @example.com |
| page | Integer | 否 | 页码，默认1 | 1 |
| size | Integer | 否 | 每页条数，默认10 | 10 |

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": {
    "total": 100,           // 总记录数
    "records": [            // 用户列表数据
      {
        "id": 1,                        // 用户ID
        "username": "admin",            // 用户名（登录账号）
        "realName": "系统管理员",        // 真实姓名
        "userType": 1,                  // 用户类型：1-管理员 2-教师 3-学生 4-家长
        "avatar": "/uploads/avatars/2024/11/19/1_abc123.jpg",  // 头像URL地址
        "phone": "13800138000",         // 手机号
        "email": "admin@example.com",   // 邮箱地址
        "gender": 1,                    // 性别：1-男 2-女
        "status": 1,                    // 状态：0-禁用 1-启用
        "lastLoginTime": "2024-11-18 10:30:00",  // 最后登录时间
        "createdAt": "2024-01-01 00:00:00",      // 创建时间
        "updatedAt": "2024-11-18 10:30:00",      // 更新时间
        "roles": ["super_admin"]        // 角色列表
      }
    ],
    "page": 1,              // 当前页码
    "size": 10              // 每页条数
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 查询用户列表
async function getUserList(params = {}) {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/api/users', {
      params: {
        username: params.username || '',
        realName: params.realName || '',
        userType: params.userType || null,
        status: params.status || null,
        page: params.page || 1,
        size: params.size || 10
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return {
        success: true,
        data: response.data.data.records,
        total: response.data.data.total,
        page: response.data.data.page,
        size: response.data.data.size
      };
    }
  } catch (error) {
    console.error('查询用户列表失败:', error);
    return { success: false, message: '查询失败' };
  }
}

// 使用示例
getUserList({
  userType: 2,  // 查询教师
  status: 1,    // 启用状态
  page: 1,
  size: 10
}).then(result => {
  if (result.success) {
    console.log('用户列表:', result.data);
    console.log('总数:', result.total);
  }
});
```

---

### 2.2 添加用户

**接口说明**
添加新用户，支持同时上传头像

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/users`
- **是否需要认证**: 是
- **请求格式**: `multipart/form-data`

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| username | String | 是 | 登录账号，3-50字符，唯一 | teacher001 |
| password | String | 是 | 密码，6-20字符 | Pass@123 |
| realName | String | 是 | 真实姓名，1-50字符 | 张老师 |
| userType | Integer | 是 | 用户类型：1-管理员 2-教师 3-学生 4-家长 | 2 |
| avatarFile | File | 否 | 头像文件（JPG/PNG/GIF，最大5MB） | - |
| phone | String | 否 | 联系电话，11位手机号 | 13800138001 |
| email | String | 否 | 邮箱地址 | teacher@example.com |
| gender | Integer | 否 | 性别：1-男 2-女 | 1 |
| status | Integer | 否 | 状态：0-禁用 1-启用，默认1 | 1 |

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": {
    "id": 10,                       // 用户ID（新创建的用户ID）
    "username": "teacher001",       // 用户名（登录账号）
    "realName": "张老师",            // 真实姓名
    "userType": 2,                  // 用户类型：1-管理员 2-教师 3-学生 4-家长
    "avatar": "/uploads/avatars/2024/11/19/10_abc123.jpg",  // 头像URL地址
    "phone": "13800138001",         // 手机号
    "email": "teacher001@example.com",  // 邮箱地址
    "gender": 1,                    // 性别：1-男 2-女
    "status": 1,                    // 状态：0-禁用 1-启用
    "createdAt": "2024-11-19 10:00:00",  // 创建时间
    "updatedAt": "2024-11-19 10:00:00",  // 更新时间
    "roles": ["teacher"]            // 角色列表（已自动分配）
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 添加用户（带头像上传）
async function addUser(userData, avatarFile) {
  try {
    const token = localStorage.getItem('token');

    // 创建FormData对象
    const formData = new FormData();
    formData.append('username', userData.username);
    formData.append('password', userData.password);
    formData.append('realName', userData.realName);
    formData.append('userType', userData.userType);

    // 可选字段
    if (avatarFile) {
      formData.append('avatarFile', avatarFile);
    }
    if (userData.phone) {
      formData.append('phone', userData.phone);
    }
    if (userData.email) {
      formData.append('email', userData.email);
    }
    if (userData.gender) {
      formData.append('gender', userData.gender);
    }
    if (userData.status !== undefined) {
      formData.append('status', userData.status);
    }

    const response = await axios.post('/api/users', formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('添加用户失败:', error);
    return { success: false, message: '添加失败' };
  }
}

// 使用示例（从表单获取数据）
// HTML: <input type="file" id="avatarInput" accept="image/*">
const avatarInput = document.getElementById('avatarInput');
const avatarFile = avatarInput.files[0];

addUser({
  username: 'teacher001',
  password: 'Pass@123',
  realName: '张老师',
  userType: 2,
  phone: '13800138001',
  email: 'teacher@example.com',
  gender: 1,
  status: 1
}, avatarFile).then(result => {
  if (result.success) {
    console.log('用户创建成功:', result.data);
  } else {
    alert(result.message);
  }
});
```

**注意事项**
- 密码将使用BCrypt算法加密后存储
- 用户名、手机号、邮箱必须唯一
- 根据用户类型自动分配默认角色
- 头像文件支持JPG、PNG、GIF格式，最大5MB

---

### 2.3 修改用户

**接口说明**
修改用户信息（不包括密码），支持更新头像

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/users/{id}`
- **是否需要认证**: 是
- **请求格式**: `multipart/form-data`

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| realName | String | 否 | 真实姓名 | 李老师 |
| avatarFile | File | 否 | 头像文件（JPG/PNG/GIF，最大5MB） | - |
| phone | String | 否 | 联系电话 | 13800138002 |
| email | String | 否 | 邮箱地址 | teacher@example.com |
| gender | Integer | 否 | 性别：1-男 2-女 | 2 |
| status | Integer | 否 | 状态：0-禁用 1-启用 | 1 |

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": {
    "id": 10,                       // 用户ID
    "username": "teacher001",       // 用户名（登录账号）
    "realName": "李老师",            // 真实姓名（已更新）
    "userType": 2,                  // 用户类型：1-管理员 2-教师 3-学生 4-家长
    "avatar": "/uploads/avatars/2024/11/19/10_def456.jpg",  // 头像URL（已更新）
    "phone": "13800138002",         // 手机号（已更新）
    "email": "teacher001@example.com",  // 邮箱地址
    "gender": 2,                    // 性别（已更新）
    "status": 1,                    // 状态：0-禁用 1-启用
    "updatedAt": "2024-11-19 11:00:00",  // 更新时间
    "roles": ["teacher"]            // 角色列表
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 修改用户信息
async function updateUser(userId, userData, avatarFile) {
  try {
    const token = localStorage.getItem('token');

    // 创建FormData对象
    const formData = new FormData();

    // 只添加需要更新的字段
    if (userData.realName) {
      formData.append('realName', userData.realName);
    }
    if (avatarFile) {
      formData.append('avatarFile', avatarFile);
    }
    if (userData.phone) {
      formData.append('phone', userData.phone);
    }
    if (userData.email) {
      formData.append('email', userData.email);
    }
    if (userData.gender) {
      formData.append('gender', userData.gender);
    }
    if (userData.status !== undefined) {
      formData.append('status', userData.status);
    }

    const response = await axios.put(`/api/users/${userId}`, formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('修改用户失败:', error);
    return { success: false, message: '修改失败' };
  }
}

// 使用示例
updateUser(10, {
  realName: '李老师',
  phone: '13800138002',
  gender: 2
}, null).then(result => {
  if (result.success) {
    console.log('用户信息已更新:', result.data);
  } else {
    alert(result.message);
  }
});
```

**注意事项**
- 不允许修改username（登录账号）
- 不允许修改userType（用户类型）
- 密码修改使用专门的修改密码接口
- 上传新头像会自动替换旧头像

---

### 2.4 删除用户

**接口说明**
删除用户（软删除）

**基本信息**
- **请求方式**: `DELETE`
- **请求路径**: `/api/users/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**响应示例**
```json
{
  "code": 200,           // 状态码：200表示成功
  "msg": "删除成功"       // 消息：操作结果描述
}
```

**前端调用示例 (Axios)**
```javascript
// 删除用户
async function deleteUser(userId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.delete(`/api/users/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, message: '删除成功' };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('删除用户失败:', error);
    return { success: false, message: '删除失败' };
  }
}

// 使用示例（带确认对话框）
function confirmDeleteUser(userId, userName) {
  if (confirm(`确定要删除用户 "${userName}" 吗？`)) {
    deleteUser(userId).then(result => {
      if (result.success) {
        alert('删除成功');
        // 刷新用户列表
        getUserList();
      } else {
        alert(result.message);
      }
    });
  }
}
```

**注意事项**
- 使用软删除，不物理删除数据
- 不允许删除超级管理员（ID=1）
- 删除后该用户无法登录

---

### 2.5 查询用户详情

**接口说明**
查询用户详细信息，包括角色信息

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/users/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": {
    "id": 10,                       // 用户ID
    "username": "teacher001",       // 用户名（登录账号）
    "realName": "张老师",            // 真实姓名
    "userType": 2,                  // 用户类型：1-管理员 2-教师 3-学生 4-家长
    "avatar": "/uploads/avatars/2024/11/19/10_abc123.jpg",  // 头像URL地址
    "phone": "13800138001",         // 手机号
    "email": "teacher001@example.com",  // 邮箱地址
    "gender": 1,                    // 性别：1-男 2-女
    "status": 1,                    // 状态：0-禁用 1-启用
    "lastLoginTime": "2024-11-19 09:30:00",  // 最后登录时间
    "lastLoginIp": "192.168.1.100",          // 最后登录IP地址
    "createdAt": "2024-11-01 10:00:00",      // 创建时间
    "updatedAt": "2024-11-19 09:30:00",      // 更新时间
    "roles": ["teacher"]            // 角色列表
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 查询用户详情
async function getUserDetail(userId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get(`/api/users/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('查询用户详情失败:', error);
    return { success: false, message: '查询失败' };
  }
}

// 使用示例
getUserDetail(10).then(result => {
  if (result.success) {
    console.log('用户详情:', result.data);
    // 渲染到页面
    displayUserInfo(result.data);
  }
});
```

---

### 2.6 分配角色

**接口说明**
为用户分配一个或多个角色

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/users/{id}/roles`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| roleIds | Array | 是 | 角色ID数组 | [1, 2, 3] |

**请求示例**
```json
{
  "roleIds": [1, 2, 3]
}
```

**响应示例**
```json
{
  "code": 200,           // 状态码：200表示成功
  "msg": "角色分配成功"   // 消息：操作结果描述
}
```

**前端调用示例 (Axios)**
```javascript
// 为用户分配角色
async function assignRoles(userId, roleIds) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post(`/api/users/${userId}/roles`, {
      roleIds: roleIds
    }, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, message: '角色分配成功' };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('分配角色失败:', error);
    return { success: false, message: '分配失败' };
  }
}

// 使用示例
assignRoles(10, [1, 2]).then(result => {
  if (result.success) {
    alert('角色分配成功');
  } else {
    alert(result.message);
  }
});
```

---

### 2.7 查询用户角色

**接口说明**
查询用户的角色列表

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/users/{id}/roles`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": ["teacher", "class_monitor"]  // 角色代码列表
}
```

**前端调用示例 (Axios)**
```javascript
// 查询用户角色
async function getUserRoles(userId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get(`/api/users/${userId}/roles`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, roles: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('查询用户角色失败:', error);
    return { success: false, message: '查询失败' };
  }
}
```

---

## 3. 菜单管理

### 3.1 获取菜单树

**接口说明**
获取系统菜单树形结构，用于渲染导航菜单

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/menu/tree`
- **是否需要认证**: 是

**请求参数**
无

**响应示例**
```json
{
  "code": 200,              // 状态码：200表示成功
  "msg": "操作成功",         // 消息：操作结果描述
  "data": [                 // 菜单树形数据
    {
      "id": 1,                    // 菜单ID
      "menuName": "系统管理",      // 菜单名称
      "menuPath": "/system",      // 菜单路径
      "menuIcon": "system",       // 菜单图标
      "menuType": 1,              // 菜单类型：1-目录 2-菜单 3-按钮
      "parentId": 0,              // 父菜单ID（0表示顶级菜单）
      "sortOrder": 1,             // 排序号
      "children": [               // 子菜单列表
        {
          "id": 2,                     // 子菜单ID
          "menuName": "用户管理",       // 子菜单名称
          "menuPath": "/system/user",  // 子菜单路径
          "menuIcon": "user",          // 子菜单图标
          "menuType": 2,               // 菜单类型：1-目录 2-菜单 3-按钮
          "parentId": 1,               // 父菜单ID
          "sortOrder": 1,              // 排序号
          "children": []               // 子菜单列表（空表示无子菜单）
        }
      ]
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 获取菜单树
async function getMenuTree() {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/menu/tree', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('获取菜单树失败:', error);
    return { success: false, message: '获取失败' };
  }
}

// 使用示例 - 渲染菜单
getMenuTree().then(result => {
  if (result.success) {
    renderMenu(result.data);
  }
});

// 递归渲染菜单树
function renderMenu(menuData) {
  const menuHtml = menuData.map(menu => {
    let html = `
      <li class="menu-item">
        <a href="${menu.menuPath}">
          <i class="icon ${menu.menuIcon}"></i>
          <span>${menu.menuName}</span>
        </a>
    `;

    if (menu.children && menu.children.length > 0) {
      html += '<ul class="submenu">';
      html += menu.children.map(child => renderMenuItem(child)).join('');
      html += '</ul>';
    }

    html += '</li>';
    return html;
  }).join('');

  document.getElementById('menu-container').innerHTML = menuHtml;
}
```

---

### 3.2 获取菜单列表

**接口说明**
获取所有菜单列表（扁平结构），用于下拉选择

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/menu/list`
- **是否需要认证**: 是

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "menuPath": "/system",
      "menuIcon": "system",
      "menuType": 1,
      "parentId": 0,
      "sortOrder": 1
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 获取菜单列表（用于下拉选择）
async function getMenuList() {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/menu/list', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    console.error('获取菜单列表失败:', error);
    return { success: false };
  }
}
```

---

### 3.3 获取菜单详情

**接口说明**
根据ID查询菜单详情

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/menu/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 菜单ID |

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "menuName": "用户管理",
    "menuPath": "/system/user",
    "menuIcon": "user",
    "menuType": 2,
    "parentId": 1,
    "sortOrder": 1,
    "permission": "system:user:list"
  }
}
```

---

### 3.4 创建菜单

**接口说明**
创建新菜单项（需要超级管理员权限）

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/menu`
- **是否需要认证**: 是
- **权限要求**: 超级管理员

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| menuName | String | 是 | 菜单名称 | 教师管理 |
| menuPath | String | 否 | 菜单路径 | /teacher |
| menuIcon | String | 否 | 菜单图标 | teacher |
| menuType | Integer | 是 | 菜单类型：1-目录 2-菜单 3-按钮 | 2 |
| parentId | Long | 否 | 父菜单ID，0表示顶级菜单 | 1 |
| sortOrder | Integer | 否 | 排序号，默认0 | 2 |
| permission | String | 否 | 权限标识 | system:teacher:list |

**请求示例**
```json
{
  "menuName": "教师管理",
  "menuPath": "/teacher",
  "menuIcon": "teacher",
  "menuType": 2,
  "parentId": 1,
  "sortOrder": 2,
  "permission": "system:teacher:list"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 10,
    "menuName": "教师管理",
    "menuPath": "/teacher",
    "menuIcon": "teacher",
    "menuType": 2,
    "parentId": 1,
    "sortOrder": 2,
    "permission": "system:teacher:list"
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 创建菜单
async function createMenu(menuData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post('/api/menu', menuData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('创建菜单失败:', error);
    return { success: false, message: '创建失败' };
  }
}

// 使用示例
createMenu({
  menuName: '教师管理',
  menuPath: '/teacher',
  menuIcon: 'teacher',
  menuType: 2,
  parentId: 1,
  sortOrder: 2
}).then(result => {
  if (result.success) {
    alert('菜单创建成功');
  }
});
```

---

### 3.5 更新菜单

**接口说明**
更新菜单信息（需要超级管理员权限）

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/menu/{id}`
- **是否需要认证**: 是
- **权限要求**: 超级管理员

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 菜单ID |

**请求参数**

参数同创建菜单接口

**前端调用示例 (Axios)**
```javascript
// 更新菜单
async function updateMenu(menuId, menuData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.put(`/api/menu/${menuId}`, menuData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '更新失败' };
  }
}
```

---

### 3.6 删除菜单

**接口说明**
删除菜单项（软删除，需要超级管理员权限）

**基本信息**
- **请求方式**: `DELETE`
- **请求路径**: `/api/menu/{id}`
- **是否需要认证**: 是
- **权限要求**: 超级管理员

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 菜单ID |

**响应示例**
```json
{
  "code": 200,
  "msg": "删除成功"
}
```

**前端调用示例 (Axios)**
```javascript
// 删除菜单
async function deleteMenu(menuId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.delete(`/api/menu/${menuId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '删除失败' };
  }
}
```

**注意事项**
- 如果菜单下有子菜单，将无法删除
- 使用软删除方式

---

## 4. 教师管理

### 4.1 查询教师列表

**接口说明**
查询教师列表，支持条件筛选

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/teacher/list`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| teacherName | String | 否 | 教师姓名（模糊查询） | 张 |
| teacherNo | String | 否 | 教师工号 | T001 |
| title | String | 否 | 职称 | 语文教师 |

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "teacherNo": "T001",
      "teacherName": "张老师",
      "gender": 1,
      "birthDate": "1985-03-15",
      "idCard": "110101198503150011",
      "phone": "13900139000",
      "email": "teacher001@school.com",
      "title": "数学教师",
      "hireDate": "2024-09-01"
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 查询教师列表
async function getTeacherList(params = {}) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/teacher/list', {
      params: {
        teacherName: params.teacherName || '',
        teacherNo: params.teacherNo || '',
        title: params.title || ''
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    console.error('查询教师列表失败:', error);
    return { success: false };
  }
}

// 使用示例
getTeacherList({ teacherName: '张' }).then(result => {
  if (result.success) {
    console.log('教师列表:', result.data);
  }
});
```

---

### 4.2 添加教师

**接口说明**
添加新教师信息

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/teacher`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| teacherNo | String | 是 | 教师工号 | T100 |
| teacherName | String | 是 | 教师姓名 | 李老师 |
| gender | Integer | 是 | 性别：1-男 2-女 | 1 |
| birthDate | String | 否 | 出生日期 | 1985-03-15 |
| idCard | String | 否 | 身份证号 | 110101198503150011 |
| phone | String | 是 | 联系电话 | 13900139000 |
| email | String | 否 | 邮箱 | teacher@school.com |
| title | String | 否 | 职称 | 数学教师 |
| hireDate | String | 否 | 入职日期 | 2024-09-01 |

**请求示例**
```json
{
  "teacherNo": "T100",
  "teacherName": "李老师",
  "gender": 1,
  "birthDate": "1985-03-15",
  "idCard": "110101198503150011",
  "phone": "13900139000",
  "email": "teacher100@school.com",
  "title": "数学教师",
  "hireDate": "2024-09-01"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 10,
    "teacherNo": "T100",
    "teacherName": "李老师",
    "gender": 1,
    "phone": "13900139000",
    "title": "数学教师"
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 添加教师
async function addTeacher(teacherData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post('/api/teacher', teacherData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    console.error('添加教师失败:', error);
    return { success: false, message: '添加失败' };
  }
}

// 使用示例
addTeacher({
  teacherNo: 'T100',
  teacherName: '李老师',
  gender: 1,
  phone: '13900139000',
  title: '数学教师'
}).then(result => {
  if (result.success) {
    alert('教师添加成功');
  }
});
```

---

### 4.3 修改教师

**接口说明**
修改教师信息

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/teacher/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 教师ID |

**请求参数**

参数同添加教师接口

**前端调用示例 (Axios)**
```javascript
// 修改教师
async function updateTeacher(teacherId, teacherData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.put(`/api/teacher/${teacherId}`, teacherData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '修改失败' };
  }
}
```

---

### 4.4 删除教师

**接口说明**
删除教师

**基本信息**
- **请求方式**: `DELETE`
- **请求路径**: `/api/teacher/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 教师ID |

**响应示例**
```json
{
  "code": 200,
  "msg": "删除成功"
}
```

**前端调用示例 (Axios)**
```javascript
// 删除教师
async function deleteTeacher(teacherId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.delete(`/api/teacher/${teacherId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '删除失败' };
  }
}
```

---

### 4.5 查询教师详情

**接口说明**
根据教师ID查询详细信息

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/teacher/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 教师ID |

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "teacherNo": "T001",
    "teacherName": "张老师",
    "gender": 1,
    "birthDate": "1985-03-15",
    "idCard": "110101198503150011",
    "phone": "13900139000",
    "email": "teacher001@school.com",
    "title": "数学教师",
    "hireDate": "2024-09-01"
  }
}
```

**前端调用示例 (Axios)**
```javascript
// 查询教师详情
async function getTeacherDetail(teacherId) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get(`/api/teacher/${teacherId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    return { success: false };
  }
}
```

---

## 5. 学生管理

### 5.1 查询学生列表

**接口说明**
查询学生列表，支持条件筛选

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/student/list`
- **是否需要认证**: 是

**请求参数**

支持多种筛选条件（具体参数由后端处理）

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "studentNo": "S2024001",
      "studentName": "小明",
      "gender": 1,
      "birthDate": "2016-05-20",
      "gradeId": 1,
      "classId": 1,
      "parentPhone": "13800138000"
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 查询学生列表
async function getStudentList(params = {}) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/student/list', {
      params: params,
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    console.error('查询学生列表失败:', error);
    return { success: false };
  }
}
```

---

### 5.2 添加学生

**接口说明**
添加新学生

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/student`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| studentNo | String | 是 | 学号 | S2024001 |
| studentName | String | 是 | 学生姓名 | 小明 |
| gender | Integer | 是 | 性别：1-男 2-女 | 1 |
| birthDate | String | 否 | 出生日期 | 2016-05-20 |
| idCard | String | 否 | 身份证号 | 110101201605200011 |
| gradeId | Long | 否 | 年级ID | 1 |
| classId | Long | 否 | 班级ID | 1 |
| parentPhone | String | 否 | 家长电话 | 13800138000 |

**请求示例**
```json
{
  "studentNo": "S2024001",
  "studentName": "小明",
  "gender": 1,
  "birthDate": "2016-05-20",
  "idCard": "110101201605200011",
  "gradeId": 1,
  "classId": 1,
  "parentPhone": "13800138000"
}
```

**前端调用示例 (Axios)**
```javascript
// 添加学生
async function addStudent(studentData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post('/api/student', studentData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '添加失败' };
  }
}
```

---

### 5.3 修改学生

**接口说明**
修改学生信息

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/student/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 学生ID |

**请求参数**

参数同添加学生接口

---

### 5.4 删除学生

**接口说明**
删除学生

**基本信息**
- **请求方式**: `DELETE`
- **请求路径**: `/api/student/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 学生ID |

**响应示例**
```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

## 6. 班级管理

### 6.1 查询班级列表

**接口说明**
查询班级列表

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/class/list`
- **是否需要认证**: 是

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "className": "一年级1班",
      "gradeId": 1,
      "headTeacherId": 1,
      "classroom": "101教室",
      "maxStudents": 40
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 查询班级列表
async function getClassList() {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/class/list', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    return { success: false };
  }
}
```

---

### 6.2 添加班级

**接口说明**
添加新班级

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/class`
- **是否需要认证**: 是

---

### 6.3 修改班级

**接口说明**
修改班级信息

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/class/{id}`
- **是否需要认证**: 是

---

### 6.4 删除班级

**接口说明**
删除班级

**基本信息**
- **请求方式**: `DELETE`
- **请求路径**: `/api/class/{id}`
- **是否需要认证**: 是

---

## 7. 年级管理

### 7.1 查询年级列表

**接口说明**
查询所有年级列表

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/grade/list`
- **是否需要认证**: 是

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "gradeName": "一年级",
      "gradeLevel": 1
    },
    {
      "id": 2,
      "gradeName": "二年级",
      "gradeLevel": 2
    }
  ]
}
```

**前端调用示例 (Axios)**
```javascript
// 查询年级列表
async function getGradeList() {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('/api/grade/list', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
  } catch (error) {
    return { success: false };
  }
}
```

---

## 8. 请假管理

### 8.1 提交请假申请

**接口说明**
学生/家长提交请假申请

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/leave`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| studentId | Long | 是 | 学生ID | 1 |
| leaveType | Integer | 是 | 请假类型：1-病假 2-事假 3-其他 | 1 |
| startDate | String | 是 | 开始日期 | 2024-11-20 |
| endDate | String | 是 | 结束日期 | 2024-11-22 |
| leaveDays | Integer | 是 | 请假天数 | 3 |
| reason | String | 是 | 请假原因 | 感冒发烧 |
| proofFiles | Array | 否 | 证明文件URL数组 | ["http://..."] |

**请求示例**
```json
{
  "studentId": 1,
  "leaveType": 1,
  "startDate": "2024-11-20",
  "endDate": "2024-11-22",
  "leaveDays": 3,
  "reason": "感冒发烧，需要在家休息",
  "proofFiles": ["http://example.com/proof1.jpg"]
}
```

**前端调用示例 (Axios)**
```javascript
// 提交请假申请
async function submitLeave(leaveData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post('/api/leave', leaveData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '提交失败' };
  }
}
```

---

### 8.2 查询请假详情

**接口说明**
查询请假申请详情

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/leave/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 请假申请ID |

---

### 8.3 我的请假记录

**接口说明**
查询当前学生的请假记录（支持分页）

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/leave/my`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| studentId | Long | 是 | 学生ID | 1 |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页条数 | 10 |

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "leaveType": 1,
        "startDate": "2024-11-20",
        "endDate": "2024-11-22",
        "leaveDays": 3,
        "reason": "感冒发烧",
        "approvalStatus": 1
      }
    ],
    "total": 20,
    "size": 10,
    "current": 1
  }
}
```

---

### 8.4 撤回请假

**接口说明**
撤回请假申请

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/leave/{id}/cancel`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 请假申请ID |

---

### 8.5 待审批列表

**接口说明**
查询待审批的请假列表（教师/管理员使用）

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/leave/pending`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| classId | Long | 否 | 班级ID | 1 |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页条数 | 10 |

---

## 9. 调课管理

### 9.1 提交调课申请

**接口说明**
教师提交调课申请

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/course-change`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| scheduleId | Long | 是 | 原课程ID | 123 |
| originalDate | String | 是 | 原上课日期 | 2024-11-20 |
| newDate | String | 是 | 调整后日期 | 2024-11-21 |
| reason | String | 是 | 调课原因 | 临时有事 |

**请求示例**
```json
{
  "scheduleId": 123,
  "originalDate": "2024-11-20",
  "newDate": "2024-11-21",
  "reason": "临时有事需要调整课程时间"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

**前端调用示例 (Axios)**
```javascript
// 提交调课申请
async function submitCourseChange(changeData) {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.post('/api/course-change', changeData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      return { success: true, id: response.data.data };
    } else {
      return { success: false, message: response.data.msg };
    }
  } catch (error) {
    return { success: false, message: '提交失败' };
  }
}
```

---

### 9.2 查询调课详情

**接口说明**
查询调课申请详情

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/course-change/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 调课申请ID |

---

### 9.3 我的调课记录

**接口说明**
查询当前教师的调课申请记录（支持分页）

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/course-change/my`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| teacherId | Long | 是 | 教师ID | 10 |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页条数 | 10 |

---

## 10. 换课管理

### 10.1 提交换课申请

**接口说明**
教师提交换课申请（与另一位教师交换课程）

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/course-swap`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| myScheduleId | Long | 是 | 我的课程ID | 123 |
| targetScheduleId | Long | 是 | 目标课程ID | 456 |
| targetTeacherId | Long | 是 | 目标教师ID | 20 |
| reason | String | 是 | 换课原因 | 时间冲突 |

**请求示例**
```json
{
  "myScheduleId": 123,
  "targetScheduleId": 456,
  "targetTeacherId": 20,
  "reason": "时间冲突，需要与李老师换课"
}
```

---

### 10.2 确认换课

**接口说明**
目标教师确认或拒绝换课申请

**基本信息**
- **请求方式**: `PUT`
- **请求路径**: `/api/course-swap/{id}/confirm`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 换课申请ID |

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| confirm | Boolean | 是 | true-同意 false-拒绝 | true |

---

### 10.3 我的换课记录

**接口说明**
查询当前教师的换课申请记录（支持分页）

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/course-swap/my`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| teacherId | Long | 是 | 教师ID | 10 |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页条数 | 10 |

---

## 11. 调班管理

### 11.1 提交调班申请

**接口说明**
家长/学生提交调班申请

**基本信息**
- **请求方式**: `POST`
- **请求路径**: `/api/class-transfer`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| studentId | Long | 是 | 学生ID | 100 |
| currentClassId | Long | 是 | 当前班级ID | 1 |
| targetClassId | Long | 是 | 目标班级ID | 2 |
| reason | String | 是 | 调班原因 | 家庭住址变更 |

**请求示例**
```json
{
  "studentId": 100,
  "currentClassId": 1,
  "targetClassId": 2,
  "reason": "家庭住址变更，申请转到离家更近的班级"
}
```

---

### 11.2 查询调班详情

**接口说明**
查询调班申请详情

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/class-transfer/{id}`
- **是否需要认证**: 是

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 调班申请ID |

---

### 11.3 我的调班记录

**接口说明**
查询当前学生的调班申请记录（支持分页）

**基本信息**
- **请求方式**: `GET`
- **请求路径**: `/api/class-transfer/my`
- **是否需要认证**: 是

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| studentId | Long | 是 | 学生ID | 100 |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页条数 | 10 |

---

## 📝 文档说明

### 关于本文档

本文档专为前端开发人员编写，包含：
- ✅ 完整的请求和响应示例（带中文注释）
- ✅ 可直接使用的Axios/Fetch代码示例
- ✅ 清晰的参数说明表格
- ✅ 错误处理建议

### 相关文档

- [数据字典](./DATA_DICTIONARY.md) - 枚举值、状态码说明
- [前端对接规范](./FRONTEND_GUIDE.md) - 认证流程、拦截器配置

### 更新日志

**v1.0** (2025-11-20)
- 初始版本发布
- 包含所有已实现接口的完整文档
- 添加前端调用示例

---

> 💡 **提示**: 如有疑问或发现文档错误，请联系后端团队
