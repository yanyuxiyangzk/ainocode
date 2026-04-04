package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.WorkflowNodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流节点Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface WorkflowNodeMapper extends BaseMapper<WorkflowNodeEntity> {

    /**
     * 根据定义ID查询所有节点
     */
    List<WorkflowNodeEntity> selectByDefinitionId(@Param("definitionId") Long definitionId);

    /**
     * 根据节点编码查询
     */
    WorkflowNodeEntity selectByNodeCode(@Param("definitionId") Long definitionId, @Param("nodeCode") String nodeCode);

    /**
     * 根据节点类型查询
     */
    List<WorkflowNodeEntity> selectByNodeType(@Param("definitionId") Long definitionId, @Param("nodeType") String nodeType);

    /**
     * 获取开始节点
     */
    WorkflowNodeEntity selectStartNode(@Param("definitionId") Long definitionId);

    /**
     * 获取结束节点
     */
    WorkflowNodeEntity selectEndNode(@Param("definitionId") Long definitionId);

    /**
     * 根据定义ID删除所有节点
     */
    int deleteByDefinitionId(@Param("definitionId") Long definitionId);

    /**
     * 批量插入节点
     */
    int batchInsert(@Param("nodes") List<WorkflowNodeEntity> nodes);
}