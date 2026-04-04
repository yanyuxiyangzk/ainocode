package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.WorkflowDefinitionEntity;
import com.ruoyi.nocode.system.entity.WorkflowInstanceEntity;
import com.ruoyi.nocode.system.entity.WorkflowNodeEntity;
import com.ruoyi.nocode.system.entity.WorkflowTaskEntity;

import java.util.List;
import java.util.Map;

/**
 * 工作流服务接口
 *
 * @author ruoyi-nocode
 */
public interface IWorkflowService {

    // ==================== 工作流定义相关 ====================

    /**
     * 查询工作流定义列表
     */
    TableDataInfo selectWorkflowDefinitionList(WorkflowDefinitionEntity workflowDefinition);

    /**
     * 获取工作流定义详情
     */
    WorkflowDefinitionEntity selectWorkflowDefinitionById(Long definitionId);

    /**
     * 根据编码获取工作流定义
     */
    WorkflowDefinitionEntity selectWorkflowDefinitionByCode(String workflowCode);

    /**
     * 获取最新版本的工作流定义
     */
    List<WorkflowDefinitionEntity> selectLatestWorkflowDefinitions();

    /**
     * 新增工作流定义
     */
    int insertWorkflowDefinition(WorkflowDefinitionEntity workflowDefinition);

    /**
     * 修改工作流定义
     */
    int updateWorkflowDefinition(WorkflowDefinitionEntity workflowDefinition);

    /**
     * 删除工作流定义
     */
    int deleteWorkflowDefinitionByIds(Long[] definitionIds);

    /**
     * 发布工作流（启用）
     */
    int publishWorkflow(Long definitionId);

    /**
     * 禁用工作流
     */
    int disableWorkflow(Long definitionId);

    /**
     * 复制工作流
     */
    int copyWorkflow(Long definitionId, String newWorkflowName, String newWorkflowCode);

    /**
     * 校验工作流编码唯一性
     */
    boolean checkWorkflowCodeUnique(String workflowCode);

    // ==================== 工作流实例相关 ====================

    /**
     * 查询工作流实例列表
     */
    TableDataInfo selectWorkflowInstanceList(WorkflowInstanceEntity workflowInstance);

    /**
     * 获取工作流实例详情
     */
    WorkflowInstanceEntity selectWorkflowInstanceById(Long instanceId);

    /**
     * 发起工作流
     */
    Long startWorkflow(Long definitionId, String title, Long applicantId, String applicantName,
                       Long deptId, String deptName, Map<String, Object> businessData);

    /**
     * 取消工作流
     */
    int cancelWorkflow(Long instanceId, Long userId, String reason);

    /**
     * 查询用户的待审批实例
     */
    List<WorkflowInstanceEntity> selectPendingInstances(Long userId);

    /**
     * 查询用户已完成的实例
     */
    List<WorkflowInstanceEntity> selectCompletedInstances(Long userId);

    // ==================== 工作流任务相关 ====================

    /**
     * 查询用户的待办任务
     */
    List<WorkflowTaskEntity> selectPendingTasks(Long userId);

    /**
     * 审批任务
     */
    int approveTask(Long taskId, Long userId, String comment, Map<String, Object> variables);

    /**
     * 驳回任务
     */
    int rejectTask(Long taskId, Long userId, String targetNodeCode, String reason);

    /**
     * 转交任务
     */
    int delegateTask(Long taskId, Long userId, Long targetUserId, String reason);

    /**
     * 获取任务详情
     */
    WorkflowTaskEntity selectTaskById(Long taskId);

    /**
     * 获取实例的所有任务
     */
    List<WorkflowTaskEntity> selectTasksByInstanceId(Long instanceId);

    /**
     * 获取实例的审批历史
     */
    List<WorkflowTaskEntity> selectApprovalHistory(Long instanceId);

    // ==================== 会签相关 ====================

    /**
     * 发起会签
     */
    int startCounterSign(Long taskId, List<Long> assigneeIds);

    /**
     * 查询会签结果
     */
    Map<String, Object> getCounterSignResult(Long taskId);

    // ==================== 节点配置相关 ====================

    /**
     * 获取工作流节点列表
     */
    List<WorkflowNodeEntity> selectWorkflowNodes(Long definitionId);

    /**
     * 保存工作流节点配置
     */
    int saveWorkflowNodes(Long definitionId, List<WorkflowNodeEntity> nodes);

    /**
     * 获取流程图
     */
    Map<String, Object> getFlowGraphic(Long definitionId);
}