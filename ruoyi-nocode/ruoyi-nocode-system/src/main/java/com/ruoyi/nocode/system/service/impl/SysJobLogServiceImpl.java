package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysJobLog;
import com.ruoyi.nocode.system.mapper.SysJobLogMapper;
import com.ruoyi.nocode.system.service.ISysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务日志Service实现
 */
@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements ISysJobLogService {

    private final SysJobLogMapper jobLogMapper;

    @Override
    public SysJobLog selectJobLogById(Long jobLogId) {
        return jobLogMapper.selectJobLogById(jobLogId);
    }

    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog, LocalDateTime beginTime, LocalDateTime endTime) {
        return jobLogMapper.selectJobLogList(jobLog, beginTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJobLog(SysJobLog jobLog) {
        return jobLogMapper.insertJobLog(jobLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobLogById(Long jobLogId) {
        return jobLogMapper.deleteJobLogById(jobLogId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobLogByIds(Long[] jobLogIds) {
        return jobLogMapper.deleteJobLogByIds(jobLogIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanJobLog() {
        return jobLogMapper.cleanJobLog();
    }
}
