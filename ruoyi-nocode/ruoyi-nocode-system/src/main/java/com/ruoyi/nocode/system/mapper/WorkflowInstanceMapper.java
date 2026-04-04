package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.WorkflowInstanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流实例Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface WorkflowInstanceMapper extends BaseMapper<WorkflowInstanceEntity> {

    /**
     * 查询工作流实例列表
     */
    List<WorkflowInstanceEntity> selectWorkflowInstanceList(WorkflowInstanceEntity workflowInstance);

    /**
     * 根据实例编码查询
     */
    WorkflowInstanceEntity selectByInstanceCode(@Param("instanceCode") String instanceCode);

    /**
     * 查询申请人的实例
     */
    List<WorkflowInstanceEntity> selectByApplicantId(@Param("applicantId") Long applicantId);

    /**
     * 查询指定定义的所有实例
     */
    List<WorkflowInstanceEntity> selectByDefinitionId(@Param("definitionId") Long definitionId);

    /**
     * 查询待审批实例
     */
    List<WorkflowInstanceEntity> selectPendingInstances(@Param("userId") Long userId);

    /**
     * 更新实例状态
     */
    int updateInstanceStatus(@Param("instanceId") Long instanceId, @Param("status") String status);
}