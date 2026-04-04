package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.WorkflowDefinitionEntity;
import com.ruoyi.nocode.system.entity.WorkflowInstanceEntity;
import com.ruoyi.nocode.system.entity.WorkflowNodeEntity;
import com.ruoyi.nocode.system.entity.WorkflowTaskEntity;
import com.ruoyi.nocode.system.service.IWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流Controller
 *
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/system/workflow")
@RequiredArgsConstructor
@Tag(name = "工作流管理", description = "工作流管理接口")
public class WorkflowController {

    private final IWorkflowService workflowService;

    // ==================== 工作流定义 ====================

    /**
     * 查询工作流定义列表
     */
    @GetMapping("/definition/list")
    @Operation(summary = "查询工作流定义列表")
    public TableDataInfo listDefinition(WorkflowDefinitionEntity workflowDefinition) {
        return workflowService.selectWorkflowDefinitionList(workflowDefinition);
    }

    /**
     * 获取工作流定义详情
     */
    @GetMapping("/definition/{definitionId}")
    @Operation(summary = "获取工作流定义详情")
    public AjaxResult getDefinitionInfo(@PathVariable @Parameter(description = "定义ID") Long definitionId) {
        return AjaxResult.success(workflowService.selectWorkflowDefinitionById(definitionId));
    }

    /**
     * 根据编码获取工作流定义
     */
    @GetMapping("/definition/code/{workflowCode}")
    @Operation(summary = "根据编码获取工作流定义")
    public AjaxResult getDefinitionByCode(@PathVariable @Parameter(description = "工作流编码") String workflowCode) {
        return AjaxResult.success(workflowService.selectWorkflowDefinitionByCode(workflowCode));
    }

    /**
     * 获取最新版本工作流列表
     */
    @GetMapping("/definition/latest")
    @Operation(summary = "获取最新版本工作流列表")
    public AjaxResult getLatestDefinitions() {
        return AjaxResult.success(workflowService.selectLatestWorkflowDefinitions());
    }

    /**
     * 新增工作流定义
     */
    @PostMapping("/definition")
    @Operation(summary = "新增工作流定义")
    public AjaxResult addDefinition(@RequestBody WorkflowDefinitionEntity workflowDefinition) {
        return AjaxResult.success(workflowService.insertWorkflowDefinition(workflowDefinition));
    }

    /**
     * 修改工作流定义
     */
    @PutMapping("/definition")
    @Operation(summary = "修改工作流定义")
    public AjaxResult editDefinition(@RequestBody WorkflowDefinitionEntity workflowDefinition) {
        return AjaxResult.success(workflowService.updateWorkflowDefinition(workflowDefinition));
    }

    /**
     * 删除工作流定义
     */
    @DeleteMapping("/definition/{definitionIds}")
    @Operation(summary = "删除工作流定义")
    public AjaxResult removeDefinition(@PathVariable @Parameter(description = "定义ID数组") Long[] definitionIds) {
        return AjaxResult.success(workflowService.deleteWorkflowDefinitionByIds(definitionIds));
    }

    /**
     * 发布工作流
     */
    @PostMapping("/definition/publish/{definitionId}")
    @Operation(summary = "发布工作流")
    public AjaxResult publish(@PathVariable @Parameter(description = "定义ID") Long definitionId) {
        return AjaxResult.success(workflowService.publishWorkflow(definitionId));
    }

    /**
     * 禁用工作流
     */
    @PostMapping("/definition/disable/{definitionId}")
    @Operation(summary = "禁用工作流")
    public AjaxResult disable(@PathVariable @Parameter(description = "定义ID") Long definitionId) {
        return AjaxResult.success(workflowService.disableWorkflow(definitionId));
    }

