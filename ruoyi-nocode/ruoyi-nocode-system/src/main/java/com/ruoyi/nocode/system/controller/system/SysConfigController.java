package com.ruoyi.nocode.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysConfig;
import com.ruoyi.nocode.system.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置Controller
 */
@Tag(name = "参数配置管理")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService configService;

    /**
     * 查询参数配置列表
     */
    @Operation(summary = "查询参数配置列表")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        IPage<SysConfig> result = configService.page(page);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 获取参数配置详细信息
     */
    @Operation(summary = "获取参数配置详细信息")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId) {
        return AjaxResult.success(configService.selectConfigById(configId));
    }

    /**
     * 根据配置键名查询配置
     */
    @Operation(summary = "根据配置键名查询配置")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping("/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable("configKey") String configKey) {
        return AjaxResult.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @Operation(summary = "新增参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config.getConfigKey())) {
            return AjaxResult.error("新增配置'" + config.getConfigName() + "'失败，配置键名已存在");
        }
        return AjaxResult.success(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @Operation(summary = "修改参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config.getConfigKey())) {
            return AjaxResult.error("修改配置'" + config.getConfigName() + "'失败，配置键名已存在");
        }
        return AjaxResult.success(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @Operation(summary = "删除参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return AjaxResult.success(configService.deleteConfigByIds(configIds));
    }
}
