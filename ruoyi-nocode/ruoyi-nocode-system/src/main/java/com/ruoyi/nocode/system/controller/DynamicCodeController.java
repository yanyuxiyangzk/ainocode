package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.DynamicCode;
import com.ruoyi.nocode.system.service.IDynamicCodeService;
import com.ruoyi.nocode.system.service.IDynamicCodeService.CompileStatistics;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.CompileResult;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.SandboxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 动态代码Controller
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/system/dynamicCode")
@Tag(name = "动态代码管理", description = "动态代码编译和执行接口")
public class DynamicCodeController {

    @Autowired
    private IDynamicCodeService dynamicCodeService;

    /**
     * 查询动态代码列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询动态代码列表")
    public TableDataInfo list(DynamicCode dynamicCode) {
        List<DynamicCode> list = dynamicCodeService.selectDynamicCodeList(dynamicCode);
        return TableDataInfo.success(list);
    }

    /**
     * 分页查询动态代码列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询动态代码列表")
    public TableDataInfo page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            DynamicCode dynamicCode) {
        List<DynamicCode> list = dynamicCodeService.selectDynamicCodeList(dynamicCode);
        return TableDataInfo.success(list, list.size());
    }

    /**
     * 获取动态代码详情
     */
    @GetMapping("/{codeId}")
    @Operation(summary = "获取动态代码详情")
    public AjaxResult getInfo(@PathVariable Long codeId) {
        DynamicCode dynamicCode = dynamicCodeService.getById(codeId);
        return dynamicCode != null ? AjaxResult.success(dynamicCode) : AjaxResult.error("代码不存在");
    }

    /**
     * 根据代码编码查询
     */
    @GetMapping("/code/{codeCode}")
    @Operation(summary = "根据代码编码查询")
    public AjaxResult getByCode(@PathVariable String codeCode) {
        DynamicCode dynamicCode = dynamicCodeService.selectByCodeCode(codeCode);
        return dynamicCode != null ? AjaxResult.success(dynamicCode) : AjaxResult.error("代码不存在");
    }

    /**
     * 根据类名查询
     */
    @GetMapping("/class/{className}")
    @Operation(summary = "根据类全限定名查询")
    public AjaxResult getByClassName(@PathVariable String className) {
        DynamicCode dynamicCode = dynamicCodeService.selectByClassName(className);
        return dynamicCode != null ? AjaxResult.success(dynamicCode) : AjaxResult.error("代码不存在");
    }

    /**
     * 新增动态代码
     */
    @PostMapping
    @Operation(summary = "新增动态代码")
    public AjaxResult add(@RequestBody DynamicCode dynamicCode) {
        // 校验编码唯一性
        if (!dynamicCodeService.checkCodeCodeUnique(dynamicCode)) {
            return AjaxResult.error("代码编码已存在");
        }
        
        // 校验类名唯一性
        if (!dynamicCodeService.checkClassNameUnique(dynamicCode)) {
            return AjaxResult.error("类名已存在");
        }
        
        // 设置初始状态
        dynamicCode.setStatus(DynamicCode.STATUS_DRAFT);
        dynamicCode.setEnabled(false);
        dynamicCode.setCodeVersion(0);
        dynamicCode.setExecuteCount(0L);
        
        boolean success = dynamicCodeService.save(dynamicCode);
        return success ? AjaxResult.success("添加成功") : AjaxResult.error("添加失败");
    }

