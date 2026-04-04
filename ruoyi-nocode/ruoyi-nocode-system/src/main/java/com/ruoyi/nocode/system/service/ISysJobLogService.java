package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysJobLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务日志Service接口
 */
public interface ISysJobLogService extends IService<SysJobLog> {

    /**
     * 根据任务日志ID查询任务日志
     */
    SysJobLog selectJobLogById(Long jobLogId);

    /**
     * 查询任务日志列表
     */
    List<SysJobLog> selectJobLogList(SysJobLog jobLog, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 新增任务日志
     */
    int insertJobLog(SysJobLog jobLog);

    /**
     * 删除任务日志
     */
    int deleteJobLogById(Long jobLogId);

    /**
     * 批量删除任务日志
     */
    int deleteJobLogByIds(Long[] jobLogIds);

    /**
     * 清空任务日志
     */
    int cleanJobLog();
}
