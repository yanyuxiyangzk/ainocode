package com.nocode.admin.repository;

import com.nocode.admin.entity.WorkflowInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 流程实例Repository
 */
@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstanceEntity, Long> {

    /**
     * 根据流程定义ID查询
     */
    List<WorkflowInstanceEntity> findByDefinitionId(Long definitionId);

    /**
     * 根据申请人查询
     */
    List<WorkflowInstanceEntity> findByApplicant(String applicant);

    /**
     * 根据业务key查询
     */
    List<WorkflowInstanceEntity> findByBusinessKey(String businessKey);

    /**
     * 根据实例状态查询
     */
    List<WorkflowInstanceEntity> findByInstanceStatus(String instanceStatus);
}