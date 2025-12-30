# E-Commerce Backend Platform

电商后端平台 - 基于 Spring Boot 的完整后端解决方案

## 技术栈

- **框架**: Java Spring Boot 3.2.0
- **构建工具**: Maven
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **消息队列**: Apache Kafka
- **云服务**: AWS (EC2, S3, RDS, CloudWatch)
- **CI/CD**: Docker + GitHub Actions
- **AI集成**: LangChain + Hugging Face (计划中)

## 核心功能

- ✅ 用户认证（JWT Token）
- ✅ 产品目录管理（CRUD + 搜索 + 筛选）
- ✅ 订单管理（创建、状态更新、取消）
- ✅ Redis 缓存集成
- ✅ Kafka 消息队列（订单事件、库存更新）
- ✅ Docker 容器化
- ⏳ AWS 云端部署（计划中）
- ⏳ CI/CD 配置（计划中）
- ⏳ AI 推荐服务（计划中）

## 项目结构

```
src/
├── main/
│   ├── java/com/ecommerce/
│   │   ├── ECommerceApplication.java      # 主应用类
│   │   ├── controller/                    # REST API 控制器
│   │   │   ├── AuthController.java        # 认证接口
│   │   │   ├── ProductController.java     # 产品管理
│   │   │   └── OrderController.java       # 订单管理
│   │   ├── config/                        # 配置类
│   │   │   ├── SecurityConfig.java        # Spring Security配置
│   │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   ├── RedisConfig.java           # Redis配置
│   │   │   └── KafkaConfig.java           # Kafka配置
│   │   ├── service/                       # 业务逻辑层
│   │   │   ├── AuthenticationService.java
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── KafkaProducerService.java
│   │   │   └── KafkaConsumerService.java
│   │   ├── repository/                    # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── OrderItemRepository.java
│   │   ├── model/                         # 实体模型
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   └── OrderItem.java
│   │   ├── dto/                           # 数据传输对象
│   │   ├── event/                         # Kafka事件
│   │   ├── util/                          # 工具类
│   │   └── exception/                     # 异常处理
│   └── resources/
│       └── application.yml                # 应用配置文件
├── Dockerfile                             # Docker构建文件
└── docker-compose.yml                     # Docker Compose配置
```

## 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Kafka 2.8+ (可选，用于消息队列)

### 方式一：使用 Docker Compose（推荐）

1. **启动所有服务（MySQL + Redis + Kafka）**
   ```bash
   docker-compose up -d
   ```

2. **构建并运行应用**
   ```bash
   # 使用 Maven Wrapper
   .\mvnw.cmd spring-boot:run
   
   # 或使用 Docker
   docker-compose up app
   ```

3. **访问应用**
   - 应用运行在: `http://localhost:8081/api`
   - 健康检查: `http://localhost:8081/api/health`

### 方式二：手动安装

1. **启动 MySQL、Redis、Kafka**
   - 使用 Docker Compose 启动基础设施：
     ```bash
     docker-compose up -d mysql redis zookeeper kafka
     ```

2. **配置数据库**
   - 数据库会自动创建（`ddl-auto: update`）
   - 或手动创建：`CREATE DATABASE ecommerce_db;`

3. **构建项目**
   ```bash
   .\mvnw.cmd clean install
   ```

4. **运行应用**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

## API 端点

### 认证接口（无需Token）
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录（返回JWT Token）

### 产品管理（需要Token）
- `GET /api/products` - 获取产品列表（支持分页、搜索、筛选）
- `GET /api/products/{id}` - 获取产品详情
- `POST /api/products` - 创建产品（需要ADMIN角色）
- `PUT /api/products/{id}` - 更新产品（需要ADMIN角色）
- `DELETE /api/products/{id}` - 删除产品（需要ADMIN角色）

### 订单管理（需要Token）
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `GET /api/orders/user/{userId}` - 获取用户订单
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}/status` - 更新订单状态
- `PUT /api/orders/{id}` - 更新订单
- `DELETE /api/orders/{id}` - 取消订单

### 健康检查（无需Token）
- `GET /api/health` - 健康检查

**注意**：除认证和健康检查接口外，其他接口需要在请求头中携带JWT Token：
```
Authorization: Bearer <your-jwt-token>
```

## 项目进度

### ✅ 已完成
- [x] 项目基础架构（Maven + Spring Boot + REST API）
- [x] 用户认证模块（JWT Token）
- [x] 产品目录管理（CRUD + 搜索 + 筛选）
- [x] 订单管理（创建、状态更新、取消）
- [x] Redis 缓存集成
- [x] Kafka 消息队列集成
- [x] Docker 容器化

### ⏳ 待完成
- [ ] AWS 云端部署（EC2 + RDS + S3 + CloudWatch）
- [ ] CI/CD 配置（GitHub Actions）
- [ ] AI 集成（LangChain + Hugging Face 推荐服务）

## 许可证

MIT License

