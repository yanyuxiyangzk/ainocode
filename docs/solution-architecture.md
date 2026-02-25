# 解决方案架构文档

> RuoYi-Cloud-Nocode 零代码微服务平台
> 版本：v1.0.0 | 更新日期：2026-02-15

---

## 一、执行摘要

### 1.1 项目概述

**RuoYi-Cloud-Nocode** 是一款基于 Spring 生态的零代码微服务平台，核心技术架构为「Skill + LLM + Json2UI + Agent + 代码知识库」，实现企业级微服务的快速构建与热插拔部署。

### 1.2 核心价值主张

| 价值 | 实现方式 |
|------|----------|
| **插件热插拔** | PF4J 3.11.1 + pf4j-spring 0.9.0 实现业务模块动态加载 |
| **业务逻辑热更新** | Liquor 1.6.3 实现运行时代码编译与执行 |
| **零代码开发** | 可视化配置 + 模板引擎自动生成 CRUD 代码 |
| **AI 智能开发** | LLM 理解需求自动生成代码 |

### 1.3 架构决策摘要

| 决策领域 | 选择 | 理由 |
|----------|------|------|
| 架构风格 | 微服务 | 企业级系统，需独立扩展、团队协作 |
| 仓库策略 | Monorepo | 统一版本管理，简化依赖管理 |
| 服务注册 | Nacos 2.x | 阿里生态，配置中心一体化 |
| 认证授权 | Sa-Token 1.37.0 | 轻量级，API 友好 |
| 插件框架 | PF4J 3.11.1 | 成熟稳定，Spring 集成完善 |
| 动态编译 | Liquor 1.6.3 | 国产框架，支持运行时编译 |

---

## 二、技术栈与库决策

### 2.1 后端技术栈

| 类别 | 技术 | 版本 | 用途 | 决策理由 |
|------|------|------|------|----------|
| **框架** | Spring Boot | 3.2.5 | 基础框架 | 企业级标准，生态完善 |
| **微服务** | Spring Cloud | 2023.0.5 | 服务治理 | 与 Spring Boot 版本兼容 |
| **阿里生态** | Spring Cloud Alibaba | 2023.0.3.2 | 阿里组件集成 | Nacos/Sentinel/Seata 统一管理 |
| **注册配置** | Nacos | 2.3.2 | 注册/配置中心 | 阿里生态，动态配置支持 |
| **网关** | Spring Cloud Gateway | 4.1.5 | API 网关 | 响应式，性能优秀 |
| **认证** | Sa-Token | 1.37.0 | 认证授权 | 轻量级，API 友好，支持多端 |
| **分布式事务** | Seata | 1.7.1 | 分布式事务 | AT 模式简单易用 |
| **插件框架** | PF4J | 3.11.1 | 插件热插拔 | 成熟稳定，Spring 集成 |
| **PF4J Spring** | pf4j-spring | 0.9.0 | PF4J Spring 集成 | 与 PF4J 3.11.1 兼容 |
| **动态编译** | Liquor | 1.6.3 | 运行时编译 | 国产框架，功能完善 |
| **ORM** | MyBatis-Plus | 3.5.7 | 数据访问 | 简化 CRUD，代码生成友好 |
| **数据库** | PostgreSQL | 14.x | 主数据库 | 企业级，JSON 支持，开源 |
| **缓存** | Redis | 7.x | 分布式缓存 | 高性能，数据结构丰富 |
| **连接池** | HikariCP | 5.1.0 | 数据库连接池 | Spring Boot 默认，性能优秀 |
| **JSON** | Jackson | 2.17.0 | JSON 序列化 | Spring Boot 默认，性能优秀 |
| **日志** | Logback | 1.5.6 | 日志框架 | Spring Boot 默认 |
| **API 文档** | SpringDoc | 2.5.0 | OpenAPI 文档 | 支持 Spring Boot 3.x |
| **工具库** | Hutool | 5.8.26 | Java 工具库 | 减少重复代码 |
| **模板引擎** | Velocity | 2.3 | 代码生成模板 | 简单易用，性能优秀 |
| **工作流** | Flowable | 7.0.0 | 工作流引擎 | BPMN 2.0 标准，功能完善 |

### 2.2 前端技术栈

