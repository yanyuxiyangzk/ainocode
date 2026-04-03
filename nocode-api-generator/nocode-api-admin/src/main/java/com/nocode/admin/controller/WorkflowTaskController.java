package com.nocode.admin.controller;

import com.nocode.admin.entity.WorkflowTaskEntity;
import com.nocode.admin.service.WorkflowTaskService;
import com.nocode.core.entity.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流任务Controller
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@RestController
@RequestMapping("/api/workflow/task")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final WorkflowTaskService workflowTaskService;

    /**
     * 获取任务
     */
    @GetMapping("/{id}")
    public ApiResult<WorkflowTaskEntity> getById(@PathVariable Long id) {
        return workflowTaskService.getById(id)
                .map(ApiResult::success)
                .orElse(ApiResult.error("Task not found"));
    }

    /**
     * 查询实例的所有任务
     */
    @GetMapping("/list/instance/{instanceId}")
    public ApiResult<List<WorkflowTaskEntity>> listByInstanceId(@PathVariable Long instanceId) {
        return ApiResult.success(workflowTaskService.findByInstanceId(instanceId));
    }

    /**
     * 查询用户的待办任务
     */
    @GetMapping("/list/todo/{userId}")
    public ApiResult<List<WorkflowTaskEntity>> listTodoTasks(@PathVariable String userId) {
        return ApiResult.success(workflowTaskService.findTodoTasks(userId));
    }

    /**
     * 查询用户的待签收任务
     */
    @GetMapping("/list/pending/{userId}")
    public ApiResult<List<WorkflowTaskEntity>> listPendingTasks(@PathVariable String userId) {
        return ApiResult.success(workflowTaskService.findPendingTasks(userId));
    }

    /**
     * 签收任务
     */
    @PostMapping("/{id}/claim")
    public ApiResult<WorkflowTaskEntity> claimTask(@PathVariable Long id, @RequestParam String userId) {
        try {
            WorkflowTaskEntity task = workflowTaskService.claimTask(id, userId);
            return ApiResult.success(task);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @PostMapping("/{id}/complete")
    public ApiResult<WorkflowTaskEntity> completeTask(@PathVariable Long id,
                                                      @RequestParam(required = false) String comment) {
        try {
            WorkflowTaskEntity task = workflowTaskService.completeTask(id, comment);
            return ApiResult.success(task);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 驳回任务
     */
    @PostMapping("/{id}/reject")
    public ApiResult<WorkflowTaskEntity> rejectTask(@PathVariable Long id,
                                                    @RequestParam(required = false) String comment) {
        try {
            WorkflowTaskEntity task = workflowTaskService.rejectTask(id, comment);
            return ApiResult.success(task);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 转交任务
     */
    @PostMapping("/{id}/transfer")
    public ApiResult<WorkflowTaskEntity> transferTask(@PathVariable Long id, @RequestParam String newAssignee) {
        try {
            WorkflowTaskEntity task = workflowTaskService.transferTask(id, newAssignee);
            return ApiResult.success(task);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 会签任务
     *
     * @param id       任务ID
     * @param userId   用户ID
     * @param comment  审批意见
     * @param approved 是否同意
     * @return 会签结果
     */
    @PostMapping("/{id}/countersign")
    public ApiResult<Map<String, Object>> countersignTask(@PathVariable Long id,
                                                          @RequestParam String userId,
                                                          @RequestParam(required = false) String comment,
                                                          @RequestParam(defaultValue = "true") boolean approved) {
        try {
            Map<String, Object> result = workflowTaskService.countersignTask(id, userId, comment, approved);
            return ApiResult.success(result);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 转交到指定节点
     *
     * @param instanceId     实例ID
     * @param targetNodeId   目标节点ID
     * @param targetNodeName 目标节点名称
     * @param targetUserId   目标用户ID
     * @param comment        转交意见
     * @return 新任务
     */
    @PostMapping("/transfer/node")
    public ApiResult<WorkflowTaskEntity> transferToNode(@RequestParam Long instanceId,
                                                       @RequestParam String targetNodeId,
                                                       @RequestParam String targetNodeName,
                                                       @RequestParam String targetUserId,
                                                       @RequestParam(required = false) String comment) {
        try {
            WorkflowTaskEntity task = workflowTaskService.transferToNode(instanceId, targetNodeId, targetNodeName, targetUserId, comment);
            return ApiResult.success(task);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        workflowTaskService.delete(id);
        return ApiResult.success();
    }
}