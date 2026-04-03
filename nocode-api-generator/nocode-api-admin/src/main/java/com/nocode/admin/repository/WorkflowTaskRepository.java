package com.nocode.admin.repository;

import com.nocode.admin.entity.WorkflowTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 工作流任务Repository
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTaskEntity, Long> {

    /**
     * 根据实例ID查询任务列表
     *
     * @param instanceId 实例ID
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByInstanceId(Long instanceId);

    /**
     * 根据办理人查询任务列表
     *
     * @param assignee 办理人
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByAssignee(String assignee);

    /**
     * 根据任务状态查询任务列表
     *
     * @param taskStatus 任务状态
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByTaskStatus(String taskStatus);

    /**
     * 根据办理人和任务状态查询
     *
     * @param assignee   办理人
     * @param taskStatus 任务状态
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByAssigneeAndTaskStatus(String assignee, String taskStatus);

    /**
     * 根据节点ID查询任务
     *
     * @param nodeId 节点ID
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByNodeId(String nodeId);

    /**
     * 根据实例ID和节点ID查询
     *
     * @param instanceId 实例ID
     * @param nodeId     节点ID
     * @return 任务列表
     */
    List<WorkflowTaskEntity> findByInstanceIdAndNodeId(Long instanceId, String nodeId);

    /**
     * 根据实例ID查询未完成任务
     *
     * @param instanceId 实例ID
     * @return 未完成任务列表
     */
    @Query("SELECT t FROM WorkflowTaskEntity t WHERE t.instanceId = :instanceId AND t.taskStatus NOT IN ('COMPLETED', 'CANCELLED')")
    List<WorkflowTaskEntity> findPendingTasksByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * 根据办理人查询待办任务数量
     *
     * @param assignee 办理人
     * @return 待办任务数量
     */
    long countByAssigneeAndTaskStatus(String assignee, String taskStatus);

    /**
     * 根据实例ID删除所有任务
     *
     * @param instanceId 实例ID
     */
    void deleteByInstanceId(Long instanceId);
}