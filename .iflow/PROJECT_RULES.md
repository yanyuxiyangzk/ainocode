# 项目开发规则文件

> 本文件定义项目的技术要求、开发规范和功能进度跟踪
> 最后更新：2026-02-13

---

## 一、技术栈要求（强制）

### 1.1 后端核心框架

| 技术 | 版本 | 用途 | 状态 |
|------|------|------|------|
| Spring Boot | 3.2.5 | 基础框架 | ✅ 已配置 |
| Spring Cloud | 2023.0.5 | 微服务治理 | ✅ 已配置 |
| Spring Cloud Alibaba | 2023.0.3.2 | 阿里云生态 | ✅ 已配置 |
| **PF4J** | **3.11.1** | **插件热插拔框架** | ✅ 已配置 |
| **pf4j-spring** | **0.9.0** | **PF4J Spring集成** | ✅ 已配置 |
| **Liquor** | **org.noear:liquor:1.6.3** | **动态代码编译** | ✅ 已配置 |
| MyBatis-Plus | 3.5.7 | ORM框架 | ✅ 已配置 |
| Sa-Token | 1.37.0 | 认证授权 | ✅ 已配置 |
| Nacos | 2.x | 注册/配置中心 | ✅ 已配置 |

### 1.2 前端框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue3 | 3.4.x | 前端框架 |
| TypeScript | 5.x | 类型安全 |
| Element Plus | 2.5.x | UI组件库 |
| Vite | 5.x | 构建工具 |

### 1.3 数据库

| 技术 | 说明 |
|------|------|
| PostgreSQL 14 | 主数据库 (127.0.0.1:15432/cost) |
| Redis 7.x | 缓存 |

---

## 二、核心技术架构（强制）

### 2.1 PF4J + Liquor + MyBatis 融合架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    插件模块 (PF4J)                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────┐                   │
│  │  静态Java代码   │    │  动态业务逻辑   │                   │
│  │  (Controller/   │    │  (Liquor加载   │                   │
│  │   Service)      │    │   的规则/Script)│                   │
│  └────────┬────────┘    └────────┬────────┘                   │
│           │                     │                             │
│           └──────────┬──────────┘                             │
│                      ▼                                         │
│           ┌─────────────────────┐                            │
│           │   MyBatis数据访问    │                            │
│           │   (统一持久层)       │                            │
│           └─────────────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 技术职责分工

| 技术 | 职责 | 特点 |
|------|------|------|
| **PF4J** | 静态模块拆分 | 物理分离、独立部署、类加载器隔离 |
| **Liquor** | 模块内逻辑热更新 | 运行时编译、即时生效、无需重启 |
| **MyBatis** | 统一数据访问 | 稳定持久层、SQL管控、映射能力 |

### 2.3 必须使用场景

| 场景 | 使用技术 |
|------|----------|
| 业务模块动态加载 | PF4J 插件机制 |
| 业务逻辑热更新 | Liquor 动态编译 |
| 数据持久化 | MyBatis-Plus |

---

## 三、微服务模块划分

| 服务名 | 模块名 | 端口 | 职责 | 状态 |
|--------|--------|------|------|------|
| gateway | ruoyi-nocode-gateway | 8080 | 请求路由、限流、鉴权 | 🔄 开发中 |
| auth | ruoyi-nocode-auth | 9200 | 认证授权 | 🔄 开发中 |
| system | ruoyi-nocode-system | 9201 | 系统管理（用户/角色/菜单） | 🔄 开发中 |

---

## 四、功能开发进度跟踪

### 4.1 迭代1：微服务基座（P0）

| 序号 | 功能点 | 状态 | 备注 |
|------|--------|------|------|
| 1.1 | 项目结构搭建 | ✅ 完成 | 多模块Maven项目 |
| 1.2 | Spring Boot集成 | ✅ 完成 | 3.2.5版本 |
| 1.3 | Spring Cloud集成 | ✅ 完成 | 2023.0.5版本 |
| 1.4 | Nacos集成 | 🔄 开发中 | 配置待完善 |
| 1.5 | 网关服务搭建 | 🔄 开发中 | Gateway基础代码 |
| 1.6 | 认证服务搭建 | 🔄 开发中 | Sa-Token集成 |

### 4.2 迭代2：插件热插拔（P0）

| 序号 | 功能点 | 状态 | 备注 |
|------|--------|------|------|
| 2.1 | PF4J依赖引入 | ✅ 完成 | 3.11.1版本 |
| 2.2 | PF4J Spring集成 | ✅ 完成 | 0.9.0版本 |
| 2.3 | 插件生命周期管理 | ✅ 完成 | NocodeSpringPluginManager |
| 2.4 | 类加载器隔离 | ✅ 完成 | PF4J默认支持 |
| 2.5 | 插件管理API | ✅ 完成 | SysPluginController |
| 2.6 | 插件数据库持久化 | ✅ 完成 | nocode_plugin_info + nocode_plugin_log |
| 2.7 | 插件扩展点定义 | ✅ 完成 | PluginExtensionPoint, ApiHandlerExtension, DataProcessorExtension |
| 2.8 | 插件状态监听 | ✅ 完成 | PluginStateListener |

