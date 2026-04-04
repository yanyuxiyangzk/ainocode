package com.ruoyi.nocode.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysOperLog;
import com.ruoyi.nocode.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Controller
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService operLogService;

    /**
     * 查询操作日志列表
     */
    @Operation(summary = "查询操作日志列表")
    @PreAuthorize("@ss.hasPermi('system:operlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOperLog operLog,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime) {
        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        IPage<SysOperLog> result = operLogService.page(page);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 导出操作日志列表
     */
    @Operation(summary = "导出操作日志列表")
    @PreAuthorize("@ss.hasPermi('system:operlog:export')")
    @GetMapping("/export")
    public AjaxResult export(SysOperLog operLog,
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog, beginTime, endTime);
        return AjaxResult.success(list);
    }

    /**
     * 获取操作日志详细信息
     */
    @Operation(summary = "获取操作日志详细信息")
    @PreAuthorize("@ss.hasPermi('system:operlog:query')")
    @GetMapping("/{operId}")
    public AjaxResult getInfo(@PathVariable("operId") Long operId) {
        return AjaxResult.success(operLogService.selectOperLogById(operId));
    }

    /**
     * 删除操作日志
     */
    @Operation(summary = "删除操作日志")
    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds) {
        return AjaxResult.success(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @PreAuthorize("@ss.hasPermi('system:operlog:clean')")
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        operLogService.cleanOperLog();
        return AjaxResult.success();
    }
}
