# Story 3.1: Liquor 动态编译集成

Status: Ready for Review

## Story

As a **系统开发者**,
I want **完成 Liquor 动态编译框架的集成**,
so that **系统可以在运行时动态编译和执行 Java 代码**.

## Acceptance Criteria

1. **AC1**: Liquor 依赖正确引入 (org.noear:liquor:1.6.3)
2. **AC2**: 动态编译器可正常工作
3. **AC3**: 支持编译 Java 源代码字符串
4. **AC4**: 支持动态加载和执行编译后的类
5. **AC5**: 编译结果缓存

## Tasks / Subtasks

- [x] **Task 1**: 依赖配置 (AC: 1)
  - [x] 1.1 添加 Liquor 依赖到 pom.xml
  - [x] 1.2 验证依赖冲突解决

- [x] **Task 2**: 编译服务实现 (AC: 2,3)
  - [x] 2.1 创建 ILiquorCompilerService 接口
  - [x] 2.2 创建 LiquorCompilerServiceImpl 实现
  - [x] 2.3 实现源代码编译方法
  - [x] 2.4 实现编译错误处理

- [x] **Task 3**: 类加载管理 (AC: 4)
  - [x] 3.1 创建动态类加载器
  - [x] 3.2 实现类实例化
  - [x] 3.3 实现方法调用
  - [x] 3.4 实现类热替换

- [x] **Task 4**: 编译缓存 (AC: 5)
  - [x] 4.1 创建编译结果缓存
  - [x] 4.2 实现缓存过期策略
  - [x] 4.3 实现缓存清理

- [x] **Task 5**: 数据库持久化 (AC: 5)
  - [x] 5.1 创建 dynamic_code 表
  - [x] 5.2 创建 DynamicCode 实体
  - [x] 5.3 创建 DynamicCodeMapper
  - [x] 5.4 创建 DynamicCodeService

## Dev Notes

### 技术约束

- **Liquor**: org.noear:liquor:1.6.3
- **JDK**: 17+

### Liquor 使用示例

```java
// 创建编译器
JavaCompiler compiler = new JavaCompiler();

// 编译源代码
Class<?> clazz = compiler.compile(
    "com.example.Demo",
    "package com.example; public class Demo { public String hello() { return \"Hello\"; } }"
);

// 实例化并调用
Object instance = clazz.getDeclaredConstructor().newInstance();
Method method = clazz.getMethod("hello");
String result = (String) method.invoke(instance);
```

### 数据库表结构

```sql
CREATE TABLE nocode_dynamic_code (
    id BIGSERIAL PRIMARY KEY,
    code_name VARCHAR(200) NOT NULL,
    code_class VARCHAR(500) NOT NULL,
    code_source TEXT NOT NULL,
    code_type VARCHAR(50) DEFAULT 'JAVA',
    status VARCHAR(20) DEFAULT 'DRAFT',
    compile_status VARCHAR(20) DEFAULT 'PENDING',
    compile_error TEXT,
    version INT DEFAULT 1,
    create_by VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(100),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Project Structure Notes

- 服务接口: `com.ruoyi.nocode.system.service.ILiquorCompilerService`
- 服务实现: `com.ruoyi.nocode.system.service.impl.LiquorCompilerServiceImpl`
- 实体类: `com.ruoyi.nocode.system.entity.DynamicCode`

### References

- [Source: .iflow/PROJECT_RULES.md#技术栈要求]
- [Source: docs/solution-architecture.md#动态编译]

## Dev Agent Record

### Context Reference

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 3.1 所有任务

### Completion Notes List

1. **Liquor 集成**: org.noear:liquor:1.6.3 已配置
2. **编译服务**: ILiquorCompilerService 提供完整编译和执行能力
3. **热替换**: 支持 HotReloadableClassLoader 实现类热替换
4. **沙箱执行**: 支持在安全沙箱环境中执行动态代码
5. **数据库持久化**: DynamicCode 实体支持编译结果存储

### File List

- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/ILiquorCompilerService.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/impl/LiquorCompilerServiceImpl.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/IDynamicCodeService.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/impl/DynamicCodeServiceImpl.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/controller/DynamicCodeController.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/DynamicCode.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/DynamicCodeMapper.java`
- `sql/nocode_dynamic_code.sql`
