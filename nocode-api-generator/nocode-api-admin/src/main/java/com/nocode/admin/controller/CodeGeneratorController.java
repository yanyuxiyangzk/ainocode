package com.nocode.admin.controller;

import com.nocode.admin.entity.CodeGeneratorConfigEntity;
import com.nocode.admin.service.CodeGeneratorService;
import com.nocode.core.entity.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代码生成器Controller
 */
@RestController
@RequestMapping("/api/code-generator")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final CodeGeneratorService codeGeneratorService;

    /**
     * 创建配置
     */
    @PostMapping("/config")
    public ApiResult<CodeGeneratorConfigEntity> createConfig(@RequestBody CodeGeneratorConfigEntity entity) {
        CodeGeneratorConfigEntity created = codeGeneratorService.create(entity);
        return ApiResult.success(created);
    }

    /**
     * 更新配置
     */
    @PutMapping("/config/{id}")
    public ApiResult<CodeGeneratorConfigEntity> updateConfig(@PathVariable Long id, @RequestBody CodeGeneratorConfigEntity entity) {
        CodeGeneratorConfigEntity updated = codeGeneratorService.update(id, entity);
        return ApiResult.success(updated);
    }

    /**
     * 获取配置
     */
    @GetMapping("/config/{id}")
    public ApiResult<CodeGeneratorConfigEntity> getConfig(@PathVariable Long id) {
        return codeGeneratorService.getById(id)
                .map(ApiResult::success)
                .orElse(ApiResult.error("Config not found"));
    }

    /**
     * 查询所有配置
     */
    @GetMapping("/config/list")
    public ApiResult<List<CodeGeneratorConfigEntity>> listConfig() {
        return ApiResult.success(codeGeneratorService.findAll());
    }

    /**
     * 根据状态查询配置
     */
    @GetMapping("/config/list/status/{status}")
    public ApiResult<List<CodeGeneratorConfigEntity>> listConfigByStatus(@PathVariable String status) {
        return ApiResult.success(codeGeneratorService.findByStatus(status));
    }

    /**
     * 生成代码
     */
    @PostMapping("/generate/{configId}")
    public ApiResult<String> generateCode(@PathVariable Long configId,
                                            @RequestParam(defaultValue = "./output") String outputPath) {
        try {
            String result = codeGeneratorService.generateCode(configId, outputPath);
            return ApiResult.success(result);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/config/{id}")
    public ApiResult<Void> deleteConfig(@PathVariable Long id) {
        codeGeneratorService.delete(id);
        return ApiResult.success();
    }
}