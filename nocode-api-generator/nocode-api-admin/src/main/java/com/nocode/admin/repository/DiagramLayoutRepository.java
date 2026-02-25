package com.nocode.admin.repository;

import com.nocode.admin.entity.DiagramLayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ER图布局存储Repository
 */
@Repository
public interface DiagramLayoutRepository extends JpaRepository<DiagramLayoutEntity, Long> {
    
    /**
     * 根据数据源和schema查找布局
     */
    Optional<DiagramLayoutEntity> findByDatasourceNameAndSchemaName(String datasourceName, String schemaName);
    
    /**
     * 根据数据源查找布局（无schema的情况，如MySQL）
     */
    Optional<DiagramLayoutEntity> findByDatasourceNameAndSchemaNameIsNull(String datasourceName);
    
    /**
     * 删除指定数据源的布局
     */
    void deleteByDatasourceName(String datasourceName);
}
