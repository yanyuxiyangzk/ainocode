package com.ruoyi.nocode.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysJob;
import com.ruoyi.nocode.system.entity.SysJobLog;
import com.ruoyi.nocode.system.service.ISysJobService;
import com.ruoyi.nocode.system.service.ISysJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务Controller
 */
@Tag(name = "定时任务管理")
@RestController
@RequestMapping("/system/job")
@RequiredArgsConstructor
public class SysJobController {

    private final ISysJobService jobService;
    private final ISysJobLogService jobLogService;

    /**
     * 查询定时任务列表
     */
    @Operation(summary = "查询定时任务列表")
    @PreAuthorize("@ss.hasPermi('system:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJob job,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysJob> page = new Page<>(pageNum, pageSize);
        IPage<SysJob> result = jobService.page(page);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 查询任务执行日志列表
     */
    @Operation(summary = "查询任务执行日志列表")
    @PreAuthorize("@ss.hasPermi('system:job:list')")
    @GetMapping("/logList")
    public TableDataInfo logList(SysJobLog jobLog,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime) {
        Page<SysJobLog> page = new Page<>(pageNum, pageSize);
        IPage<SysJobLog> result = jobLogService.page(page);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 获取定时任务详细信息
     */
    @Operation(summary = "获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('system:job:query')")
    @GetMapping("/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @Operation(summary = "新增定时任务")
    @PreAuthorize("@ss.hasPermi('system:job:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysJob job) {
        if (!jobService.checkCronExpression(job.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        return AjaxResult.success(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @Operation(summary = "修改定时任务")
    @PreAuthorize("@ss.hasPermi('system:job:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysJob job) {
        if (!jobService.checkCronExpression(job.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        return AjaxResult.success(jobService.updateJob(job));
    }

    /**
     * 删除定时任务
     */
    @Operation(summary = "删除定时任务")
    @PreAuthorize("@ss.hasPermi('system:job:remove')")
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) {
        return AjaxResult.success(jobService.deleteJobByIds(jobIds));
    }

    /**
     * 立即执行任务
     */
    @Operation(summary = "立即执行任务")
    @PreAuthorize("@ss.hasPermi('system:job:execute')")
    @PostMapping("/{jobId}/run")
    public AjaxResult run(@PathVariable("jobId") Long jobId) {
        jobService.run(jobId);
        return AjaxResult.success();
    }

    /**
     * 暂停任务
     */
    @Operation(summary = "暂停任务")
    @PreAuthorize("@ss.hasPermi('system:job:pause')")
    @PostMapping("/{jobId}/pause")
    public AjaxResult pause(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(jobService.pause(jobId));
    }

    /**
     * 恢复任务
     */
    @Operation(summary = "恢复任务")
    @PreAuthorize("@ss.hasPermi('system:job:resume')")
    @PostMapping("/{jobId}/resume")
    public AjaxResult resume(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(jobService.resume(jobId));
    }

    /**
     * 校验cron表达式
     */
    @Operation(summary = "校验cron表达式")
    @GetMapping("/checkCron")
    public AjaxResult checkCron(String cronExpression) {
        if (jobService.checkCronExpression(cronExpression)) {
            return AjaxResult.success();
        }
        return AjaxResult.error("cron表达式不正确");
    }

    /**
     * 清空任务日志
     */
    @Operation(summary = "清空任务日志")
    @PreAuthorize("@ss.hasPermi('system:job:clean')")
    @DeleteMapping("/log/clean")
    public AjaxResult cleanLog() {
        return AjaxResult.success(jobLogService.cleanJobLog());
    }
}
