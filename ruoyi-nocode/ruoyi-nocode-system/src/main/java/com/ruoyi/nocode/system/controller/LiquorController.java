package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.compiler.LiquorCompilerResult;
import com.ruoyi.nocode.common.core.compiler.LiquorCompilerService;
import com.ruoyi.nocode.common.core.compiler.LiquorHotSwapService;
import com.ruoyi.nocode.common.core.compiler.HotSwapResult;
import com.ruoyi.nocode.common.core.sandbox.LiquorSandboxExecutor;
import com.ruoyi.nocode.common.core.sandbox.SandboxResult;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Liquor动态编译控制器
 * 提供即时编译、热替换、沙箱执行等核心功能
 *
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/tool/liquor")
@Tag(name = "Liquor动态编译", description = "Liquor即时编译和热替换接口")
public class LiquorController {

    @Autowired
    private LiquorCompilerService liquorCompilerService;

    @Autowired
    private LiquorHotSwapService liquorHotSwapService;

    @Autowired
    private LiquorSandboxExecutor liquorSandboxExecutor;

    /**
     * 编译Java源码
     */
    @PostMapping("/compile")
    @Operation(summary = "编译Java源码", description = "编译Java源代码并返回编译结果")
    public AjaxResult compile(
            @Parameter(description = "Java源代码") @RequestBody Map<String, String> request) {
        String sourceCode = request.get("sourceCode");
        if (sourceCode == null || sourceCode.isBlank()) {
            return AjaxResult.error("源代码不能为空");
        }

        try {
            long startTime = System.currentTimeMillis();
            LiquorCompilerResult result = liquorCompilerService.compile(sourceCode);
            long compileTime = System.currentTimeMillis() - startTime;

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("className", result.getClassName());
            data.put("compileTime", compileTime);

            if (result.isSuccess()) {
                data.put("message", "编译成功");
                return AjaxResult.success(data);
            } else {
                data.put("errorMessage", result.getErrors());
                return AjaxResult.error("编译失败", data);
            }
        } catch (Exception e) {
            log.error("编译异常", e);
            return AjaxResult.error("编译异常: " + e.getMessage());
        }
    }

