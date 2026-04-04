package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.FormComponentEntity;
import com.ruoyi.nocode.system.entity.FormConfigEntity;
import com.ruoyi.nocode.system.service.IFormConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单配置Controller
 *
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/system/form")
@RequiredArgsConstructor
@Tag(name = "表单配置", description = "表单配置管理接口")
public class FormConfigController {

    private final IFormConfigService formConfigService;

    /**
     * 查询表单配置列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询表单配置列表")
    public TableDataInfo list(FormConfigEntity formConfig) {
        return formConfigService.selectFormConfigList(formConfig);
    }

    /**
     * 获取表单配置详情
     */
    @GetMapping("/{formId}")
    @Operation(summary = "获取表单配置详情")
    public AjaxResult getInfo(@PathVariable @Parameter(description = "表单ID") Long formId) {
        return AjaxResult.success(formConfigService.selectFormConfigById(formId));
    }

    /**
     * 根据表单编码获取表单
     */
    @GetMapping("/code/{formCode}")
    @Operation(summary = "根据表单编码获取表单")
    public AjaxResult getByCode(@PathVariable @Parameter(description = "表单编码") String formCode) {
        return AjaxResult.success(formConfigService.selectFormConfigByCode(formCode));
    }

    /**
     * 获取表单详情（包含组件）
     */
    @GetMapping("/detail/{formId}")
    @Operation(summary = "获取表单详情")
    public AjaxResult getFormDetail(@PathVariable @Parameter(description = "表单ID") Long formId) {
        return AjaxResult.success(formConfigService.getFormDetail(formId));
    }

    /**
     * 获取启用的表单列表
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取启用的表单列表")
    public AjaxResult getEnabledForms() {
        return AjaxResult.success(formConfigService.selectEnabledFormList());
    }

    /**
     * 新增表单配置
     */
    @PostMapping
    @Operation(summary = "新增表单配置")
    public AjaxResult add(@RequestBody FormConfigEntity formConfig) {
        return AjaxResult.success(formConfigService.insertFormConfig(formConfig));
    }

    /**
     * 修改表单配置
     */
    @PutMapping
    @Operation(summary = "修改表单配置")
    public AjaxResult edit(@RequestBody FormConfigEntity formConfig) {
        return AjaxResult.success(formConfigService.updateFormConfig(formConfig));
    }

    /**
     * 批量保存表单和组件
     */
    @PostMapping("/saveWithComponents")
    @Operation(summary = "批量保存表单和组件")
    public AjaxResult saveWithComponents(
            @RequestBody Map<String, Object> params) {
        FormConfigEntity formConfig = (FormConfigEntity) params.get("formConfig");
        @SuppressWarnings("unchecked")
        List<FormComponentEntity> components = (List<FormComponentEntity>) params.get("components");
        return AjaxResult.success(formConfigService.saveFormWithComponents(formConfig, components));
    }

    /**
     * 保存表单组件
     */
    @PostMapping("/components/{formId}")
    @Operation(summary = "保存表单组件")
    public AjaxResult saveComponents(
            @PathVariable @Parameter(description = "表单ID") Long formId,
            @RequestBody List<FormComponentEntity> components) {
        return AjaxResult.success(formConfigService.saveFormComponents(formId, components));
    }

    /**
     * 获取表单组件列表
     */
    @GetMapping("/components/{formId}")
    @Operation(summary = "获取表单组件列表")
    public AjaxResult getComponents(@PathVariable @Parameter(description = "表单ID") Long formId) {
        return AjaxResult.success(formConfigService.selectFormComponentList(formId));
    }

    /**
     * 复制表单
     */
    @PostMapping("/copy/{formId}")
    @Operation(summary = "复制表单")
    public AjaxResult copy(
            @PathVariable @Parameter(description = "表单ID") Long formId,
            @RequestBody Map<String, String> params) {
        String newFormName = params.get("newFormName");
        String newFormCode = params.get("newFormCode");
        return AjaxResult.success(formConfigService.copyForm(formId, newFormName, newFormCode));
    }

    /**
     * 删除表单配置
     */
    @DeleteMapping("/{formIds}")
    @Operation(summary = "删除表单配置")
    public AjaxResult remove(@PathVariable @Parameter(description = "表单ID数组") Long[] formIds) {
        return AjaxResult.success(formConfigService.deleteFormConfigByIds(formIds));
    }

    /**
     * 物理删除表单配置
     */
    @DeleteMapping("/物理删除/{formId}")
    @Operation(summary = "物理删除表单配置")
    public AjaxResult physicallyRemove(@PathVariable @Parameter(description = "表单ID") Long formId) {
        return AjaxResult.success(formConfigService.physicallyDeleteFormConfigById(formId));
    }

    /**
     * 校验表单编码唯一性
     */
    @GetMapping("/checkCodeUnique")
    @Operation(summary = "校验表单编码唯一性")
    public AjaxResult checkCodeUnique(@Parameter(description = "表单编码") @RequestParam String formCode) {
        return AjaxResult.success(formConfigService.checkFormCodeUnique(formCode));
    }
}