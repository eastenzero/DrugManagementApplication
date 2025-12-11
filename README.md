# 药品库存管理系统（DrugManagementApplication）

本项目是一个基于 **Spring Boot + MySQL** 后端和 **Vue 3 + TypeScript + Vite + Element Plus** 前端的药品库存管理系统，实现了药品分类、药品信息、入库/出库、库存预警以及基础用户认证（注册 / 登录）等功能，可作为课程大作业或小型业务系统的参考实现。

---

## 一、项目结构

```bash
DrugManagementApplication /
├── backend/          # 后端：Spring Boot 药品管理系统
├── frontend/         # 前端：Vue 3 管理后台界面
├── report.md         # 项目报告（Markdown）
└── 课程大作业模板.docx 等
```

### 1. backend（后端）

- 技术栈：
  - Spring Boot 2.7.18
  - Spring Web
  - Spring Data JPA
  - Spring MVC + Thymeleaf（保留了一套传统页面）
  - MySQL 8
  - Maven
- 主要功能：
  - 用户注册 / 登录（REST 接口：`/api/auth/register`、`/api/auth/login`）
  - 药品分类管理：增删改查
  - 药品信息管理：增删改查、模糊查询
  - 药品入库管理：入库记录、库存增加
  - 药品出库管理：出库记录、库存扣减、库存校验
  - 库存总览与预警
- 关键配置文件：
  - `backend/src/main/resources/application.properties`
  - `backend/src/main/resources/db/schema.sql`（数据库初始化脚本，可选）

### 2. frontend（前端）

> 详见 `frontend/README.md`。

- 技术栈：
  - Vue 3 (Composition API) + TypeScript
  - Vite
  - Element Plus
  - Pinia
  - Vue Router 4
  - Axios
- 主要页面：
  - 登录 / 注册
  - 仪表盘（Dashboard）
  - 药品分类管理
  - 药品信息管理
  - 入库记录 / 出库记录
  - 库存总览

---

## 二、环境准备

- JDK：8 或以上
- Maven：3.6+（可选，也可直接运行打包好的 JAR）
- Node.js：建议 16+（用于前端开发）
- 数据库：MySQL 8（本地或远程均可）

---

## 三、后端运行说明（backend）

### 1. 数据库配置

后端默认使用本地 MySQL，数据库名称为 `drug_management`，配置位于：

`backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/drug_management?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
```

请根据本地环境修改：

- `spring.datasource.username`
- `spring.datasource.password`

> 提示：`allowPublicKeyRetrieval=true` 用于解决 MySQL 8 的 `Public Key Retrieval is not allowed` 报错，开发环境可保留该设置。

### 2. 初始化数据库

项目提供了一个可选的 SQL 脚本：

- 路径：`backend/src/main/resources/db/schema.sql`
- 作用：创建 `drug_management` 数据库以及 `sys_user`、`drug_category`、`drug_info`、`stock_in`、`stock_out` 等表结构。

一般情况下，JPA 会根据实体自动建表；如需要更精确控制字段/约束，可以在 MySQL 中手动执行该脚本。

### 3. 启动后端服务

在终端中进入 `backend` 目录：

```bash
cd backend

# 方式一：使用 Maven 运行（如果已安装 Maven）
mvn spring-boot:run

# 方式二：运行已打包的 JAR（如已构建）
java -jar target/drug-management-0.0.1-SNAPSHOT.jar
```

启动成功后，默认监听端口：`http://localhost:8080`。

### 4. REST 接口与 CORS

- 所有前端调用的 REST 接口统一前缀为：`/api/**`
- 示例：
  - 用户认证：`POST /api/auth/login`、`POST /api/auth/register`
  - 药品管理：`GET /api/drugs`、`POST /api/drugs`、`PUT /api/drugs/{id}`、`DELETE /api/drugs/{id}`
  - 分类管理：`GET /api/categories`、`POST /api/categories` 等
  - 入库管理：`GET /api/stock-in`、`POST /api/stock-in`
  - 出库管理：`GET /api/stock-out`、`POST /api/stock-out`

为方便前端本地开发，后端在 `CorsConfig` 中全局开启了跨域：

```java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173", "http://localhost:5174")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
```

> 请使用 `http://localhost:5173`（或 5174）访问前端，否则可能会被浏览器 CORS 拦截。

### 5. 用户注册 / 登录说明

后端提供了 REST 风格的注册和登录接口：

- 注册：`POST /api/auth/register`
  - 请求体：`{ "username": "xxx", "password": "yyy" }`
  - 注册成功后，可以使用该账号登录。
- 登录：`POST /api/auth/login`
  - 请求体：同上
  - 成功返回：`{"token": "dummy-token", "username": "xxx"}`

前端会将 token 存储在 `localStorage` 中，并在之后的请求中通过 `Authorization: Bearer <token>` 方式传递（示例实现，并未集成复杂权限控制）。

---

## 四、前端运行说明（frontend）

详细前端说明见 `frontend/README.md`，这里给出快速指引：

```bash
cd frontend

# 安装依赖
npm install

# 启动开发环境
npm run dev
```

启动后，在浏览器中访问：

- `http://localhost:5173`

前端通过统一封装的 Axios 实例（`src/api/request.ts`），请求后端 `http://localhost:8080/api` 下的接口，并在拦截器中自动附加 `Authorization` 头部、统一错误提示。

---

## 五、常见问题

1. **后端启动时报错：`Public Key Retrieval is not allowed`**
   - 原因：MySQL 8 连接方式与驱动安全策略导致。
   - 解决：JDBC URL 中已加入 `allowPublicKeyRetrieval=true`，并关闭 `useSSL`，如仍有问题请检查数据库用户权限和防火墙设置。

2. **前端调用接口报 CORS 错误**
   - 请确认：前端访问地址为 `http://localhost:5173` 或 `http://localhost:5174`。
   - 如需使用其他域名/端口，请在后端 `CorsConfig` 中添加对应的 `allowedOrigins`。

3. **登录失败提示“用户名或密码错误，或账号被禁用”**
   - 请先通过注册接口/前端注册页面创建用户；
   - 确保数据库中 `sys_user.status = 1`，表示账号启用。

---

## 六、许可证与说明

本项目主要用于学习和课程设计示例，未刻意区分开源许可证。如需在生产环境使用，请根据实际业务进行安全加固（密码加密存储、完整权限体系、审计日志等），并补充合适的 LICENSE 声明。
