package com.ruoyi.nocode.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.common.core.exception.ServiceException;
import com.ruoyi.nocode.system.entity.WorkflowDefinitionEntity;
import com.ruoyi.nocode.system.entity.WorkflowInstanceEntity;
import com.ruoyi.nocode.system.entity.WorkflowNodeEntity;
import com.ruoyi.nocode.system.entity.WorkflowTaskEntity;
import com.ruoyi.nocode.system.mapper.WorkflowDefinitionMapper;
import com.ruoyi.nocode.system.mapper.WorkflowInstanceMapper;
import com.ruoyi.nocode.system.mapper.WorkflowNodeMapper;
import com.ruoyi.nocode.system.mapper.WorkflowTaskMapper;
import com.ruoyi.nocode.system.service.IWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流服务实现
 *
 * @author ruoyi-nocode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl extends ServiceImpl<WorkflowDefinitionMapper, WorkflowDefinitionEntity>
        implements IWorkflowService {

    private final WorkflowDefinitionMapper workflowDefinitionMapper;
    private final WorkflowInstanceMapper workflowInstanceMapper;
    private final WorkflowNodeMapper workflowNodeMapper;
    private final WorkflowTaskMapper workflowTaskMapper;

    // ==================== 工作流定义相关 ====================

    @Override
    public TableDataInfo selectWorkflowDefinitionList(WorkflowDefinitionEntity workflowDefinition) {
        Page<WorkflowDefinitionEntity> page = new Page<>(workflowDefinition.getPageNum(), workflowDefinition.getPageSize());
        List<WorkflowDefinitionEntity> list = workflowDefinitionMapper.selectWorkflowDefinitionList(workflowDefinition);
        return TableDataInfo.success(list, list.size());
    }

    @Override
    public WorkflowDefinitionEntity selectWorkflowDefinitionById(Long definitionId) {
        return workflowDefinitionMapper.selectById(definitionId);
    }

    @Override
    public WorkflowDefinitionEntity selectWorkflowDefinitionByCode(String workflowCode) {
        return workflowDefinitionMapper.selectByWorkflowCode(workflowCode);
    }

    @Override
    public List<WorkflowDefinitionEntity> selectLatestWorkflowDefinitions() {
        return workflowDefinitionMapper.selectLatestVersions();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWorkflowDefinition(WorkflowDefinitionEntity workflowDefinition) {
        if (!checkWorkflowCodeUnique(workflowDefinition.getWorkflowCode())) {
            throw new ServiceException("工作流编码已存在");
        }
        workflowDefinition.setVersion(1);
        workflowDefinition.setIsLatest("1");
        workflowDefinition.setDelFlag("0");
        workflowDefinition.setStatus("0"); // 默认禁用
        return workflowDefinitionMapper.insert(workflowDefinition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWorkflowDefinition(WorkflowDefinitionEntity workflowDefinition) {
        WorkflowDefinitionEntity existDefinition = workflowDefinitionMapper.selectById(workflowDefinition.getDefinitionId());
        if (existDefinition == null) {
            throw new ServiceException("工作流不存在");
        }
        // 如果修改了编码，检查唯一性
        if (StringUtils.hasText(workflowDefinition.getWorkflowCode())
                && !workflowDefinition.getWorkflowCode().equals(existDefinition.getWorkflowCode())) {
            if (!checkWorkflowCodeUnique(workflowDefinition.getWorkflowCode())) {
                throw new ServiceException("工作流编码已存在");
            }
        }
        return workflowDefinitionMapper.updateById(workflowDefinition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWorkflowDefinitionByIds(Long[] definitionIds) {
        int count = 0;
        for (Long definitionId : definitionIds) {
            WorkflowDefinitionEntity definition = new WorkflowDefinitionEntity();
            definition.setDefinitionId(definitionId);
            definition.setDelFlag("1");
            count += workflowDefinitionMapper.updateById(definition);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishWorkflow(Long definitionId) {
        WorkflowDefinitionEntity definition = workflowDefinitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new ServiceException("工作流不存在");
        }
        if ("1".equals(definition.getStatus())) {
            throw new ServiceException("工作流已是启用状态");
        }
        definition.setStatus("1");
        definition.setIsLatest("1");
        int result = workflowDefinitionMapper.updateById(definition);

        // 将同编码的其他版本设置为非最新
        List<WorkflowDefinitionEntity> otherVersions = workflowDefinitionMapper.selectVersionsByCode(definition.getWorkflowCode());
        for (WorkflowDefinitionEntity other : otherVersions) {
            if (!other.getDefinitionId().equals(definitionId)) {
                other.setIsLatest("0");
                workflowDefinitionMapper.updateById(other);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int disableWorkflow(Long definitionId) {
        WorkflowDefinitionEntity definition = workflowDefinitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new ServiceException("工作流不存在");
        }
        definition.setStatus("0");
        return workflowDefinitionMapper.updateById(definition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyWorkflow(Long definitionId, String newWorkflowName, String newWorkflowCode) {
        WorkflowDefinitionEntity original = workflowDefinitionMapper.selectById(definitionId);
        if (original == null) {
            throw new ServiceException("原工作流不存在");
        }

        if (!checkWorkflowCodeUnique(newWorkflowCode)) {
            throw new ServiceException("新工作流编码已存在");
        }

        WorkflowDefinitionEntity newDefinition = new WorkflowDefinitionEntity();
        newDefinition.setWorkflowName(newWorkflowName);
        newDefinition.setWorkflowCode(newWorkflowCode);
        newDefinition.setWorkflowType(original.getWorkflowType());
        newDefinition.setDescription(original.getDescription());
        newDefinition.setFormId(original.getFormId());
        newDefinition.setFormName(original.getFormName());
        newDefinition.setProcessDefinition(original.getProcessDefinition());
        newDefinition.setFlowGraphic(original.getFlowGraphic());
        newDefinition.setStatus("0"); // 复制后默认禁用
        newDefinition.setVersion(1);
        newDefinition.setIsLatest("1");
        newDefinition.setDelFlag("0");

        int result = workflowDefinitionMapper.insert(newDefinition);
        if (result > 0) {
            // 复制节点
            List<WorkflowNodeEntity> nodes = workflowNodeMapper.selectByDefinitionId(definitionId);
            if (nodes != null && !nodes.isEmpty()) {
                for (WorkflowNodeEntity node : nodes) {
                    node.setNodeId(null);
                    node.setDefinitionId(newDefinition.getDefinitionId());
                    node.setCreateTime(LocalDateTime.now());
                    node.setUpdateTime(LocalDateTime.now());
                }
                workflowNodeMapper.batchInsert(nodes);
            }
        }
        return result;
    }

    @Override
    public boolean checkWorkflowCodeUnique(String workflowCode) {
        if (!StringUtils.hasText(workflowCode)) {
            return true;
        }
        WorkflowDefinitionEntity exist = workflowDefinitionMapper.selectByWorkflowCode(workflowCode);
        return exist == null;
    }

    // ==================== 工作流实例相关 ====================

    @Override
    public TableDataInfo selectWorkflowInstanceList(WorkflowInstanceEntity workflowInstance) {
        Page<WorkflowInstanceEntity> page = new Page<>(workflowInstance.getPageNum(), workflowInstance.getPageSize());
        List<WorkflowInstanceEntity> list = workflowInstanceMapper.selectWorkflowInstanceList(workflowInstance);
        return TableDataInfo.success(list, list.size());
    }

    @Override
    public WorkflowInstanceEntity selectWorkflowInstanceById(Long instanceId) {
        return workflowInstanceMapper.selectById(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startWorkflow(Long definitionId, String title, Long applicantId, String applicantName,
                              Long deptId, String deptName, Map<String, Object> businessData) {
        WorkflowDefinitionEntity definition = workflowDefinitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new ServiceException("工作流不存在");
        }
        if (!"1".equals(definition.getStatus())) {
            throw new ServiceException("工作流未启用");
        }

        // 获取开始节点
        WorkflowNodeEntity startNode = workflowNodeMapper.selectStartNode(definitionId);
        if (startNode == null) {
            throw new ServiceException("工作流未配置开始节点");
        }

        // 创建实例
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setDefinitionId(definitionId);
        instance.setInstanceTitle(title);
        instance.setInstanceCode(generateInstanceCode());
        instance.setApplicantId(applicantId);
        instance.setApplicantName(applicantName);
        instance.setDeptId(deptId);
        instance.setDeptName(deptName);
        instance.setCurrentNodeId(startNode.getNodeId());
        instance.setCurrentNodeName(startNode.getNodeName());
        instance.setCurrentNodeType(startNode.getNodeType());
        instance.setInstanceStatus("1"); // 审批中
        instance.setBusinessData(businessData != null ? JSON.toJSONString(businessData) : null);
        instance.setDelFlag("0");
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());

        workflowInstanceMapper.insert(instance);

        // 创建第一个任务
        createTask(instance, startNode, applicantId, applicantName);

        return instance.getInstanceId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelWorkflow(Long instanceId, Long userId, String reason) {
        WorkflowInstanceEntity instance = workflowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new ServiceException("工作流实例不存在");
        }
        if (!instance.getApplicantId().equals(userId)) {
            throw new ServiceException("只有申请人可以取消工作流");
        }
        if (!"1".equals(instance.getInstanceStatus())) {
            throw new ServiceException("当前状态不允许取消");
        }

        instance.setInstanceStatus("5"); // 已取消
        instance.setEndTime(LocalDateTime.now());
        return workflowInstanceMapper.updateById(instance);
    }

    @Override
    public List<WorkflowInstanceEntity> selectPendingInstances(Long userId) {
        return workflowInstanceMapper.selectPendingInstances(userId);
    }

    @Override
    public List<WorkflowInstanceEntity> selectCompletedInstances(Long userId) {
        WorkflowInstanceEntity query = new WorkflowInstanceEntity();
        query.setApplicantId(userId);
        query.setInstanceStatus("2"); // 已完成
        return workflowInstanceMapper.selectWorkflowInstanceList(query);
    }

    // ==================== 工作流任务相关 ====================

    @Override
    public List<WorkflowTaskEntity> selectPendingTasks(Long userId) {
        return workflowTaskMapper.selectPendingByAssigneeId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveTask(Long taskId, Long userId, String comment, Map<String, Object> variables) {
        WorkflowTaskEntity task = workflowTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        if (!task.getAssigneeId().equals(userId)) {
            throw new ServiceException("不是当前任务的办理人");
        }
        if (!"0".equals(task.getTaskStatus()) && !"1".equals(task.getTaskStatus())) {
            throw new ServiceException("任务状态不允许审批");
        }

        // 更新任务
        task.setTaskStatus("2"); // 已完成
        task.setComment(comment);
        task.setResult("agree");
        task.setCompleteTime(LocalDateTime.now());
        if (variables != null) {
            task.setTaskVariables(JSON.toJSONString(variables));
        }
        workflowTaskMapper.updateById(task);

        // 获取下一个节点
        WorkflowInstanceEntity instance = workflowInstanceMapper.selectById(task.getInstanceId());
        WorkflowNodeEntity currentNode = workflowNodeMapper.selectById(task.getNodeId());
        WorkflowNodeEntity nextNode = findNextNode(instance.getDefinitionId(), currentNode, variables);

        if (nextNode != null) {
            // 更新实例当前节点
            instance.setCurrentNodeId(nextNode.getNodeId());
            instance.setCurrentNodeName(nextNode.getNodeName());
            instance.setCurrentNodeType(nextNode.getNodeType());
            workflowInstanceMapper.updateById(instance);

            // 创建下一个任务
            createTask(instance, nextNode, null, null);
        } else {
            // 工作流结束
            instance.setInstanceStatus("2"); // 已完成
            instance.setEndTime(LocalDateTime.now());
            workflowInstanceMapper.updateById(instance);
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectTask(Long taskId, Long userId, String targetNodeCode, String reason) {
        WorkflowTaskEntity task = workflowTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        if (!task.getAssigneeId().equals(userId)) {
            throw new ServiceException("不是当前任务的办理人");
        }

        WorkflowNodeEntity currentNode = workflowNodeMapper.selectById(task.getNodeId());
        if (!"1".equals(currentNode.getAllowReject())) {
            throw new ServiceException("当前节点不允许驳回");
        }

        // 更新任务
        task.setTaskStatus("2");
        task.setComment(reason);
        task.setResult("reject");
        task.setCompleteTime(LocalDateTime.now());
        workflowTaskMapper.updateById(task);

        // 更新实例
        WorkflowInstanceEntity instance = workflowInstanceMapper.selectById(task.getInstanceId());

        if (StringUtils.hasText(targetNodeCode)) {
            // 驳回到指定节点
            WorkflowNodeEntity targetNode = workflowNodeMapper.selectByNodeCode(instance.getDefinitionId(), targetNodeCode);
            if (targetNode != null) {
                instance.setCurrentNodeId(targetNode.getNodeId());
                instance.setCurrentNodeName(targetNode.getNodeName());
                instance.setCurrentNodeType(targetNode.getNodeType());
                createTask(instance, targetNode, instance.getApplicantId(), instance.getApplicantName());
            }
        } else {
            // 驳回到申请人
            instance.setCurrentNodeId(null);
            instance.setCurrentNodeName("已驳回");
            instance.setInstanceStatus("3"); // 已驳回
            instance.setEndTime(LocalDateTime.now());
        }
        workflowInstanceMapper.updateById(instance);

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delegateTask(Long taskId, Long userId, Long targetUserId, String reason) {
        WorkflowTaskEntity task = workflowTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        if (!task.getAssigneeId().equals(userId)) {
            throw new ServiceException("不是当前任务的办理人");
        }

        WorkflowNodeEntity node = workflowNodeMapper.selectById(task.getNodeId());
        if (!"1".equals(node.getAllowDelegate())) {
            throw new ServiceException("当前节点不允许转交");
        }

        // 记录原办理人
        task.setOriginalAssigneeId(task.getAssigneeId());
        task.setOriginalAssigneeName(task.getAssigneeName());
        task.setAssigneeId(targetUserId);
        task.setAssigneeName(""); // TODO: 查询目标用户名称
        task.setTaskStatus("4"); // 已转交
        task.setComment(reason);
        task.setUpdateTime(LocalDateTime.now());
        return workflowTaskMapper.updateById(task);
    }

    @Override
    public WorkflowTaskEntity selectTaskById(Long taskId) {
        return workflowTaskMapper.selectById(taskId);
    }

    @Override
    public List<WorkflowTaskEntity> selectTasksByInstanceId(Long instanceId) {
        return workflowTaskMapper.selectByInstanceId(instanceId);
    }

    @Override
    public List<WorkflowTaskEntity> selectApprovalHistory(Long instanceId) {
        return workflowTaskMapper.selectCompletedByInstanceId(instanceId);
    }

    // ==================== 会签相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startCounterSign(Long taskId, List<Long> assigneeIds) {
        WorkflowTaskEntity parentTask = workflowTaskMapper.selectById(taskId);
        if (parentTask == null) {
            throw new ServiceException("任务不存在");
        }

        // 标记原任务为会签中
        parentTask.setTaskStatus("1"); // 处理中
        parentTask.setTaskType("counterSign");
        workflowTaskMapper.updateById(parentTask);

        // 为每个会签人创建子任务
        for (Long assigneeId : assigneeIds) {
            WorkflowTaskEntity subtask = new WorkflowTaskEntity();
            subtask.setInstanceId(parentTask.getInstanceId());
            subtask.setNodeId(parentTask.getNodeId());
            subtask.setNodeName(parentTask.getNodeName());
            subtask.setTaskType("counterSign");
            subtask.setTaskStatus("0"); // 待处理
            subtask.setAssigneeId(assigneeId);
            // assigneeName需要查询
            subtask.setReceiveTime(LocalDateTime.now());
            subtask.setCreateTime(LocalDateTime.now());
            subtask.setUpdateTime(LocalDateTime.now());
            workflowTaskMapper.insert(subtask);
        }

        return assigneeIds.size();
    }

    @Override
    public Map<String, Object> getCounterSignResult(Long taskId) {
        WorkflowTaskEntity task = workflowTaskMapper.selectById(taskId);
        List<WorkflowTaskEntity> subtasks = workflowTaskMapper.selectByNodeId(task.getNodeId());

        long completed = subtasks.stream().filter(t -> "2".equals(t.getTaskStatus())).count();
        long total = subtasks.size();
        long agree = subtasks.stream().filter(t -> "agree".equals(t.getResult())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("agree", agree);
        result.put("reject", total - completed - agree);
        result.put("completed", completed == total);

        return result;
    }

    // ==================== 节点配置相关 ====================

    @Override
    public List<WorkflowNodeEntity> selectWorkflowNodes(Long definitionId) {
        return workflowNodeMapper.selectByDefinitionId(definitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveWorkflowNodes(Long definitionId, List<WorkflowNodeEntity> nodes) {
        // 删除原有节点
        workflowNodeMapper.deleteByDefinitionId(definitionId);
        // 保存新节点
        if (nodes != null && !nodes.isEmpty()) {
            for (WorkflowNodeEntity node : nodes) {
                node.setDefinitionId(definitionId);
                node.setCreateTime(LocalDateTime.now());
                node.setUpdateTime(LocalDateTime.now());
            }
            return workflowNodeMapper.batchInsert(nodes);
        }
        return 0;
    }

    @Override
    public Map<String, Object> getFlowGraphic(Long definitionId) {
        WorkflowDefinitionEntity definition = workflowDefinitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new ServiceException("工作流不存在");
        }

        List<WorkflowNodeEntity> nodes = workflowNodeMapper.selectByDefinitionId(definitionId);

        Map<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("graphic", definition.getFlowGraphic());
        result.put("definitionId", definitionId);
        result.put("workflowName", definition.getWorkflowName());

        return result;
    }

    // ==================== 私有方法 ====================

    private void createTask(WorkflowInstanceEntity instance, WorkflowNodeEntity node, Long assigneeId, String assigneeName) {
        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setInstanceId(instance.getInstanceId());
        task.setNodeId(node.getNodeId());
        task.setNodeName(node.getNodeName());
        task.setTaskType("approve");
        task.setTaskStatus("0"); // 待处理

        // 确定审批人
        if (assigneeId != null) {
            task.setAssigneeId(assigneeId);
            task.setAssigneeName(assigneeName);
        } else {
            // TODO: 根据节点配置解析审批人
            task.setAssigneeId(0L);
            task.setAssigneeName("待指定");
        }

        task.setReceiveTime(LocalDateTime.now());
        if (node.getTimeoutMinutes() != null && node.getTimeoutMinutes() > 0) {
            task.setDueTime(LocalDateTime.now().plusMinutes(node.getTimeoutMinutes()));
        }
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        workflowTaskMapper.insert(task);
    }

    private WorkflowNodeEntity findNextNode(Long definitionId, WorkflowNodeEntity currentNode, Map<String, Object> variables) {
        // TODO: 实现节点路由逻辑
        // 根据条件表达式找到下一个节点
        List<WorkflowNodeEntity> allNodes = workflowNodeMapper.selectByDefinitionId(definitionId);

        // 简单实现：找到排序大于当前节点的第一个审批节点
        return allNodes.stream()
                .filter(n -> "approve".equals(n.getNodeType()) || "counterSign".equals(n.getNodeType()))
                .filter(n -> n.getSort() > currentNode.getSort())
                .findFirst()
                .orElse(null);
    }

    private String generateInstanceCode() {
        return "WF" + System.currentTimeMillis();
    }
}