package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.common.core.exception.ServiceException;
import com.ruoyi.nocode.system.entity.FormComponentEntity;
import com.ruoyi.nocode.system.entity.FormConfigEntity;
import com.ruoyi.nocode.system.mapper.FormComponentMapper;
import com.ruoyi.nocode.system.mapper.FormConfigMapper;
import com.ruoyi.nocode.system.service.IFormConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单配置服务实现
 *
 * @author ruoyi-nocode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormConfigServiceImpl extends ServiceImpl<FormConfigMapper, FormConfigEntity> implements IFormConfigService {

    private final FormConfigMapper formConfigMapper;
    private final FormComponentMapper formComponentMapper;

    @Override
    public TableDataInfo selectFormConfigList(FormConfigEntity formConfig) {
        Page<FormConfigEntity> page = new Page<>(formConfig.getPageNum(), formConfig.getPageSize());
        List<FormConfigEntity> list = formConfigMapper.selectFormConfigList(formConfig);
        return TableDataInfo.success(list, list.size());
    }

    @Override
    public FormConfigEntity selectFormConfigById(Long formId) {
        return formConfigMapper.selectById(formId);
    }

    @Override
    public FormConfigEntity selectFormConfigByCode(String formCode) {
        return formConfigMapper.selectByFormCode(formCode);
    }

    @Override
    public List<FormConfigEntity> selectEnabledFormList() {
        FormConfigEntity formConfig = new FormConfigEntity();
        formConfig.setStatus("1");
        return formConfigMapper.selectFormConfigList(formConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFormConfig(FormConfigEntity formConfig) {
        if (!checkFormCodeUnique(formConfig.getFormCode())) {
            throw new ServiceException("表单编码已存在");
        }
        formConfig.setVersion(1);
        formConfig.setDelFlag("0");
        return formConfigMapper.insert(formConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFormConfig(FormConfigEntity formConfig) {
        FormConfigEntity existConfig = formConfigMapper.selectById(formConfig.getFormId());
        if (existConfig == null) {
            throw new ServiceException("表单不存在");
        }
        // 如果修改了编码，检查唯一性
        if (StringUtils.hasText(formConfig.getFormCode())
                && !formConfig.getFormCode().equals(existConfig.getFormCode())) {
            if (!checkFormCodeUnique(formConfig.getFormCode())) {
                throw new ServiceException("表单编码已存在");
            }
        }
        formConfig.setVersion(existConfig.getVersion() + 1);
        return formConfigMapper.updateById(formConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFormConfigByIds(Long[] formIds) {
        int count = 0;
        for (Long formId : formIds) {
            if (canDelete(formId)) {
                FormConfigEntity formConfig = new FormConfigEntity();
                formConfig.setFormId(formId);
                formConfig.setDelFlag("1");
                count += formConfigMapper.updateById(formConfig);
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int physicallyDeleteFormConfigById(Long formId) {
        // 先删除组件
        formComponentMapper.deleteByFormId(formId);
        // 再删除表单
        return formConfigMapper.physicallyDeleteById(formId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveFormWithComponents(FormConfigEntity formConfig, List<FormComponentEntity> components) {
        int result = 0;
        if (formConfig.getFormId() == null) {
            // 新增
            result = insertFormConfig(formConfig);
        } else {
            // 更新
            result = updateFormConfig(formConfig);
        }

        if (result > 0 && components != null && !components.isEmpty()) {
            // 删除原有组件
            formComponentMapper.deleteByFormId(formConfig.getFormId());
            // 保存新组件
            for (FormComponentEntity component : components) {
                component.setFormId(formConfig.getFormId());
            }
            formComponentMapper.batchInsert(components);
        }
        return result;
    }

    @Override
    public List<FormComponentEntity> selectFormComponentList(Long formId) {
        return formComponentMapper.selectByFormId(formId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveFormComponents(Long formId, List<FormComponentEntity> components) {
        if (formId == null) {
            throw new ServiceException("表单ID不能为空");
        }
        // 删除原有组件
        formComponentMapper.deleteByFormId(formId);
        // 保存新组件
        if (components != null && !components.isEmpty()) {
            for (FormComponentEntity component : components) {
                component.setFormId(formId);
                component.setCreateTime(LocalDateTime.now());
                component.setUpdateTime(LocalDateTime.now());
            }
            return formComponentMapper.batchInsert(components);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyForm(Long formId, String newFormName, String newFormCode) {
        FormConfigEntity originalForm = formConfigMapper.selectById(formId);
        if (originalForm == null) {
            throw new ServiceException("原表单不存在");
        }

        if (!checkFormCodeUnique(newFormCode)) {
            throw new ServiceException("新表单编码已存在");
        }

        // 复制表单配置
        FormConfigEntity newForm = new FormConfigEntity();
        newForm.setFormName(newFormName);
        newForm.setFormCode(newFormCode);
        newForm.setFormType(originalForm.getFormType());
        newForm.setDescription(originalForm.getDescription());
        newForm.setFormConfig(originalForm.getFormConfig());
        newForm.setFormStyle(originalForm.getFormStyle());
        newForm.setDatasourceId(originalForm.getDatasourceId());
        newForm.setDatasourceName(originalForm.getDatasourceName());
        newForm.setTableName(originalForm.getTableName());
        newForm.setStatus("0"); // 复制后默认禁用
        newForm.setVersion(1);
        newForm.setDelFlag("0");

        int result = formConfigMapper.insert(newForm);
        if (result > 0) {
            // 复制组件
            List<FormComponentEntity> components = formComponentMapper.selectByFormId(formId);
            if (components != null && !components.isEmpty()) {
                for (FormComponentEntity component : components) {
                    component.setComponentId(null);
                    component.setFormId(newForm.getFormId());
                    component.setCreateTime(LocalDateTime.now());
                    component.setUpdateTime(LocalDateTime.now());
                }
                formComponentMapper.batchInsert(components);
            }
        }
        return result;
    }

    @Override
    public boolean checkFormCodeUnique(String formCode) {
        if (!StringUtils.hasText(formCode)) {
            return true;
        }
        FormConfigEntity existConfig = formConfigMapper.selectByFormCode(formCode);
        return existConfig == null;
    }

    @Override
    public Map<String, Object> getFormDetail(Long formId) {
        Map<String, Object> result = new HashMap<>();
        FormConfigEntity formConfig = formConfigMapper.selectById(formId);
        if (formConfig == null) {
            throw new ServiceException("表单不存在");
        }
        List<FormComponentEntity> components = formComponentMapper.selectByFormId(formId);
        result.put("formConfig", formConfig);
        result.put("components", components);
        return result;
    }

    @Override
    public boolean canDelete(Long formId) {
        // 可以在这里添加业务规则检查
        // 例如：检查表单是否被工作流引用等
        return true;
    }
}