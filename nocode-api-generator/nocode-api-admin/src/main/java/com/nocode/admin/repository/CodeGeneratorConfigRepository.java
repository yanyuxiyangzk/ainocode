package com.nocode.admin.repository;

import com.nocode.admin.entity.CodeGeneratorConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 代码生成器配置Repository
 */
@Repository
public interface CodeGeneratorConfigRepository extends JpaRepository<CodeGeneratorConfigEntity, Long> {

    /**
     * 根据状态查询
     */
    List<CodeGeneratorConfigEntity> findByStatus(String status);

    /**
     * 根据模块名查询
     */
    List<CodeGeneratorConfigEntity> findByModuleName(String moduleName);
}