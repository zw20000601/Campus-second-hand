# 校园二手交易平台

> 一个面向大学校园场景的二手闲置交易平台，MVP 版本。

## 项目结构

```
campus-second-hand/
├── campus-market-web/          # 用户端前端（Vue 3 + Naive UI）
├── campus-market-admin/        # 管理后台（Vue 3 + Naive UI）
├── campus-market-server/       # 后端服务（Spring Boot 3）
├── docs/
│   └── sql/
│       └── init.sql            # 数据库初始化脚本
├── nginx/
│   └── nginx.conf              # Nginx 配置
└── docker-compose.yml          # Docker 编排
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 用户端前端 | Vue 3 + Vite + TypeScript + Pinia + Naive UI |
| 管理后台 | Vue 3 + Vite + TypeScript + Pinia + Naive UI |
| 后端 | Spring Boot 3 + MyBatis-Plus + Sa-Token |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 部署 | Docker + Nginx |

## 本地开发运行

### 前置条件

- JDK 17+
- Node.js 18+
- MySQL 8.0
- Redis 7

### 1. 初始化数据库

```sql
-- 在 MySQL 中执行
source docs/sql/init.sql
```

### 2. 启动后端

```bash
cd campus-market-server

# 修改 src/main/resources/application-dev.yml 中的数据库连接信息
# 然后启动
mvn spring-boot:run
```

后端启动后访问 API 文档：http://localhost:8080/doc.html

默认管理员账号：`admin` / `admin123456`

### 3. 启动用户端前端

```bash
cd campus-market-web
npm install
npm run dev
# 访问 http://localhost:3000
```

### 4. 启动管理后台

```bash
cd campus-market-admin
npm install
npm run dev
# 访问 http://localhost:3001
```

## Docker 部署

```bash
# 1. 编译后端
cd campus-market-server
mvn clean package -DskipTests

# 2. 构建前端
cd ../campus-market-web && npm install && npm run build
cd ../campus-market-admin && npm install && npm run build

# 3. 一键启动所有服务
cd ..
docker-compose up -d
```

服务启动后：
- 用户端：http://localhost
- 管理后台：http://localhost/admin/
- API 文档：http://localhost:8080/doc.html

## 功能说明

### 用户端
- 首页商品浏览、分类筛选
- 注册/登录
- 商品发布（图片上传 + 详细信息）
- 商品详情（收藏、留言、举报）
- 我的发布（下架、标记售出、删除）
- 我的收藏
- 个人资料编辑

### 管理后台
- 数据看板（用户数、商品数、待审核、待处理举报）
- 商品审核（通过/拒绝）
- 用户管理（封禁/解封）
- 举报处理
- 分类管理（CRUD）
- 学校/校区管理（CRUD）
- 公告管理（CRUD）

## 默认账号

| 账号 | 密码 | 说明 |
|------|------|------|
| admin | admin123456 | 超级管理员 |

## API 接口

完整 API 文档请访问：`http://localhost:8080/doc.html`（Knife4j）

主要接口：
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/products` - 商品列表
- `GET /api/products/{id}` - 商品详情
- `POST /api/products` - 发布商品（需登录）
- `POST /admin/auth/login` - 管理员登录
- `GET /admin/api/products` - 后台商品列表
- `PUT /admin/api/products/{id}/audit` - 商品审核
