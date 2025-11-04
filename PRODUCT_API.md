# 商品管理 API 文档

## 概述

商品模块已实现，提供完整的商品管理功能，包括创建、查询、更新和删除商品。

## 数据库表结构

应用启动时会自动创建 `products` 表，包含以下字段：

- `id`: 主键（自增）
- `name`: 商品名称（必填）
- `description`: 商品描述
- `price`: 价格（必填，大于0）
- `stock`: 库存数量（必填）
- `category`: 分类
- `brand`: 品牌
- `image_url`: 图片URL
- `sku`: 商品编码（唯一）
- `is_active`: 是否激活（默认true）
- `created_at`: 创建时间
- `updated_at`: 更新时间

## API 端点

### 1. 获取所有商品

**GET** `/api/products`

获取商品列表，支持多种查询参数。

**查询参数**:
- `activeOnly` (Boolean, 可选): 是否只返回激活的商品
- `category` (String, 可选): 按分类筛选
- `search` (String, 可选): 按商品名称搜索（模糊匹配）
- `page` (int, 默认0): 页码（从0开始）
- `size` (int, 默认10): 每页数量
- `sortBy` (String, 默认"id"): 排序字段
- `sortDir` (String, 默认"ASC"): 排序方向（ASC/DESC）

**示例请求**:
```bash
# 获取所有商品
GET http://localhost:8081/api/products

# 只获取激活的商品
GET http://localhost:8081/api/products?activeOnly=true

# 按分类筛选
GET http://localhost:8081/api/products?category=Electronics

# 搜索商品
GET http://localhost:8081/api/products?search=laptop

# 分页查询
GET http://localhost:8081/api/products?page=0&size=20&sortBy=price&sortDir=DESC
```

**成功响应** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stock": 50,
    "category": "Electronics",
    "brand": "TechBrand",
    "imageUrl": "https://example.com/laptop.jpg",
    "sku": "LAPTOP-001",
    "isActive": true,
    "createdAt": "2025-11-04T17:00:00",
    "updatedAt": "2025-11-04T17:00:00"
  }
]
```

---

### 2. 根据ID获取商品

**GET** `/api/products/{id}`

获取指定ID的商品详情。

**路径参数**:
- `id` (Long): 商品ID

**示例请求**:
```bash
GET http://localhost:8081/api/products/1
```

**成功响应** (200 OK):
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop.jpg",
  "sku": "LAPTOP-001",
  "isActive": true,
  "createdAt": "2025-11-04T17:00:00",
  "updatedAt": "2025-11-04T17:00:00"
}
```

**错误响应** (404 Not Found):
```json
{
  "status": "error",
  "message": "Product not found with id: 1"
}
```

---

### 3. 创建商品

**POST** `/api/products`

创建新商品（需要JWT认证）。

**请求头**:
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop.jpg",
  "sku": "LAPTOP-001",
  "isActive": true
}
```

**必填字段**:
- `name`: 商品名称（1-200字符）
- `price`: 价格（必须大于0）
- `stock`: 库存数量

**可选字段**:
- `description`: 商品描述（最多1000字符）
- `category`: 分类（最多100字符）
- `brand`: 品牌（最多100字符）
- `imageUrl`: 图片URL（最多500字符）
- `sku`: 商品编码（最多50字符，必须唯一）
- `isActive`: 是否激活（默认true）

**成功响应** (201 Created):
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop.jpg",
  "sku": "LAPTOP-001",
  "isActive": true,
  "createdAt": "2025-11-04T17:00:00",
  "updatedAt": "2025-11-04T17:00:00"
}
```

**错误响应** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Product with SKU LAPTOP-001 already exists"
}
```

---

### 4. 更新商品

**PUT** `/api/products/{id}`

更新现有商品（需要JWT认证）。

**路径参数**:
- `id` (Long): 商品ID

**请求头**:
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 899.99,
  "stock": 30,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop-updated.jpg",
  "sku": "LAPTOP-001",
  "isActive": true
}
```

