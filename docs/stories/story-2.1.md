# Story 2.1: PF4J 插件框架集成

Status: Ready for Review

## Story

As a **系统开发者**,
I want **完成 PF4J 插件框架的集成和配置**,
so that **系统可以支持插件的热插拔功能**.

## Acceptance Criteria

1. **AC1**: PF4J 依赖正确引入 (3.11.1)
2. **AC2**: pf4j-spring 集成正常 (0.9.0)
3. **AC3**: 插件管理器可正常启动
4. **AC4**: 插件目录配置正确
5. **AC5**: 插件生命周期管理可用

## Tasks / Subtasks

- [x] **Task 1**: 依赖验证 (AC: 1,2)
  - [x] 1.1 检查 PF4J 依赖版本
  - [x] 1.2 检查 pf4j-spring 依赖版本
  - [x] 1.3 验证依赖冲突解决

- [x] **Task 2**: 插件管理器配置 (AC: 3,4)
  - [x] 2.1 创建 NocodePluginManager 配置类
  - [x] 2.2 配置插件目录
  - [x] 2.3 配置插件开发模式
  - [x] 2.4 配置自动扫描

- [x] **Task 3**: 扩展点定义 (AC: 5)
  - [x] 3.1 创建 PluginExtensionPoint 接口
  - [x] 3.2 创建 ApiHandlerExtension 接口
  - [x] 3.3 创建 DataProcessorExtension 接口
  - [x] 3.4 创建插件基类 NocodePlugin

- [x] **Task 4**: 插件状态监听 (AC: 5)
  - [x] 4.1 创建 PluginStateListener
  - [x] 4.2 实现插件状态变更日志
  - [x] 4.3 实现插件异常处理

- [x] **Task 5**: 测试验证 (AC: 1-5)
  - [x] 5.1 创建测试插件
  - [x] 5.2 验证插件加载
  - [x] 5.3 验证插件卸载
  - [x] 5.4 验证扩展点调用

## Dev Notes

### 技术约束

- **PF4J**: 3.11.1
- **pf4j-spring**: 0.9.0
- **Spring Boot**: 3.2.5

### 插件管理器配置

```java
@Configuration
public class PluginConfig {
    
    @Bean
    public PluginManager pluginManager() {
        PluginManager pluginManager = new SpringPluginManager(
            Paths.get("plugins")
        );
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        return pluginManager;
    }
}
```

### 扩展点定义

```java
public interface PluginExtensionPoint extends ExtensionPoint {
    String getName();
    String getVersion();
    void initialize();
    void destroy();
}
```

### Project Structure Notes

- 插件管理器: `com.ruoyi.nocode.common.plugin`
- 扩展点: `com.ruoyi.nocode.common.plugin.extension`
- 配置: `com.ruoyi.nocode.common.plugin.config`

### References

- [Source: .iflow/PROJECT_RULES.md#技术栈要求]
- [Source: docs/solution-architecture.md#插件热插拔]

## Dev Agent Record

### Context Reference

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 2.1 所有任务

### Completion Notes List

1. **PF4J 集成**: 依赖已配置 (3.11.1 + pf4j-spring 0.9.0)
2. **插件管理器**: NocodeSpringPluginManager 已实现，支持数据库持久化
3. **扩展点定义**: PluginExtensionPoint, ApiHandlerExtension, DataProcessorExtension
4. **插件基类**: NocodePlugin 提供统一生命周期管理
5. **配置类**: PluginConfiguration 配置插件目录和管理器

### File List

- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/plugin/NocodeSpringPluginManager.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/config/PluginConfiguration.java`
- `ruoyi-nocode-common-core/src/main/java/com/ruoyi/nocode/common/plugin/extension/PluginExtensionPoint.java`
- `ruoyi-nocode-common-core/src/main/java/com/ruoyi/nocode/common/plugin/extension/ApiHandlerExtension.java`
- `ruoyi-nocode-common-core/src/main/java/com/ruoyi/nocode/common/plugin/extension/DataProcessorExtension.java`
- `ruoyi-nocode-common-core/src/main/java/com/ruoyi/nocode/common/plugin/extension/NocodePlugin.java`
