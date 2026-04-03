package com.nocode.admin.controller;

import com.nocode.admin.entity.FormConfigEntity;
import com.nocode.admin.service.FormConfigService;
import com.nocode.core.entity.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 表单配置Controller
 */
@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
public class FormConfigController {

    private final FormConfigService formConfigService;

    /**
     * 创建表单
     */
    @PostMapping
    public ApiResult<FormConfigEntity> create(@RequestBody FormConfigEntity entity) {
        FormConfigEntity created = formConfigService.create(entity);
        return ApiResult.success(created);
    }

    /**
     * 更新表单
     */
    @PutMapping("/{id}")
    public ApiResult<FormConfigEntity> update(@PathVariable Long id, @RequestBody FormConfigEntity entity) {
        FormConfigEntity updated = formConfigService.update(id, entity);
        return ApiResult.success(updated);
    }

    /**
     * 获取表单
     */
    @GetMapping("/{id}")
    public ApiResult<FormConfigEntity> getById(@PathVariable Long id) {
        return formConfigService.getById(id)
                .map(ApiResult::success)
                .orElse(ApiResult.error("Form not found"));
    }

    /**
     * 查询所有表单
     */
    @GetMapping("/list")
    public ApiResult<List<FormConfigEntity>> list() {
        return ApiResult.success(formConfigService.findAll());
    }

    /**
     * 根据状态查询
     */
    @GetMapping("/list/status/{status}")
    public ApiResult<List<FormConfigEntity>> listByStatus(@PathVariable String status) {
        return ApiResult.success(formConfigService.findByStatus(status));
    }

    /**
     * 发布表单
     */
    @PostMapping("/{id}/publish")
    public ApiResult<FormConfigEntity> publish(@PathVariable Long id) {
        try {
            FormConfigEntity published = formConfigService.publish(id);
            return ApiResult.success(published);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除表单
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        formConfigService.delete(id);
        return ApiResult.success();
    }

    /**
     * 搜索表单
     */
    @GetMapping("/search")
    public ApiResult<List<FormConfigEntity>> search(@RequestParam String name) {
        return ApiResult.success(formConfigService.searchByName(name));
    }
}