| 类别 | 技术 | 版本 | 用途 | 决策理由 |
|------|------|------|------|----------|
| **框架** | Vue | 3.4.27 | 前端框架 | 组合式 API，性能优秀 |
| **语言** | TypeScript | 5.4.5 | 类型安全 | 企业级开发必备 |
| **UI 组件** | Element Plus | 2.6.3 | 组件库 | Vue 3 生态，组件丰富 |
| **构建** | Vite | 5.2.11 | 构建工具 | 快速开发，HMR 优秀 |
| **状态管理** | Pinia | 2.1.7 | 状态管理 | Vue 3 官方推荐 |
| **路由** | Vue Router | 4.3.2 | 路由管理 | Vue 生态标准 |
| **HTTP** | Axios | 1.6.8 | 网络请求 | 拦截器完善，广泛使用 |
| **工具** | VueUse | 10.9.0 | 组合式工具 | 减少 hooks 重复代码 |
| **样式** | UnoCSS | 0.59.4 | 原子化 CSS | 性能优秀，按需生成 |
| **图标** | Iconify | 3.1.0 | 图标库 | 图标丰富，按需加载 |

### 2.3 基础设施

| 服务 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 容器运行时 | Docker | 24.x | 应用容器化 |
| 容器编排 | Kubernetes | 1.28+ | 生产环境编排 |
| 日志收集 | ELK Stack | 8.x | Elasticsearch + Logstash + Kibana |
| 链路追踪 | SkyWalking | 9.x | APM 监控 |
| 向量存储 | Milvus | 2.3.x | AI 向量检索 |
| 对象存储 | MinIO | RELEASE.2024-xx | 文件存储 |

---

## 三、系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RuoYi-Cloud-Nocode 技术架构                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                         用户交互层 (User Interface)                  │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │   │
│  │   │  Web前端    │  │  移动端H5   │  │   API调用   │               │   │
│  │   │ plus-ui-ts  │  │   Vue3+H5   │  │   REST API  │               │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘               │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                      │                                        │
│                                      ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                         网关层 (Gateway)                              │   │
│  │   ┌─────────────────────────────────────────────────────────────┐   │   │
│  │   │              Spring Cloud Gateway 4.1.5                     │   │   │
│  │   │   • 路由转发   • 限流熔断   • 统一鉴权   • 请求日志          │   │   │
│  │   └─────────────────────────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                      │                                        │
│                                      ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                         服务层 (Services)                             │   │
│  │                                                                         │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │   │
│  │  │   认证服务       │  │   系统服务       │  │   代码生成服务  │      │   │
│  │  │ ruoyi-nocode-auth│ │ruoyi-nocode-system│ │ nocode-api-gen │      │   │
│  │  │    (9200)       │  │    (9201)       │  │    (9202)      │      │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘      │   │
│  │                                                                         │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │   │
│  │  │   插件服务       │  │   零代码服务     │  │   工作流服务    │      │   │
│  │  │  (PF4J Plugin)  │  │   (nocode-api)  │  │   (Flowable)   │      │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘      │   │
│  │                                                                         │   │
│  │  ┌─────────────────┐  ┌─────────────────┐                            │   │
│  │  │   动态编译服务   │  │   AI智能服务     │                            │   │
│  │  │   (Liquor)      │  │    (LLM+RAG)    │                            │   │
│  │  └─────────────────┘  └─────────────────┘                            │   │
│  │                                                                         │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                      │                                        │
│                                      ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                       基础设施层 (Infrastructure)                    │   │
│  │   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐              │   │
│  │   │PostgreSQL│ │  Nacos   │ │  Seata   │ │  Redis   │              │   │
│  │   │   14.x   │ │  2.3.2   │ │  1.7.1   │ │   7.x    │              │   │
│  │   └──────────┘ └──────────┘ └──────────┘ └──────────┘              │   │
│  │   ┌──────────┐ ┌──────────┐ ┌──────────┐                           │   │
│  │   │  MinIO   │ │ Milvus   │ │   ELK    │                           │   │
│  │   │ 文件存储  │ │ 向量存储  │ │ 日志分析  │                           │   │
│  │   └──────────┘ └──────────┘ └──────────┘                           │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 核心技术融合架构

