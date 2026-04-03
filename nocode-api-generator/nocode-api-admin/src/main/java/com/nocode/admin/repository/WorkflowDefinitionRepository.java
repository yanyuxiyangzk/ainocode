package com.nocode.admin.repository;

import com.nocode.admin.entity.WorkflowDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 流程定义Repository
 */
@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinitionEntity, Long> {

    /**
     * 根据流程标识查询
     */
    Optional<WorkflowDefinitionEntity> findByProcessKey(String processKey);

    /**
     * 根据流程标识和版本查询
     */
    Optional<WorkflowDefinitionEntity> findByProcessKeyAndVersion(String processKey, Integer version);
}
