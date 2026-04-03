package com.nocode.admin.repository;

import com.nocode.admin.entity.FormComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 表单组件Repository
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Repository
public interface FormComponentRepository extends JpaRepository<FormComponentEntity, Long> {

    /**
     * 根据表单ID查询
     *
     * @param formId 表单ID
     * @return 组件列表
     */
    List<FormComponentEntity> findByFormId(Long formId);

    /**
     * 根据组件类型查询
     *
     * @param componentType 组件类型
     * @return 组件列表
     */
    List<FormComponentEntity> findByComponentType(String componentType);

    /**
     * 根据表单ID查询并按排序号排序
     *
     * @param formId 表单ID
     * @return 组件列表
     */
    List<FormComponentEntity> findByFormIdOrderBySort(Long formId);

    /**
     * 根据字段名查询
     *
     * @param fieldName 字段名
     * @return 组件列表
     */
    List<FormComponentEntity> findByFieldName(String fieldName);

    /**
     * 根据表单ID和组件类型查询
     *
     * @param formId        表单ID
     * @param componentType 组件类型
     * @return 组件列表
     */
    List<FormComponentEntity> findByFormIdAndComponentType(Long formId, String componentType);

    /**
     * 根据表单ID删除所有组件
     *
     * @param formId 表单ID
     */
    void deleteByFormId(Long formId);

    /**
     * 统计指定表单的组件数量
     *
     * @param formId 表单ID
     * @return 组件数量
     */
    long countByFormId(Long formId);

    /**
     * 根据是否必填查询
     *
     * @param required 是否必填
     * @return 组件列表
     */
    List<FormComponentEntity> findByRequired(Boolean required);

    /**
     * 查询所有组件类型
     *
     * @return 组件类型列表
     */
    @Query("SELECT DISTINCT c.componentType FROM FormComponentEntity c")
    List<String> findDistinctComponentTypes();
}