```
┌─────────────────────────────────────────────────────────────────┐
│              PF4J + Liquor + MyBatis 融合架构                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    插件模块 (PF4J)                       │   │
│  │  ┌─────────────────┐    ┌─────────────────┐            │   │
│  │  │  静态Java代码   │    │  动态业务逻辑   │            │   │
│  │  │  (Controller/   │    │  (Liquor加载   │            │   │
│  │  │   Service)      │    │   规则/Script) │            │   │
│  │  └────────┬────────┘    └────────┬────────┘            │   │
│  │           │                      │                      │   │
│  │           └──────────┬───────────┘                      │   │
│  │                      ▼                                   │   │
│  │           ┌─────────────────────┐                      │   │
│  │           │   MyBatis数据访问    │                      │   │
│  │           │   (统一持久层)       │                      │   │
│  │           └─────────────────────┘                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  技术职责分工：                                                  │
│  • PF4J: 静态模块拆分，物理分离，独立部署，类加载器隔离         │
│  • Liquor: 模块内逻辑热更新，运行时编译，即时生效               │
│  • MyBatis: 统一数据访问，稳定持久层，SQL管控                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、服务架构

### 4.1 服务划分

| 服务名 | 模块名 | 端口 | 职责 | 技术栈 |
|--------|--------|------|------|--------|
| gateway | ruoyi-nocode-gateway | 8080 | API 网关、路由、鉴权、限流 | Spring Cloud Gateway |
| auth | ruoyi-nocode-auth | 9200 | 认证授权、Token 管理 | Sa-Token |
| system | ruoyi-nocode-system | 9201 | 用户、角色、菜单、部门管理 | Spring Boot + MyBatis-Plus |
| gen | nocode-api-generator | 9202 | 代码生成、模板管理 | Velocity + Liquor |
| plugin | - | - | 插件管理（嵌入各服务） | PF4J |
| workflow | - | 9203 | 工作流引擎 | Flowable |
| nocode | - | 9204 | 零代码配置 | Json2UI + PF4J |

### 4.2 服务依赖关系

```
                    ┌─────────────┐
                    │   Gateway   │
                    │   (8080)    │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
   │    Auth     │  │   System    │  │    Gen      │
   │   (9200)    │  │   (9201)    │  │   (9202)    │
   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
          │                │                │
          └────────────────┼────────────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │   基础设施服务         │
              │ Nacos / Redis / DB     │
              └────────────────────────┘
```

---

## 五、数据架构

### 5.1 数据库规划

| 数据库名 | 服务 | 说明 |
|----------|------|------|
| cost | system/auth | 系统核心库（用户/角色/菜单/插件） |
| ry-gen | gen | 代码生成库 |
| ry-workflow | workflow | 工作流库 |
| ry-nocode | nocode | 零代码配置库 |

### 5.2 数据库连接配置

```yaml
# PostgreSQL 主库配置
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:15432/cost
    username: ods
    password: Test@123
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000
```

### 5.3 核心实体模型

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   SysDept   │       │   SysUser   │       │   SysRole   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ deptId(PK)  │◀─────│ deptId(FK)  │       │ roleId(PK)  │
│ parentId    │       │ userId(PK)  │       │ roleKey     │
│ deptName    │       │ userName    │       │ roleName    │
│ ancestors   │       │ nickName    │       │ dataScope   │
└─────────────┘       └──────┬──────┘       └──────┬──────┘
                             │                     │
                     ┌───────┴───────┐      ┌──────┴──────┐
                     │ SysUserRole   │      │SysRoleMenu  │
                     ├───────────────┤      ├─────────────┤
                     │ userId(FK)    │      │ roleId(FK)  │
                     │ roleId(FK)    │      │ menuId(FK)  │
                     └───────────────┘      └─────────────┘
                                                    │
                                                    ▼
                                          ┌─────────────┐
                                          │   SysMenu   │
                                          ├─────────────┤
                                          │ menuId(PK)  │
                                          │ parentId    │
                                          │ menuName    │
                                          │ path        │
                                          │ perms       │
                                          └─────────────┘
```

---

## 六、API 设计

### 6.1 API 设计原则

| 原则 | 说明 |
|------|------|
| RESTful | 遵循 REST 设计规范 |
| 版本控制 | URL 路径版本 `/api/v1/` |
| 统一响应 | `{code, msg, data, rows, total}` |
| 认证方式 | Bearer Token (Sa-Token) |

### 6.2 核心接口列表

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | `/auth/login` | POST | 用户登录 |
| 认证 | `/auth/logout` | POST | 用户登出 |
| 用户 | `/system/user/list` | GET | 用户列表 |
| 用户 | `/system/user/{id}` | GET | 用户详情 |
| 用户 | `/system/user` | POST | 新增用户 |
| 角色 | `/system/role/list` | GET | 角色列表 |
| 菜单 | `/system/menu/treeselect` | GET | 菜单树 |
| 插件 | `/plugin/list` | GET | 插件列表 |
| 插件 | `/plugin/install` | POST | 安装插件 |
| 插件 | `/plugin/{id}` | DELETE | 卸载插件 |
| 代码生成 | `/gen/table/list` | GET | 生成表列表 |
| 代码生成 | `/gen/code/{tableName}` | GET | 生成代码 |

---

## 七、安全架构

