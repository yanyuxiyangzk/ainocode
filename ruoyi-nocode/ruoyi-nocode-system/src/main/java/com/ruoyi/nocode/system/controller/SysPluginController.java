package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.PluginInfo;
import com.ruoyi.nocode.system.entity.PluginLog;
import com.ruoyi.nocode.system.service.ISysPluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 插件管理Controller
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/system/plugin")
@Tag(name = "插件管理", description = "插件生命周期管理接口")
public class SysPluginController {

    @Autowired
    private ISysPluginService pluginService;

    /**
     * 查询插件列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询插件列表", description = "分页查询插件信息列表")
    public TableDataInfo list(PluginInfo pluginInfo) {
        List<PluginInfo> list = pluginService.selectPluginList(pluginInfo);
        return TableDataInfo.success(list);
    }

    /**
     * 分页查询插件列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询插件列表")
    public TableDataInfo page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            PluginInfo pluginInfo) {
        List<PluginInfo> list = pluginService.selectPluginList(pluginInfo);
        return TableDataInfo.success(list, list.size());
    }

    /**
     * 获取插件详情
     */
    @GetMapping("/{pluginId}")
    @Operation(summary = "获取插件详情")
    public AjaxResult getInfo(@PathVariable Long pluginId) {
        PluginInfo pluginInfo = pluginService.getById(pluginId);
        if (pluginInfo != null) {
            // 获取运行时状态
            String runtimeStatus = pluginService.getPluginRuntimeStatus(pluginId);
            // 添加到响应中（不修改实体类）
            AjaxResult result = AjaxResult.success(pluginInfo);
            result.put("runtimeStatus", runtimeStatus);
            return result;
        }
        return AjaxResult.error("插件不存在");
    }

    /**
     * 根据插件编码查询
     */
    @GetMapping("/code/{pluginCode}")
    @Operation(summary = "根据插件编码查询")
    public AjaxResult getByCode(@PathVariable String pluginCode) {
        PluginInfo pluginInfo = pluginService.selectByPluginCode(pluginCode);
        return pluginInfo != null ? AjaxResult.success(pluginInfo) : AjaxResult.error("插件不存在");
    }

    /**
     * 查询已启用的插件
     */
    @GetMapping("/enabled")
    @Operation(summary = "查询已启用的插件")
    public AjaxResult getEnabledPlugins() {
        List<PluginInfo> list = pluginService.selectEnabledPlugins();
        return AjaxResult.success(list);
    }

    /**
     * 上传并安装插件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传并安装插件")
    public AjaxResult upload(
            @Parameter(description = "插件JAR文件") @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("请选择要上传的插件文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".jar")) {
                return AjaxResult.error("只支持JAR格式的插件文件");
            }
            
            String pluginId = pluginService.installPlugin(
                    file.getInputStream(),
                    fileName,
                    "admin",  // TODO: 从上下文获取当前用户
                    "127.0.0.1"  // TODO: 从请求获取IP
            );
            
            return AjaxResult.success("插件安装成功", pluginId);
            
        } catch (Exception e) {
            log.error("上传插件失败", e);
            return AjaxResult.error("插件安装失败: " + e.getMessage());
        }
    }

    /**
     * 卸载插件
     */
    @DeleteMapping("/{pluginId}")
    @Operation(summary = "卸载插件")
    public AjaxResult uninstall(@PathVariable Long pluginId) {
        try {
            boolean success = pluginService.uninstallPlugin(
                    pluginId,
                    "admin",  // TODO: 从上下文获取当前用户
                    "127.0.0.1"  // TODO: 从请求获取IP
            );
            return success ? AjaxResult.success("卸载成功") : AjaxResult.error("卸载失败");
        } catch (Exception e) {
            log.error("卸载插件失败", e);
            return AjaxResult.error("卸载失败: " + e.getMessage());
        }
    }

    /**
     * 启用插件
     */
    @PutMapping("/enable/{pluginId}")
    @Operation(summary = "启用插件")
    public AjaxResult enable(@PathVariable Long pluginId) {
        try {
            boolean success = pluginService.enablePlugin(
                    pluginId,
                    "admin",  // TODO: 从上下文获取当前用户
                    "127.0.0.1"  // TODO: 从请求获取IP
            );
            return success ? AjaxResult.success("启用成功") : AjaxResult.error("启用失败");
        } catch (Exception e) {
            log.error("启用插件失败", e);
            return AjaxResult.error("启用失败: " + e.getMessage());
        }
    }

    /**
     * 停用插件
     */
    @PutMapping("/disable/{pluginId}")
    @Operation(summary = "停用插件")
    public AjaxResult disable(@PathVariable Long pluginId) {
        try {
            boolean success = pluginService.disablePlugin(
                    pluginId,
                    "admin",  // TODO: 从上下文获取当前用户
                    "127.0.0.1"  // TODO: 从请求获取IP
            );
            return success ? AjaxResult.success("停用成功") : AjaxResult.error("停用失败");
        } catch (Exception e) {
            log.error("停用插件失败", e);
            return AjaxResult.error("停用失败: " + e.getMessage());
        }
    }

    /**
     * 重载插件
     */
    @PutMapping("/reload/{pluginId}")
    @Operation(summary = "重载插件")
    public AjaxResult reload(@PathVariable Long pluginId) {
        try {
            boolean success = pluginService.reloadPlugin(
                    pluginId,
                    "admin",  // TODO: 从上下文获取当前用户
                    "127.0.0.1"  // TODO: 从请求获取IP
            );
            return success ? AjaxResult.success("重载成功") : AjaxResult.error("重载失败");
        } catch (Exception e) {
            log.error("重载插件失败", e);
            return AjaxResult.error("重载失败: " + e.getMessage());
        }
    }

    /**
     * 查询插件操作日志
     */
    @GetMapping("/logs/{pluginId}")
    @Operation(summary = "查询插件操作日志")
    public AjaxResult getLogs(@PathVariable Long pluginId) {
        List<PluginLog> logs = pluginService.selectPluginLogs(pluginId);
        return AjaxResult.success(logs);
    }

    /**
     * 校验插件编码是否唯一
     */
    @GetMapping("/checkCodeUnique")
    @Operation(summary = "校验插件编码是否唯一")
    public AjaxResult checkCodeUnique(PluginInfo pluginInfo) {
        boolean unique = pluginService.checkPluginCodeUnique(pluginInfo);
        return AjaxResult.success(unique);
    }

    /**
     * 获取插件运行时状态
     */
    @GetMapping("/runtime/{pluginId}")
    @Operation(summary = "获取插件运行时状态")
    public AjaxResult getRuntimeStatus(@PathVariable Long pluginId) {
        String status = pluginService.getPluginRuntimeStatus(pluginId);
        return AjaxResult.success(status);
    }
}
