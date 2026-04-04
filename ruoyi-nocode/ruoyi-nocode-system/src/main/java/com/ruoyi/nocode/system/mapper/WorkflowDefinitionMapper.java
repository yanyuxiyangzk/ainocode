package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.WorkflowDefinitionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流定义Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface WorkflowDefinitionMapper extends BaseMapper<WorkflowDefinitionEntity> {

    /**
     * 查询工作流定义列表
     */
    List<WorkflowDefinitionEntity> selectWorkflowDefinitionList(WorkflowDefinitionEntity workflowDefinition);

    /**
     * 根据编码查询
     */
    WorkflowDefinitionEntity selectByWorkflowCode(@Param("workflowCode") String workflowCode);

    /**
     * 查询最新版本的工作流
     */
    List<WorkflowDefinitionEntity> selectLatestVersions();

    /**
     * 查询指定工作流的所有版本
     */
    List<WorkflowDefinitionEntity> selectVersionsByCode(@Param("workflowCode") String workflowCode);

    /**
     * 查询启用的工作流
     */
    List<WorkflowDefinitionEntity> selectEnabledList();

    /**
     * 更新版本标识
     */
    int updateVersionFlag(@Param("definitionId") Long definitionId, @Param("isLatest") String isLatest);
}