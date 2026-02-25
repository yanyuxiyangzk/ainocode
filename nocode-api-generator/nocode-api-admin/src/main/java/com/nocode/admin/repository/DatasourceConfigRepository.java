package com.nocode.admin.repository;

import com.nocode.admin.entity.DatasourceConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据源配置Repository
 */
@Repository
public interface DatasourceConfigRepository extends JpaRepository<DatasourceConfigEntity, Long> {
    /**
     * 根据名称查找数据源配置
     */
    Optional<DatasourceConfigEntity> findByName(String name);

    /**
     * 检查数据源名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 获取所有启用的数据源配置
     */
    List<DatasourceConfigEntity> findByEnabledTrue();

    /**
     * 删除数据源配置
     */
    void deleteByName(String name);
}
