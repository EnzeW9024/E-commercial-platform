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

- ✅ 用户认证 (待实现)
- ✅ 产品目录管理 (待实现)
- ✅ 订单管理 (待实现)

## 项目结构

```
src/
├── main/
│   ├── java/com/ecommerce/
│   │   ├── ECommerceApplication.java      # 主应用类
│   │   ├── controller/                    # REST API 控制器
│   │   │   ├── HealthController.java
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   └── OrderController.java
│   │   ├── config/                        # 配置类
│   │   │   ├── WebConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── service/                       # 业务逻辑层 (待创建)
│   │   ├── repository/                    # 数据访问层 (待创建)
│   │   └── model/                         # 实体模型 (待创建)
│   └── resources/
│       └── application.yml                # 应用配置文件
└── test/                                  # 测试代码 (待创建)
```

## 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Kafka 2.8+ (可选，用于消息队列)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd E-Commerce
   ```

2. **配置数据库**
   - 创建 MySQL 数据库：
     ```sql
     CREATE DATABASE ecommerce_db;
     ```
   - 修改 `src/main/resources/application.yml` 中的数据库连接信息

3. **配置 Redis**
   - 确保 Redis 服务运行在 `localhost:6379`
   - 或修改 `application.yml` 中的 Redis 配置

4. **构建项目**
   ```bash
   mvn clean install
   ```

5. **运行应用**
   ```bash
   mvn spring-boot:run
   ```
   或
   ```bash
   java -jar target/ecommerce-backend-1.0.0.jar
   ```

6. **访问应用**
   - 应用运行在: `http://localhost:8080/api`
   - 健康检查: `http://localhost:8080/api/health`

## API 端点

### 健康检查
- `GET /api/health` - 健康检查

### 用户管理 (待实现)
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 产品管理 (待实现)
- `GET /api/products` - 获取所有产品
- `GET /api/products/{id}` - 获取产品详情
- `POST /api/products` - 创建产品
- `PUT /api/products/{id}` - 更新产品
- `DELETE /api/products/{id}` - 删除产品

### 订单管理 (待实现)
- `GET /api/orders` - 获取所有订单
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}` - 更新订单
- `DELETE /api/orders/{id}` - 取消订单

## 开发计划

### 本周任务 ✅
- [x] 创建 Maven 项目结构
- [x] 配置 Spring Boot
- [x] 创建基础 REST API 框架

### 后续任务
- [ ] 实现用户认证 (JWT)
- [ ] 实现产品目录管理
- [ ] 实现订单管理
- [ ] 集成 Redis 缓存
- [ ] 集成 Kafka 消息队列
- [ ] AWS 云端部署
- [ ] CI/CD 配置
- [ ] AI 功能集成

## 许可证

MIT License