### 7.1 认证授权流程

```
请求 ──▶ Gateway ──▶ Token验证 ──▶ 权限校验 ──▶ 业务处理
              │           │            │
              ▼           ▼            ▼
         ┌─────────┐  ┌─────────┐  ┌─────────┐
         │ 白名单  │  │ Sa-Token │  │ 权限注解 │
         │ 放行   │  │ 验证    │  │ 拦截   │
         └─────────┘  └─────────┘  └─────────┘
```

### 7.2 插件隔离机制

| 隔离层级 | 实现方式 | 说明 |
|----------|----------|------|
| 类加载隔离 | PF4J PluginClassLoader | 每个插件独立类加载器 |
| 资源隔离 | 独立目录/配置文件 | 插件资源独立管理 |
| 数据隔离 | 租户ID + 插件ID | 数据行级隔离 |

---

## 八、横切关注点

### 8.1 日志管理

- **日志框架**: Logback + SLF4J
- **日志级别**: TRACE < DEBUG < INFO < WARN < ERROR
- **日志输出**: 控制台 + 文件 + ELK
- **链路追踪**: SkyWalking TraceID 集成

### 8.2 异常处理

```java
// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail("系统异常，请联系管理员");
    }
}
```

### 8.3 缓存策略

| 缓存类型 | 过期时间 | 说明 |
|----------|----------|------|
| 用户信息 | 30min | 热点数据缓存 |
| 字典数据 | 1h | 变更频率低 |
| 权限数据 | 15min | 需及时刷新 |
| 接口限流 | 1s | 滑动窗口 |

---

## 九、架构决策记录 (ADR)

### ADR-001: 选择微服务架构

**状态**: 已采纳

**背景**: 系统需要支持插件热插拔、多团队协作、独立扩展

**决策**: 采用微服务架构，服务按业务域划分

**后果**:
- (+) 服务独立部署和扩展
- (+) 技术栈灵活选择
- (+) 故障隔离
- (-) 运维复杂度增加
- (-) 分布式事务处理复杂

### ADR-002: 选择 PF4J 作为插件框架

**状态**: 已采纳

**背景**: 需要实现业务模块热插拔，无需重启服务

**决策**: 采用 PF4J 3.11.1 + pf4j-spring 0.9.0

**理由**:
- 成熟稳定，社区活跃
- Spring 集成完善
- 类加载器隔离机制完善
- 支持多种插件加载方式

**后果**:
- (+) 插件独立类加载器，避免类冲突
- (+) 支持运行时安装/卸载
- (-) 需要额外管理插件生命周期

### ADR-003: 选择 Liquor 作为动态编译框架

**状态**: 已采纳

**背景**: 需要实现业务逻辑热更新，无需重启服务

**决策**: 采用 org.noear:liquor:1.6.3

**理由**:
- 国产框架，文档友好
- 支持运行时 Java 代码编译
- 支持类热替换
- 轻量级，无侵入

**后果**:
- (+) 业务逻辑即时生效
- (+) 支持规则动态更新
- (-) 需要安全管理机制

### ADR-004: 选择 PostgreSQL 作为主数据库

**状态**: 已采纳

**背景**: 系统需要企业级数据库，支持 JSON 类型

**决策**: 采用 PostgreSQL 14.x

**理由**:
- 开源免费，无许可证问题
- JSON/JSONB 类型支持
- 优秀的并发性能
- 扩展性强（pgvector 等）

**后果**:
- (+) 成本可控
- (+) 支持向量检索扩展
- (-) 与 MySQL 语法有差异

### ADR-005: 选择 Sa-Token 作为认证框架

**状态**: 已采纳

**背景**: 需要轻量级认证授权框架

**决策**: 采用 Sa-Token 1.37.0

**理由**:
- 轻量级，API 简洁
- 支持多端登录
- 内置权限验证
- 与 Spring Boot 集成简单

**后果**:
- (+) 开发效率高
- (+) 功能完善
- (-) 社区相对较小

---

## 十、实施指南

### 10.1 开发环境搭建

```bash
# 1. 克隆代码
git clone https://github.com/yanyuxiyangzk/ainocode.git

# 2. 安装依赖
cd ainocode/ruoyi-nocode
mvn clean install -DskipTests

# 3. 启动基础设施
docker-compose up -d nacos redis postgres

# 4. 启动服务
# Gateway
java -jar ruoyi-nocode-gateway/target/ruoyi-nocode-gateway.jar

# Auth
java -jar ruoyi-nocode-auth/target/ruoyi-nocode-auth.jar

# System
java -jar ruoyi-nocode-system/target/ruoyi-nocode-system.jar
```

