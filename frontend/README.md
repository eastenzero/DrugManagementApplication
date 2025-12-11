# 药品管理系统前端

这是一个基于 Vue 3 + TypeScript + Vite + Element Plus 的药品管理系统前端项目。

## 技术栈

- **Vue 3**: 核心框架 (Composition API)
- **TypeScript**: 静态类型检查
- **Vite**: 构建工具
- **Element Plus**: UI 组件库
- **Pinia**: 状态管理
- **Vue Router 4**: 路由管理
- **Axios**: HTTP 请求库

## 项目结构

```
src/
├── api/          # API 接口封装
├── layout/       # 布局组件
├── router/       # 路由配置
├── store/        # Pinia 状态管理
├── views/        # 页面组件
├── App.vue       # 根组件
└── main.ts       # 入口文件
```

## 快速开始

### 1. 安装依赖

确保本地已安装 Node.js (推荐 v16+)。

```bash
npm install
# 或者
pnpm install
```

### 2. 运行开发环境

```bash
npm run dev
```

启动后访问: `http://localhost:5173` (端口可能会变化，请查看终端输出)

### 3. 构建生产版本

```bash
npm run build
```

## 后端接口说明

默认配置的后端接口地址为 `http://localhost:8080/api`。
如需修改，请编辑 `src/api/request.ts` 文件中的 `baseURL` 配置。

## 功能模块

- **登录/注册**: 用户认证
- **仪表盘**: 系统首页
- **药品分类管理**: 分类的增删改查
- **药品信息管理**: 药品的增删改查、搜索
- **药品入库管理**: 药品入库记录、库存增加
- **药品出库管理**: 药品出库记录、库存扣减、库存校验
- **库存总览**: 库存预警展示
