package com.nocode.admin.service;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 流程定义Service - 处理工作流核心业务逻辑
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowTaskRepository workflowTaskRepository;

    /**
     * 创建流程定义
     *
     * @param entity 流程定义实体
     * @return 创建后的流程定义
     */
    @Transactional
    public WorkflowDefinitionEntity create(WorkflowDefinitionEntity entity) {
        entity.setVersion(1);
        entity.setStatus("DRAFT");
        return workflowDefinitionRepository.save(entity);
    }

    /**
     * 更新流程定义
     *
     * @param id     流程定义ID
     * @param entity 流程定义实体
     * @return 更新后的流程定义
     */
    @Transactional
    public WorkflowDefinitionEntity update(Long id, WorkflowDefinitionEntity entity) {
        Optional<WorkflowDefinitionEntity> existing = workflowDefinitionRepository.findById(id);
        if (existing.isPresent()) {
            WorkflowDefinitionEntity workflow = existing.get();
            workflow.setName(entity.getName());
            workflow.setDescription(entity.getDescription());
            workflow.setDiagramJson(entity.getDiagramJson());
            workflow.setNodeDefinition(entity.getNodeDefinition());
            workflow.setSequenceFlow(entity.getSequenceFlow());
            workflow.setVersion(workflow.getVersion() + 1);
            return workflowDefinitionRepository.save(workflow);
        }
        throw new ResourceNotFoundException("Workflow", id);
    }

    /**
     * 获取流程定义
     *
     * @param id 流程定义ID
     * @return 流程定义
     */
    public Optional<WorkflowDefinitionEntity> getById(Long id) {
        return workflowDefinitionRepository.findById(id);
    }

    /**
     * 查询所有流程定义
     *
     * @return 流程定义列表
     */
    public List<WorkflowDefinitionEntity> findAll() {
        return workflowDefinitionRepository.findAll();
    }

    /**
     * 根据状态查询流程定义
     *
     * @param status 状态
     * @return 流程定义列表
     */
    public List<WorkflowDefinitionEntity> findByStatus(String status) {
        return workflowDefinitionRepository.findByStatus(status);
    }

    /**
     * 部署流程
     *
     * @param id 流程定义ID
     * @return 部署后的流程定义
     */
    @Transactional
    public WorkflowDefinitionEntity deploy(Long id) {
        Optional<WorkflowDefinitionEntity> existing = workflowDefinitionRepository.findById(id);
        if (existing.isPresent()) {
            WorkflowDefinitionEntity workflow = existing.get();
            workflow.setStatus("DEPLOYED");
            workflow.setSuspended(false);
            return workflowDefinitionRepository.save(workflow);
        }
        throw new ResourceNotFoundException("Workflow", id);
    }

    /**
     * 挂起流程
     *
     * @param id 流程定义ID
     * @return 挂起后的流程定义
     */
    @Transactional
    public WorkflowDefinitionEntity suspend(Long id) {
        Optional<WorkflowDefinitionEntity> existing = workflowDefinitionRepository.findById(id);
        if (existing.isPresent()) {
            WorkflowDefinitionEntity workflow = existing.get();
            workflow.setSuspended(true);
            workflow.setStatus("SUSPENDED");
            return workflowDefinitionRepository.save(workflow);
        }
        throw new ResourceNotFoundException("Workflow", id);
    }

    /**
     * 激活流程（恢复挂起的流程）
     *
     * @param id 流程定义ID
     * @return 激活后的流程定义
     */
    @Transactional
    public WorkflowDefinitionEntity activate(Long id) {
        Optional<WorkflowDefinitionEntity> existing = workflowDefinitionRepository.findById(id);
        if (existing.isPresent()) {
            WorkflowDefinitionEntity workflow = existing.get();
            workflow.setSuspended(false);
            workflow.setStatus("DEPLOYED");
            return workflowDefinitionRepository.save(workflow);
        }
        throw new ResourceNotFoundException("Workflow", id);
    }

    /**
     * 启动流程实例
     *
     * @param definitionId 流程定义ID
     * @param applicant    申请人
     * @param businessKey  业务key
     * @return 流程实例
     */
    @Transactional
    public WorkflowInstanceEntity startProcess(Long definitionId, String applicant, String businessKey) {
        Optional<WorkflowDefinitionEntity> definition = workflowDefinitionRepository.findById(definitionId);
        if (definition.isEmpty()) {
            throw new ResourceNotFoundException("Workflow definition", definitionId);
        }

        WorkflowDefinitionEntity def = definition.get();
        if (def.getSuspended()) {
            throw new WorkflowException("Workflow is suspended");
        }

        // 解析开始节点
        String startNodeId = "start";
        String startNodeName = "开始";

        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setDefinitionId(definitionId);
        instance.setApplicant(applicant);
        instance.setBusinessKey(businessKey);
        instance.setCurrentNodeId(startNodeId);
        instance.setCurrentNodeName(startNodeName);
        instance.setInstanceStatus("RUNNING");

        WorkflowInstanceEntity savedInstance = workflowInstanceRepository.save(instance);

        // 创建第一个任务
        createTaskForNode(savedInstance.getId(), startNodeId, startNodeName, null);

        log.info("Process started: definitionId={}, instanceId={}, applicant={}",
                definitionId, savedInstance.getId(), applicant);

        return savedInstance;
    }

    /**
     * 为节点创建任务
     */
    private WorkflowTaskEntity createTaskForNode(Long instanceId, String nodeId, String nodeName, String assignee) {
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
     * 完成任务
     *
     * @param instanceId 流程实例ID
     * @param userId     办理人
     * @param comment    审批意见
     * @return 流程实例
     */
    @Transactional
    public WorkflowInstanceEntity completeTask(Long instanceId, String userId, String comment) {
        Optional<WorkflowInstanceEntity> existing = workflowInstanceRepository.findById(instanceId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Workflow instance", instanceId);
        }

        WorkflowInstanceEntity instance = existing.get();
        if (!"RUNNING".equals(instance.getInstanceStatus())) {
            throw new WorkflowException("Workflow instance is not running");
        }

        // 更新当前任务
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceId(instanceId);
        for (WorkflowTaskEntity task : tasks) {
            if ("CLAIMED".equals(task.getTaskStatus()) && userId.equals(task.getAssignee())) {
                task.setTaskStatus("COMPLETED");
                task.setCompleteTime(LocalDateTime.now());
                task.setComment(comment);
                workflowTaskRepository.save(task);
            }
        }

        // 移动到下一节点
        instance.setCurrentNodeId("end");
        instance.setCurrentNodeName("结束");
        instance.setInstanceStatus("COMPLETED");
        instance.setEndTime(LocalDateTime.now());

        return workflowInstanceRepository.save(instance);
    }

    /**
     * 会签任务
     *
     * @param instanceId 流程实例ID
     * @param userId     办理人
     * @param agree      是否同意
     * @param comment    审批意见
     * @return 会签结果
     */
    @Transactional
    public Map<String, Object> counterSign(Long instanceId, String userId, boolean agree, String comment) {
        Optional<WorkflowInstanceEntity> existing = workflowInstanceRepository.findById(instanceId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Workflow instance", instanceId);
        }

        WorkflowInstanceEntity instance = existing.get();

        // 查找当前用户的会签任务
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceId(instanceId);
        WorkflowTaskEntity currentTask = null;
        for (WorkflowTaskEntity task : tasks) {
            if (userId.equals(task.getAssignee()) && "CLAIMED".equals(task.getTaskStatus())) {
                currentTask = task;
                break;
            }
        }

        if (currentTask == null) {
            throw new WorkflowException("No task found for user: " + userId);
        }

        // 更新会签结果
        currentTask.setTaskStatus("COMPLETED");
        currentTask.setCompleteTime(LocalDateTime.now());
        currentTask.setComment(comment);
        if (agree) {
            currentTask.setCountersignResult("AGREE");
        } else {
            currentTask.setCountersignResult("REJECT");
        }
        workflowTaskRepository.save(currentTask);

        // 统计会签结果
        int totalCount = 0;
        int agreeCount = 0;
        int rejectCount = 0;
        for (WorkflowTaskEntity task : tasks) {
            if ("COMPLETED".equals(task.getTaskStatus())) {
                totalCount++;
                if ("AGREE".equals(task.getCountersignResult())) {
                    agreeCount++;
                } else if ("REJECT".equals(task.getCountersignResult())) {
                    rejectCount++;
                }
            }
        }

        // 判断会签是否完成
        boolean counterSignComplete = false;
        String outcome = null;

        if (rejectCount > 0) {
            // 有人驳回，流程结束
            counterSignComplete = true;
            outcome = "REJECT";
            instance.setInstanceStatus("REJECTED");
            instance.setEndTime(LocalDateTime.now());
        } else if (agreeCount == totalCount) {
            // 全部同意，流程继续
            counterSignComplete = true;
            outcome = "AGREE";
        }

        workflowInstanceRepository.save(instance);

        return Map.of(
                "counterSignComplete", counterSignComplete,
                "outcome", outcome,
                "totalCount", totalCount,
                "agreeCount", agreeCount,
                "rejectCount", rejectCount
        );
    }

    /**
     * 驳回任务到上一节点
     *
     * @param instanceId 流程实例ID
     * @param userId     办理人
     * @param comment    驳回意见
     * @return 驳回后的流程实例
     */
    @Transactional
    public WorkflowInstanceEntity rejectToPreviousNode(Long instanceId, String userId, String comment) {
        Optional<WorkflowInstanceEntity> existing = workflowInstanceRepository.findById(instanceId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Workflow instance", instanceId);
        }

        WorkflowInstanceEntity instance = existing.get();
        if (!"RUNNING".equals(instance.getInstanceStatus())) {
            throw new WorkflowException("Workflow instance is not running");
        }

        // 查找并更新当前任务
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceId(instanceId);
        for (WorkflowTaskEntity task : tasks) {
            if (userId.equals(task.getAssignee()) && "CLAIMED".equals(task.getTaskStatus())) {
                task.setTaskStatus("REJECTED");
                task.setCompleteTime(LocalDateTime.now());
                task.setComment(comment);
                workflowTaskRepository.save(task);
            }
        }

        // 获取流程定义，查找上一节点
        Optional<WorkflowDefinitionEntity> definition = workflowDefinitionRepository.findById(instance.getDefinitionId());
        if (definition.isPresent()) {
            // 简化为驳回到上一个节点
            String previousNodeId = findPreviousNodeId(instance.getCurrentNodeId(), definition.get());
            if (previousNodeId != null) {
                instance.setCurrentNodeId(previousNodeId);
                // 创建驳回节点的新任务
                createTaskForNode(instance.getId(), previousNodeId, previousNodeId, null);
            }
        }

        instance.setInstanceStatus("REJECTED");
        workflowInstanceRepository.save(instance);

        log.info("Task rejected: instanceId={}, userId={}, comment={}", instanceId, userId, comment);

        return instance;
    }

    /**
     * 查找上一节点ID
     */
    private String findPreviousNodeId(String currentNodeId, WorkflowDefinitionEntity definition) {
        // 简化实现：实际应解析流程图JSON确定上一节点
        return "start";
    }

    /**
     * 转交任务
     *
     * @param instanceId  流程实例ID
     * @param userId      当前办理人
     * @param targetUserId 目标办理人
     * @param reason      转交原因
     * @return 转交后的任务
     */
    @Transactional
    public WorkflowTaskEntity transferTask(Long instanceId, String userId, String targetUserId, String reason) {
        Optional<WorkflowInstanceEntity> existing = workflowInstanceRepository.findById(instanceId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Workflow instance", instanceId);
        }

        WorkflowInstanceEntity instance = existing.get();
        if (!"RUNNING".equals(instance.getInstanceStatus())) {
            throw new WorkflowException("Workflow instance is not running");
        }

        // 查找当前用户的任务
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceId(instanceId);
        WorkflowTaskEntity currentTask = null;
        for (WorkflowTaskEntity task : tasks) {
            if (userId.equals(task.getAssignee()) && "CLAIMED".equals(task.getTaskStatus())) {
                currentTask = task;
                break;
            }
        }

        if (currentTask == null) {
            throw new WorkflowException("No task found for user: " + userId);
        }

        // 创建新任务给目标办理人
        WorkflowTaskEntity newTask = new WorkflowTaskEntity();
        newTask.setInstanceId(instanceId);
        newTask.setNodeId(currentTask.getNodeId());
        newTask.setNodeName(currentTask.getNodeName());
        newTask.setAssignee(targetUserId);
        newTask.setTaskStatus("PENDING");
        newTask.setPriority(currentTask.getPriority());
        newTask.setParentTaskId(currentTask.getId());
        newTask.setComment("由 " + userId + " 转交: " + reason);
        workflowTaskRepository.save(newTask);

        // 标记原任务为已转交
        currentTask.setTaskStatus("TRANSFERRED");
        currentTask.setCompleteTime(LocalDateTime.now());
        workflowTaskRepository.save(currentTask);

        log.info("Task transferred: instanceId={}, from={}, to={}, reason={}",
                instanceId, userId, targetUserId, reason);

        return newTask;
    }

    /**
     * 取消流程实例
     *
     * @param instanceId 流程实例ID
     * @param userId     取消人
     * @param reason     取消原因
     * @return 取消后的流程实例
     */
    @Transactional
    public WorkflowInstanceEntity cancelProcess(Long instanceId, String userId, String reason) {
        Optional<WorkflowInstanceEntity> existing = workflowInstanceRepository.findById(instanceId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Workflow instance", instanceId);
        }

        WorkflowInstanceEntity instance = existing.get();

        // 只有申请人或管理员可以取消
        if (!userId.equals(instance.getApplicant())) {
            throw new WorkflowException("Only applicant can cancel the process");
        }

        instance.setInstanceStatus("CANCELLED");
        instance.setEndTime(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        // 取消所有任务
        List<WorkflowTaskEntity> tasks = workflowTaskRepository.findByInstanceId(instanceId);
        for (WorkflowTaskEntity task : tasks) {
            if (!"COMPLETED".equals(task.getTaskStatus())) {
                task.setTaskStatus("CANCELLED");
                task.setCompleteTime(LocalDateTime.now());
                workflowTaskRepository.save(task);
            }
        }

        log.info("Process cancelled: instanceId={}, userId={}, reason={}", instanceId, userId, reason);

        return instance;
    }

    /**
     * 删除流程定义
     *
     * @param id 流程定义ID
     */
    @Transactional
    public void delete(Long id) {
        workflowDefinitionRepository.deleteById(id);
    }

    /**
     * 根据流程标识查询
     *
     * @param processKey 流程标识
     * @return 流程定义
     */
    public Optional<WorkflowDefinitionEntity> findByProcessKey(String processKey) {
        return workflowDefinitionRepository.findByProcessKey(processKey);
    }

    /**
     * 根据流程标识和版本查询
     *
     * @param processKey 流程标识
     * @param version    版本号
     * @return 流程定义
     */
    public Optional<WorkflowDefinitionEntity> findByProcessKeyAndVersion(String processKey, Integer version) {
        return workflowDefinitionRepository.findByProcessKeyAndVersion(processKey, version);
    }
}
