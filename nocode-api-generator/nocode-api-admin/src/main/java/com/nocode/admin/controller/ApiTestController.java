package com.nocode.admin.controller;

import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.entity.ApiResult;
import com.nocode.core.executor.SqlExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/nocode-admin/api/test")
public class ApiTestController {
    private final SqlExecutor sqlExecutor;
    private final NocodeApiProperties properties;

    public ApiTestController(SqlExecutor sqlExecutor, NocodeApiProperties properties) {
        this.sqlExecutor = sqlExecutor;
        this.properties = properties;
    }

    /**
     * 测试查询列表
     */
    @GetMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> testQueryList(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection) {

        com.nocode.core.entity.ApiQueryParam param = new com.nocode.core.entity.ApiQueryParam();
        param.setPage(page);
        param.setSize(size);
        param.setOrderBy(orderBy);
        param.setOrderDirection(orderDirection);

        ApiResult result = sqlExecutor.queryList(datasource, table, param);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试查询单条
     */
    @GetMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> testQueryOne(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id) {

        ApiResult result = sqlExecutor.queryOne(datasource, table, id);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试新增
     */
    @PostMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> testInsert(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody Map<String, Object> data) {

        ApiResult result = sqlExecutor.insert(datasource, table, data);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试更新
     */
    @PutMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> testUpdate(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id,
            @RequestBody Map<String, Object> data) {

        ApiResult result = sqlExecutor.update(datasource, table, id, data);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试删除
     */
    @DeleteMapping("/{datasource}/{table}/{id}")
    public ResponseEntity<ApiResult> testDelete(
            @PathVariable String datasource,
            @PathVariable String table,
            @PathVariable Object id) {

        ApiResult result = sqlExecutor.delete(datasource, table, id);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试批量删除
     */
    @DeleteMapping("/{datasource}/{table}")
    public ResponseEntity<ApiResult> testBatchDelete(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody List<Object> ids) {

        ApiResult result = sqlExecutor.batchDelete(datasource, table, ids);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试批量新增
     */
    @PostMapping("/{datasource}/{table}/batch")
    public ResponseEntity<ApiResult> testBatchInsert(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody List<Map<String, Object>> dataList) {

        ApiResult result = sqlExecutor.batchInsert(datasource, table, dataList);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试自定义查询
     */
    @PostMapping("/{datasource}/{table}/query")
    public ResponseEntity<ApiResult> testCustomQuery(
            @PathVariable String datasource,
            @PathVariable String table,
            @RequestBody Map<String, Object> request) {

        String sql = (String) request.get("sql");
        @SuppressWarnings("unchecked")
        List<Object> params = (List<Object>) request.get("params");
        ApiResult result = sqlExecutor.customQuery(datasource, sql, params != null ? params : new java.util.ArrayList<>());
        return ResponseEntity.ok(result);
    }
}
