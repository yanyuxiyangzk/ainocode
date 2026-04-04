package com.nocode.admin.controller;

import com.nocode.admin.entity.FormComponentEntity;
import com.nocode.admin.service.FormComponentService;
import com.nocode.core.entity.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 表单组件Controller
 */
@RestController
@RequestMapping("/api/form-component")
public class FormComponentController {

    private final FormComponentService formComponentService;

    @Autowired
    public FormComponentController(FormComponentService formComponentService) {
        this.formComponentService = formComponentService;
    }

    /**
     * 创建组件
     */
    @PostMapping
    public ApiResult<FormComponentEntity> create(@RequestBody FormComponentEntity entity) {
        FormComponentEntity created = formComponentService.create(entity);
        return ApiResult.success(created);
    }

    /**
     * 更新组件
     */
    @PutMapping("/{id}")
    public ApiResult<FormComponentEntity> update(@PathVariable Long id, @RequestBody FormComponentEntity entity) {
        FormComponentEntity updated = formComponentService.update(id, entity);
        return ApiResult.success(updated);
    }

    /**
     * 获取组件
     */
    @GetMapping("/{id}")
    public ApiResult<FormComponentEntity> getById(@PathVariable Long id) {
        return formComponentService.getById(id)
                .map(ApiResult::success)
                .orElse(ApiResult.error("Component not found"));
    }

    /**
     * 查询所有组件
     */
    @GetMapping("/list")
    public ApiResult<List<FormComponentEntity>> list() {
        return ApiResult.success(formComponentService.findAll());
    }

    /**
     * 根据表单ID查询
     */
    @GetMapping("/list/form/{formId}")
    public ApiResult<List<FormComponentEntity>> listByFormId(@PathVariable Long formId) {
        return ApiResult.success(formComponentService.findByFormId(formId));
    }

    /**
     * 删除组件
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        formComponentService.delete(id);
        return ApiResult.success();
    }

    /**
     * 获取组件类型列表
     */
    @GetMapping("/types")
    public ApiResult<List<String>> getComponentTypes() {
        List<String> types = Arrays.asList(
            "input", "textarea", "number", "select", "radio", "checkbox",
            "date", "datetime", "switch", "slider", "cascader", "upload"
        );
        return ApiResult.success(types);
    }
}