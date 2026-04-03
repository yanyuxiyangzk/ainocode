package com.nocode.admin.service;

import com.nocode.admin.entity.FormComponentEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.repository.FormComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 表单组件Service
 */
@Service
@RequiredArgsConstructor
public class FormComponentService {

    private final FormComponentRepository formComponentRepository;

    /**
     * 创建组件
     */
    @Transactional
    public FormComponentEntity create(FormComponentEntity entity) {
        return formComponentRepository.save(entity);
    }

    /**
     * 更新组件
     */
    @Transactional
    public FormComponentEntity update(Long id, FormComponentEntity entity) {
        Optional<FormComponentEntity> existing = formComponentRepository.findById(id);
        if (existing.isPresent()) {
            FormComponentEntity component = existing.get();
            component.setComponentType(entity.getComponentType());
            component.setLabel(entity.getLabel());
            component.setFieldName(entity.getFieldName());
            component.setPlaceholder(entity.getPlaceholder());
            component.setDefaultValue(entity.getDefaultValue());
            component.setRequired(entity.getRequired());
            component.setValidationRules(entity.getValidationRules());
            component.setComponentProps(entity.getComponentProps());
            component.setSort(entity.getSort());
            return formComponentRepository.save(component);
        }
        throw new ResourceNotFoundException("Form component", id);
    }

    /**
     * 获取组件
     */
    public Optional<FormComponentEntity> getById(Long id) {
        return formComponentRepository.findById(id);
    }

    /**
     * 查询所有组件
     */
    public List<FormComponentEntity> findAll() {
        return formComponentRepository.findAll();
    }

    /**
     * 根据表单ID查询
     */
    public List<FormComponentEntity> findByFormId(Long formId) {
        return formComponentRepository.findByFormId(formId);
    }

    /**
     * 删除组件（软删除）
     */
    @Transactional
    public void delete(Long id) {
        formComponentRepository.findById(id).ifPresent(component -> {
            component.setDelFlag("1");
            formComponentRepository.save(component);
        });
    }

    /**
     * 彻底删除组件（硬删除）
     */
    @Transactional
    public void hardDelete(Long id) {
        formComponentRepository.deleteById(id);
    }

    /**
     * 根据表单ID删除所有组件（软删除）
     */
    @Transactional
    public void deleteByFormId(Long formId) {
        List<FormComponentEntity> components = formComponentRepository.findByFormId(formId);
        components.forEach(component -> component.setDelFlag("1"));
        formComponentRepository.saveAll(components);
    }
}