    /**
     * 复制工作流
     */
    @PostMapping("/definition/copy/{definitionId}")
    @Operation(summary = "复制工作流")
    public AjaxResult copy(
            @PathVariable @Parameter(description = "定义ID") Long definitionId,
            @RequestBody Map<String, String> params) {
        String newWorkflowName = params.get("newWorkflowName");
        String newWorkflowCode = params.get("newWorkflowCode");
        return AjaxResult.success(workflowService.copyWorkflow(definitionId, newWorkflowName, newWorkflowCode));
    }

    /**
     * 校验工作流编码唯一性
     */
    @GetMapping("/definition/checkCodeUnique")
    @Operation(summary = "校验工作流编码唯一性")
    public AjaxResult checkCodeUnique(@Parameter(description = "工作流编码") @RequestParam String workflowCode) {
        return AjaxResult.success(workflowService.checkWorkflowCodeUnique(workflowCode));
    }

    /**
     * 获取工作流节点
     */
    @GetMapping("/nodes/{definitionId}")
    @Operation(summary = "获取工作流节点")
    public AjaxResult getNodes(@PathVariable @Parameter(description = "定义ID") Long definitionId) {
        return AjaxResult.success(workflowService.selectWorkflowNodes(definitionId));
    }

    /**
     * 保存工作流节点
     */
    @PostMapping("/nodes/{definitionId}")
    @Operation(summary = "保存工作流节点")
    public AjaxResult saveNodes(
            @PathVariable @Parameter(description = "定义ID") Long definitionId,
            @RequestBody List<WorkflowNodeEntity> nodes) {
        return AjaxResult.success(workflowService.saveWorkflowNodes(definitionId, nodes));
    }

    /**
     * 获取流程图
     */
    @GetMapping("/flowGraphic/{definitionId}")
    @Operation(summary = "获取流程图")
    public AjaxResult getFlowGraphic(@PathVariable @Parameter(description = "定义ID") Long definitionId) {
        return AjaxResult.success(workflowService.getFlowGraphic(definitionId));
    }

    // ==================== 工作流实例 ====================

    /**
     * 查询工作流实例列表
     */
    @GetMapping("/instance/list")
    @Operation(summary = "查询工作流实例列表")
    public TableDataInfo listInstance(WorkflowInstanceEntity workflowInstance) {
        return workflowService.selectWorkflowInstanceList(workflowInstance);
    }

    /**
     * 获取工作流实例详情
     */
    @GetMapping("/instance/{instanceId}")
    @Operation(summary = "获取工作流实例详情")
    public AjaxResult getInstanceInfo(@PathVariable @Parameter(description = "实例ID") Long instanceId) {
        return AjaxResult.success(workflowService.selectWorkflowInstanceById(instanceId));
    }

    /**
     * 发起工作流
     */
    @PostMapping("/instance/start")
    @Operation(summary = "发起工作流")
    public AjaxResult startWorkflow(@RequestBody Map<String, Object> params) {
        Long definitionId = Long.valueOf(params.get("definitionId").toString());
        String title = (String) params.get("title");
        Long applicantId = Long.valueOf(params.get("applicantId").toString());
        String applicantName = (String) params.get("applicantName");
        Long deptId = params.get("deptId") != null ? Long.valueOf(params.get("deptId").toString()) : null;
        String deptName = (String) params.get("deptName");
        @SuppressWarnings("unchecked")
        Map<String, Object> businessData = (Map<String, Object>) params.get("businessData");

        Long instanceId = workflowService.startWorkflow(definitionId, title, applicantId, applicantName,
                deptId, deptName, businessData);
        return AjaxResult.success("发起成功", instanceId);
    }

    /**
     * 取消工作流
     */
    @PostMapping("/instance/cancel/{instanceId}")
    @Operation(summary = "取消工作流")
    public AjaxResult cancelWorkflow(
            @PathVariable @Parameter(description = "实例ID") Long instanceId,
            @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String reason = (String) params.get("reason");
        return AjaxResult.success(workflowService.cancelWorkflow(instanceId, userId, reason));
    }

