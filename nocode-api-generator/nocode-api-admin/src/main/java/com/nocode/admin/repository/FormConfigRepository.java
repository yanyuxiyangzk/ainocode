package com.nocode.admin.repository;

import com.nocode.admin.entity.FormConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 表单配置Repository
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Repository
public interface FormConfigRepository extends JpaRepository<FormConfigEntity, Long> {

    /**
     * 根据状态查询表单
     *
     * @param status 表单状态
     * @return 表单列表
     */
    List<FormConfigEntity> findByStatus(String status);

    /**
     * 根据名称模糊查询
     *
     * @param name 表单名称
     * @return 表单列表
     */
    List<FormConfigEntity> findByNameContaining(String name);

    /**
     * 根据名称精确查询
     *
     * @param name 表单名称
     * @return 表单
     */
    Optional<FormConfigEntity> findByName(String name);

    /**
     * 根据创建人查询
     *
     * @param createBy 创建人
     * @return 表单列表
     */
    List<FormConfigEntity> findByCreateBy(String createBy);

    /**
     * 根据状态和创建人查询
     *
     * @param status    表单状态
     * @param createBy  创建人
     * @return 表单列表
     */
    List<FormConfigEntity> findByStatusAndCreateBy(String status, String createBy);

    /**
     * 查询已发布的表单
     *
     * @return 已发布表单列表
     */
    @Query("SELECT f FROM FormConfigEntity f WHERE f.status = 'PUBLISHED'")
    List<FormConfigEntity> findPublishedForms();

    /**
     * 根据版本号查询
     *
     * @param version 版本号
     * @return 表单列表
     */
    List<FormConfigEntity> findByVersion(Integer version);

    /**
     * 统计指定状态的表单数量
     *
     * @param status 表单状态
     * @return 数量
     */
    long countByStatus(String status);

    /**
     * 根据删除标志查询
     *
     * @param delFlag 删除标志
     * @return 表单列表
     */
    List<FormConfigEntity> findByDelFlag(String delFlag);

    /**
     * 根据删除标志和状态查询
     *
     * @param delFlag 删除标志
     * @param status  表单状态
     * @return 表单列表
     */
    List<FormConfigEntity> findByDelFlagAndStatus(String delFlag, String status);
}