    /**
     * 编译并实例化
     */
    @PostMapping("/compile-and-instantiate")
    @Operation(summary = "编译并实例化", description = "编译Java源代码并创建实例")
    public AjaxResult compileAndInstantiate(
            @Parameter(description = "Java源代码") @RequestBody Map<String, String> request) {
        String sourceCode = request.get("sourceCode");
        if (sourceCode == null || sourceCode.isBlank()) {
            return AjaxResult.error("源代码不能为空");
        }

        try {
            LiquorCompilerService.CompileAndInstantiateResult result =
                    liquorCompilerService.compileAndInstantiate(sourceCode);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());

            if (result.isSuccess()) {
                data.put("instanceClass", result.getClazz().getName());
                data.put("message", "编译并实例化成功");
                return AjaxResult.success(data);
            } else {
                data.put("errors", result.getErrors());
                return AjaxResult.error("编译并实例化失败", data);
            }
        } catch (Exception e) {
            log.error("编译并实例化异常", e);
            return AjaxResult.error("编译并实例化异常: " + e.getMessage());
        }
    }

    /**
     * 热替换类
     */
    @PostMapping("/hot-swap")
    @Operation(summary = "热替换类", description = "在指定域中热替换Java类")
    public AjaxResult hotSwap(
            @Parameter(description = "Java源代码") @RequestBody Map<String, String> request) {
        String sourceCode = request.get("sourceCode");
        String domain = request.getOrDefault("domain", "default");

        if (sourceCode == null || sourceCode.isBlank()) {
            return AjaxResult.error("源代码不能为空");
        }

        try {
            HotSwapResult result = liquorHotSwapService.hotSwap(domain, sourceCode);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("version", result.getVersion());
            data.put("swapId", result.getSwapId());
            data.put("classLoaderId", result.getClassLoaderId());

            if (result.isSuccess()) {
                data.put("message", "热替换成功");
                return AjaxResult.success(data);
            } else {
                data.put("errorMessage", result.getErrors());
                return AjaxResult.error("热替换失败", data);
            }
        } catch (Exception e) {
            log.error("热替换异常", e);
            return AjaxResult.error("热替换异常: " + e.getMessage());
        }
    }

    /**
     * 回滚到指定版本
     */
    @PostMapping("/rollback")
    @Operation(summary = "回滚版本", description = "回滚热替换域到指定版本")
    public AjaxResult rollback(
            @Parameter(description = "域名称") @RequestParam String domain,
            @Parameter(description = "目标版本") @RequestParam int version) {
        try {
            HotSwapResult result = liquorHotSwapService.rollback(domain, version);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("version", result.getVersion());

            if (result.isSuccess()) {
                return AjaxResult.success("回滚成功", data);
            } else {
                data.put("errorMessage", result.getErrors());
                return AjaxResult.error("回滚失败", data);
            }
        } catch (Exception e) {
            log.error("回滚异常", e);
            return AjaxResult.error("回滚异常: " + e.getMessage());
        }
    }

    /**
     * 在沙箱中执行代码
     */
    @PostMapping("/execute")
    @Operation(summary = "沙箱执行", description = "在沙箱环境中执行JavaScript代码")
    public AjaxResult execute(
            @Parameter(description = "执行请求") @RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        Long timeout = request.get("timeout") != null ?
                ((Number) request.get("timeout")).longValue() : 5000L;

        if (code == null || code.isBlank()) {
            return AjaxResult.error("代码不能为空");
        }

        try {
            SandboxResult result = liquorSandboxExecutor.execute(code, timeout);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("executionTime", result.getExecutionTime());

            if (result.isSuccess()) {
                data.put("result", result.getResult());
                data.put("resultType", result.getResult() != null ?
                        result.getResult().getClass().getSimpleName() : "null");
                return AjaxResult.success("执行成功", data);
            } else {
                data.put("errorMessage", result.getError());
                data.put("errorType", result.getErrorCode() != null ?
                        result.getErrorCode().name() : "UNKNOWN");
                return AjaxResult.error("执行失败", data);
            }
        } catch (Exception e) {
            log.error("执行异常", e);
            return AjaxResult.error("执行异常: " + e.getMessage());
        }
    }

    /**
     * 获取域列表
     */
    @GetMapping("/domains")
    @Operation(summary = "获取域列表", description = "获取所有热替换域的信息")
    public AjaxResult listDomains() {
        try {
            List<String> domains = liquorHotSwapService.getAllDomains();
            return AjaxResult.success(domains);
        } catch (Exception e) {
            log.error("获取域列表异常", e);
            return AjaxResult.error("获取域列表异常: " + e.getMessage());
        }
    }

    /**
     * 获取域详情
     */
    @GetMapping("/domains/{domain}")
    @Operation(summary = "获取域详情", description = "获取指定热替换域的详细信息")
    public AjaxResult getDomain(@PathVariable String domain) {
        try {
            LiquorHotSwapService.DomainInfo info = liquorHotSwapService.getDomainInfo(domain);
            if (info == null) {
                return AjaxResult.error("域不存在: " + domain);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("domain", info.getDomain());
            data.put("classLoaderId", info.getClassLoaderId());
            data.put("currentVersion", info.getCurrentVersion());
            data.put("latestVersion", info.getLatestVersion());
            data.put("classCount", info.getClassCount());

            return AjaxResult.success(data);
        } catch (Exception e) {
            log.error("获取域详情异常", e);
            return AjaxResult.error("获取域详情异常: " + e.getMessage());
        }
    }

    /**
     * 清除域
     */
    @DeleteMapping("/domains/{domain}")
    @Operation(summary = "清除域", description = "销毁指定的热替换域，释放资源")
    public AjaxResult clearDomain(@PathVariable String domain) {
        try {
            liquorHotSwapService.destroyDomain(domain);
            return AjaxResult.success("域已清除: " + domain);
        } catch (Exception e) {
            log.error("清除域异常", e);
            return AjaxResult.error("清除域异常: " + e.getMessage());
        }
    }

    /**
     * 获取编译缓存大小
     */
    @GetMapping("/cache/size")
    @Operation(summary = "获取缓存大小", description = "获取当前编译缓存的类数量")
    public AjaxResult getCacheSize() {
        try {
            int size = liquorCompilerService.getCacheSize();
            return AjaxResult.success(Map.of("cacheSize", size));
        } catch (Exception e) {
            log.error("获取缓存大小异常", e);
            return AjaxResult.error("获取缓存大小异常: " + e.getMessage());
        }
    }

    /**
     * 清除编译缓存
     */
    @DeleteMapping("/cache")
    @Operation(summary = "清除缓存", description = "清除所有编译缓存")
    public AjaxResult clearCache() {
        try {
            liquorCompilerService.clearCache();
            return AjaxResult.success("缓存已清除");
        } catch (Exception e) {
            log.error("清除缓存异常", e);
            return AjaxResult.error("清除缓存异常: " + e.getMessage());
        }
    }
}
