package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.FormComponentEntity;
import com.ruoyi.nocode.system.entity.FormConfigEntity;

import java.util.List;
import java.util.Map;

/**
 * 表单配置服务接口
 *
 * @author ruoyi-nocode
 */
public interface IFormConfigService {

    /**
     * 查询表单配置列表
     */
    TableDataInfo selectFormConfigList(FormConfigEntity formConfig);

    /**
     * 根据表单ID查询表单配置
     */
    FormConfigEntity selectFormConfigById(Long formId);

    /**
     * 根据表单编码查询表单配置
     */
    FormConfigEntity selectFormConfigByCode(String formCode);

    /**
     * 查询启用的表单列表
     */
    List<FormConfigEntity> selectEnabledFormList();

    /**
     * 新增表单配置
     */
    int insertFormConfig(FormConfigEntity formConfig);

    /**
     * 修改表单配置
     */
    int updateFormConfig(FormConfigEntity formConfig);

    /**
     * 删除表单配置（逻辑删除）
     */
    int deleteFormConfigByIds(Long[] formIds);

    /**
     * 物理删除表单配置
     */
    int physicallyDeleteFormConfigById(Long formId);

    /**
     * 批量保存表单和组件
     */
    int saveFormWithComponents(FormConfigEntity formConfig, List<FormComponentEntity> components);

    /**
     * 查询表单组件列表
     */
    List<FormComponentEntity> selectFormComponentList(Long formId);

    /**
     * 保存表单组件
     */
    int saveFormComponents(Long formId, List<FormComponentEntity> components);

    /**
     * 复制表单
     */
    int copyForm(Long formId, String newFormName, String newFormCode);

    /**
     * 校验表单编码唯一性
     */
    boolean checkFormCodeUnique(String formCode);

    /**
     * 获取表单配置详情（包含组件）
     */
    Map<String, Object> getFormDetail(Long formId);

    /**
     * 校验表单是否可以删除
     */
    boolean canDelete(Long formId);
}