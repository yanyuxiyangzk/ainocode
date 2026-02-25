# 项目记忆文件

> 存储项目的关键信息，供AI开发助手参考
> 最后更新：2026-02-13

---

## 一、项目概述

### 1.1 项目名称
**RuoYi-Cloud-Nocode** - 零代码微服务平台

### 1.2 项目定位
基于Spring生态的零代码微服务平台，采用「Skill + LLM + Json2UI + Agent + 代码知识库」核心技术架构。

### 1.3 核心价值
- 插件热插拔热启动 - 业务模块动态加载无需重启
- 一站式企业级解决方案 - 开箱即用
- AI智能开发 - LLM理解需求自动生成代码

---

## 二、核心技术要求（重要！）

### 2.1 必须使用的技术

| 技术 | 说明 | 为什么必须 |
|------|------|------------|
| **PF4J 3.11.1** | 插件热插拔框架 | 实现业务模块动态加载，无需重启 |
| **pf4j-spring 0.9.0** | PF4J Spring集成 | 与Spring Boot无缝集成 |
| **Liquor (org.noear:liquor:1.6.3)** | 动态代码编译 | 运行时编译Java代码，实现业务逻辑热更新 |

### 2.2 技术融合架构

```
PF4J（静态模块拆分）+ Liquor（动态逻辑更新）+ MyBatis（统一数据访问）
```

**架构意义：**
- PF4J 负责物理分离、独立部署、类加载器隔离
- Liquor 负责运行时编译、即时生效、无需重启
- MyBatis 负责稳定持久层、SQL管控

### 2.3 依赖版本已验证

以下版本已在Maven中央仓库验证可用：
- `org.pf4j:pf4j:3.11.1`
- `org.pf4j:pf4j-spring:0.9.0`
- `org.noear:liquor:1.6.3`

---

## 三、当前项目结构

```
ruoyi-nocode/
├── pom.xml                      # 父POM，版本管理
├── ruoyi-nocode-common/         # 公共模块
│   ├── ruoyi-nocode-common-core/
│   ├── ruoyi-nocode-common-redis/
│   ├── ruoyi-nocode-common-security/
│   └── ruoyi-nocode-common-swagger/
├── ruoyi-nocode-gateway/        # 网关服务 (8080)
├── ruoyi-nocode-auth/           # 认证服务 (9200)
└── ruoyi-nocode-system/         # 系统服务 (9201)
```

---

## 四、已完成功能

### 4.1 基础框架
- [x] Maven多模块项目结构
- [x] Spring Boot 3.2.5 集成
- [x] Spring Cloud 2023.0.5 集成
- [x] Spring Cloud Alibaba 2023.0.3.2 集成
- [x] MyBatis-Plus 3.5.7 集成
- [x] Sa-Token 1.37.0 集成
- [x] **PF4J 3.11.1 依赖引入**
- [x] **pf4j-spring 0.9.0 依赖引入**
- [x] **Liquor 1.6.3 依赖引入**

### 4.2 实体类
- [x] SysUser - 用户实体
- [x] SysRole - 角色实体
- [x] SysMenu - 菜单实体
- [x] SysDept - 部门实体
- [x] SysPost - 岗位实体
- [x] SysUserRole - 用户角色关联实体
- [x] SysUserPost - 用户岗位关联实体
- [x] SysRoleMenu - 角色菜单关联实体
- [x] BaseEntity - 基础实体（含createTime, updateTime等公共字段）

### 4.3 Mapper层
- [x] SysUserMapper + XML
- [x] SysRoleMapper + XML
- [x] SysMenuMapper + XML
- [x] SysDeptMapper + XML
- [x] SysPostMapper + XML
- [x] SysUserRoleMapper
- [x] SysUserPostMapper
- [x] SysRoleMenuMapper

### 4.4 Service层
- [x] ISysUserService - 用户服务接口（完整）
- [x] SysUserServiceImpl - 用户服务实现（完整CRUD）
- [x] ISysRoleService - 角色服务接口（完整）
- [x] SysRoleServiceImpl - 角色服务实现（完整CRUD）

### 4.5 Controller层
- [x] SysUserController - 用户控制器（完整REST API）
- [x] SysRoleController - 角色控制器（完整REST API）

### 4.6 配置类
- [x] MyBatisPlusConfig - 分页配置
- [x] JacksonConfig - JSON序列化配置

---

## 五、待开发功能

### 5.1 高优先级（P0）- 核心功能