    /**
     * 修改动态代码
     */
    @PutMapping
    @Operation(summary = "修改动态代码")
    public AjaxResult edit(@RequestBody DynamicCode dynamicCode) {
        // 校验编码唯一性
        if (!dynamicCodeService.checkCodeCodeUnique(dynamicCode)) {
            return AjaxResult.error("代码编码已存在");
        }
        
        // 校验类名唯一性
        if (!dynamicCodeService.checkClassNameUnique(dynamicCode)) {
            return AjaxResult.error("类名已存在");
        }
        
        // 修改代码后重置状态为草稿
        dynamicCode.setStatus(DynamicCode.STATUS_DRAFT);
        dynamicCode.setCompiledBytes(null);
        
        boolean success = dynamicCodeService.updateById(dynamicCode);
        return success ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 删除动态代码
     */
    @DeleteMapping("/{codeId}")
    @Operation(summary = "删除动态代码")
    public AjaxResult remove(@PathVariable Long codeId) {
        boolean success = dynamicCodeService.removeById(codeId);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 编译动态代码
     */
    @PostMapping("/compile/{codeId}")
    @Operation(summary = "编译动态代码")
    public AjaxResult compile(@PathVariable Long codeId) {
        try {
            CompileResult result = dynamicCodeService.compileCode(codeId);
            
            if (result.success()) {
                return AjaxResult.success("编译成功", Map.of(
                        "className", result.className(),
                        "compileTime", result.compileTime()
                ));
            } else {
                return AjaxResult.error("编译失败: " + result.error());
            }
        } catch (Exception e) {
            log.error("编译失败", e);
            return AjaxResult.error("编译异常: " + e.getMessage());
        }
    }

    /**
     * 编译所有草稿代码
     */
    @PostMapping("/compileAll")
    @Operation(summary = "编译所有草稿状态的代码")
    public AjaxResult compileAll() {
        try {
            CompileStatistics stats = dynamicCodeService.compileAllDraft();
            return AjaxResult.success("批量编译完成", Map.of(
                    "total", stats.total(),
                    "success", stats.success(),
                    "failed", stats.failed(),
                    "failedCodes", stats.failedCodes()
            ));
        } catch (Exception e) {
            log.error("批量编译失败", e);
            return AjaxResult.error("批量编译异常: " + e.getMessage());
        }
    }

    /**
     * 执行动态代码方法
     */
    @PostMapping("/execute/{codeId}")
    @Operation(summary = "执行动态代码方法")
    public AjaxResult execute(
            @PathVariable Long codeId,
            @Parameter(description = "方法名") @RequestParam String methodName,
            @Parameter(description = "参数（JSON数组）") @RequestBody(required = false) Object[] args) {
        try {
            Object result = dynamicCodeService.executeMethod(codeId, methodName, args != null ? args : new Object[0]);
            return AjaxResult.success("执行成功", result);
        } catch (Exception e) {
            log.error("执行失败", e);
            return AjaxResult.error("执行失败: " + e.getMessage());
        }
    }

    /**
     * 在沙箱中执行
     */
    @PostMapping("/sandbox/{codeId}")
    @Operation(summary = "在沙箱环境中执行方法")
    public AjaxResult executeInSandbox(
            @PathVariable Long codeId,
            @Parameter(description = "方法名") @RequestParam String methodName,
            @Parameter(description = "参数（JSON数组）") @RequestBody(required = false) Object[] args) {
        try {
            SandboxResult result = dynamicCodeService.executeInSandbox(
                    codeId, methodName, args != null ? args : new Object[0]);
            
            if (result.success()) {
                return AjaxResult.success("执行成功", Map.of(
                        "result", result.result(),
                        "executionTime", result.executionTime()
                ));
            } else {
                return AjaxResult.error("执行失败: " + result.error());
            }
        } catch (Exception e) {
            log.error("沙箱执行失败", e);
            return AjaxResult.error("沙箱执行异常: " + e.getMessage());
        }
    }

    /**
     * 热替换代码
     */
    @PostMapping("/hotReplace/{codeId}")
    @Operation(summary = "热替换动态代码")
    public AjaxResult hotReplace(@PathVariable Long codeId) {
        try {
            boolean success = dynamicCodeService.hotReplace(codeId);
            return success ? AjaxResult.success("热替换成功") : AjaxResult.error("热替换失败");
        } catch (Exception e) {
            log.error("热替换失败", e);
            return AjaxResult.error("热替换异常: " + e.getMessage());
        }
    }

    /**
     * 发布动态代码
     */
    @PostMapping("/publish/{codeId}")
    @Operation(summary = "发布动态代码")
    public AjaxResult publish(@PathVariable Long codeId) {
        try {
            boolean success = dynamicCodeService.publishCode(codeId);
            return success ? AjaxResult.success("发布成功") : AjaxResult.error("发布失败");
        } catch (Exception e) {
            log.error("发布失败", e);
            return AjaxResult.error("发布异常: " + e.getMessage());
        }
    }

    /**
     * 创建动态代码实例
     */
    @PostMapping("/instance/{codeId}")
    @Operation(summary = "创建动态代码实例")
    public AjaxResult createInstance(
            @PathVariable Long codeId,
            @Parameter(description = "构造参数（JSON数组）") @RequestBody(required = false) Object[] args) {
        try {
            Object instance = dynamicCodeService.createInstance(codeId, args != null ? args : new Object[0]);
            return AjaxResult.success("创建实例成功", Map.of(
                    "instanceClass", instance.getClass().getName()
            ));
        } catch (Exception e) {
            log.error("创建实例失败", e);
            return AjaxResult.error("创建实例失败: " + e.getMessage());
        }
    }

    /**
     * 校验代码编码是否唯一
     */
    @GetMapping("/checkCodeUnique")
    @Operation(summary = "校验代码编码是否唯一")
    public AjaxResult checkCodeUnique(DynamicCode dynamicCode) {
        boolean unique = dynamicCodeService.checkCodeCodeUnique(dynamicCode);
        return AjaxResult.success(unique);
    }

    /**
     * 校验类名是否唯一
     */
    @GetMapping("/checkClassNameUnique")
    @Operation(summary = "校验类名是否唯一")
    public AjaxResult checkClassNameUnique(DynamicCode dynamicCode) {
        boolean unique = dynamicCodeService.checkClassNameUnique(dynamicCode);
        return AjaxResult.success(unique);
    }

    /**
     * 查询已启用的动态代码
     */
    @GetMapping("/enabled")
    @Operation(summary = "查询已启用的动态代码")
    public AjaxResult getEnabledCodes() {
        List<DynamicCode> list = dynamicCodeService.selectEnabledCodes();
        return AjaxResult.success(list);
    }
}
