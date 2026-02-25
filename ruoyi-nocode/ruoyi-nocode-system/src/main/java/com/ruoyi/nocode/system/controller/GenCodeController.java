package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.GenTable;
import com.ruoyi.nocode.system.service.IGenCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成Controller
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping("/system/gen")
@Tag(name = "代码生成", description = "代码生成管理接口")
public class GenCodeController {

    @Autowired
    private IGenCodeService genCodeService;

    /**
     * 查询业务表列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询业务表列表")
    public TableDataInfo list(GenTable genTable) {
        List<GenTable> list = genCodeService.selectGenTableList(genTable);
        return TableDataInfo.success(list);
    }

    /**
     * 查询数据库表列表
     */
    @GetMapping("/db/list")
    @Operation(summary = "查询数据库表列表")
    public TableDataInfo dbList(
            @Parameter(description = "表名称") @RequestParam(required = false) String tableName) {
        List<Map<String, Object>> list = genCodeService.selectDbTableList(tableName);
        return TableDataInfo.success(list);
    }

    /**
     * 获取表信息
     */
    @GetMapping("/{tableId}")
    @Operation(summary = "获取表信息")
    public AjaxResult getInfo(@PathVariable Long tableId) {
        GenTable genTable = genCodeService.selectGenTableById(tableId);
        return AjaxResult.success(genTable);
    }

    /**
     * 导入表结构
     */
    @PostMapping("/import")
    @Operation(summary = "导入表结构")
    public AjaxResult importTable(@RequestBody List<String> tableNames) {
        try {
            genCodeService.importGenTable(tableNames);
            return AjaxResult.success("导入成功");
        } catch (Exception e) {
            log.error("导入表失败", e);
            return AjaxResult.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 修改表信息
     */
    @PutMapping
    @Operation(summary = "修改表信息")
    public AjaxResult edit(@RequestBody GenTable genTable) {
        int result = genCodeService.updateGenTable(genTable);
        return result > 0 ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 删除表信息
     */
    @DeleteMapping("/{tableIds}")
    @Operation(summary = "删除表信息")
    public AjaxResult remove(@PathVariable Long[] tableIds) {
        int result = genCodeService.deleteGenTableByIds(tableIds);
        return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 预览代码
     */
    @GetMapping("/preview/{tableId}")
    @Operation(summary = "预览代码")
    public AjaxResult preview(@PathVariable Long tableId) {
        try {
            Map<String, String> data = genCodeService.previewCode(tableId);
            return AjaxResult.success(data);
        } catch (Exception e) {
            log.error("预览代码失败", e);
            return AjaxResult.error("预览失败: " + e.getMessage());
        }
    }

    /**
     * 生成代码（下载）
     */
    @GetMapping("/download/{tableId}")
    @Operation(summary = "生成代码下载")
    public void download(@PathVariable Long tableId, HttpServletResponse response) {
        try {
            byte[] data = genCodeService.downloadCode(tableId);
            genCode(response, data, "code.zip");
        } catch (Exception e) {
            log.error("下载代码失败", e);
        }
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/download/batch")
    @Operation(summary = "批量生成代码下载")
    public void downloadBatch(@RequestParam Long[] tableIds, HttpServletResponse response) {
        try {
            byte[] data = genCodeService.downloadCodeBatch(tableIds);
            genCode(response, data, "code.zip");
        } catch (Exception e) {
            log.error("批量下载代码失败", e);
        }
    }

    /**
     * 生成代码（自定义路径）
     */
    @PostMapping("/gen/{tableId}")
    @Operation(summary = "生成代码到自定义路径")
    public AjaxResult generatorCode(@PathVariable Long tableId) {
        try {
            genCodeService.generatorCode(tableId);
            return AjaxResult.success("生成成功");
        } catch (Exception e) {
            log.error("生成代码失败", e);
            return AjaxResult.error("生成失败: " + e.getMessage());
        }
    }

    /**
     * 同步表结构
     */
    @PostMapping("/synch/{tableId}")
    @Operation(summary = "同步表结构")
    public AjaxResult synchDbTable(@PathVariable Long tableId) {
        try {
            genCodeService.synchDbTable(tableId);
            return AjaxResult.success("同步成功");
        } catch (Exception e) {
            log.error("同步表结构失败", e);
            return AjaxResult.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 生成代码响应
     */
    private void genCode(HttpServletResponse response, byte[] data, String fileName) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
