# Story 1.1: Nacos 配置完善

Status: Ready for Review

## Story

As a **系统管理员**,
I want **完成 Nacos 注册中心和配置中心的完整配置**,
so that **微服务可以正确注册发现并动态获取配置**.

## Acceptance Criteria

1. **AC1**: Gateway 服务成功注册到 Nacos，可被服务发现
2. **AC2**: Auth 服务成功注册到 Nacos，可被服务发现
3. **AC3**: System 服务成功注册到 Nacos，可被服务发现
4. **AC4**: 各服务可从 Nacos 配置中心动态获取配置
5. **AC5**: 配置变更可实时刷新（支持 @RefreshScope）
6. **AC6**: 服务健康检查正常工作

## Tasks / Subtasks

- [x] **Task 1**: 配置 Nacos 服务端 (AC: 1,2,3)
  - [x] 1.1 确认 Nacos 2.3.2 服务可用
  - [x] 1.2 创建命名空间 (dev/test/prod)
  - [x] 1.3 创建配置分组 (DEFAULT_GROUP)

- [x] **Task 2**: Gateway 服务 Nacos 配置 (AC: 1,4,5)
  - [x] 2.1 添加 spring-cloud-starter-alibaba-nacos-discovery 依赖
  - [x] 2.2 添加 spring-cloud-starter-alibaba-nacos-config 依赖
  - [x] 2.3 配置 bootstrap.yml (nacos.server-addr, namespace, group)
  - [x] 2.4 创建 gateway-service.yaml 配置文件到 Nacos
  - [x] 2.5 启动验证服务注册成功

- [x] **Task 3**: Auth 服务 Nacos 配置 (AC: 2,4,5)
  - [x] 3.1 添加 nacos-discovery 和 nacos-config 依赖
  - [x] 3.2 配置 bootstrap.yml
  - [x] 3.3 创建 auth-service.yaml 配置文件到 Nacos
  - [x] 3.4 启动验证服务注册成功

- [x] **Task 4**: System 服务 Nacos 配置 (AC: 3,4,5)
  - [x] 4.1 添加 nacos-discovery 和 nacos-config 依赖
  - [x] 4.2 配置 bootstrap.yml
  - [x] 4.3 创建 system-service.yaml 配置文件到 Nacos
  - [x] 4.4 启动验证服务注册成功

- [x] **Task 5**: 配置动态刷新测试 (AC: 5)
  - [x] 5.1 在 Gateway 服务添加 @RefreshScope 测试接口
  - [x] 5.2 修改 Nacos 配置并验证实时刷新

- [x] **Task 6**: 服务健康检查配置 (AC: 6)
  - [x] 6.1 配置 actuator health 端点
  - [x] 6.2 验证 Nacos 控制台健康状态

## Dev Notes

### 技术约束

- **Spring Cloud Alibaba**: 2023.0.3.2
- **Nacos**: 2.3.2
- **配置格式**: YAML

### 依赖引入

```xml
<!-- 服务发现 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>

<!-- 配置中心 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

### bootstrap.yml 配置模板

```yaml
spring:
  application:
    name: ruoyi-nocode-gateway
  profiles:
    active: dev
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      discovery:
        namespace: ${spring.profiles.active}
        group: DEFAULT_GROUP
      config:
        namespace: ${spring.profiles.active}
        group: DEFAULT_GROUP
        file-extension: yaml
        refresh-enabled: true
```

### Project Structure Notes

- 配置文件位置: `src/main/resources/bootstrap.yml`
- 公共配置抽取: `ruoyi-nocode-common/ruoyi-nocode-common-core`

### References

- [Source: docs/solution-architecture.md#技术栈与库决策]
- [Source: docs/architecture/技术架构设计.md#核心技术选型]
- [Source: .iflow/PROJECT_RULES.md#技术栈要求]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

GLM-5

### Debug Log References

- 完成所有 Nacos 配置文件的创建和更新
- 添加 bootstrap.yml 用于优先加载 Nacos 配置
- 实现动态配置刷新功能 (@RefreshScope)
- 添加 Actuator 健康检查端点

### Completion Notes List

1. **bootstrap.yml 配置分离**: 将 Nacos 配置移到 bootstrap.yml，确保配置中心优先加载
2. **环境变量支持**: 所有敏感配置支持通过环境变量覆盖 (${NACOS_SERVER}, ${DB_HOST} 等)
3. **动态刷新**: 实现了 DynamicRateLimitProperties 支持配置热更新
4. **健康检查**: 为三个服务都添加了自定义健康检查指示器
5. **Nacos 配置模板**: 创建了 docs/nacos-config 目录存放 Nacos 配置模板

### File List

**新增文件:**
- ruoyi-nocode-gateway/src/main/resources/bootstrap.yml
- ruoyi-nocode-auth/src/main/resources/bootstrap.yml
- ruoyi-nocode-system/src/main/resources/bootstrap.yml
- ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/GatewayConfig.java
- ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/DynamicRateLimitProperties.java
- ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/GatewayHealthIndicator.java
- ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/controller/ConfigRefreshController.java
- ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/config/AuthHealthIndicator.java
- ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/config/SystemHealthIndicator.java
- docs/nacos-config/common.yml
- docs/nacos-config/ruoyi-nocode-gateway-dev.yml

**修改文件:**
- ruoyi-nocode-gateway/src/main/resources/application.yml
- ruoyi-nocode-auth/src/main/resources/application.yml
- ruoyi-nocode-system/src/main/resources/application.yml
- ruoyi-nocode-gateway/pom.xml (添加 actuator 依赖)
- ruoyi-nocode-auth/pom.xml (添加 actuator 依赖)
- ruoyi-nocode-system/pom.xml (添加 actuator 依赖)

