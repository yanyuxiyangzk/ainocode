# Story 2.2: 插件生命周期管理

Status: Ready for Review

## Story

As a **系统管理员**,
I want **通过管理界面管理插件的生命周期**,
so that **我可以动态安装、卸载、启用、停用插件**.

## Acceptance Criteria

1. **AC1**: 可以上传插件 JAR 包
2. **AC2**: 可以安装插件
3. **AC3**: 可以卸载插件
4. **AC4**: 可以启用/停用插件
5. **AC5**: 插件状态持久化到数据库
6. **AC6**: 插件操作日志记录

## Tasks / Subtasks

- [x] **Task 1**: 插件管理 API (AC: 2,3,4)
  - [x] 1.1 创建插件列表接口
  - [x] 1.2 创建插件安装接口
  - [x] 1.3 创建插件卸载接口
  - [x] 1.4 创建插件启用/停用接口
  - [x] 1.5 创建插件详情接口

- [x] **Task 2**: 插件上传 (AC: 1)
  - [x] 2.1 创建插件上传接口
  - [x] 2.2 验证 JAR 包格式
  - [x] 2.3 存储到插件目录
  - [x] 2.4 解析 plugin.properties

- [x] **Task 3**: 数据库持久化 (AC: 5)
  - [x] 3.1 创建 plugin_info 表
  - [x] 3.2 创建 PluginInfo 实体
  - [x] 3.3 创建 PluginInfoMapper
  - [x] 3.4 创建 PluginInfoService
  - [x] 3.5 同步插件状态到数据库

- [x] **Task 4**: 操作日志 (AC: 6)
  - [x] 4.1 创建 plugin_log 表
  - [x] 4.2 创建 PluginLog 实体
  - [x] 4.3 创建 PluginLogMapper
  - [x] 4.4 创建 PluginLogService
  - [x] 4.5 记录所有操作日志

- [x] **Task 5**: 异常处理 (AC: 1-6)
  - [x] 5.1 插件安装失败处理
  - [x] 5.2 插件依赖冲突处理
  - [x] 5.3 插件版本兼容检查

## Dev Notes

### 数据库表结构

```sql
CREATE TABLE nocode_plugin_info (
    id BIGSERIAL PRIMARY KEY,
    plugin_id VARCHAR(100) NOT NULL,
    plugin_name VARCHAR(200) NOT NULL,
    plugin_version VARCHAR(50) NOT NULL,
    plugin_path VARCHAR(500),
    plugin_class VARCHAR(500),
    state VARCHAR(20) DEFAULT 'CREATED',
    description VARCHAR(500),
    author VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE nocode_plugin_log (
    id BIGSERIAL PRIMARY KEY,
    plugin_id VARCHAR(100) NOT NULL,
    operation VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message TEXT,
    operator VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/system/plugin/list` | GET | 插件列表 |
| `/system/plugin/upload` | POST | 上传插件 |
| `/system/plugin/install/{pluginId}` | POST | 安装插件 |
| `/system/plugin/uninstall/{pluginId}` | POST | 卸载插件 |
| `/system/plugin/start/{pluginId}` | POST | 启用插件 |
| `/system/plugin/stop/{pluginId}` | POST | 停用插件 |

### Project Structure Notes

- Controller: `com.ruoyi.nocode.system.controller.SysPluginController`
- Service: `com.ruoyi.nocode.system.service.ISysPluginService`
- Entity: `com.ruoyi.nocode.system.entity.PluginInfo`
- Mapper: `com.ruoyi.nocode.system.mapper.PluginInfoMapper`

### References

- [Source: .iflow/PROJECT_RULES.md#迭代2]
- [Source: docs/solution-architecture.md#插件管理]

## Dev Agent Record

### Context Reference

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 2.2 所有任务

### Completion Notes List

1. **插件管理 API**: SysPluginController 提供完整的 CRUD 和生命周期管理
2. **插件上传**: 支持上传 JAR 文件并自动安装
3. **数据库持久化**: PluginInfo/PluginLog 实体和 Mapper 已实现
4. **操作日志**: 所有操作记录到 plugin_log 表
5. **异常处理**: 完整的异常捕获和状态更新

### File List

- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/controller/SysPluginController.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/ISysPluginService.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/impl/SysPluginServiceImpl.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/PluginInfo.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/PluginLog.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/PluginDependency.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/PluginInfoMapper.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/PluginLogMapper.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/PluginDependencyMapper.java`
- `sql/nocode_plugin.sql`
