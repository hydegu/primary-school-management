# 小学教务管理系统 - API接口文档（整合版）

> **文档版本**: v3.0
> **最后更新**: 2025-11-19
> **项目状态**: 开发中 🚧

---

## 📌 文档说明

本文档整合了小学教务管理系统的所有API接口设计，并**准确标注**了当前的实现状态。

**图例说明**：
- ✅ **已实现** - 接口已完成开发并可正常使用
- ⚠️ **部分实现** - 接口已开发但功能不完整或存在问题
- 🚧 **未实现** - 接口设计已确定，但尚未开发

**当前实际实现进度**：
- **系统管理模块**: 63% (15/24 接口已实现)
- **教务核心模块**: 35% (14/40 接口已实现)
- **业务流程模块**: 56% (14/25 接口已实现)
- **总体进度**: 48% (43/89 接口已实现)

**本次更新(v3.0)主要内容**:
- ✅ 新增完整的菜单管理模块(6个接口)
- ✅ 新增完整的用户管理模块(7个接口,含文件上传)
- ✅ 新增调课申请接口(3个接口)
- ✅ 新增换课申请接口(3个接口)
- ✅ 新增调班申请接口(3个接口)
- ✅ 修复教师详情接口BUG
- ✅ 修复学生删除接口参数绑定问题

---

## 📋 目录

