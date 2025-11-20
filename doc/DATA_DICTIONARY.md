# 数据字典

> **文档版本**: v1.0
> **最后更新**: 2025-11-20

---

## 📑 目录

- [1. 通用响应格式](#1-通用响应格式)
- [2. HTTP状态码](#2-http状态码)
- [3. 业务状态码](#3-业务状态码)
- [4. 枚举值定义](#4-枚举值定义)
  - [4.1 用户类型](#41-用户类型-usertype)
  - [4.2 性别](#42-性别-gender)
  - [4.3 状态](#43-状态-status)
  - [4.4 菜单类型](#44-菜单类型-menutype)
  - [4.5 请假类型](#45-请假类型-leavetype)
  - [4.6 审批状态](#46-审批状态-approvalstatus)
  - [4.7 换课确认状态](#47-换课确认状态-targetconfirm)
  - [4.8 业务类型](#48-业务类型-businesstype)
- [5. 分页参数规范](#5-分页参数规范)
- [6. 时间格式约定](#6-时间格式约定)
- [7. 文件上传规范](#7-文件上传规范)

---

## 1. 通用响应格式

所有API接口统一使用以下JSON格式返回数据：

```json
{
  "code": 200,        // 业务状态码
  "msg": "操作成功",   // 提示消息
  "data": {}          // 业务数据（可能是对象、数组或基本类型）
}
```

### 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码，200表示成功，其他值表示失败 |
| msg | String | 操作结果的描述信息，用于提示用户 |
| data | Any | 业务数据，类型取决于具体接口（对象、数组、字符串等） |

### 示例

**成功响应**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  }
}
```

**失败响应**
```json
{
  "code": 400,
  "msg": "用户名不能为空",
  "data": null
}
```

**列表响应**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    { "id": 1, "name": "张三" },
    { "id": 2, "name": "李四" }
  ]
}
```

---

## 2. HTTP状态码

系统使用标准HTTP状态码：

| 状态码 | 说明 | 场景 |
|--------|------|------|
| 200 | OK | 请求成功 |
| 400 | Bad Request | 请求参数错误或格式不正确 |
| 401 | Unauthorized | 未登录或Token失效 |
| 403 | Forbidden | 无权限访问该资源 |
| 404 | Not Found | 请求的资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |

### 前端处理建议

```javascript
// Axios响应拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 跳转到登录页
          window.location.href = '/login';
          break;
        case 403:
          alert('无权限访问');
          break;
        case 404:
          alert('请求的资源不存在');
          break;
        case 500:
          alert('服务器错误，请稍后重试');
          break;
        default:
          alert('请求失败');
      }
    }
    return Promise.reject(error);
  }
);
```

---

## 3. 业务状态码

业务状态码在响应体的`code`字段中返回：

| 状态码 | 说明 | 前端处理建议 |
|--------|------|-------------|
| 200 | 操作成功 | 显示成功提示或直接处理数据 |
| 400 | 请求参数错误 | 显示错误信息，提示用户修正 |
| 401 | 未授权（未登录） | 清除本地Token，跳转到登录页 |
| 403 | 无权限 | 显示无权限提示 |
| 404 | 资源不存在 | 显示资源不存在提示 |
| 500 | 服务器内部错误 | 显示系统错误提示 |

### 前端处理示例

```javascript
// 统一处理业务状态码
function handleResponse(response) {
  const { code, msg, data } = response.data;

  if (code === 200) {
    // 成功
    return { success: true, data: data, message: msg };
  } else if (code === 401) {
    // 未登录
    localStorage.removeItem('token');
    window.location.href = '/login';
    return { success: false, message: msg };
  } else {
    // 其他错误
    return { success: false, message: msg };
  }
}

// 使用示例
async function getUser() {
  try {
    const response = await axios.get('/api/users/1');
    const result = handleResponse(response);

    if (result.success) {
      console.log('用户数据:', result.data);
    } else {
      alert(result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}
```

---

## 4. 枚举值定义

### 4.1 用户类型 (userType)

| 值 | 名称 | 说明 | 默认角色 |
|----|------|------|----------|
| 1 | 管理员 | 系统管理员 | super_admin |
| 2 | 教师 | 任课教师/班主任 | teacher |
| 3 | 学生 | 在校学生 | student |
| 4 | 家长 | 学生家长 | parent |

**前端使用示例**
```javascript
// 用户类型常量定义
const USER_TYPE = {
  ADMIN: 1,
  TEACHER: 2,
  STUDENT: 3,
  PARENT: 4
};

// 用户类型名称映射
const USER_TYPE_NAME = {
  1: '管理员',
  2: '教师',
  3: '学生',
  4: '家长'
};

// 使用示例
function getUserTypeName(userType) {
  return USER_TYPE_NAME[userType] || '未知';
}

// 渲染用户类型下拉框
const userTypeOptions = [
  { value: 1, label: '管理员' },
  { value: 2, label: '教师' },
  { value: 3, label: '学生' },
  { value: 4, label: '家长' }
];
```

---

### 4.2 性别 (gender)

| 值 | 名称 | 说明 |
|----|------|------|
| 1 | 男 | 男性 |
| 2 | 女 | 女性 |

**前端使用示例**
```javascript
// 性别常量定义
const GENDER = {
  MALE: 1,
  FEMALE: 2
};

// 性别名称映射
const GENDER_NAME = {
  1: '男',
  2: '女'
};

// 性别图标映射
const GENDER_ICON = {
  1: '♂',
  2: '♀'
};

// 使用示例
function getGenderName(gender) {
  return GENDER_NAME[gender] || '未知';
}

// 渲染性别单选框
const genderOptions = [
  { value: 1, label: '男' },
  { value: 2, label: '女' }
];
```

---

### 4.3 状态 (status)

| 值 | 名称 | 说明 | CSS类名建议 |
|----|------|------|------------|
| 0 | 禁用 | 账号已禁用，无法登录 | status-disabled |
| 1 | 启用 | 账号正常，可以登录 | status-enabled |

**前端使用示例**
```javascript
// 状态常量定义
const STATUS = {
  DISABLED: 0,
  ENABLED: 1
};

// 状态名称映射
const STATUS_NAME = {
  0: '禁用',
  1: '启用'
};

// 状态标签映射（用于显示）
const STATUS_TAG = {
  0: { text: '禁用', class: 'tag-danger' },
  1: { text: '启用', class: 'tag-success' }
};

// 使用示例 - 渲染状态标签
function renderStatusTag(status) {
  const tag = STATUS_TAG[status];
  return `<span class="tag ${tag.class}">${tag.text}</span>`;
}

// 状态切换开关
const statusOptions = [
  { value: 0, label: '禁用' },
  { value: 1, label: '启用' }
];
```

**CSS样式建议**
```css
.tag {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.tag-success {
  background-color: #67c23a;
  color: white;
}

.tag-danger {
  background-color: #f56c6c;
  color: white;
}
```

---

### 4.4 菜单类型 (menuType)

| 值 | 名称 | 说明 | 图标建议 |
|----|------|------|---------|
| 1 | 目录 | 菜单目录，包含子菜单 | folder |
| 2 | 菜单 | 普通菜单项，可点击跳转 | file |
| 3 | 按钮 | 页面内的操作按钮 | button |

**前端使用示例**
```javascript
// 菜单类型常量定义
const MENU_TYPE = {
  DIRECTORY: 1,
  MENU: 2,
  BUTTON: 3
};

// 菜单类型名称映射
const MENU_TYPE_NAME = {
  1: '目录',
  2: '菜单',
  3: '按钮'
};

// 菜单类型图标映射
const MENU_TYPE_ICON = {
  1: 'el-icon-folder',
  2: 'el-icon-menu',
  3: 'el-icon-s-operation'
};

// 使用示例 - 递归渲染菜单树
function renderMenuTree(menus) {
  return menus.map(menu => {
    if (menu.menuType === MENU_TYPE.DIRECTORY && menu.children) {
      // 目录类型，递归渲染子菜单
      return {
        ...menu,
        children: renderMenuTree(menu.children)
      };
    } else if (menu.menuType === MENU_TYPE.MENU) {
      // 菜单类型，渲染为可点击项
      return menu;
    }
    // 按钮类型不在导航中显示
    return null;
  }).filter(Boolean);
}
```

---

### 4.5 请假类型 (leaveType)

| 值 | 名称 | 说明 | 需要证明 |
|----|------|------|---------|
| 1 | 病假 | 因病请假 | 建议提供病假条 |
| 2 | 事假 | 因事请假 | 可选 |
| 3 | 其他 | 其他原因 | 可选 |

**前端使用示例**
```javascript
// 请假类型常量定义
const LEAVE_TYPE = {
  SICK: 1,
  PERSONAL: 2,
  OTHER: 3
};

// 请假类型名称映射
const LEAVE_TYPE_NAME = {
  1: '病假',
  2: '事假',
  3: '其他'
};

// 请假类型颜色映射
const LEAVE_TYPE_COLOR = {
  1: '#e6a23c',  // 橙色
  2: '#409eff',  // 蓝色
  3: '#909399'   // 灰色
};

// 使用示例
function getLeaveTypeTag(leaveType) {
  return {
    text: LEAVE_TYPE_NAME[leaveType],
    color: LEAVE_TYPE_COLOR[leaveType]
  };
}

// 请假类型下拉选项
const leaveTypeOptions = [
  { value: 1, label: '病假', tip: '建议提供病假条' },
  { value: 2, label: '事假' },
  { value: 3, label: '其他' }
];
```

---

### 4.6 审批状态 (approvalStatus)

| 值 | 名称 | 说明 | 颜色建议 | 可操作 |
|----|------|------|---------|--------|
| 1 | 待审批 | 等待审批人处理 | #909399（灰色） | 可撤回 |
| 2 | 审批中 | 多级审批进行中 | #409eff（蓝色） | - |
| 3 | 已通过 | 审批通过 | #67c23a（绿色） | - |
| 4 | 已拒绝 | 审批被拒绝 | #f56c6c（红色） | - |
| 5 | 已撤回 | 申请人主动撤回 | #e6a23c（橙色） | - |

**前端使用示例**
```javascript
// 审批状态常量定义
const APPROVAL_STATUS = {
  PENDING: 1,
  APPROVING: 2,
  APPROVED: 3,
  REJECTED: 4,
  CANCELLED: 5
};

// 审批状态配置（名称、颜色、图标）
const APPROVAL_STATUS_CONFIG = {
  1: {
    name: '待审批',
    color: '#909399',
    icon: 'el-icon-time',
    canCancel: true  // 可以撤回
  },
  2: {
    name: '审批中',
    color: '#409eff',
    icon: 'el-icon-loading',
    canCancel: false
  },
  3: {
    name: '已通过',
    color: '#67c23a',
    icon: 'el-icon-success',
    canCancel: false
  },
  4: {
    name: '已拒绝',
    color: '#f56c6c',
    icon: 'el-icon-error',
    canCancel: false
  },
  5: {
    name: '已撤回',
    color: '#e6a23c',
    icon: 'el-icon-refresh-left',
    canCancel: false
  }
};

// 使用示例 - 渲染审批状态标签
function renderApprovalStatus(status) {
  const config = APPROVAL_STATUS_CONFIG[status];
  return `
    <span class="status-tag" style="color: ${config.color}">
      <i class="${config.icon}"></i>
      ${config.name}
    </span>
  `;
}

// 判断是否可以撤回
function canCancelApproval(status) {
  return APPROVAL_STATUS_CONFIG[status]?.canCancel || false;
}

// 过滤待审批的申请
function getPendingApprovals(approvals) {
  return approvals.filter(item =>
    item.approvalStatus === APPROVAL_STATUS.PENDING
  );
}
```

**Vue组件示例**
```vue
<template>
  <el-tag
    :type="getStatusType(status)"
    :icon="getStatusIcon(status)">
    {{ getStatusName(status) }}
  </el-tag>
</template>

<script>
export default {
  props: {
    status: {
      type: Number,
      required: true
    }
  },
  methods: {
    getStatusType(status) {
      const typeMap = {
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'danger',
        5: 'warning'
      };
      return typeMap[status];
    },
    getStatusName(status) {
      const nameMap = {
        1: '待审批',
        2: '审批中',
        3: '已通过',
        4: '已拒绝',
        5: '已撤回'
      };
      return nameMap[status];
    },
    getStatusIcon(status) {
      const iconMap = {
        1: 'el-icon-time',
        2: 'el-icon-loading',
        3: 'el-icon-success',
        4: 'el-icon-error',
        5: 'el-icon-refresh-left'
      };
      return iconMap[status];
    }
  }
}
</script>
```

---

### 4.7 换课确认状态 (targetConfirm)

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | 待确认 | 等待目标教师确认 |
| 1 | 已同意 | 目标教师同意换课 |
| 2 | 已拒绝 | 目标教师拒绝换课 |

**前端使用示例**
```javascript
// 换课确认状态常量
const CONFIRM_STATUS = {
  PENDING: 0,
  AGREED: 1,
  REJECTED: 2
};

// 换课确认状态配置
const CONFIRM_STATUS_CONFIG = {
  0: { name: '待确认', color: '#909399', type: 'info' },
  1: { name: '已同意', color: '#67c23a', type: 'success' },
  2: { name: '已拒绝', color: '#f56c6c', type: 'danger' }
};

// 使用示例
function renderConfirmStatus(status) {
  const config = CONFIRM_STATUS_CONFIG[status];
  return `<span class="tag tag-${config.type}">${config.name}</span>`;
}
```

---

### 4.8 业务类型 (businessType)

| 值 | 名称 | 说明 | 对应表 |
|----|------|------|--------|
| 1 | 请假申请 | 学生请假 | edu_leave |
| 2 | 调课申请 | 教师调课 | edu_course_change |
| 3 | 换课申请 | 教师换课 | edu_course_swap |
| 4 | 调班申请 | 学生调班 | edu_class_transfer |

**前端使用示例**
```javascript
// 业务类型常量
const BUSINESS_TYPE = {
  LEAVE: 1,
  COURSE_CHANGE: 2,
  COURSE_SWAP: 3,
  CLASS_TRANSFER: 4
};

// 业务类型名称映射
const BUSINESS_TYPE_NAME = {
  1: '请假申请',
  2: '调课申请',
  3: '换课申请',
  4: '调班申请'
};

// 业务类型路由映射
const BUSINESS_TYPE_ROUTE = {
  1: '/leave',
  2: '/course-change',
  3: '/course-swap',
  4: '/class-transfer'
};

// 使用示例 - 根据业务类型跳转到详情页
function goToDetail(businessType, id) {
  const route = BUSINESS_TYPE_ROUTE[businessType];
  if (route) {
    window.location.href = `${route}/${id}`;
  }
}
```

---

## 5. 分页参数规范

### 5.1 请求参数

所有分页接口统一使用以下参数：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page 或 pageNum | Integer | 否 | 1 | 当前页码，从1开始 |
| size 或 pageSize | Integer | 否 | 10 | 每页条数，建议范围：10-100 |

### 5.2 响应格式

分页接口统一返回以下格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,        // 总记录数
    "records": [],       // 当前页数据列表
    "page": 1,           // 当前页码
    "size": 10,          // 每页条数
    "pages": 10          // 总页数（可选）
  }
}
```

### 5.3 前端使用示例

**Vue + Element UI 分页组件**
```vue
<template>
  <div>
    <!-- 数据表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="name" label="姓名"></el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      :current-page="pagination.page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.size"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [],
      loading: false,
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const response = await axios.get('/api/users', {
          params: {
            page: this.pagination.page,
            size: this.pagination.size
          }
        });

        if (response.data.code === 200) {
          this.tableData = response.data.data.records;
          this.pagination.total = response.data.data.total;
        }
      } catch (error) {
        this.$message.error('数据加载失败');
      } finally {
        this.loading = false;
      }
    },
    handleSizeChange(newSize) {
      this.pagination.size = newSize;
      this.pagination.page = 1; // 改变每页条数时回到第一页
      this.loadData();
    },
    handlePageChange(newPage) {
      this.pagination.page = newPage;
      this.loadData();
    }
  }
};
</script>
```

**React 分页组件示例**
```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function UserList() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 1,
    size: 10,
    total: 0
  });

  useEffect(() => {
    loadData();
  }, [pagination.page, pagination.size]);

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/users', {
        params: {
          page: pagination.page,
          size: pagination.size
        }
      });

      if (response.data.code === 200) {
        setData(response.data.data.records);
        setPagination(prev => ({
          ...prev,
          total: response.data.data.total
        }));
      }
    } catch (error) {
      console.error('数据加载失败', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  return (
    <div>
      {/* 数据列表 */}
      {loading ? <div>加载中...</div> : (
        <ul>
          {data.map(item => (
            <li key={item.id}>{item.name}</li>
          ))}
        </ul>
      )}

      {/* 分页控件 */}
      <div className="pagination">
        <button
          disabled={pagination.page === 1}
          onClick={() => handlePageChange(pagination.page - 1)}>
          上一页
        </button>
        <span>第 {pagination.page} 页 / 共 {Math.ceil(pagination.total / pagination.size)} 页</span>
        <button
          disabled={pagination.page * pagination.size >= pagination.total}
          onClick={() => handlePageChange(pagination.page + 1)}>
          下一页
        </button>
      </div>
    </div>
  );
}

export default UserList;
```

---

## 6. 时间格式约定

### 6.1 标准格式

系统统一使用以下时间格式：

| 场景 | 格式 | 示例 | 说明 |
|------|------|------|------|
| 日期时间 | `yyyy-MM-dd HH:mm:ss` | 2024-11-20 10:30:00 | 完整的日期时间 |
| 日期 | `yyyy-MM-dd` | 2024-11-20 | 仅日期 |
| 时间 | `HH:mm:ss` | 10:30:00 | 仅时间 |
| 年月 | `yyyy-MM` | 2024-11 | 年月 |

### 6.2 前端处理

**JavaScript 日期格式化工具**
```javascript
// 日期格式化工具类
const DateUtils = {
  /**
   * 格式化日期时间
   * @param {Date|string} date - 日期对象或字符串
   * @param {string} format - 格式：'datetime'|'date'|'time'
   * @returns {string} 格式化后的字符串
   */
  format(date, format = 'datetime') {
    const d = new Date(date);

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');

    switch (format) {
      case 'datetime':
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      case 'date':
        return `${year}-${month}-${day}`;
      case 'time':
        return `${hours}:${minutes}:${seconds}`;
      case 'month':
        return `${year}-${month}`;
      default:
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
  },

  /**
   * 解析日期字符串
   * @param {string} dateString - 日期字符串
   * @returns {Date} 日期对象
   */
  parse(dateString) {
    return new Date(dateString);
  },

  /**
   * 获取当前日期时间
   * @param {string} format - 格式
   * @returns {string} 格式化后的当前时间
   */
  now(format = 'datetime') {
    return this.format(new Date(), format);
  }
};

// 使用示例
console.log(DateUtils.format(new Date(), 'datetime')); // 2024-11-20 10:30:00
console.log(DateUtils.format(new Date(), 'date'));     // 2024-11-20
console.log(DateUtils.now('date'));                     // 当前日期
```

**使用Day.js库（推荐）**
```javascript
import dayjs from 'dayjs';

// 格式化日期
dayjs().format('YYYY-MM-DD HH:mm:ss');  // 2024-11-20 10:30:00
dayjs().format('YYYY-MM-DD');           // 2024-11-20

// 解析日期
dayjs('2024-11-20 10:30:00');

// 日期计算
dayjs().add(7, 'day');     // 7天后
dayjs().subtract(1, 'month'); // 1个月前

// 日期比较
dayjs('2024-11-20').isAfter('2024-11-19');  // true
dayjs('2024-11-20').isBefore('2024-11-21'); // true
```

**Vue过滤器示例**
```vue
<template>
  <div>
    <p>创建时间: {{ user.createdAt | formatDateTime }}</p>
    <p>入职日期: {{ teacher.hireDate | formatDate }}</p>
  </div>
</template>

<script>
import dayjs from 'dayjs';

export default {
  filters: {
    formatDateTime(value) {
      return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
    },
    formatDate(value) {
      return value ? dayjs(value).format('YYYY-MM-DD') : '-';
    }
  }
};
</script>
```

### 6.3 日期选择器配置

**Element UI DatePicker**
```vue
<template>
  <!-- 日期选择 -->
  <el-date-picker
    v-model="formData.birthDate"
    type="date"
    placeholder="选择日期"
    value-format="yyyy-MM-dd">
  </el-date-picker>

  <!-- 日期时间选择 -->
  <el-date-picker
    v-model="formData.createdAt"
    type="datetime"
    placeholder="选择日期时间"
    value-format="yyyy-MM-dd HH:mm:ss">
  </el-date-picker>

  <!-- 日期范围选择 -->
  <el-date-picker
    v-model="dateRange"
    type="daterange"
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
    value-format="yyyy-MM-dd">
  </el-date-picker>
</template>
```

---

## 7. 文件上传规范

### 7.1 上传配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 最大文件大小 | 5MB | 单个文件最大5MB |
| 支持的图片格式 | JPG, PNG, GIF | 头像上传支持的格式 |
| 上传方式 | multipart/form-data | 表单数据格式 |
| 存储路径 | /uploads/avatars/yyyy/MM/dd/ | 按日期分类存储 |
| 访问路径前缀 | /uploads | 静态资源访问前缀 |

### 7.2 文件命名规则

```
{userId}_{UUID}.{ext}

示例: 10_abc123def456.jpg
```

- `userId`: 用户ID
- `UUID`: 随机生成的唯一标识
- `ext`: 文件扩展名

### 7.3 前端上传示例

**原生JavaScript上传**
```javascript
// 文件上传函数
async function uploadAvatar(userId, file) {
  // 文件大小验证
  if (file.size > 5 * 1024 * 1024) {
    alert('文件大小不能超过5MB');
    return { success: false, message: '文件太大' };
  }

  // 文件类型验证
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
  if (!allowedTypes.includes(file.type)) {
    alert('只支持JPG、PNG、GIF格式的图片');
    return { success: false, message: '格式不支持' };
  }

  // 创建FormData
  const formData = new FormData();
  formData.append('avatarFile', file);
  formData.append('realName', '张三'); // 其他字段

  try {
    const token = localStorage.getItem('token');
    const response = await fetch(`/api/users/${userId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });

    const data = await response.json();

    if (data.code === 200) {
      return {
        success: true,
        avatar: data.data.avatar  // 返回头像URL
      };
    } else {
      return { success: false, message: data.msg };
    }
  } catch (error) {
    console.error('上传失败:', error);
    return { success: false, message: '上传失败' };
  }
}

// 使用示例
const fileInput = document.getElementById('avatarInput');
fileInput.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (file) {
    const result = await uploadAvatar(10, file);
    if (result.success) {
      console.log('头像上传成功:', result.avatar);
      // 更新页面显示
      document.getElementById('avatar').src = result.avatar;
    } else {
      alert(result.message);
    }
  }
});
```

**Vue + Element UI 上传组件**
```vue
<template>
  <div>
    <el-upload
      class="avatar-uploader"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError">
      <img v-if="imageUrl" :src="imageUrl" class="avatar">
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
    </el-upload>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageUrl: '',
      uploadUrl: '/api/users/1',
      uploadHeaders: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    };
  },
  methods: {
    beforeUpload(file) {
      // 文件类型验证
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
      if (!isImage) {
        this.$message.error('只能上传JPG、PNG、GIF格式的图片');
        return false;
      }

      // 文件大小验证
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB');
        return false;
      }

      return true;
    },
    handleSuccess(response) {
      if (response.code === 200) {
        this.imageUrl = response.data.avatar;
        this.$message.success('头像上传成功');
      } else {
        this.$message.error(response.msg);
      }
    },
    handleError(error) {
      console.error('上传失败:', error);
      this.$message.error('上传失败，请稍后重试');
    }
  }
};
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
```

### 7.4 图片预览

**图片URL拼接**
```javascript
// 完整URL构建
function getImageUrl(avatarPath) {
  if (!avatarPath) {
    return '/default-avatar.png'; // 默认头像
  }

  // 如果是完整URL，直接返回
  if (avatarPath.startsWith('http')) {
    return avatarPath;
  }

  // 否则拼接服务器地址
  const baseUrl = 'http://localhost:8082';
  return `${baseUrl}${avatarPath}`;
}

// 使用示例
const user = {
  avatar: '/uploads/avatars/2024/11/19/1_abc123.jpg'
};
const fullUrl = getImageUrl(user.avatar);
// 结果: http://localhost:8082/uploads/avatars/2024/11/19/1_abc123.jpg
```

---

## 📝 总结

本数据字典涵盖了系统中所有的枚举值、常量定义和数据格式规范。前端开发时应遵循这些约定，确保代码的一致性和可维护性。

### 建议

1. **将枚举值定义为常量**：避免在代码中硬编码数字
2. **使用映射对象**：便于获取显示文本、颜色、图标等
3. **统一日期格式**：使用Day.js等工具库处理日期
4. **统一错误处理**：在Axios拦截器中统一处理HTTP状态码和业务状态码
5. **封装通用组件**：如状态标签、分页组件等

---

> 💡 **提示**: 建议将本文档中的常量定义提取到独立的配置文件中（如 `constants.js`），方便全局引用。
