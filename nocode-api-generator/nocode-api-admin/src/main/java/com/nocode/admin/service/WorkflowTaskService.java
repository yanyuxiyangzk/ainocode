package com.nocode.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.WorkflowDefinitionEntity;
import com.nocode.admin.entity.WorkflowInstanceEntity;
import com.nocode.admin.entity.WorkflowTaskEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.exception.WorkflowException;
import com.nocode.admin.repository.WorkflowDefinitionRepository;
import com.nocode.admin.repository.WorkflowInstanceRepository;
import com.nocode.admin.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 工作流任务Service - 处理工作流任务执行逻辑
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskService {

    private final WorkflowTaskRepository workflowTaskRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建任务
     */
    @Transactional
    public WorkflowTaskEntity createTask(Long instanceId, String nodeId, String nodeName, String assignee) {
        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setInstanceId(instanceId);
        task.setNodeId(nodeId);
        task.setNodeName(nodeName);
        task.setAssignee(assignee);
        task.setTaskStatus("PENDING");
        task.setPriority("NORMAL");
        return workflowTaskRepository.save(task);
    }

    /**
     * 签收任务
     */
    @Transactional
    public WorkflowTaskEntity claimTask(Long taskId, String userId) {
        WorkflowTaskEntity task = workflowTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (!"PENDING".equals(task.getTaskStatus())) {
            throw new WorkflowException("Task is not pending: " + task.getTaskStatus());
        }

        task.setAssignee(userId);
        task.setTaskStatus("CLAIMED");
        task.setClaimTime(LocalDateTime.now());

        log.info("Task claimed: taskId={}, userId={}", taskId, userId);
        return workflowTaskRepository.save(task);
    }

    /**
     * 完成任务
     */
    @Transactional
    public WorkflowTaskEntity completeTask(Long taskId, String comment) {
        WorkflowTaskEntity task = workflowTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (!"CLAIMED".equals(task.getTaskStatus())) {
            throw new WorkflowException("Task is not claimed");
        }

        task.setTaskStatus("COMPLETED");
        task.setCompleteTime(LocalDateTime.now());
        if (comment != null) {
            task.setComment(comment);
        }

        // 更新流程实例到下一节点
        moveToNextNode(task);

        log.info("Task completed: taskId={}", taskId);
        return workflowTaskRepository.save(task);
    }

    /**
     * 驳回任务
     */
    @Transactional
    public WorkflowTaskEntity rejectTask(Long taskId, String comment) {
        WorkflowTaskEntity task = workflowTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        task.setTaskStatus("REJECTED");
        task.setCompleteTime(LocalDateTime.now());
        if (comment != null) {
            task.setComment(comment);
        }

        // 更新流程实例状态
        WorkflowInstanceEntity instance = workflowInstanceRepository.findById(task.getInstanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow instance"));

        instance.setInstanceStatus("REJECTED");
        instance.setEndTime(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        log.info("Task rejected: taskId={}, comment={}", taskId, comment);
        return workflowTaskRepository.save(task);
    }

    /**
     * 转交任务
     */
    @Transactional
    public WorkflowTaskEntity transferTask(Long taskId, String newAssignee) {
        WorkflowTaskEntity task = workflowTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        String oldAssignee = task.getAssignee();
        task.setAssignee(newAssignee);

        log.info("Task transferred: taskId={}, from={}, to={}", taskId, oldAssignee, newAssignee);
        return workflowTaskRepository.save(task);
    }

    /**
     * 会签任务
     *
     * @param taskId   任务ID
     * @param userId   用户ID
     * @param comment  审批意见
     * @param approved 是否同意
     * @return 会签结果信息
     */
    @Transactional
    public Map<String, Object> countersignTask(Long taskId, String userId, String comment, boolean approved) {
        WorkflowTaskEntity task = workflowTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("userId", userId);
        result.put("approved", approved);

        if (task.getIsCounterSign() == null || !task.getIsCounterSign()) {
            throw new WorkflowException("Task does not support countersign");
        }

        int countersignedCount = task.getCountersignedCount() == null ? 0 : task.getCountersignedCount();
        int countersignCount = task.getCountersignCount() == null ? 0 : task.getCountersignCount();

        countersignedCount++;
        task.setCountersignedCount(countersignedCount);
        task.setTaskStatus("COMPLETED");
        task.setComment(comment);
        task.setCountersignResult(approved ? "AGREE" : "REJECT");
        task.setCompleteTime(LocalDateTime.now());
        workflowTaskRepository.save(task);

        result.put("countersignedCount", countersignedCount);
        result.put("countersignCount", countersignCount);

        WorkflowInstanceEntity instance = workflowInstanceRepository.findById(task.getInstanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow instance"));

        if (countersignCount > 0 && countersignedCount >= countersignCount) {
            boolean allApproved = checkCountersignResult(task.getInstanceId(), task.getNodeId());
            result.put("counterSignComplete", true);
            result.put("outcome", allApproved ? "AGREE" : "REJECT");

            if (allApproved) {
                moveToNextNode(task);
            } else {
                rejectTask(taskId, "会签未通过");
            }
        } else {
            result.put("counterSignComplete", false);
        }

        return result;
    }

    /**
     * 检查会签结果
     */
    private boolean checkCountersignResult(Long instanceId, String nodeId) {
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceIdAndNodeId(instanceId, nodeId);
        return tasks.stream()
                .allMatch(t -> "AGREE".equals(t.getCountersignResult()));
    }

    /**
     * 创建会签任务
     *
     * @param instanceId 实例ID
     * @param nodeId     节点ID
     * @param nodeName   节点名称
     * @param assignees  办理人列表
     * @return 任务列表
     */
    @Transactional
    public List<WorkflowTaskEntity> createCountersignTasks(Long instanceId, String nodeId, String nodeName, List<String> assignees) {
        List<WorkflowTaskEntity> tasks = new ArrayList<>();
        for (String assignee : assignees) {
            WorkflowTaskEntity task = new WorkflowTaskEntity();
            task.setInstanceId(instanceId);
            task.setNodeId(nodeId);
            task.setNodeName(nodeName);
            task.setIsCounterSign(true);
            task.setTaskStatus("PENDING");
            task.setAssignee(assignee);
            task.setCountersignCount(assignees.size());
            task.setCountersignedCount(0);
            tasks.add(workflowTaskRepository.save(task));
        }
        return tasks;
    }

    /**
     * 转交到指定节点
     *
     * @param instanceId      实例ID
     * @param targetNodeId    目标节点ID
     * @param targetNodeName  目标节点名称
     * @param targetUserId    目标用户ID
     * @param comment         转交意见
     * @return 新任务
     */
    @Transactional
    public WorkflowTaskEntity transferToNode(Long instanceId, String targetNodeId, String targetNodeName,
                                            String targetUserId, String comment) {
        WorkflowInstanceEntity instance = workflowInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow instance", instanceId));

        instance.setInstanceStatus("RUNNING");
        instance.setCurrentNodeId(targetNodeId);
        instance.setCurrentNodeName(targetNodeName);
        workflowInstanceRepository.save(instance);

        List<WorkflowTaskEntity> pendingTasks = workflowTaskRepository.findPendingTasksByInstanceId(instanceId);
        for (WorkflowTaskEntity t : pendingTasks) {
            t.setTaskStatus("CANCELLED");
            t.setComment("流程转交，被取消");
            t.setCompleteTime(LocalDateTime.now());
            workflowTaskRepository.save(t);
        }

        WorkflowTaskEntity newTask = new WorkflowTaskEntity();
        newTask.setInstanceId(instanceId);
        newTask.setNodeId(targetNodeId);
        newTask.setNodeName(targetNodeName);
        newTask.setNodeType("TASK");
        newTask.setTaskStatus("CLAIMED");
        newTask.setAssignee(targetUserId);
        newTask.setComment(comment);

        log.info("Task transferred to node: instanceId={}, targetNodeId={}, targetUserId={}", instanceId, targetNodeId, targetUserId);
        return workflowTaskRepository.save(newTask);
    }

    /**
     * 获取下一节点并移动
     */
    private void moveToNextNode(WorkflowTaskEntity task) {
        WorkflowInstanceEntity instance = workflowInstanceRepository.findById(task.getInstanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow instance"));

        WorkflowDefinitionEntity definition = workflowDefinitionRepository.findById(instance.getDefinitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow definition"));

        try {
            // 解析节点定义和流转
            List<Map<String, Object>> nodes = objectMapper.readValue(
                    definition.getNodeDefinition(), List.class);
            List<Map<String, Object>> flows = objectMapper.readValue(
                    definition.getSequenceFlow(), List.class);

            // 查找当前节点的下一个节点
            String currentNodeId = task.getNodeId();
            Map<String, Object> nextNode = findNextNode(currentNodeId, nodes, flows);

            if (nextNode != null) {
                String nextNodeId = (String) nextNode.get("id");
                String nextNodeName = (String) nextNode.get("name");
                String nextNodeType = (String) nextNode.get("type");

                // 更新实例当前节点
                instance.setCurrentNodeId(nextNodeId);
                instance.setCurrentNodeName(nextNodeName);

                // 如果下一节点是用户任务，创建新任务
                if ("userTask".equals(nextNodeType)) {
                    createTask(instance.getId(), nextNodeId, nextNodeName, null);
                } else if ("endEvent".equals(nextNodeType)) {
                    // 结束事件
                    instance.setInstanceStatus("COMPLETED");
                    instance.setEndTime(LocalDateTime.now());
                }

                workflowInstanceRepository.save(instance);
                log.info("Instance moved to next node: instanceId={}, nextNodeId={}", instance.getId(), nextNodeId);
            } else {
                // 没有下一节点，流程结束
                instance.setInstanceStatus("COMPLETED");
                instance.setEndTime(LocalDateTime.now());
                instance.setCurrentNodeId("end");
                instance.setCurrentNodeName("结束");
                workflowInstanceRepository.save(instance);
                log.info("Instance completed: instanceId={}", instance.getId());
            }
        } catch (Exception e) {
            log.error("Failed to move to next node", e);
            throw new WorkflowException("Failed to process next node: " + e.getMessage(), e);
        }
    }

    /**
     * 查找下一节点
     */
    private Map<String, Object> findNextNode(String currentNodeId, List<Map<String, Object>> nodes,
                                             List<Map<String, Object>> flows) {
        // 简单实现：查找流出路径的下一节点
        for (Map<String, Object> flow : flows) {
            String sourceRef = (String) flow.get("sourceRef");
            if (sourceRef != null && sourceRef.equals(currentNodeId)) {
                String targetRef = (String) flow.get("targetRef");
                for (Map<String, Object> node : nodes) {
                    String nodeId = (String) node.get("id");
                    if (nodeId != null && nodeId.equals(targetRef)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取任务
     */
    public Optional<WorkflowTaskEntity> getById(Long id) {
        return workflowTaskRepository.findById(id);
    }

    /**
     * 查询实例的所有任务
     */
    public List<WorkflowTaskEntity> findByInstanceId(Long instanceId) {
        return workflowTaskRepository.findByInstanceId(instanceId);
    }

    /**
     * 查询用户的待办任务
     */
    public List<WorkflowTaskEntity> findTodoTasks(String userId) {
        return workflowTaskRepository.findByAssigneeAndTaskStatus(userId, "CLAIMED");
    }

    /**
     * 查询用户的待签收任务
     */
    public List<WorkflowTaskEntity> findPendingTasks(String userId) {
        return workflowTaskRepository.findByAssigneeAndTaskStatus(userId, "PENDING");
    }

    /**
     * 删除任务
     */
    @Transactional
    public void delete(Long id) {
        workflowTaskRepository.deleteById(id);
    }
}