| 序号 | 功能 | 说明 | 状态 |
|------|------|------|------|
| 1 | PF4J插件生命周期管理 | 安装/卸载/启用/停用插件 | ⏳ 待开发 |
| 2 | 类加载器隔离 | 插件间类隔离 | ⏳ 待开发 |
| 3 | Liquor即时编译 | Java源码动态编译 | ⏳ 待开发 |
| 4 | Liquor类热替换 | 运行时类替换 | ⏳ 待开发 |
| 5 | 代码生成引擎 | 表解析+模板渲染 | ⏳ 待开发 |

### 5.2 中优先级（P1）- 系统服务

| 序号 | 功能 | 说明 | 状态 |
|------|------|------|------|
| 1 | 菜单Service/Controller | 树形结构CRUD | ⏳ 待开发 |
| 2 | 部门Service/Controller | 树形结构CRUD | ⏳ 待开发 |
| 3 | 岗位Service/Controller | 基础CRUD | ⏳ 待开发 |
| 4 | 字典管理 | 字典数据管理 | ⏳ 待开发 |
| 5 | 沙箱执行环境 | Liquor安全执行 | ⏳ 待开发 |

### 5.3 开发中功能（P0）

| 功能 | 当前状态 | 下一步 |
|------|----------|--------|
| Nacos配置完善 | 🔄 开发中 | 配置服务注册 |
| 网关服务 | 🔄 开发中 | 路由配置 |
| 认证服务 | 🔄 开发中 | Sa-Token登录接口 |

---

## 六、数据库信息

### 6.1 连接配置
```yaml
url: jdbc:postgresql://127.0.0.1:15432/cost
username: ods
password: Test@123
driver: org.postgresql.Driver
```

### 6.2 表命名规范
- 用户表：sys_user
- 角色表：sys_role
- 菜单表：sys_menu
- 部门表：sys_dept
- 岗位表：sys_post
- 用户角色关联：sys_user_role
- 角色菜单关联：sys_role_menu

---

## 七、常见问题与解决方案

### 7.1 版本问题
| 问题 | 解决方案 |
|------|----------|
| Spring Cloud Alibaba版本不存在 | 使用 2023.0.3.2 |
| PF4J 3.10.0找不到 | 使用 3.11.1 |
| pf4j-spring版本与pf4j不同 | pf4j-spring独立版本 0.9.0 |
| Liquor com.ecfront找不到 | 使用 org.noear:liquor:1.6.3 |

### 7.2 实体类问题
| 问题 | 解决方案 |
|------|----------|
| getCreateTime()无法覆盖 | 子类不要重复定义BaseEntity已有的字段 |

### 7.3 MyBatis-Plus问题
| 问题 | 解决方案 |
|------|----------|
| DbType.POSTGRESQL不存在 | 3.5.x版本自动检测，无需指定 |
| IPage无法转Page | Controller使用Page，Service返回IPage |

---

## 八、开发约定

### 8.1 命名约定
- 实体类：`Sys` 前缀，如 SysUser
- Mapper：`实体名 + Mapper`，如 SysUserMapper
- Service：`实体名 + Service`，如 SysUserService
- Controller：`实体名 + Controller`，如 SysUserController

### 8.2 返回类型约定
- 单条数据：`R<T>`
- 分页数据：`TableDataInfo`
- 列表数据：`R<List<T>>`

### 8.3 注解约定
- Controller：`@RestController`, `@RequestMapping`
- 接口：`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- 参数：`@RequestBody`, `@PathVariable`, `@RequestParam`

---

## 九、版本迭代计划

| 版本 | 周期 | 主要内容 |
|------|------|----------|
| v1.0.0 | 12周 | 核心框架+插件热插拔+动态编译 |
| v1.1.0 | 8周 | 零代码配置+权限体系 |
| v1.2.0 | 8周 | AI智能开发+工作流 |
| v1.3.0 | 8周 | 插件市场+监控运维 |

---

## 十、记忆更新日志

| 日期 | 更新内容 |
|------|----------|
| 2026-02-13 | 初始化记忆文件，记录核心技术要求和项目进度 |
| 2026-02-14 | 更新已完成功能：用户/角色Service和Controller已完成 |

---

## 十一、下一步开发建议

**推荐开发顺序：**

1. **菜单/部门/岗位Service+Controller**（完善系统服务基础）
2. **PF4J插件生命周期管理**（核心功能，依赖已就绪）
3. **Liquor即时编译**（核心功能，依赖已就绪）
4. **代码生成引擎**（零代码平台核心）
