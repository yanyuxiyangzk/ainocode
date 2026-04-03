`../../` > [`nocode-api-generator`](../../CLAUDE.md) > **example**

# example 模块

## 模块职责

示例项目，演示如何使用 `nocode-api-starter` 快速集成 NoCode API 功能。

## 入口与启动

**入口**: `ExampleApplication.java` (`com.nocode.example.ExampleApplication`)

```java
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

## 对外接口

该模块本身不提供额外接口，复用 `nocode-api-starter` 的所有功能。

## 关键依赖与配置

### Maven 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-admin</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

## 测试与质量

**测试状态**: 当前模块无测试目录

## 相关文件清单

```
example/src/main/java/com/nocode/example/
└── ExampleApplication.java
```

## 变更记录 (Changelog)

### 2026-03-24 - 文档初始化

- 创建 example 模块文档
