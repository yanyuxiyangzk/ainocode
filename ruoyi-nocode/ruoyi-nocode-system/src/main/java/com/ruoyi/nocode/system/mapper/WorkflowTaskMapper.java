package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.WorkflowTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流任务Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface WorkflowTaskMapper extends BaseMapper<WorkflowTaskEntity> {

    /**
     * 根据实例ID查询所有任务
     */
    List<WorkflowTaskEntity> selectByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * 根据办理人查询待办任务
     */
    List<WorkflowTaskEntity> selectPendingByAssigneeId(@Param("assigneeId") Long assigneeId);

    /**
     * 根据节点ID查询任务
     */
    List<WorkflowTaskEntity> selectByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 查询转交任务
     */
    List<WorkflowTaskEntity> selectDelegateTasks(@Param("originalAssigneeId") Long originalAssigneeId);

    /**
     * 更新任务状态
     */
    int updateTaskStatus(@Param("taskId") Long taskId, @Param("status") String status);

    /**
     * 查询已完成的任务
     */
    List<WorkflowTaskEntity> selectCompletedByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * 根据外部任务ID查询
     */
    WorkflowTaskEntity selectByExternalTaskId(@Param("externalTaskId") String externalTaskId);
}