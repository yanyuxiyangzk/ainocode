package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysOperLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Service接口
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 查询操作日志
     */
    SysOperLog selectOperLogById(Long operId);

    /**
     * 查询操作日志列表
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 新增操作日志
     */
    int insertOperLog(SysOperLog operLog);

    /**
     * 删除操作日志
     */
    int deleteOperLogById(Long operId);

    /**
     * 批量删除操作日志
     */
    int deleteOperLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
