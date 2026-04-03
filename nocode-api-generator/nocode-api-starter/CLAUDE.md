`../../` > [`nocode-api-generator`](../../CLAUDE.md) > **nocode-api-starter**

# nocode-api-starter 模块

## 模块职责

Spring Boot Starter 模块，提供：
- 自动配置（`NocodeApiConfiguration`）
- 启用注解（`@EnableNocodeApi`）
- CORS 跨域配置
- 核心模块的依赖整合

## 入口与启动

### 启用方式

**方式一：使用注解（推荐）**

```java
@EnableNocodeApi(enableAdmin = true)
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

**方式二：配置类**

```java
@Configuration
@EnableConfigurationProperties(NocodeApiProperties.class)
@ConditionalOnProperty(prefix = "nocode.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.nocode.core")
public class NocodeApiConfiguration {
    // ...
}
```

## 对外接口

### 核心组件

| 类 | 路径 | 职责 |
|----|------|------|
| `EnableNocodeApi` | `EnableNocodeApi.java` | 启用注解 |
| `NocodeApiConfiguration` | `NocodeApiConfiguration.java` | 自动配置类 |

### EnableNocodeApi 注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({NocodeApiConfiguration.class})
public @interface EnableNocodeApi {
    /**
     * 是否自动启动管理界面
     */
    boolean enableAdmin() default true;
}
```

### NocodeApiConfiguration 配置

当 `nocode.api.enabled=true` 时自动开启，提供：

- `DatasourceRegistry` Bean（无则创建）
- `CorsFilter` Bean（全局跨域配置）

```java
@Configuration
@EnableConfigurationProperties(NocodeApiProperties.class)
@ConditionalOnProperty(prefix = "nocode.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.nocode.core")
public class NocodeApiConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DatasourceRegistry datasourceRegistry() {
        return new DatasourceRegistry(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        // CORS 配置
    }
}
```

## 关键依赖与配置

### Maven 依赖

```xml
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-core</artifactId>
</dependency>
```

### 配置属性

```yaml
nocode:
  api:
    enabled: true          # 默认 true
    admin:
      enabled: true       # 是否启用管理界面
    datasources: []       # 数据源配置
```

## 测试与质量

**测试状态**: 当前模块无测试目录

## 常见问题 (FAQ)

**Q: 为什么 starter 不直接依赖 admin 模块？**
A: 为避免循环依赖。admin 模块依赖 starter，而非相反。集成时使用 `nocode-api-admin` jar 包。

**Q: 如何禁用自动配置？**
A: 设置 `nocode.api.enabled=false`

## 相关文件清单

```
nocode-api-starter/src/main/java/com/nocode/starter/
├── EnableNocodeApi.java              # 启用注解
└── NocodeApiConfiguration.java       # 自动配置类
```

## 变更记录 (Changelog)

### 2026-03-24 - 文档初始化

- 创建 nocode-api-starter 模块文档
- 梳理自动配置机制
