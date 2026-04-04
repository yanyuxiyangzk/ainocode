package com.ruoyi.nocode.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysLogininfor;
import com.ruoyi.nocode.system.service.ISysLogininforService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志Controller
 */
@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/system/logininfor")
@RequiredArgsConstructor
public class SysLogininforController {

    private final ISysLogininforService logininforService;

    /**
     * 查询登录日志列表
     */
    @Operation(summary = "查询登录日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime) {
        Page<SysLogininfor> page = new Page<>(pageNum, pageSize);
        IPage<SysLogininfor> result = logininforService.page(page);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 导出登录日志列表
     */
    @Operation(summary = "导出登录日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @GetMapping("/export")
    public AjaxResult export(SysLogininfor logininfor,
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor, beginTime, endTime);
        return AjaxResult.success(list);
    }

    /**
     * 获取登录日志详细信息
     */
    @Operation(summary = "获取登录日志详细信息")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:query')")
    @GetMapping("/{infoId}")
    public AjaxResult getInfo(@PathVariable("infoId") Long infoId) {
        return AjaxResult.success(logininforService.selectLogininforById(infoId));
    }

    /**
     * 删除登录日志
     */
    @Operation(summary = "删除登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds) {
        return AjaxResult.success(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清空登录日志
     */
    @Operation(summary = "清空登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:clean')")
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        logininforService.cleanLogininfor();
        return AjaxResult.success();
    }

    /**
     * 解锁登录日志记录
     */
    @Operation(summary = "解锁登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @PutMapping("/unlock/{infoId}")
    public AjaxResult unlock(@PathVariable("infoId") Long infoId) {
        return AjaxResult.success();
    }
}
