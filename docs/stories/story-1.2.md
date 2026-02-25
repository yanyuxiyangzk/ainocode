# Story 1.2: 网关服务完善

Status: Ready for Review

## Story

As a **系统管理员**,
I want **完成网关服务的路由配置、过滤器配置和限流熔断配置**,
so that **请求可以正确路由到后端服务，并具备限流熔断能力**.

## Acceptance Criteria

1. **AC1**: 网关正确路由 `/auth/**` 请求到 Auth 服务
2. **AC2**: 网关正确路由 `/system/**` 请求到 System 服务
3. **AC3**: 网关实现统一鉴权过滤器（Sa-Token）
4. **AC4**: 网关实现请求日志过滤器
5. **AC5**: 网关实现跨域配置
6. **AC6**: 网关实现限流熔断（Sentinel）
7. **AC7**: 网关健康检查端点可访问

## Tasks / Subtasks

- [x] **Task 1**: 路由配置 (AC: 1,2)
  - [x] 1.1 配置 Auth 服务路由规则
  - [x] 1.2 配置 System 服务路由规则
  - [x] 1.3 配置路由断言 (Path, Method)
  - [x] 1.4 配置路由过滤器 (StripPrefix)
  - [x] 1.5 测试路由转发正确性

- [x] **Task 2**: 鉴权过滤器 (AC: 3)
  - [x] 2.1 创建 AuthGlobalFilter 全局过滤器
  - [x] 2.2 配置白名单路径 (登录、登出、验证码等)
  - [x] 2.3 集成 Sa-Token 验证 Token
  - [x] 2.4 从 Token 解析用户信息存入请求头
  - [x] 2.5 测试鉴权逻辑

- [x] **Task 3**: 请求日志过滤器 (AC: 4)
  - [x] 3.1 创建 RequestLogFilter 全局过滤器
  - [x] 3.2 记录请求路径、方法、参数
  - [x] 3.3 记录响应状态码、耗时
  - [x] 3.4 测试日志输出

- [x] **Task 4**: 跨域配置 (AC: 5)
  - [x] 4.1 创建 CorsConfig 配置类
  - [x] 4.2 配置允许的域名、方法、头
  - [x] 4.3 配置是否允许携带凭证
  - [x] 4.4 测试跨域请求

- [x] **Task 5**: 限流熔断配置 (AC: 6)
  - [x] 5.1 添加 sentinel-spring-cloud-gateway-adapter 依赖
  - [x] 5.2 配置 Sentinel 网关限流规则
  - [x] 5.3 配置降级响应
  - [x] 5.4 测试限流熔断效果

- [x] **Task 6**: 健康检查端点 (AC: 7)
  - [x] 6.1 添加 spring-boot-starter-actuator 依赖
  - [x] 6.2 配置 health 端点暴露
  - [x] 6.3 测试 `/actuator/health` 访问

## Dev Notes

### 技术约束

- **Spring Cloud Gateway**: 4.1.5
- **Sa-Token**: 1.37.0
- **Sentinel**: 与 Spring Cloud Alibaba 2023.0.3.2 版本兼容

### 路由配置示例

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ruoyi-auth
          uri: lb://ruoyi-nocode-auth
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
        - id: ruoyi-system
          uri: lb://ruoyi-nocode-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
```

### 鉴权过滤器核心逻辑

```java
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        
        // Token 验证
        String token = request.getHeaders().getFirst("Authorization");
        if (StrUtil.isEmpty(token)) {
            return unauthorized(exchange);
        }
        
        // Sa-Token 验证
        Object loginId = StpUtil.getLoginIdByToken(token.replace("Bearer ", ""));
        if (loginId == null) {
            return unauthorized(exchange);
        }
        
        // 传递用户信息
        ServerHttpRequest newRequest = request.mutate()
            .header("X-User-Id", loginId.toString())
            .build();
        
        return chain.filter(exchange.mutate().request(newRequest).build());
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}
```

### Project Structure Notes

- 过滤器位置: `com.ruoyi.nocode.gateway.filter`
- 配置类位置: `com.ruoyi.nocode.gateway.config`
- 白名单配置: 在 Nacos 配置中心管理

### References

- [Source: docs/solution-architecture.md#服务架构]
- [Source: docs/architecture/技术架构设计.md#网关层]
- [Source: docs/architecture/接口设计.md#接口安全]
- [Source: .iflow/PROJECT_RULES.md#技术栈要求]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 1.2 所有任务

### Completion Notes List

1. **路由配置**: application.yml 已配置 Auth 和 System 服务路由规则
2. **鉴权过滤器**: 创建 AuthGlobalFilter，集成 Sa-Token，支持白名单配置
3. **白名单配置**: 创建 AuthWhiteListProperties，支持从 Nacos 动态刷新
4. **日志过滤器**: 创建 RequestLogFilter，记录请求/响应信息
5. **跨域配置**: 创建 CorsConfig，支持前端跨域访问
6. **限流熔断**: 创建 SentinelConfig，配置自定义限流响应

### File List

- `ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/filter/AuthGlobalFilter.java`
- `ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/filter/RequestLogFilter.java`
- `ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/AuthWhiteListProperties.java`
- `ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/CorsConfig.java`
- `ruoyi-nocode-gateway/src/main/java/com/ruoyi/nocode/gateway/config/SentinelConfig.java`