    /**
     * 获取待审批实例
     */
    @GetMapping("/instance/pending/{userId}")
    @Operation(summary = "获取待审批实例")
    public AjaxResult getPendingInstances(@PathVariable @Parameter(description = "用户ID") Long userId) {
        return AjaxResult.success(workflowService.selectPendingInstances(userId));
    }

    /**
     * 获取已完成后实例
     */
    @GetMapping("/instance/completed/{userId}")
    @Operation(summary = "获取已完成后实例")
    public AjaxResult getCompletedInstances(@PathVariable @Parameter(description = "用户ID") Long userId) {
        return AjaxResult.success(workflowService.selectCompletedInstances(userId));
    }

    // ==================== 工作流任务 ====================

    /**
     * 获取待办任务
     */
    @GetMapping("/task/pending/{userId}")
    @Operation(summary = "获取待办任务")
    public AjaxResult getPendingTasks(@PathVariable @Parameter(description = "用户ID") Long userId) {
        return AjaxResult.success(workflowService.selectPendingTasks(userId));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务详情")
    public AjaxResult getTaskInfo(@PathVariable @Parameter(description = "任务ID") Long taskId) {
        return AjaxResult.success(workflowService.selectTaskById(taskId));
    }

    /**
     * 审批任务
     */
    @PostMapping("/task/approve/{taskId}")
    @Operation(summary = "审批任务")
    public AjaxResult approve(
            @PathVariable @Parameter(description = "任务ID") Long taskId,
            @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String comment = (String) params.get("comment");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) params.get("variables");
        return AjaxResult.success(workflowService.approveTask(taskId, userId, comment, variables));
    }

    /**
     * 驳回任务
     */
    @PostMapping("/task/reject/{taskId}")
    @Operation(summary = "驳回任务")
    public AjaxResult reject(
            @PathVariable @Parameter(description = "任务ID") Long taskId,
            @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String targetNodeCode = (String) params.get("targetNodeCode");
        String reason = (String) params.get("reason");
        return AjaxResult.success(workflowService.rejectTask(taskId, userId, targetNodeCode, reason));
    }

    /**
     * 转交任务
     */
    @PostMapping("/task/delegate/{taskId}")
    @Operation(summary = "转交任务")
    public AjaxResult delegate(
            @PathVariable @Parameter(description = "任务ID") Long taskId,
            @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long targetUserId = Long.valueOf(params.get("targetUserId").toString());
        String reason = (String) params.get("reason");
        return AjaxResult.success(workflowService.delegateTask(taskId, userId, targetUserId, reason));
    }

    /**
     * 获取实例的所有任务
     */
    @GetMapping("/task/instance/{instanceId}")
    @Operation(summary = "获取实例的所有任务")
    public AjaxResult getTasksByInstance(@PathVariable @Parameter(description = "实例ID") Long instanceId) {
        return AjaxResult.success(workflowService.selectTasksByInstanceId(instanceId));
    }

    /**
     * 获取审批历史
     */
    @GetMapping("/task/history/{instanceId}")
    @Operation(summary = "获取审批历史")
    public AjaxResult getApprovalHistory(@PathVariable @Parameter(description = "实例ID") Long instanceId) {
        return AjaxResult.success(workflowService.selectApprovalHistory(instanceId));
    }

    // ==================== 会签 ====================

    /**
     * 发起会签
     */
    @PostMapping("/task/countersign/{taskId}")
    @Operation(summary = "发起会签")
    public AjaxResult startCounterSign(
            @PathVariable @Parameter(description = "任务ID") Long taskId,
            @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> assigneeIds = (List<Long>) params.get("assigneeIds");
        return AjaxResult.success(workflowService.startCounterSign(taskId, assigneeIds));
    }

    /**
     * 获取会签结果
     */
    @GetMapping("/task/countersign/result/{taskId}")
    @Operation(summary = "获取会签结果")
    public AjaxResult getCounterSignResult(@PathVariable @Parameter(description = "任务ID") Long taskId) {
        return AjaxResult.success(workflowService.getCounterSignResult(taskId));
    }
}