**成功响应** (200 OK):
```json
{
  "id": 1,
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 899.99,
  "stock": 30,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop-updated.jpg",
  "sku": "LAPTOP-001",
  "isActive": true,
  "createdAt": "2025-11-04T17:00:00",
  "updatedAt": "2025-11-04T17:05:00"
}
```

---

### 5. 删除商品（硬删除）

**DELETE** `/api/products/{id}`

永久删除商品（需要JWT认证）。

**路径参数**:
- `id` (Long): 商品ID

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**成功响应** (200 OK):
```json
{
  "message": "Product deleted successfully"
}
```

---

### 6. 停用商品（软删除）

**PATCH** `/api/products/{id}/deactivate`

停用商品，不删除数据（需要JWT认证）。

**路径参数**:
- `id` (Long): 商品ID

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**成功响应** (200 OK):
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "brand": "TechBrand",
  "imageUrl": "https://example.com/laptop.jpg",
  "sku": "LAPTOP-001",
  "isActive": false,
  "createdAt": "2025-11-04T17:00:00",
  "updatedAt": "2025-11-04T17:10:00"
}
```

---

## 使用示例

### PowerShell 示例

```powershell
# 1. 登录获取token
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password123"
} | ConvertTo-Json

$authResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
    -Method Post -Body $loginBody -ContentType "application/json"
$token = $authResponse.token

# 2. 创建商品
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

$productBody = @{
    name = "iPhone 15"
    description = "Latest iPhone model"
    price = 999.99
    stock = 100
    category = "Electronics"
    brand = "Apple"
    sku = "IPHONE-15-001"
    isActive = $true
} | ConvertTo-Json

$product = Invoke-RestMethod -Uri "http://localhost:8081/api/products" `
    -Method Post -Headers $headers -Body $productBody

# 3. 获取所有商品
$products = Invoke-RestMethod -Uri "http://localhost:8081/api/products"

# 4. 按分类获取商品
$electronics = Invoke-RestMethod -Uri "http://localhost:8081/api/products?category=Electronics"

# 5. 搜索商品
$searchResults = Invoke-RestMethod -Uri "http://localhost:8081/api/products?search=iPhone"

# 6. 更新商品
$updateBody = @{
    name = "iPhone 15 Pro"
    description = "Updated description"
    price = 1099.99
    stock = 80
    category = "Electronics"
    brand = "Apple"
    sku = "IPHONE-15-001"
    isActive = $true
} | ConvertTo-Json

$updatedProduct = Invoke-RestMethod -Uri "http://localhost:8081/api/products/$($product.id)" `
    -Method Put -Headers $headers -Body $updateBody

# 7. 停用商品
$deactivatedProduct = Invoke-RestMethod -Uri "http://localhost:8081/api/products/$($product.id)/deactivate" `
    -Method Patch -Headers $headers
```

### cURL 示例

```bash
# 登录
TOKEN=$(curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"password123"}' \
  | jq -r '.token')

# 创建商品
curl -X POST http://localhost:8081/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stock": 50,
    "category": "Electronics",
    "brand": "TechBrand",
    "sku": "LAPTOP-001"
  }'

# 获取所有商品
curl http://localhost:8081/api/products

# 搜索商品
curl "http://localhost:8081/api/products?search=laptop"
```

---

## 注意事项

1. **认证**: 创建、更新、删除商品需要JWT token认证
2. **SKU唯一性**: SKU必须唯一，如果重复会返回错误
3. **价格验证**: 价格必须大于0
4. **软删除 vs 硬删除**: 
   - 软删除（deactivate）只是将商品标记为非激活，数据仍保留
   - 硬删除（delete）会永久删除商品数据
5. **分页**: 默认每页10条记录，可以通过 `page` 和 `size` 参数调整

