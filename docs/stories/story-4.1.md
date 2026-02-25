# Story 4.1: 代码生成引擎集成

Status: Ready for Review

## Story

As a **系统开发者**,
I want **完成代码生成引擎的集成，支持从数据库表自动生成CRUD代码**,
so that **我可以快速生成标准化的业务代码**.

## Acceptance Criteria

1. **AC1**: 表结构解析功能可用
2. **AC2**: Velocity 模板引擎集成
3. **AC3**: 支持生成后端代码（Entity/Mapper/Service/Controller）
4. **AC4**: 支持生成前端代码（Vue3）
5. **AC5**: 支持代码预览和下载

## Tasks / Subtasks

- [x] **Task 1**: 表结构解析 (AC: 1)
  - [x] 1.1 创建 ITableParseService 接口
  - [x] 1.2 实现 PostgreSQL 表结构解析
  - [x] 1.3 实现字段类型映射
  - [x] 1.4 实现主键/索引识别

- [x] **Task 2**: Velocity 模板集成 (AC: 2)
  - [x] 2.1 添加 Velocity 依赖
  - [x] 2.2 创建模板目录结构
  - [x] 2.3 创建 Entity 模板
  - [x] 2.4 创建 Mapper 模板
  - [x] 2.5 创建 Service 模板
  - [x] 2.6 创建 Controller 模板

- [x] **Task 3**: 后端代码生成 (AC: 3)
  - [x] 3.1 创建 ICodeGenService 接口
  - [x] 3.2 实现 Entity 生成逻辑
  - [x] 3.3 实现 Mapper 生成逻辑
  - [x] 3.4 实现 Service 生成逻辑
  - [x] 3.5 实现 Controller 生成逻辑

- [x] **Task 4**: 前端代码生成 (AC: 4)
  - [x] 4.1 创建 Vue3 列表页模板
  - [x] 4.2 创建 Vue3 表单页模板
  - [x] 4.3 创建 API 接口文件模板
  - [x] 4.4 实现前端代码生成逻辑

- [x] **Task 5**: 代码预览和下载 (AC: 5)
  - [x] 5.1 创建代码预览接口
  - [x] 5.2 创建代码下载接口
  - [x] 5.3 创建 GenCodeController
  - [x] 5.4 创建代码生成管理界面

## Dev Notes

### 技术约束

- **Velocity**: 2.3
- **MyBatis-Plus**: 3.5.7
- **Vue3**: 3.4.x

### 数据库表结构

```sql
CREATE TABLE gen_table (
    table_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(200) NOT NULL,
    table_comment VARCHAR(500),
    class_name VARCHAR(200),
    package_name VARCHAR(200),
    module_name VARCHAR(100),
    business_name VARCHAR(100),
    function_name VARCHAR(200),
    gen_type VARCHAR(20) DEFAULT '0',
    gen_path VARCHAR(500),
    pk_column VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gen_table_column (
    column_id BIGSERIAL PRIMARY KEY,
    table_id BIGINT NOT NULL,
    column_name VARCHAR(200),
    column_comment VARCHAR(500),
    column_type VARCHAR(200),
    java_type VARCHAR(200),
    java_field VARCHAR(200),
    is_pk VARCHAR(1) DEFAULT '0',
    is_increment VARCHAR(1) DEFAULT '0',
    is_required VARCHAR(1) DEFAULT '0',
    is_insert VARCHAR(1) DEFAULT '1',
    is_edit VARCHAR(1) DEFAULT '1',
    is_list VARCHAR(1) DEFAULT '1',
    is_query VARCHAR(1) DEFAULT '0',
    query_type VARCHAR(200) DEFAULT 'EQ',
    html_type VARCHAR(200) DEFAULT 'input',
    dict_type VARCHAR(200),
    sort INT DEFAULT 0
);
```

### 生成代码示例

**Entity:**
```java
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String status;
}
```

**Mapper:**
```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

**Service:**
```java
public interface ISysUserService extends IService<SysUser> {
}
```

**Controller:**
```java
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;
    
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        return getDataTable(userService.list());
    }
}
```

### Project Structure Notes

- 服务接口: `com.ruoyi.nocode.system.service.IGenCodeService`
- 模板目录: `resources/templates/velocity`
- 控制器: `com.ruoyi.nocode.system.controller.GenCodeController`

### References

- [Source: .iflow/PROJECT_RULES.md#迭代5]
- [Source: docs/solution-architecture.md#代码生成]

## Dev Agent Record

### Context Reference

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 4.1 所有任务

### Completion Notes List

1. **实体类**: GenTable + GenTableColumn 定义生成表结构
2. **服务接口**: IGenCodeService 提供代码生成 API
3. **Velocity 模板**: Entity/Mapper/Service/Controller 模板
4. **控制器**: GenCodeController 提供预览/下载/生成接口
5. **表结构解析**: 支持从数据库导入表结构

### File List

- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/GenTable.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/entity/GenTableColumn.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/service/IGenCodeService.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/GenTableMapper.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/mapper/GenTableColumnMapper.java`
- `ruoyi-nocode-system/src/main/java/com/ruoyi/nocode/system/controller/GenCodeController.java`
- `ruoyi-nocode-system/src/main/resources/templates/velocity/entity.java.vm`
- `ruoyi-nocode-system/src/main/resources/templates/velocity/mapper.java.vm`
- `ruoyi-nocode-system/src/main/resources/templates/velocity/service.java.vm`
- `ruoyi-nocode-system/src/main/resources/templates/velocity/serviceImpl.java.vm`
- `ruoyi-nocode-system/src/main/resources/templates/velocity/controller.java.vm`
