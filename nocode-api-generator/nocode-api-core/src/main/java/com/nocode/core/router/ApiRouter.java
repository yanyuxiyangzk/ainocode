package com.nocode.core.router;

import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.datasource.DatasourceRegistry;
import com.nocode.core.entity.*;
import com.nocode.core.executor.SqlExecutor;
import com.nocode.core.metadata.MetadataCache;
import com.nocode.core.parser.TableParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 动态API路由处理器
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiRouter {
    private final DatasourceRegistry registry;
    private final SqlExecutor sqlExecutor;
    private final MetadataCache metadataCache;
    private final TableParserFactory parserFactory;
    private final NocodeApiProperties properties;

    public ApiRouter(DatasourceRegistry registry,
                     SqlExecutor sqlExecutor,
                     MetadataCache metadataCache,
                     TableParserFactory parserFactory,
                     NocodeApiProperties properties) {
        this.registry = registry;
        this.sqlExecutor = sqlExecutor;
        this.metadataCache = metadataCache;
        this.parserFactory = parserFactory;
        this.properties = properties;
    }

    /**
     * 查询列表（分页+过滤）
     */
    @GetMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> queryList(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderBy,
            @RequestParam(defaultValue = "asc") String orderDirection,
            @RequestParam(required = false) String where,
            @RequestParam(required = false) List<Object> params,
            @RequestParam(required = false) String[] fields) {

        // 参数校验
        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        // 分页参数校验
        size = Math.min(size, properties.getMaxPageSize());
        if (size <= 0) size = properties.getDefaultPageSize();
        if (page <= 0) page = 1;

        ApiQueryParam queryParam = new ApiQueryParam();
        queryParam.setPage(page);
        queryParam.setSize(size);
        queryParam.setOrderBy(orderBy);
        queryParam.setOrderDirection(orderDirection);
        queryParam.setWhere(where);
        queryParam.setFields(fields);
        if (params != null) {
            queryParam.setParams(params.toArray());
        }

        ApiResult result = sqlExecutor.queryList(datasource, table, queryParam);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询单条记录
     */
    @GetMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> queryOne(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        ApiResult result = sqlExecutor.queryOne(datasource, table, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增记录
     */
    @PostMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> insert(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody Map<String, Object> data) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        ApiResult result = sqlExecutor.insert(datasource, table, data);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新记录
     */
    @PutMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> update(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id,
            @RequestBody Map<String, Object> data) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        ApiResult result = sqlExecutor.update(datasource, table, id, data);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> delete(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        ApiResult result = sqlExecutor.delete(datasource, table, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> batchDelete(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody List<Object> ids) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResult.fail("请提供要删除的ID列表"));
        }

        ApiResult result = sqlExecutor.batchDelete(datasource, table, ids);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量新增
     */
    @PostMapping("/{datasource}/{table}/batch")
    public ResponseEntity<ApiResult> batchInsert(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody List<Map<String, Object>> dataList) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        if (dataList == null || dataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResult.fail("请提供要新增的数据列表"));
        }

        ApiResult result = sqlExecutor.batchInsert(datasource, table, dataList);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 自定义查询
     */
    @PostMapping("/{datasource}/{table}/query")
    public ResponseEntity<ApiResult> customQuery(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody Map<String, Object> request) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        String sql = (String) request.get("sql");
        @SuppressWarnings("unchecked")
        List<Object> params = (List<Object>) request.get("params");

        if (!StringUtils.hasText(sql)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResult.fail("请提供SQL语句"));
        }

        ApiResult result = sqlExecutor.customQuery(datasource, sql, params != null ? params : new java.util.ArrayList<>());
        return ResponseEntity.ok(result);
    }

    /**
     * 执行SQL（危险操作）
     */
    @PostMapping("/{datasource}/execute")
    public ResponseEntity<ApiResult> execute(
            @PathVariable String datasource,
            @RequestBody Map<String, Object> request) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        String sql = (String) request.get("sql");
        if (!StringUtils.hasText(sql)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResult.fail("请提供SQL语句"));
        }

        ApiResult result = sqlExecutor.execute(datasource, sql);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取表结构信息
     */
    @GetMapping("/{datasource}/{table}/schema")
    public ResponseEntity<ApiResult> getSchema(
            @PathVariable String datasource,
            @PathVariable String table) {

        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        DatasourceConfig config = registry.getDatasourceConfig(datasource);
        DataSource ds = registry.getDatasource(datasource);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, table);

        return ResponseEntity.ok(ApiResult.ok(tableInfo));
    }

    /**
     * 获取数据源下的所有表
     */
    @GetMapping("/{datasource}/tables")
    public ResponseEntity<ApiResult> getTables(@PathVariable String datasource) {
        if (!registry.containsDatasource(datasource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.fail("数据源不存在: " + datasource));
        }

        DatasourceConfig config = registry.getDatasourceConfig(datasource);
        java.util.List<String> tables = metadataCache.getTables(config, registry.getDatasource(datasource));
        return ResponseEntity.ok(ApiResult.ok(tables));
    }

    /**
     * 获取所有数据源
     */
    @GetMapping("/datasources")
    public ResponseEntity<ApiResult> getDatasources() {
        java.util.List<String> names = registry.getDatasourceNames();
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (String name : names) {
            DatasourceConfig config = registry.getDatasourceConfig(name);
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("name", name);
            item.put("jdbcUrl", config.getJdbcUrl());
            item.put("driverClassName", config.getDriverClassName());
            item.put("enabled", config.isEnabled());
            result.add(item);
        }
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResult> health() {
        Map<String, Object> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("datasources", registry.getDatasourceNames().size());
        return ResponseEntity.ok(ApiResult.ok(health));
    }
}