### 4.3 迭代3：动态编译（P0）

| 序号 | 功能点 | 状态 | 备注 |
|------|--------|------|------|
| 3.1 | Liquor依赖引入 | ✅ 完成 | org.noear:liquor:1.6.3 |
| 3.2 | 即时编译功能 | ⏳ 待开发 | |
| 3.3 | 类热替换 | ⏳ 待开发 | |
| 3.4 | 沙箱执行 | ⏳ 待开发 | |

### 4.4 迭代4：系统服务基础功能（P1）

| 序号 | 功能点 | 状态 | 备注 |
|------|--------|------|------|
| 4.1 | 用户实体类 | ✅ 完成 | SysUser |
| 4.2 | 角色实体类 | ✅ 完成 | SysRole |
| 4.3 | 菜单实体类 | ✅ 完成 | SysMenu |
| 4.4 | 部门实体类 | ✅ 完成 | SysDept |
| 4.5 | 岗位实体类 | ✅ 完成 | SysPost |
| 4.6 | 插件实体类 | ✅ 完成 | PluginInfo + PluginLog |
| 4.7 | 用户Mapper | ✅ 完成 | SysUserMapper |
| 4.8 | 角色Mapper | ✅ 完成 | SysRoleMapper |
| 4.9 | 菜单Mapper | ✅ 完成 | SysMenuMapper |
| 4.10 | 部门Mapper | ✅ 完成 | SysDeptMapper |
| 4.11 | 岗位Mapper | ✅ 完成 | SysPostMapper |
| 4.12 | 插件Mapper | ✅ 完成 | PluginInfoMapper + PluginLogMapper |
| 4.13 | 用户Service | ✅ 完成 | ISysUserService + Impl |
| 4.14 | 角色Service | ✅ 完成 | ISysRoleService + Impl |
| 4.15 | 菜单Service | ✅ 完成 | ISysMenuService + Impl |
| 4.16 | 部门Service | ✅ 完成 | ISysDeptService + Impl |
| 4.17 | 岗位Service | ✅ 完成 | ISysPostService + Impl |
| 4.18 | 插件Service | ✅ 完成 | ISysPluginService + Impl |
| 4.19 | 用户Controller | ✅ 完成 | SysUserController |
| 4.20 | 角色Controller | ✅ 完成 | SysRoleController |
| 4.21 | 菜单Controller | ✅ 完成 | SysMenuController |
| 4.22 | 部门Controller | ✅ 完成 | SysDeptController |
| 4.23 | 岗位Controller | ✅ 完成 | SysPostController |
| 4.24 | 插件Controller | ✅ 完成 | SysPluginController |

### 4.5 迭代5：代码生成引擎（P0）

| 序号 | 功能点 | 状态 | 备注 |
|------|--------|------|------|
| 5.1 | 表结构解析 | ⏳ 待开发 | |
| 5.2 | 模板引擎集成 | ⏳ 待开发 | Velocity |
| 5.3 | 后端代码生成 | ⏳ 待开发 | |
| 5.4 | 前端代码生成 | ⏳ 待开发 | |

---

## 五、开发规范

### 5.1 包结构规范

```
com.ruoyi.nocode
├── common          # 公共模块
│   ├── core        # 核心工具
│   ├── redis       # Redis配置
│   ├── security    # 安全配置
│   └── swagger     # API文档
├── gateway         # 网关服务
├── auth            # 认证服务
└── system          # 系统服务
    ├── entity      # 实体类
    ├── mapper      # Mapper接口
    ├── service     # 服务层
    │   └── impl    # 服务实现
    └── controller  # 控制器
```

### 5.2 代码规范

- 实体类继承 `BaseEntity`，不要重复定义 createTime、updateTime 等字段
- Mapper 继承 `BaseMapper<T>`，使用 MyBatis-Plus
- Service 继承 `IService<T>`
- Controller 使用 REST 风格，返回 `R<T>` 或 `TableDataInfo`

### 5.3 数据库连接

```yaml
url: jdbc:postgresql://127.0.0.1:15432/cost
username: ods
password: Test@123
```

---

## 六、状态说明

| 状态 | 含义 |
|------|------|
| ✅ 完成 | 功能已开发完成 |
| 🔄 开发中 | 正在开发 |
| ⏳ 待开发 | 尚未开始 |
| ❌ 已取消 | 功能已取消 |

---

## 七、下一步开发任务

**当前优先级 P0 任务：**

1. ~~完善系统服务基础功能（Service层、Controller层）~~ ✅ 已完成
2. ~~菜单/部门/岗位Service+Controller~~ ✅ 已完成
3. ~~实现PF4J插件生命周期管理~~ ✅ 已完成
4. 实现Liquor即时编译功能 ⏳ 待开发

**开发新功能前请：**
1. 阅读本文档了解技术要求
2. 检查功能状态，开发未完成的功能
3. 严格遵循技术栈要求（必须使用PF4J + Liquor）
