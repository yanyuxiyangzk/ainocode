package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysOperLog;
import com.ruoyi.nocode.system.mapper.SysOperLogMapper;
import com.ruoyi.nocode.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Service实现
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return operLogMapper.selectOperLogById(operId);
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog, LocalDateTime beginTime, LocalDateTime endTime) {
        return operLogMapper.selectOperLogList(operLog, beginTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOperLog(SysOperLog operLog) {
        return operLogMapper.insert(operLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOperLogById(Long operId) {
        return operLogMapper.deleteById(operId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOperLogByIds(Long[] operIds) {
        return operLogMapper.deleteBatchIds(List.of(operIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
