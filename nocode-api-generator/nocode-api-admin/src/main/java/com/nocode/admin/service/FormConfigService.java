package com.nocode.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.FormConfigEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.exception.WorkflowException;
import com.nocode.admin.repository.FormConfigRepository;
import com.nocode.core.ddl.DdlGenerator;
import com.nocode.core.entity.ApiResult;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.executor.SqlExecutor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表单配置Service
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Service
@RequiredArgsConstructor
public class FormConfigService {
    private static final Logger log = LoggerFactory.getLogger(FormConfigService.class);

    private final FormConfigRepository formConfigRepository;
    private final SqlExecutor sqlExecutor;
    private final ObjectMapper objectMapper;

    /**
     * 创建表单
     *
     * @param entity 表单实体
     * @return 创建后的表单
     */
    @Transactional
    public FormConfigEntity create(FormConfigEntity entity) {
        entity.setVersion(1);
        entity.setStatus("DRAFT");
        return formConfigRepository.save(entity);
    }

    /**
     * 更新表单
     *
     * @param id     表单ID
     * @param entity 表单实体
     * @return 更新后的表单
     */
    @Transactional
    public FormConfigEntity update(Long id, FormConfigEntity entity) {
        Optional<FormConfigEntity> existing = formConfigRepository.findById(id);
        if (existing.isPresent()) {
            FormConfigEntity form = existing.get();
            form.setName(entity.getName());
            form.setDescription(entity.getDescription());
            form.setFormConfig(entity.getFormConfig());
            form.setVersion(form.getVersion() + 1);
            return formConfigRepository.save(form);
        }
        throw new ResourceNotFoundException("Form", id);
    }

    /**
     * 获取表单
     *
     * @param id 表单ID
     * @return 表单实体
     */
    public Optional<FormConfigEntity> getById(Long id) {
        return formConfigRepository.findById(id);
    }

    /**
     * 查询所有表单
     *
     * @return 表单列表
     */
    public List<FormConfigEntity> findAll() {
        return formConfigRepository.findAll();
    }

    /**
     * 根据状态查询
     *
     * @param status 表单状态
     * @return 表单列表
     */
    public List<FormConfigEntity> findByStatus(String status) {
        return formConfigRepository.findByStatus(status);
    }

    /**
     * 发布表单 - 自动建表
     *
     * @param id 表单ID
     * @return 发布后的表单
     */
    @Transactional
    public FormConfigEntity publish(Long id) {
        Optional<FormConfigEntity> existing = formConfigRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Form", id);
        }

        FormConfigEntity form = existing.get();

        // 如果已经是发布状态，直接返回
        if ("PUBLISHED".equals(form.getStatus())) {
            return form;
        }

        // 解析表单JSON配置
        try {
            Map<String, Object> formConfig = objectMapper.readValue(form.getFormConfig(), Map.class);

            // 生成DDL
            String tableName = generateTableName(formConfig, form);
            String ddl = DdlGenerator.generateDdl(formConfig, tableName, form.getName(), DatabaseType.MYSQL);

            log.info("生成建表DDL: {}", ddl);

            // 执行DDL创建表
            ApiResult result = sqlExecutor.execute("default", ddl);
            if (!result.isSuccess()) {
                throw new WorkflowException("建表失败: " + result.getMessage());
            }

            // 更新表单状态和表名
            form.setStatus("PUBLISHED");
            form.setTableName(tableName);

            log.info("表单[{}]发布成功，建表[{}]", form.getName(), tableName);

            return formConfigRepository.save(form);

        } catch (JsonProcessingException e) {
            log.error("解析表单JSON失败", e);
            throw new WorkflowException("解析表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据表单配置生成表名
     */
    private String generateTableName(Map<String, Object> formConfig, FormConfigEntity form) {
        // 1. 优先使用formConfig中的tableName
        Object tableName = formConfig.get("tableName");
        if (tableName != null && !tableName.toString().trim().isEmpty()) {
            return tableName.toString().trim();
        }

        // 2. 使用formId生成表名（添加t_前缀）
        Object formId = formConfig.get("formId");
        if (formId != null && !formId.toString().trim().isEmpty()) {
            return "t_" + formId.toString().trim();
        }

        // 3. 使用表单名称生成表名
        if (form.getName() != null && !form.getName().trim().isEmpty()) {
            String name = form.getName().trim()
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .toLowerCase();
            return "t_" + name;
        }

        // 4. 默认表名
        return "t_form_" + form.getId();
    }

    /**
     * 撤销发布
     *
     * @param id 表单ID
     * @return 撤销后的表单
     */
    @Transactional
    public FormConfigEntity unpublish(Long id) {
        Optional<FormConfigEntity> existing = formConfigRepository.findById(id);
        if (existing.isPresent()) {
            FormConfigEntity form = existing.get();
            form.setStatus("DRAFT");
            return formConfigRepository.save(form);
        }
        throw new ResourceNotFoundException("Form", id);
    }

    /**
     * 删除表单（软删除）
     *
     * @param id 表单ID
     */
    @Transactional
    public void delete(Long id) {
        formConfigRepository.findById(id).ifPresent(form -> {
            form.setDelFlag("1");
            formConfigRepository.save(form);
        });
    }

    /**
     * 彻底删除表单（硬删除）
     *
     * @param id 表单ID
     */
    @Transactional
    public void hardDelete(Long id) {
        formConfigRepository.deleteById(id);
    }

    /**
     * 根据名称模糊查询
     *
     * @param name 表单名称
     * @return 表单列表
     */
    public List<FormConfigEntity> searchByName(String name) {
        return formConfigRepository.findByNameContaining(name);
    }

    /**
     * 根据名称精确查询
     *
     * @param name 表单名称
     * @return 表单实体
     */
    public Optional<FormConfigEntity> findByName(String name) {
        return formConfigRepository.findByName(name);
    }

    /**
     * 根据创建人查询
     *
     * @param createBy 创建人
     * @return 表单列表
     */
    public List<FormConfigEntity> findByCreateBy(String createBy) {
        return formConfigRepository.findByCreateBy(createBy);
    }

    /**
     * 获取所有已发布的表单
     *
     * @return 已发布表单列表
     */
    public List<FormConfigEntity> findPublishedForms() {
        return formConfigRepository.findPublishedForms();
    }

    /**
     * 复制表单
     *
     * @param id      原表单ID
     * @param newName 新表单名称
     * @return 复制后的表单
     */
    @Transactional
    public FormConfigEntity copyForm(Long id, String newName) {
        Optional<FormConfigEntity> existing = formConfigRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Form", id);
        }
        FormConfigEntity original = existing.get();
        FormConfigEntity copy = new FormConfigEntity();
        copy.setName(newName);
        copy.setDescription(original.getDescription());
        copy.setFormConfig(original.getFormConfig());
        copy.setStatus("DRAFT");
        copy.setVersion(1);
        copy.setCreateBy(original.getCreateBy());
        return formConfigRepository.save(copy);
    }

    /**
     * 统计指定状态的表单数量
     *
     * @param status 表单状态
     * @return 表单数量
     */
    public long countByStatus(String status) {
        return formConfigRepository.countByStatus(status);
    }

    /**
     * 校验表单名称是否存在
     *
     * @param name 表单名称
     * @return 是否存在
     */
    public boolean existsByName(String name) {
        return formConfigRepository.findByName(name).isPresent();
    }
}
