package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysLogininfor;
import com.ruoyi.nocode.system.service.ISysLogininforService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("@ss.hasPermi('system:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor,
            logininfor.getBeginTime(),
            logininfor.getEndTime());
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(list);
        dataTable.setTotal((long) list.size());
        return dataTable;
    }

    /**
     * 导出登录日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:logininfor:export')")
    @GetMapping("/export")
    public AjaxResult export(SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor, null, null);
        return AjaxResult.success(list);
    }

    /**
     * 获取登录日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:logininfor:query')")
    @GetMapping("/{infoId}")
    public AjaxResult getInfo(@PathVariable("infoId") Long infoId) {
        return AjaxResult.success(logininforService.selectLogininforById(infoId));
    }

    /**
     * 删除登录日志
     */
    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds) {
        return AjaxResult.success(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清空登录日志
     */
    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        logininforService.cleanLogininfor();
        return AjaxResult.success();
    }
}