### 10.2 插件开发指南

```java
// 1. 定义扩展点接口
public interface MyExtensionPoint extends ExtensionPoint {
    void execute();
}

// 2. 实现扩展点
@Extension
public class MyExtension implements MyExtensionPoint {
    @Override
    public void execute() {
        System.out.println("Plugin executed!");
    }
}

// 3. 打包插件 JAR
// plugin.properties:
// plugin.id=my-plugin
# plugin.version=1.0.0
# plugin.class=com.example.MyPlugin
```

### 10.3 代码生成使用

```bash
# 1. 访问代码生成页面
http://localhost:8080/tool/gen

# 2. 导入数据库表

# 3. 编辑生成配置

# 4. 生成代码下载
```

---

## 十一、建议的源代码结构

```
ainocode/
├── ruoyi-nocode/                          # 后端主项目
│   ├── pom.xml                            # 父POM
│   ├── ruoyi-nocode-common/               # 公共模块
│   │   ├── ruoyi-nocode-common-core/      # 核心工具
│   │   ├── ruoyi-nocode-common-redis/     # Redis配置
│   │   ├── ruoyi-nocode-common-security/  # 安全配置
│   │   └── ruoyi-nocode-common-swagger/   # API文档
│   ├── ruoyi-nocode-gateway/              # 网关服务 (8080)
│   │   ├── src/main/java/
│   │   │   └── com/ruoyi/nocode/gateway/
│   │   │       ├── config/                # 网关配置
│   │   │       ├── filter/                # 过滤器
│   │   │       └── handler/               # 处理器
│   │   └── src/main/resources/
│   │       └── application.yml
│   ├── ruoyi-nocode-auth/                 # 认证服务 (9200)
│   │   ├── src/main/java/
│   │   │   └── com/ruoyi/nocode/auth/
│   │   │       ├── controller/            # 登录控制器
│   │   │       ├── service/               # 认证服务
│   │   │       └── form/                  # 表单对象
│   │   └── src/main/resources/
│   ├── ruoyi-nocode-system/               # 系统服务 (9201)
│   │   ├── src/main/java/
│   │   │   └── com/ruoyi/nocode/system/
│   │   │       ├── entity/                # 实体类
│   │   │       ├── mapper/                # Mapper接口
│   │   │       ├── service/               # 服务层
│   │   │       │   └── impl/              # 服务实现
│   │   │       ├── controller/            # 控制器
│   │   │       └── plugin/                # 插件管理
│   │   │           ├── manager/           # 插件管理器
│   │   │           ├── listener/          # 插件监听器
│   │   │           └── extension/         # 扩展点定义
│   │   └── src/main/resources/
│   │       └── mapper/                    # Mapper XML
│   └── ruoyi-nocode-plugin/               # 插件模块
│       ├── plugin-api/                    # 插件API
│       └── plugins/                       # 插件目录
│           └── example-plugin/
│               ├── src/main/java/
│               └── plugin.properties
├── nocode-api-generator/                  # 代码生成器
│   ├── nocode-api-core/                   # 核心引擎
│   ├── nocode-api-admin/                  # 管理接口
│   └── nocode-api-starter/                # 启动器
├── plus-ui-ts/                            # 前端项目
│   ├── src/
│   │   ├── api/                           # API接口
│   │   ├── components/                    # 组件
│   │   ├── views/                         # 页面
│   │   ├── stores/                        # 状态管理
│   │   ├── router/                        # 路由
│   │   └── utils/                         # 工具函数
│   ├── package.json
│   └── vite.config.ts
├── docs/                                  # 文档目录
│   ├── requirements/                      # 需求文档
│   ├── architecture/                      # 架构文档
│   ├── iteration/                         # 迭代文档
│   └── stories/                           # 开发故事
├── sql/                                   # SQL脚本
└── docker-compose.yml                     # Docker编排
```

---

## 十二、下一步行动

| 优先级 | 行动项 | 负责人 | 预计完成 |
|--------|--------|--------|----------|
| P0 | 完善 Nacos 配置 | Dev | 1天 |
| P0 | 完善网关服务路由配置 | Dev | 1天 |
| P0 | 完善认证服务登录接口 | Dev | 1天 |
| P1 | 创建开发故事文档 | SM | 1天 |
| P1 | 实现 Liquor 即时编译功能 | Dev | 3天 |
| P1 | 完善插件管理 API | Dev | 2天 |

---

**文档维护**:
- 架构变更需更新本文档
- 新增 ADR 需追加记录
- 技术版本更新需同步修改
