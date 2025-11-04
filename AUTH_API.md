# 用户认证 API 文档

## 概述

用户登录模块已实现，支持用户注册和登录功能，使用 JWT (JSON Web Token) 进行身份认证。

## API 端点

### 1. 用户注册

**POST** `/api/auth/register`

注册新用户账户。

**请求体**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890"
}
```

**必填字段**:
- `username`: 用户名（3-50字符）
- `email`: 邮箱地址（必须是有效的邮箱格式）
- `password`: 密码（至少6个字符）

**可选字段**:
- `firstName`: 名字
- `lastName`: 姓氏
- `phoneNumber`: 电话号码

**成功响应** (201 Created):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**错误响应** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Username already exists"
}
```

---

### 2. 用户登录

**POST** `/api/auth/login`

用户登录，获取 JWT token。

**请求体**:
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

**字段说明**:
- `usernameOrEmail`: 用户名或邮箱地址
- `password`: 密码

**成功响应** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**错误响应** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Invalid username/email or password"
}
```

---

## 使用 JWT Token

登录成功后，会返回一个 JWT token。在后续请求中，需要在 HTTP 请求头中包含这个 token：

```
Authorization: Bearer <your-jwt-token>
```

### 示例

使用 curl 测试登录：

```bash
# 登录
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'

# 使用 token 访问受保护的端点
curl -X GET http://localhost:8081/api/users \
  -H "Authorization: Bearer <your-jwt-token>"
```

使用 PowerShell 测试：

```powershell
# 登录
$loginBody = @{
    usernameOrEmail = "john_doe"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json"

$token = $response.token

# 使用 token 访问受保护的端点
$headers = @{
    Authorization = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8081/api/users" `
    -Headers $headers
```

## 安全说明

1. **密码加密**: 所有密码使用 BCrypt 算法加密存储
2. **JWT Token**: 
   - 默认有效期为 24 小时
   - 包含用户信息和角色信息
   - 签名密钥在 `application.yml` 中配置
3. **受保护的端点**: 除了 `/api/health` 和 `/api/auth/**` 之外，所有端点都需要 JWT token 认证

## 数据库要求

确保 MySQL 数据库已安装并运行：

1. 创建数据库：
   ```sql
   CREATE DATABASE ecommerce_db;
   ```

2. 更新 `application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/ecommerce_db
       username: your_username
       password: your_password
   ```

3. 应用启动时会自动创建 `users` 表（通过 Hibernate）

## 用户角色

- **USER**: 普通用户（默认角色）
- **ADMIN**: 管理员用户

## 错误处理

所有错误都会返回统一的 JSON 格式：

```json
{
  "status": "error",
  "message": "错误描述信息"
}
```

验证错误会返回详细的字段错误：

```json
{
  "status": "error",
  "message": "Validation failed",
  "errors": {
    "username": "Username is required",
    "email": "Email should be valid"
  }
}
```

