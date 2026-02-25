package com.nocode.admin.controller;

import com.nocode.admin.entity.DatasourceConfigEntity;
import com.nocode.admin.service.DatasourceConfigService;
import com.nocode.admin.service.DiagramLayoutService;
import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.datasource.DatasourceRegistry;
import com.nocode.core.entity.DatasourceConfig;
import com.nocode.core.entity.ForeignKeyInfo;
import com.nocode.core.entity.TableInfo;
import com.nocode.core.executor.SqlExecutor;
import com.nocode.core.metadata.MetadataCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理界面控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final DatasourceRegistry registry;
    private final MetadataCache metadataCache;
    private final NocodeApiProperties properties;
    private final DatasourceConfigService configService;
    private final SqlExecutor sqlExecutor;
    private final DiagramLayoutService diagramLayoutService;
    private final ObjectMapper objectMapper;

    public AdminController(DatasourceRegistry registry,
                           MetadataCache metadataCache,
                           NocodeApiProperties properties,
                           DatasourceConfigService configService,
                           SqlExecutor sqlExecutor,
                           DiagramLayoutService diagramLayoutService,
                           ObjectMapper objectMapper) {
        this.registry = registry;
        this.metadataCache = metadataCache;
        this.properties = properties;
        this.configService = configService;
        this.sqlExecutor = sqlExecutor;
        this.diagramLayoutService = diagramLayoutService;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "NoCode API Generator");
        info.put("version", "1.0.0");
        info.put("datasourceCount", registry.getDatasourceNames().size());
        info.put("cachedTableCount", metadataCache.getCachedDatasourceCount());
        info.put("apiEnabled", properties.isEnabled());
        info.put("adminEnabled", properties.isAdminEnabled());
        return ResponseEntity.ok(info);
    }

    /**
     * 获取所有数据源
     */
    @GetMapping("/datasources")
    public ResponseEntity<List<Map<String, Object>>> getDatasources() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : registry.getDatasourceNames()) {
            DatasourceConfig config = registry.getDatasourceConfig(name);
            if (config == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("name", config.getName());
            item.put("jdbcUrl", config.getJdbcUrl());
            item.put("driverClassName", config.getDriverClassName());
            item.put("enabled", config.isEnabled());
            item.put("tableCount", metadataCache.getCachedTableCount(name));
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 添加数据源
     */
    @PostMapping("/datasources")
    public ResponseEntity<Map<String, Object>> addDatasource(@RequestBody DatasourceConfig config) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!StringUtils.hasText(config.getName())) {
                result.put("success", false);
                result.put("message", "数据源名称不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            if (registry.containsDatasource(config.getName())) {
                result.put("success", false);
                result.put("message", "数据源名称已存在: " + config.getName());
                return ResponseEntity.badRequest().body(result);
            }

            // 保存到数据库
            configService.save(config);
            log.info("数据源配置已保存到数据库: {}", config.getName());

            // 注册到内存
            registry.registerDatasource(config);
            log.info("数据源已注册到内存: {}", config.getName());

            // 清除旧缓存并重新加载
            metadataCache.refreshDatasource(config, registry.getDatasource(config.getName()));
            log.info("数据源缓存已刷新: {}", config.getName());

            result.put("success", true);
            result.put("message", "数据源添加成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("添加数据源失败", e);
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/datasources/{name}")
    public ResponseEntity<Map<String, Object>> deleteDatasource(@PathVariable String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 从数据库删除
            configService.deleteByName(name);
            // 从内存注销
            registry.unregisterDatasource(name);
            result.put("success", true);
            result.put("message", "数据源删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除数据源失败", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 获取数据源下的表列表
     */
    @GetMapping("/datasources/{name}/tables")
    public ResponseEntity<List<String>> getTables(@PathVariable String name) {
        log.info("getTables 请求: datasource={}", name);
        if (!registry.containsDatasource(name)) {
            log.warn("数据源不存在: {}", name);
            return ResponseEntity.notFound().build();
        }
        DatasourceConfig config = registry.getDatasourceConfig(name);
        log.info("获取到数据源配置: {}", config.getName());
        List<String> tables = metadataCache.getTables(config, registry.getDatasource(name));
        log.info("获取到表列表: {} 个", tables.size());
        return ResponseEntity.ok(tables);
    }

    /**
     * 获取数据源下的模式列表
     */
    @GetMapping("/datasources/{name}/schemas")
    public ResponseEntity<List<String>> getSchemas(@PathVariable String name) {
        log.info("getSchemas 请求: datasource={}", name);
        if (!registry.containsDatasource(name)) {
            log.warn("数据源不存在: {}", name);
            return ResponseEntity.notFound().build();
        }
        try {
            DataSource ds = registry.getDatasource(name);
            DatasourceConfig config = registry.getDatasourceConfig(name);
            List<String> schemas = metadataCache.getSchemas(config, ds);
            log.info("获取到模式列表: {} 个", schemas.size());
            return ResponseEntity.ok(schemas);
        } catch (Exception e) {
            log.error("获取模式列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取指定模式下的表列表
     */
    @GetMapping("/datasources/{name}/schemas/{schema}/tables")
    public ResponseEntity<List<String>> getTablesBySchema(@PathVariable String name,
                                                          @PathVariable String schema) {
        log.info("getTablesBySchema 请求: datasource={}, schema={}", name, schema);
        if (!registry.containsDatasource(name)) {
            log.warn("数据源不存在: {}", name);
            return ResponseEntity.notFound().build();
        }
        try {
            DataSource ds = registry.getDatasource(name);
            DatasourceConfig config = registry.getDatasourceConfig(name);
            List<String> tables = metadataCache.getTablesBySchema(config, ds, schema);
            log.info("获取到表列表: {} 个", tables.size());
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            log.error("获取表列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取表结构信息
     */
    @GetMapping("/datasources/{name}/tables/{table}")
    public ResponseEntity<TableInfo> getTableInfo(@PathVariable String name,
                                                   @PathVariable String table,
                                                   @RequestParam(required = false) String schema) {
        log.info("getTableInfo 请求: datasource={}, table={}, schema={}", name, table, schema);
        if (!registry.containsDatasource(name)) {
            log.warn("数据源不存在: {}", name);
            return ResponseEntity.notFound().build();
        }
        DatasourceConfig config = registry.getDatasourceConfig(name);
        log.info("开始获取表结构: {}", table);
        TableInfo tableInfo = metadataCache.getTableInfo(config, registry.getDatasource(name), table, schema);
        log.info("表结构获取成功: {}, 字段数={}", table, tableInfo.getColumns() != null ? tableInfo.getColumns().size() : 0);
        return ResponseEntity.ok(tableInfo);
    }

    /**
     * 刷新数据源缓存
     */
    @PostMapping("/datasources/{name}/refresh")
    public ResponseEntity<Map<String, Object>> refreshDatasource(@PathVariable String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!registry.containsDatasource(name)) {
                result.put("success", false);
                result.put("message", "数据源不存在: " + name);
                return ResponseEntity.notFound().build();
            }
            DatasourceConfig config = registry.getDatasourceConfig(name);
            metadataCache.refreshDatasource(config, registry.getDatasource(name));
            result.put("success", true);
            result.put("message", "刷新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("刷新数据源失败", e);
            result.put("success", false);
            result.put("message", "刷新失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 执行SQL语句
     */
    @PostMapping("/datasources/{name}/execute")
    public ResponseEntity<Map<String, Object>> executeSql(@PathVariable String name,
                                                          @RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!registry.containsDatasource(name)) {
                result.put("success", false);
                result.put("message", "数据源不存在: " + name);
                return ResponseEntity.notFound().build();
            }
            String sql = body.get("sql");
            if (!StringUtils.hasText(sql)) {
                result.put("success", false);
                result.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            com.nocode.core.entity.ApiResult execResult = sqlExecutor.execute(name, sql);
            Map<String, Object> response = new HashMap<>();
            response.put("success", execResult.isSuccess());
            response.put("message", execResult.getMessage());
            response.put("data", execResult.getData());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("执行SQL失败", e);
            result.put("success", false);
            result.put("message", "执行失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 生成API文档
     */
    @GetMapping("/datasources/{name}/tables/{table}/api-doc")
    public ResponseEntity<Map<String, Object>> getApiDoc(@PathVariable String name,
                                                          @PathVariable String table) {
        if (!registry.containsDatasource(name)) {
            return ResponseEntity.notFound().build();
        }
        DatasourceConfig config = registry.getDatasourceConfig(name);
        TableInfo tableInfo = metadataCache.getTableInfo(config, registry.getDatasource(name), table);

        Map<String, Object> doc = new HashMap<>();
        doc.put("tableName", tableInfo.getTableName());
        doc.put("tableComment", tableInfo.getComment());
        doc.put("primaryKey", tableInfo.getPrimaryKeyColumn());
        doc.put("primaryKeyType", tableInfo.getPrimaryKeyType());

        // API列表
        List<Map<String, Object>> apis = new ArrayList<>();
        String basePath = "/api/" + name + "/" + table;

        // 查询列表
        Map<String, Object> listApi = new HashMap<>();
        listApi.put("method", "GET");
        listApi.put("path", basePath);
        listApi.put("description", "查询列表（分页）");
        listApi.put("parameters", new String[]{"page: 页码", "size: 每页数量", "orderBy: 排序字段", "orderDirection: 排序方向", "where: 查询条件"});
        apis.add(listApi);

        // 查询单条
        Map<String, Object> getApi = new HashMap<>();
        getApi.put("method", "GET");
        getApi.put("path", basePath + "/{id}");
        getApi.put("description", "查询单条记录");
        getApi.put("parameters", new String[]{"id: 主键值"});
        apis.add(getApi);

        // 新增
        Map<String, Object> postApi = new HashMap<>();
        postApi.put("method", "POST");
        postApi.put("path", basePath);
        postApi.put("description", "新增记录");
        postApi.put("parameters", new String[]{"请求体: JSON对象"});
        apis.add(postApi);

        // 更新
        Map<String, Object> putApi = new HashMap<>();
        putApi.put("method", "PUT");
        putApi.put("path", basePath + "/{id}");
        putApi.put("description", "更新记录");
        putApi.put("parameters", new String[]{"id: 主键值", "请求体: JSON对象"});
        apis.add(putApi);

        // 删除
        Map<String, Object> deleteApi = new HashMap<>();
        deleteApi.put("method", "DELETE");
        deleteApi.put("path", basePath + "/{id}");
        deleteApi.put("description", "删除记录");
        deleteApi.put("parameters", new String[]{"id: 主键值"});
        apis.add(deleteApi);

        // 批量删除
        Map<String, Object> batchDeleteApi = new HashMap<>();
        batchDeleteApi.put("method", "DELETE");
        batchDeleteApi.put("path", basePath);
        batchDeleteApi.put("description", "批量删除");
        batchDeleteApi.put("parameters", new String[]{"请求体: [id1, id2, ...]"});
        apis.add(batchDeleteApi);

        // 批量新增
        Map<String, Object> batchInsertApi = new HashMap<>();
        batchInsertApi.put("method", "POST");
        batchInsertApi.put("path", basePath + "/batch");
        batchInsertApi.put("description", "批量新增");
        batchInsertApi.put("parameters", new String[]{"请求体: [{...}, {...}]"});
        apis.add(batchInsertApi);

        // 自定义查询
        Map<String, Object> queryApi = new HashMap<>();
        queryApi.put("method", "POST");
        queryApi.put("path", basePath + "/query");
        queryApi.put("description", "自定义查询");
        queryApi.put("parameters", new String[]{"sql: SQL语句", "params: 参数数组"});
        apis.add(queryApi);

        // 表结构
        Map<String, Object> schemaApi = new HashMap<>();
        schemaApi.put("method", "GET");
        schemaApi.put("path", basePath + "/schema");
        schemaApi.put("description", "获取表结构");
        apis.add(schemaApi);

        doc.put("apis", apis);
        doc.put("fields", tableInfo.getColumns());

        return ResponseEntity.ok(doc);
    }

    /**
     * 获取ER图数据
     */
    @GetMapping("/datasources/{name}/er-diagram")
    public ResponseEntity<Map<String, Object>> getErDiagram(
            @PathVariable String name,
            @RequestParam(required = false) String schema) {
        log.info("getErDiagram 请求: datasource={}, schema={}", name, schema);
        if (!registry.containsDatasource(name)) {
            log.warn("数据源不存在: {}", name);
            return ResponseEntity.notFound().build();
        }

        DatasourceConfig config = registry.getDatasourceConfig(name);
        DataSource dataSource = registry.getDatasource(name);

        Map<String, Object> result = new HashMap<>();

        // 获取所有表信息
        List<TableInfo> tables = new ArrayList<>();
        List<String> tableNames;
        
        log.info("准备获取表列表, schema 参数: [{}]", schema);
        
        if (schema != null && !schema.isEmpty()) {
            log.info("调用 getTablesBySchema 方法...");
            tableNames = metadataCache.getTablesBySchema(config, dataSource, schema);
        } else {
            log.info("调用 getTables 方法（无 schema）...");
            tableNames = metadataCache.getTables(config, dataSource);
        }
        
        log.info("获取到 {} 个表名: {}", tableNames.size(), tableNames);
        
        for (String tableName : tableNames) {
            try {
                TableInfo tableInfo = metadataCache.getTableInfo(config, dataSource, tableName, schema);
                tables.add(tableInfo);
            } catch (Exception e) {
                log.warn("获取表结构失败: {}", tableName, e);
            }
        }
        result.put("tables", tables);

        // 获取所有外键关系
        List<ForeignKeyInfo> foreignKeys = metadataCache.getAllForeignKeys(config, dataSource, schema);
        result.put("relationships", foreignKeys);

        log.info("ER图数据获取成功: {} 个表, {} 个关系", tables.size(), foreignKeys.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 保存ER图布局
     */
    @PostMapping("/datasources/{name}/diagram-layout")
    public ResponseEntity<Map<String, Object>> saveDiagramLayout(
            @PathVariable String name,
            @RequestBody Map<String, Object> body) {
        log.info("saveDiagramLayout 请求: datasource={}", name);
        
        Map<String, Object> result = new HashMap<>();
        try {
            String schemaName = (String) body.get("schema");
            Object layoutData = body.get("layoutData");
            Object viewConfig = body.get("viewConfig");
            
            // 转换为JSON字符串
            String layoutJson = layoutData != null ? objectMapper.writeValueAsString(layoutData) : null;
            String viewConfigJson = viewConfig != null ? objectMapper.writeValueAsString(viewConfig) : null;
            
            diagramLayoutService.saveLayout(name, schemaName, layoutJson, viewConfigJson);
            
            result.put("success", true);
            result.put("message", "布局保存成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("保存布局失败", e);
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 获取ER图布局
     */
    @GetMapping("/datasources/{name}/diagram-layout")
    public ResponseEntity<Map<String, Object>> getDiagramLayout(
            @PathVariable String name,
            @RequestParam(required = false) String schema) {
        log.info("getDiagramLayout 请求: datasource={}, schema={}", name, schema);
        
        Map<String, Object> layout = diagramLayoutService.getLayout(name, schema);
        return ResponseEntity.ok(layout);
    }

    /**
     * 删除ER图布局
     */
    @DeleteMapping("/datasources/{name}/diagram-layout")
    public ResponseEntity<Map<String, Object>> deleteDiagramLayout(
            @PathVariable String name,
            @RequestParam(required = false) String schema) {
        log.info("deleteDiagramLayout 请求: datasource={}, schema={}", name, schema);
        
        Map<String, Object> result = new HashMap<>();
        try {
            diagramLayoutService.deleteLayout(name, schema);
            result.put("success", true);
            result.put("message", "布局删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除布局失败", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