- [一、系统管理模块](#一系统管理模块)
  - [1.1 用户认证接口](#11-用户认证接口)
  - [1.2 用户管理接口](#12-用户管理接口)
  - [1.3 角色管理接口](#13-角色管理接口)
  - [1.4 菜单管理接口](#14-菜单管理接口)
  - [1.5 文件上传接口](#15-文件上传接口)
- [二、教务核心模块](#二教务核心模块)
  - [2.1 教师管理接口](#21-教师管理接口)
  - [2.2 学生管理接口](#22-学生管理接口)
  - [2.3 班级管理接口](#23-班级管理接口)
  - [2.4 年级管理接口](#24-年级管理接口)
  - [2.5 科目管理接口](#25-科目管理接口)
  - [2.6 课程管理接口](#26-课程管理接口)
  - [2.7 排课管理接口](#27-排课管理接口)
- [三、业务流程模块](#三业务流程模块)
  - [3.1 请假申请接口](#31-请假申请接口)
  - [3.2 调课申请接口](#32-调课申请接口)
  - [3.3 换课申请接口](#33-换课申请接口)
  - [3.4 调班申请接口](#34-调班申请接口)
  - [3.5 审批流程接口](#35-审批流程接口)

---

## 一、系统管理模块

### 1.1 用户认证接口

#### 1.1.1 用户登录 ✅

**接口地址**: `POST /api/user/login`

**实现状态**: ✅ 已实现

**功能描述**: 用户登录验证，成功后返回JWT Token

**请求参数**:
```json
{
  "identifier": "admin",      // 用户名或手机号或邮箱
  "password": "admin123"      // 密码
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  // JWT Token
}
```

**实现位置**: `UserController.java:50`

---

#### 1.1.2 用户登出 ✅

**接口地址**: `POST /api/user/logout`

**实现状态**: ✅ 已实现

**功能描述**: 用户登出，将Token加入黑名单

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "登出成功"
}
```

**实现位置**: `UserController.java:80`

---

#### 1.1.5 获取当前用户信息 🚧

**接口地址**: `GET /api/user/profile`

**实现状态**: 🚧 未实现

**未实现原因**:
- ⚠️ 需要从Token中解析用户ID
- ⚠️ 需要关联查询角色和权限信息

---

### 1.2 用户管理接口

#### 1.2.1 用户列表查询 ✅

**接口地址**: `GET /api/users`

**实现状态**: ✅ 已实现

**功能描述**: 查询用户列表，支持条件筛选和分页

**请求参数**:
```
username=admin&userType=1&status=1&page=1&size=10
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 否 | 用户名（模糊查询） |
| realName | String | 否 | 真实姓名（模糊查询） |
| userType | Integer | 否 | 用户类型：1-管理员 2-教师 3-学生 4-家长 |
| status | Integer | 否 | 状态：0-禁用 1-启用 |
| phone | String | 否 | 手机号（模糊查询） |
| email | String | 否 | 邮箱（模糊查询） |
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "userType": 1,
        "avatar": "/uploads/avatars/2024/11/19/1_abc123.jpg",
        "phone": "13800138000",
        "email": "admin@example.com",
        "gender": 1,
        "status": 1,
        "lastLoginTime": "2024-11-18 10:30:00",
        "createdAt": "2024-01-01 00:00:00",
        "updatedAt": "2024-11-18 10:30:00",
        "roles": ["super_admin"]
      }
    ],
    "page": 1,
    "size": 10
  }
}
```

**实现位置**: `UserManagementController.java:84`

**已实现功能**:
- ✅ Controller接口开发
- ✅ Service层分页查询功能
- ✅ 多条件组合查询
- ✅ 关联查询用户角色信息

---

#### 1.2.2 添加用户 ✅

**接口地址**: `POST /api/users`

**实现状态**: ✅ 已实现

**功能描述**: 添加新用户（管理员、教师、学生、家长），支持同时上传头像

**请求格式**: `Content-Type: multipart/form-data`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 登录账号，3-50个字符，唯一 |
| password | String | 是 | 密码，6-20个字符 |
| realName | String | 是 | 真实姓名，1-50个字符 |
| userType | Integer | 是 | 用户类型：1-管理员 2-教师 3-学生 4-家长 |
| avatarFile | File | 否 | 头像文件（JPG/PNG/GIF，最大5MB） |
| phone | String | 否 | 联系电话，11位手机号 |
| email | String | 否 | 邮箱地址 |
| gender | Integer | 否 | 性别：1-男 2-女 |
| status | Integer | 否 | 状态：0-禁用 1-启用，默认1 |

**cURL 示例**:
```bash
curl -X POST http://localhost:8082/api/users \
  -H "Authorization: Bearer {token}" \
  -F "username=teacher001" \
  -F "password=Pass@123" \
  -F "realName=张老师" \
  -F "userType=2" \
  -F "phone=13800138001" \
  -F "email=teacher001@example.com" \
  -F "gender=1" \
  -F "avatarFile=@/path/to/avatar.jpg"
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 10,
    "username": "teacher001",
    "realName": "张老师",
    "userType": 2,
    "avatar": "/uploads/avatars/2024/11/19/10_abc123.jpg",
    "phone": "13800138001",
    "email": "teacher001@example.com",
    "gender": 1,
    "status": 1,
    "createdAt": "2024-11-19 10:00:00",
    "updatedAt": "2024-11-19 10:00:00",
    "roles": ["teacher"]
  }
}
```

**实现位置**: `UserManagementController.java:38`

**已实现功能**:
- ✅ Controller接口开发
- ✅ 用户名唯一性验证
- ✅ 手机号唯一性验证（如果提供）
- ✅ 邮箱唯一性验证（如果提供）
- ✅ 密码自动加密（BCrypt）
- ✅ 自动分配默认角色
- ✅ 参数校验
- ✅ 支持头像文件上传

**注意事项**:
- 密码将使用BCrypt算法加密后存储
- 用户名、手机号、邮箱必须唯一
- 根据用户类型自动分配默认角色（管理员→super_admin，教师→teacher，学生→student）
- 头像文件支持JPG、PNG、GIF格式，最大5MB
- 头像文件按日期分类存储（uploads/avatars/yyyy/MM/dd/）

---

#### 1.2.3 修改用户 ✅

**接口地址**: `PUT /api/users/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 修改用户信息（不包括密码），支持更新头像文件

**请求格式**: `Content-Type: multipart/form-data`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| realName | String | 否 | 真实姓名 |
| avatarFile | File | 否 | 头像文件（JPG/PNG/GIF，最大5MB） |
| phone | String | 否 | 联系电话 |
| email | String | 否 | 邮箱地址 |
| gender | Integer | 否 | 性别：1-男 2-女 |
| status | Integer | 否 | 状态：0-禁用 1-启用 |

**cURL 示例**:
```bash
curl -X PUT http://localhost:8082/api/users/10 \
  -H "Authorization: Bearer {token}" \
  -F "realName=张老师" \
  -F "phone=13800138002" \
  -F "avatarFile=@/path/to/new_avatar.jpg"
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 10,
    "username": "teacher001",
    "realName": "张老师",
    "userType": 2,
    "avatar": "/uploads/avatars/2024/11/19/10_def456.jpg",
    "phone": "13800138002",
    "email": "teacher001@example.com",
    "gender": 1,
    "status": 1,
    "updatedAt": "2024-11-19 11:00:00",
    "roles": ["teacher"]
  }
}
```

**实现位置**: `UserManagementController.java:96`

**已实现功能**:
- ✅ Controller接口开发
- ✅ Service层更新方法
- ✅ 手机号唯一性验证（如果修改）
- ✅ 邮箱唯一性验证（如果修改）
- ✅ 参数校验
- ✅ 清除相关缓存
- ✅ 支持头像文件上传

**注意事项**:
- 不允许修改username（登录账号）
- 不允许修改userType（用户类型）
- 密码修改使用专门的修改密码接口
- 修改手机号或邮箱时需要验证唯一性
- 上传新头像会自动替换旧头像

---

#### 1.2.4 删除用户 ✅

**接口地址**: `DELETE /api/users/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 删除用户（软删除，设置is_deleted=1）

**请求参数**:
- 路径参数：`id` - 用户ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "删除成功"
}
```

**实现位置**: `UserManagementController.java:144`

**已实现功能**:
- ✅ Controller接口开发
- ✅ Service层软删除方法
- ✅ 删除用户角色关联
- ✅ 清除相关缓存
- ✅ 权限验证（不能删除超级管理员ID=1）

**注意事项**:
- 使用软删除，不物理删除数据
- 不允许删除超级管理员（userType=1 且 id=1）
- 删除后该用户无法登录
- 同时删除用户角色关联关系

---

#### 1.2.5 用户详情 ✅

**接口地址**: `GET /api/users/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 查询用户详细信息，包括角色信息

**请求参数**:
- 路径参数：`id` - 用户ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 10,
    "username": "teacher001",
    "realName": "张老师",
    "userType": 2,
    "avatar": "/uploads/avatars/2024/11/19/10_abc123.jpg",
    "phone": "13800138001",
    "email": "teacher001@example.com",
    "gender": 1,
    "status": 1,
    "lastLoginTime": "2024-11-19 09:30:00",
    "lastLoginIp": "192.168.1.100",
    "createdAt": "2024-11-01 10:00:00",
    "updatedAt": "2024-11-19 09:30:00",
    "roles": ["teacher"]
  }
}
```

**实现位置**: `UserManagementController.java:152`

**已实现功能**:
- ✅ Controller接口开发
- ✅ Service层详情查询方法
- ✅ 关联查询用户角色

**注意事项**:
- 不返回密码字段
- 自动关联查询用户的角色信息

---

#### 1.2.6 分配角色给用户 ✅

**接口地址**: `POST /api/users/{id}/roles`

**实现状态**: ✅ 已实现

**功能描述**: 为用户分配一个或多个角色

**请求参数**:
```json
{
  "roleIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "角色分配成功"
}
```

**实现位置**: `UserManagementController.java:195`

**已实现功能**:
- ✅ 批量分配角色
- ✅ 自动删除用户原有角色
- ✅ 角色存在性验证

---

#### 1.2.7 获取用户角色列表 ✅

**接口地址**: `GET /api/users/{id}/roles`

**实现状态**: ✅ 已实现

**功能描述**: 查询用户的角色列表

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": ["teacher", "class_monitor"]
}
```

**实现位置**: `UserManagementController.java:207`

---

### 1.3 角色管理接口

#### 1.3.1 ~ 1.3.5 角色相关接口 🚧

**实现状态**: 🚧 **完全未实现**

**未实现原因**:

- ⚠️ 未创建 `RoleController`
- ⚠️ 未创建 `RoleService` 接口
- ⚠️ 需要实现分页查询功能

**涉及接口**:
- `GET /api/role/list` - 角色列表查询
- `POST /api/role` - 添加角色
- `PUT /api/role/{id}` - 修改角色
- `DELETE /api/role/{id}` - 删除角色
- `POST /api/role/{id}/menu` - 分配权限

---

### 1.4 菜单管理接口

#### 1.4.1 获取菜单树 ✅

**接口地址**: `GET /api/menu/tree`

**实现状态**: ✅ 已实现

**功能描述**: 获取系统菜单树形结构

**请求参数**: 无

**响应示例**:
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
      "sortOrder": 1,
      "children": [
        {
          "id": 2,
          "menuName": "用户管理",
          "menuPath": "/system/user",
          "menuIcon": "user",
          "menuType": 2,
          "parentId": 1,
          "sortOrder": 1,
          "children": []
        }
      ]
    }
  ]
}
```

**实现位置**: `MenuController.java:32`

**已实现功能**:
- ✅ 递归查询树形结构
- ✅ 按排序字段排序
- ✅ 返回完整菜单树

---

#### 1.4.2 获取所有菜单列表 ✅

**接口地址**: `GET /api/menu/list`

**实现状态**: ✅ 已实现

**功能描述**: 获取所有菜单列表（扁平结构，用于下拉选择等）

**实现位置**: `MenuController.java:43`

---

#### 1.4.3 获取菜单详情 ✅

**接口地址**: `GET /api/menu/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 根据ID查询菜单详情

**请求参数**:
- 路径参数：`id` - 菜单ID

**实现位置**: `MenuController.java:54`

---

#### 1.4.4 创建菜单 ✅

**接口地址**: `POST /api/menu`

**实现状态**: ✅ 已实现

**功能描述**: 创建新菜单项

**权限要求**: 超级管理员 (`super_admin`)

**请求参数**:
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

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| menuName | String | 是 | 菜单名称 |
| menuPath | String | 否 | 菜单路径 |
| menuIcon | String | 否 | 菜单图标 |
| menuType | Integer | 是 | 菜单类型：1-目录 2-菜单 3-按钮 |
| parentId | Long | 否 | 父菜单ID，0表示顶级菜单 |
| sortOrder | Integer | 否 | 排序号，默认0 |
| permission | String | 否 | 权限标识 |

**实现位置**: `MenuController.java:66`

---

#### 1.4.5 更新菜单 ✅

**接口地址**: `PUT /api/menu/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 更新菜单信息

**权限要求**: 超级管理员 (`super_admin`)

**实现位置**: `MenuController.java:78`

---

#### 1.4.6 删除菜单 ✅

**接口地址**: `DELETE /api/menu/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 删除菜单项

**权限要求**: 超级管理员 (`super_admin`)

**注意事项**:
- 如果菜单下有子菜单，将无法删除
- 使用软删除方式

**实现位置**: `MenuController.java:90`

---

### 1.5 文件上传接口

#### 1.5.1 文件上传功能说明 ✅

**实现状态**: ✅ 已实现

**功能描述**:

系统实现了完整的文件上传功能，主要用于用户头像上传。文件上传功能已集成到用户管理接口中，无需单独调用上传接口。

**技术实现**:

1. **文件存储服务** (`FileStorageService`)
   - 支持文件验证（格式、大小、文件名）
   - 按日期分类存储（uploads/avatars/yyyy/MM/dd/）
   - 生成唯一文件名（userId_UUID.ext）
   - 支持文件删除

2. **配置信息**:
   - 上传目录：`uploads/`
   - 头像子目录：`avatars/`
   - 文件大小限制：5MB
   - 支持格式：JPG、PNG、GIF
   - 访问URL前缀：`/uploads`

3. **静态资源访问**:
   - 配置了静态资源映射 (`WebMvcConfig`)
   - 可通过 `GET /uploads/**` 访问已上传的文件
   - 例如：`http://localhost:8082/uploads/avatars/2024/11/19/1_abc123.jpg`

**实现位置**:
- 配置类：`FileUploadProperties.java`
- 服务接口：`FileStorageService.java`
- 服务实现：`FileStorageServiceImpl.java`
- Web配置：`WebMvcConfig.java`

**使用说明**:

头像上传已集成到以下用户管理接口中：
- **创建用户时上传头像**: `POST /api/users` - 通过 `avatarFile` 参数上传
- **更新用户时上传头像**: `PUT /api/users/{id}` - 通过 `avatarFile` 参数上传

**配置参数** (`application.yml`):

```yaml
# Spring 文件上传配置
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB
      file-size-threshold: 0

# 自定义文件上传配置
file:
  upload:
    upload-dir: uploads
    avatar-dir: avatars
    max-file-size: 5242880  # 5MB
    access-url-prefix: /uploads
    allowed-image-types:
      - image/jpeg
      - image/png
      - image/jpg
      - image/gif
```

**文件存储结构**:

```
uploads/
└── avatars/
    └── 2024/
        └── 11/
            └── 19/
                ├── 1_abc123def456.jpg
                ├── 2_def456ghi789.png
                └── ...
```

**安全说明**:
- ✅ 文件类型验证
- ✅ 文件大小限制
- ✅ 文件名安全检查（防止目录遍历）
- ✅ uploads 目录已加入 .gitignore

---

## 二、教务核心模块

### 2.1 教师管理接口

#### 2.1.1 教师列表查询 ✅

**接口地址**: `GET /api/teacher/list`

**实现状态**: ✅ 已实现

**功能描述**: 查询教师列表，支持条件筛选（姓名、工号、职称）

**请求参数**:
```
teacherName=张&teacherNo=T001&title=语文教师
```

**实现位置**: `TeacherController.java:28`

**已实现功能**:
- ✅ 支持姓名模糊查询
- ✅ 支持工号查询
- ✅ 支持职称查询
- ⚠️ 未实现分页功能

---

#### 2.1.2 添加教师 ✅

**接口地址**: `POST /api/teacher`

**实现状态**: ✅ 已实现

**功能描述**: 添加新教师信息

**请求参数**:
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

**实现位置**: `TeacherController.java:53`

**已实现功能**:
- ✅ 基本信息添加
- ⚠️ 未实现教师编号唯一性验证
- ⚠️ 未自动创建对应的 sys_user 账号
- ⚠️ 未自动分配教师角色

---

#### 2.1.3 修改教师 ✅

**接口地址**: `PUT /api/teacher/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `TeacherController.java:61`

---

#### 2.1.4 删除教师 ✅

**接口地址**: `DELETE /api/teacher/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `TeacherController.java:57`

**已实现功能**:
- ✅ 基本删除功能
- ⚠️ 未检查教师是否担任班主任
- ⚠️ 未检查教师是否有任课记录
- ⚠️ 未使用软删除

---

#### 2.1.5 教师详情 ✅

**接口地址**: `GET /api/teacher/{id}`

**实现状态**: ✅ 已实现（**BUG已修复**）

**实现位置**: `TeacherController.java:16`

**功能描述**: 根据教师ID查询教师详细信息

**请求参数**:
- 路径参数：`id` - 教师ID

**响应示例**:
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

**已修复问题**:
- ✅ 使用了正确的 `@PathVariable` 注解
- ✅ 调用了正确的 `getTeacherById(id)` 方法

---

#### 2.1.6 教师课程表 🚧

**接口地址**: `GET /api/teacher/{id}/schedule`

**实现状态**: 🚧 未实现

**未实现原因**:
- ⚠️ 需要关联查询 `edu_schedule`、`edu_course`、`edu_subject` 表
- ⚠️ 需要先实现排课管理模块

---

### 2.2 学生管理接口

#### 2.2.1 学生列表查询 ✅

**接口地址**: `GET /api/student/list`

**实现状态**: ✅ 已实现

**功能描述**: 查询学生列表，支持条件筛选

**请求参数**: 支持Map参数（具体字段由Service层处理）

**实现位置**: `StudentController.java:35`

**已实现功能**:
- ✅ 基本列表查询
- ✅ 支持条件筛选
- ⚠️ 未明确支持的查询字段
- ⚠️ 未实现分页功能

---

#### 2.2.2 添加学生 ✅

**接口地址**: `POST /api/student`

**实现状态**: ✅ 已实现

**功能描述**: 添加新学生

**请求参数**:
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

**实现位置**: `StudentController.java:22`

**已实现功能**:
- ✅ 基本信息添加
- ✅ 学号和姓名必填验证
- ⚠️ 未实现学号唯一性验证
- ⚠️ 未自动创建对应的 sys_user 账号
- ⚠️ 未更新班级学生人数
- ⚠️ 未关联家长信息

---

#### 2.2.3 修改学生 ✅

**接口地址**: `PUT /api/student/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `StudentController.java:44`

---

#### 2.2.4 删除学生 ✅

**接口地址**: `DELETE /api/student/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `StudentController.java:57`

**已实现功能**:
- ✅ 基本删除功能
- ⚠️ 未更新班级学生人数
- ⚠️ 未使用软删除

---

#### 2.2.5 学生详情 🚧

**接口地址**: `GET /api/student/{id}`

**实现状态**: 🚧 未实现

---

#### 2.2.6 学生家长关联 🚧

**接口地址**: `POST /api/student/{id}/parent`

**实现状态**: 🚧 未实现

**未实现原因**:
- ⚠️ 未创建 `Parent` 实体类
- ⚠️ 需要操作 `edu_student_parent` 关联表

---

### 2.3 班级管理接口

#### 2.3.1 班级列表查询 ✅

**接口地址**: `GET /api/class/list`

**实现状态**: ✅ 已实现

**实现位置**: `ClassesController.java:16`

**已实现功能**:
- ✅ 基本列表查询
- ⚠️ 未关联查询年级、班主任信息
- ⚠️ 未显示当前学生人数
- ⚠️ 未实现分页功能

---

#### 2.3.2 添加班级 ✅

**接口地址**: `POST /api/class`

**实现状态**: ✅ 已实现

**实现位置**: `ClassesController.java:20`

**已实现功能**:
- ✅ 基本信息添加
- ⚠️ 未实现班级编号唯一性验证
- ⚠️ 未验证班主任是否已担任其他班级班主任
- ⚠️ 未验证教室是否被占用

---

#### 2.3.3 修改班级 ✅

**接口地址**: `PUT /api/class/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `ClassesController.java:28`

---

#### 2.3.4 删除班级 ✅

**接口地址**: `DELETE /api/class/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `ClassesController.java:24`

**已实现功能**:
- ✅ 基本删除功能
- ⚠️ 未检查班级是否有学生
- ⚠️ 未使用软删除

---

#### 2.3.5 班级详情 🚧

**接口地址**: `GET /api/class/{id}`

**实现状态**: 🚧 未实现

---

#### 2.3.6 班级课程表 🚧

**接口地址**: `GET /api/class/{id}/schedule`

**实现状态**: 🚧 未实现

---

#### 2.3.7 班级学生列表 🚧

**接口地址**: `GET /api/class/{id}/students`

**实现状态**: 🚧 未实现

---

### 2.4 年级管理接口

#### 2.4.1 年级列表查询 ✅

**接口地址**: `GET /api/grade/**`

**实现状态**: ✅ 已实现（仅查询功能）

**实现位置**: `GradeController.java:18`

**已实现功能**:
- ✅ 基本列表查询
- ⚠️ 路径使用了 `**` 通配符，不规范
- ⚠️ 未实现分页功能

---

#### 2.4.2 ~ 2.4.4 年级增删改接口 🚧

**实现状态**: 🚧 未实现

**涉及接口**:
- `POST /api/grade` - 添加年级
- `PUT /api/grade/{id}` - 修改年级
- `DELETE /api/grade/{id}` - 删除年级

---

### 2.5 科目管理接口

#### 2.5.1 ~ 2.5.4 科目相关接口 🚧

**实现状态**: 🚧 **完全未实现**

**未实现原因**:
- ⚠️ 未创建 `Subject` 实体类
- ⚠️ 未创建 `SubjectController`
- ⚠️ 未创建 `SubjectService`

**涉及接口**:
- `GET /api/subject/list` - 科目列表
- `POST /api/subject` - 添加科目
- `PUT /api/subject/{id}` - 修改科目
- `DELETE /api/subject/{id}` - 删除科目

---

### 2.6 课程管理接口

#### 2.6.1 ~ 2.6.4 课程相关接口 🚧

**实现状态**: 🚧 **完全未实现**

**未实现原因**:
- ⚠️ 未创建 `Course` 实体类
- ⚠️ 未创建 `CourseController`
- ⚠️ 课程涉及教师、班级、科目的关联

**涉及接口**:
- `GET /api/course/list` - 课程列表
- `POST /api/course` - 添加课程
- `PUT /api/course/{id}` - 修改课程
- `DELETE /api/course/{id}` - 删除课程

---

### 2.7 排课管理接口

#### 2.7.1 ~ 2.7.3 排课相关接口 🚧

**实现状态**: 🚧 **完全未实现**

**未实现原因**:
- ⚠️ 未创建 `Schedule` 实体类
- ⚠️ 未创建 `ScheduleController`
- ⚠️ 排课涉及复杂的冲突检测逻辑

**涉及接口**:
- `GET /api/schedule/list` - 课程表查询
- `POST /api/schedule` - 添加排课
- `PUT /api/schedule/{id}` - 修改排课
- `DELETE /api/schedule/{id}` - 删除排课

**关键功能**:
- 教室冲突检测（同一时间段教室不能被占用）
- 教师冲突检测（同一时间段教师不能上两节课）
- 班级冲突检测（同一时间段班级不能有两节课）

---

## 三、业务流程模块

### 3.1 请假申请接口

#### 3.1.1 提交请假申请 ✅

**接口地址**: `POST /api/leave`

**实现状态**: ✅ 已实现

**功能描述**: 学生/家长提交请假申请

**请求参数**:
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

**实现位置**: `LeaveController.java:23`

**已实现功能**:
- ✅ 提交请假申请
- ✅ 生成请假记录
- ⚠️ 审批流程可能未完全实现

---

#### 3.1.2 请假详情 ✅

**接口地址**: `GET /api/leave/{id}`

**实现状态**: ✅ 已实现

**实现位置**: `LeaveController.java:32`

---

#### 3.1.3 我的请假记录 ✅

**接口地址**: `GET /api/leave/my`

**实现状态**: ✅ 已实现（支持分页）

**请求参数**:
```
studentId=1&page=1&size=10
```

**实现位置**: `LeaveController.java:39`

---

#### 3.1.4 撤回请假 ✅

**接口地址**: `PUT /api/leave/{id}/cancel`

**实现状态**: ✅ 已实现

**实现位置**: `LeaveController.java:49`

---

#### 3.1.5 待审批请假列表 ✅

**接口地址**: `GET /api/leave/pending`

**实现状态**: ✅ 已实现（支持分页）

**请求参数**:
```
classId=1&page=1&size=10
```

**实现位置**: `LeaveController.java:58`

---

### 3.2 调课申请接口

#### 3.2.1 提交调课申请 ✅

**接口地址**: `POST /api/course-change`

**实现状态**: ✅ 已实现

**功能描述**: 教师提交调课申请

**请求参数**:
```json
{
  "scheduleId": 123,
  "originalDate": "2024-11-20",
  "newDate": "2024-11-21",
  "reason": "临时有事需要调整课程时间"
}
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| scheduleId | Long | 是 | 原课程ID |
| originalDate | String | 是 | 原上课日期 |
| newDate | String | 是 | 调整后日期 |
| reason | String | 是 | 调课原因 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1  // 调课申请ID
}
```

**实现位置**: `CourseChangeController.java:24`

**已实现功能**:
- ✅ 提交调课申请
- ✅ 自动获取当前教师信息
- ✅ 记录申请时间

---

#### 3.2.2 查询调课详情 ✅

**接口地址**: `GET /api/course-change/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 查询调课申请详情

**请求参数**:
- 路径参数：`id` - 调课申请ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "teacherId": 10,
    "teacherName": "张老师",
    "scheduleId": 123,
    "originalDate": "2024-11-20",
    "newDate": "2024-11-21",
    "reason": "临时有事需要调整课程时间",
    "approvalStatus": 1,
    "createdAt": "2024-11-19 10:00:00"
  }
}
```

**实现位置**: `CourseChangeController.java:33`

---

#### 3.2.3 我的调课记录 ✅

**接口地址**: `GET /api/course-change/my`

**实现状态**: ✅ 已实现

**功能描述**: 查询当前教师的调课申请记录（支持分页）

**请求参数**:
```
teacherId=10&page=1&size=10
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| teacherId | Long | 是 | 教师ID |
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "scheduleId": 123,
        "originalDate": "2024-11-20",
        "newDate": "2024-11-21",
        "reason": "临时有事",
        "approvalStatus": 1,
        "createdAt": "2024-11-19 10:00:00"
      }
    ],
    "total": 20,
    "size": 10,
    "current": 1,
    "pages": 2
  }
}
```

**实现位置**: `CourseChangeController.java:40`

**已实现功能**:
- ✅ 分页查询
- ✅ 根据教师ID筛选
- ✅ 按时间倒序排列

---

### 3.3 换课申请接口

#### 3.3.1 提交换课申请 ✅

**接口地址**: `POST /api/course-swap`

**实现状态**: ✅ 已实现

**功能描述**: 教师提交换课申请（与另一位教师交换课程）

**请求参数**:
```json
{
  "myScheduleId": 123,
  "targetScheduleId": 456,
  "targetTeacherId": 20,
  "reason": "时间冲突，需要与李老师换课"
}
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| myScheduleId | Long | 是 | 我的课程ID |
| targetScheduleId | Long | 是 | 目标课程ID |
| targetTeacherId | Long | 是 | 目标教师ID |
| reason | String | 是 | 换课原因 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1  // 换课申请ID
}
```

**实现位置**: `CourseSwapController.java:25`

**已实现功能**:
- ✅ 提交换课申请
- ✅ 自动获取申请教师信息
- ✅ 等待目标教师确认

---

#### 3.3.2 对方确认换课 ✅

**接口地址**: `PUT /api/course-swap/{id}/confirm`

**实现状态**: ✅ 已实现

**功能描述**: 目标教师确认或拒绝换课申请

**请求参数**:
```
confirm=true  // true表示同意，false表示拒绝
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| confirm | Boolean | 是 | true-同意换课 false-拒绝换课 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功"
}
```

**实现位置**: `CourseSwapController.java:44`

**已实现功能**:
- ✅ 目标教师确认功能
- ✅ 状态更新
- ✅ 权限验证（只有目标教师可以确认）

---

#### 3.3.3 我的换课记录 ✅

**接口地址**: `GET /api/course-swap/my`

**实现状态**: ✅ 已实现

**功能描述**: 查询当前教师的换课申请记录（支持分页）

**请求参数**:
```
teacherId=10&page=1&size=10
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| teacherId | Long | 是 | 教师ID |
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "myScheduleId": 123,
        "targetScheduleId": 456,
        "targetTeacherId": 20,
        "targetTeacherName": "李老师",
        "reason": "时间冲突",
        "targetConfirm": 0,
        "approvalStatus": 1,
        "createdAt": "2024-11-19 10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

**实现位置**: `CourseSwapController.java:34`

**已实现功能**:
- ✅ 分页查询
- ✅ 查询我发起的和我收到的换课申请
- ✅ 显示确认状态

---

### 3.4 调班申请接口

#### 3.4.1 提交调班申请 ✅

**接口地址**: `POST /api/class-transfer`

**实现状态**: ✅ 已实现

**功能描述**: 家长/学生提交调班申请

**请求参数**:
```json
{
  "studentId": 100,
  "currentClassId": 1,
  "targetClassId": 2,
  "reason": "家庭住址变更，申请转到离家更近的班级"
}
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentId | Long | 是 | 学生ID |
| currentClassId | Long | 是 | 当前班级ID |
| targetClassId | Long | 是 | 目标班级ID |
| reason | String | 是 | 调班原因 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1  // 调班申请ID
}
```

**实现位置**: `ClassTransferController.java:24`

**已实现功能**:
- ✅ 提交调班申请
- ✅ 自动获取申请用户信息
- ✅ 记录申请时间

---

#### 3.4.2 查询调班详情 ✅

**接口地址**: `GET /api/class-transfer/{id}`

**实现状态**: ✅ 已实现

**功能描述**: 查询调班申请详情

**请求参数**:
- 路径参数：`id` - 调班申请ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "studentId": 100,
    "studentName": "小明",
    "currentClassId": 1,
    "currentClassName": "一年级1班",
    "targetClassId": 2,
    "targetClassName": "一年级2班",
    "reason": "家庭住址变更",
    "approvalStatus": 1,
    "createdAt": "2024-11-19 10:00:00"
  }
}
```

**实现位置**: `ClassTransferController.java:33`

---

#### 3.4.3 我的调班记录 ✅

**接口地址**: `GET /api/class-transfer/my`

**实现状态**: ✅ 已实现

**功能描述**: 查询当前学生的调班申请记录（支持分页）

**请求参数**:
```
studentId=100&page=1&size=10
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentId | Long | 是 | 学生ID |
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "currentClassId": 1,
        "currentClassName": "一年级1班",
        "targetClassId": 2,
        "targetClassName": "一年级2班",
        "reason": "家庭住址变更",
        "approvalStatus": 1,
        "createdAt": "2024-11-19 10:00:00"
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

**实现位置**: `ClassTransferController.java:40`

**已实现功能**:
- ✅ 分页查询
- ✅ 根据学生ID筛选
- ✅ 显示班级名称

---

### 3.5 审批流程接口

#### 3.5.1 ~ 3.5.5 审批相关接口 🚧

**实现状态**: 🚧 **完全未实现**

**未实现原因**:
- ⚠️ 未创建 `Approval` 实体类
- ⚠️ 未创建 `ApprovalController`
- ⚠️ 审批流程引擎未实现

**涉及接口**:
- `GET /api/approval/pending` - 待我审批
- `GET /api/approval/{id}` - 审批详情
- `POST /api/approval/{id}/submit` - 提交审批
- `GET /api/approval/my-records` - 我的审批记录
- `GET /api/approval/my-applications` - 我的申请记录

---

## 附录

### A. 通用响应格式

所有接口统一使用以下响应格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

**状态码说明**：
- `200` - 操作成功
- `400` - 请求参数错误
- `401` - 未授权（未登录或Token失效）
- `403` - 无权限访问
- `404` - 资源不存在
- `500` - 服务器内部错误

---

### B. 分页请求参数

所有分页接口统一使用以下参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum 或 page | int | 否 | 页码，默认1 |
| pageSize 或 size | int | 否 | 每页条数，默认10 |

**分页响应格式**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "list": []
  }
}
```

---

### C. Token使用说明

1. **获取Token**: 调用登录接口获取JWT Token
2. **使用Token**: 在请求头中添加 `Authorization: Bearer <token>`
3. **Token有效期**: 默认24小时（可在配置文件中调整）
4. **Token刷新**: 暂未实现自动刷新机制

---

### D. 数据字典

#### 用户类型 (user_type)
- `1` - 管理员
- `2` - 教师
- `3` - 学生
- `4` - 家长

#### 性别 (gender)
- `1` - 男
- `2` - 女

#### 状态 (status)
- `0` - 禁用
- `1` - 启用

#### 请假类型 (leave_type)
- `1` - 病假
- `2` - 事假
- `3` - 其他

#### 审批状态 (approval_status)
- `1` - 待审批
- `2` - 审批中
- `3` - 已通过
- `4` - 已拒绝
- `5` - 已撤回

#### 业务类型 (business_type)
- `1` - 请假申请
- `2` - 调课申请
- `3` - 换课申请
- `4` - 调班申请

---

### E. 开发优先级建议

根据业务重要性和依赖关系，建议按以下顺序实现：

**第一阶段：基础功能**（优先级：高）
1. ✅ 用户登录/登出
2. 🚧 用户注册
3. 🚧 用户信息查询
4. 🚧 角色管理（增删改查）
5. 🚧 菜单管理（增删改查、树形结构）

**第二阶段：教务核心**（优先级：高）
1. ✅ 年级管理（查询已实现，增删改待实现）
2. 🚧 科目管理
3. ✅ 班级管理（基本CRUD已实现）
4. ✅ 教师管理（基本CRUD已实现）
5. ✅ 学生管理（基本CRUD已实现）

**第三阶段：课程排课**（优先级：中）
1. 🚧 课程管理
2. 🚧 排课管理（含冲突检测）
3. 🚧 课程表查询（教师课表、班级课表）

**第四阶段：业务流程**（优先级：中）
1. 🚧 审批流程引擎
2. ✅ 请假申请（基本功能已实现）
3. 🚧 调课申请
4. 🚧 换课申请
5. 🚧 调班申请

**第五阶段：扩展功能**（优先级：低）
1. 🚧 成绩管理
2. 🚧 考勤管理
3. 🚧 通知公告
4. 🚧 家校互动
5. 🚧 数据统计报表

---

### F. 技术实现建议

#### 1. 实体类创建
- 所有实体类继承 `BaseEntity`（包含通用字段：id, createdAt, updatedAt, isDeleted）
- 使用 MyBatis-Plus 注解（@TableName, @TableId, @TableField）
- 使用 Lombok 注解（@Data, @Accessors(chain = true)）

#### 2. Controller层规范
- 统一使用 `@RestController` 注解
- 统一请求路径前缀 `/api/{模块名}`
- 统一返回 `R` 对象（ResultCode枚举）
- 添加参数校验（@Valid, @NotNull, @NotBlank等）
- 添加权限注解（待实现）

#### 3. Service层规范
- 继承 `IService<T>`（MyBatis-Plus）
- 实现类继承 `ServiceImpl<M, T>`
- 复杂业务逻辑添加事务注解 `@Transactional`

#### 4. 缓存策略
- 使用Redis缓存菜单树
- 使用Redis缓存角色权限
- 使用Redis存储Token黑名单
- 使用Redis缓存基础数据（年级、科目等）

#### 5. 审批流程引擎设计
- 使用责任链模式实现多级审批
- 使用状态机模式管理审批状态流转
- 支持动态配置审批流程
- 记录每个审批节点的审批人和审批意见

---

### G. 当前存在的问题

#### 🐛 已知BUG

1. **TeacherController.java:14** - `GET /api/teacher/{id}` 接口实现错误
   - 问题：方法体调用的是 `teacherList()` 而不是根据ID查询
   - 影响：无法查询单个教师详情
   - 修复建议：修改为 `getTeacherById(id)` 方法调用

2. **GradeController.java:18** - `GET /api/grade/**` 使用了不规范的通配符
   - 问题：路径使用了 `**` 通配符
   - 影响：路径不规范，可能导致意外的路由匹配
   - 修复建议：改为 `/api/grade/list`

#### ⚠️ 功能缺失

1. **分页功能缺失**
   - 教师列表、学生列表、班级列表、年级列表都未实现分页
   - 数据量大时可能导致性能问题
   - 建议：统一实现分页功能

2. **唯一性验证缺失**
   - 教师编号、学生学号、班级编号等未做唯一性验证
   - 可能导致重复数据
   - 建议：在Service层添加唯一性验证

3. **软删除未实现**
   - 所有删除操作都是物理删除
   - 无法恢复误删除的数据
   - 建议：改为软删除（更新 is_deleted 字段）

4. **关联数据验证缺失**
   - 删除教师时未检查是否担任班主任
   - 删除班级时未检查是否有学生
   - 可能导致数据不一致
   - 建议：添加关联数据验证


- 项目源码：`/src/main/java/com/example/primaryschoolmanagement/`


**文档维护**: 请在每次实现或修改接口后及时更新此文档

---

## 📝 更新历史

### v3.0 (2025-11-19)

**主要更新**:
- ✅ 完成菜单管理模块（1.4）：菜单树、菜单列表、菜单详情、创建、更新、删除共6个接口
- ✅ 完成调课申请模块（3.2）：提交申请、查询详情、我的记录共3个接口
- ✅ 完成换课申请模块（3.3）：提交申请、对方确认、我的记录共3个接口
- ✅ 完成调班申请模块（3.4）：提交申请、查询详情、我的记录共3个接口
- ✅ 修复教师详情接口（2.1.5）：解决参数绑定和方法调用错误
- ✅ 修复学生删除接口（2.2.4）：修正参数绑定方式(@RequestBody改为@PathVariable)

**统计数据**:
- 系统管理模块实现进度提升至 63% (15/24)
- 业务流程模块实现进度提升至 56% (14/25)
- 总体进度提升至 48% (43/89)

### v2.2 (2025-11-19)

**主要更新**:
- ✅ 完成用户管理模块（1.2）：用户列表查询、添加、修改、删除、详情查询共7个接口
- ✅ 新增文件上传模块（1.5）：实现头像文件上传功能，集成到用户创建和更新接口中
- ✅ 支持 multipart/form-data 格式上传头像文件
- ✅ 实现文件存储服务：按日期分类、文件验证、静态资源访问

**统计数据**:
- 系统管理模块实现进度提升至 47% (7/15)
