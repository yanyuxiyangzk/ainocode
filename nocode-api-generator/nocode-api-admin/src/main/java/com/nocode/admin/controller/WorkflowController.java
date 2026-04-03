package com.nocode.admin.controller;

import com.nocode.admin.entity.WorkflowDefinitionEntity;
import com.nocode.admin.entity.WorkflowInstanceEntity;
import com.nocode.admin.service.WorkflowDefinitionService;
import com.nocode.core.entity.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程定义Controller
 */
@RestController
@RequestMapping("/api/workflow/definition")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowDefinitionService workflowDefinitionService;

    /**
     * 创建流程定义
     */
    @PostMapping
    public ApiResult<WorkflowDefinitionEntity> create(@RequestBody WorkflowDefinitionEntity entity) {
        WorkflowDefinitionEntity created = workflowDefinitionService.create(entity);
        return ApiResult.success(created);
    }

    /**
     * 更新流程定义
     */
    @PutMapping("/{id}")
    public ApiResult<WorkflowDefinitionEntity> update(@PathVariable Long id, @RequestBody WorkflowDefinitionEntity entity) {
        WorkflowDefinitionEntity updated = workflowDefinitionService.update(id, entity);
        return ApiResult.success(updated);
    }

    /**
     * 获取流程定义
     */
    @GetMapping("/{id}")
    public ApiResult<WorkflowDefinitionEntity> getById(@PathVariable Long id) {
        return workflowDefinitionService.getById(id)
                .map(ApiResult::success)
                .orElse(ApiResult.error("Workflow not found"));
    }

    /**
     * 查询所有流程定义
     */
    @GetMapping("/list")
    public ApiResult<List<WorkflowDefinitionEntity>> list() {
        return ApiResult.success(workflowDefinitionService.findAll());
    }

    /**
     * 部署流程
     */
    @PostMapping("/{id}/deploy")
    public ApiResult<WorkflowDefinitionEntity> deploy(@PathVariable Long id) {
        try {
            WorkflowDefinitionEntity deployed = workflowDefinitionService.deploy(id);
            return ApiResult.success(deployed);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 挂起流程
     */
    @PostMapping("/{id}/suspend")
    public ApiResult<WorkflowDefinitionEntity> suspend(@PathVariable Long id) {
        try {
            WorkflowDefinitionEntity suspended = workflowDefinitionService.suspend(id);
            return ApiResult.success(suspended);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 启动流程实例
     */
    @PostMapping("/{id}/start")
    public ApiResult<WorkflowInstanceEntity> startProcess(@PathVariable Long id,
                                                           @RequestParam String applicant,
                                                           @RequestParam(required = false) String businessKey) {
        try {
            WorkflowInstanceEntity instance = workflowDefinitionService.startProcess(id, applicant, businessKey);
            return ApiResult.success(instance);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 完成当前任务
     */
    @PostMapping("/instance/{instanceId}/complete")
    public ApiResult<WorkflowInstanceEntity> completeTask(@PathVariable Long instanceId, @RequestParam String userId) {
        try {
            WorkflowInstanceEntity instance = workflowDefinitionService.completeTask(instanceId, userId);
            return ApiResult.success(instance);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        workflowDefinitionService.delete(id);
        return ApiResult.success();
    }
}