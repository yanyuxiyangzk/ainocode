package com.ruoyi.nocode.system.service.impl;

import cn.hutool.cron.CronUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysJob;
import com.ruoyi.nocode.system.mapper.SysJobMapper;
import com.ruoyi.nocode.system.service.ISysJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 定时任务Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {

    private final SysJobMapper jobMapper;

    @Override
    public SysJob selectJobById(Long jobId) {
        return jobMapper.selectJobById(jobId);
    }

    @Override
    public List<SysJob> selectJobList(SysJob job) {
        return jobMapper.selectJobList(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(SysJob job) {
        return jobMapper.insertJob(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJob job) {
        return jobMapper.updateJob(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobById(Long jobId) {
        return jobMapper.deleteJobById(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobByIds(Long[] jobIds) {
        return jobMapper.deleteJobByIds(jobIds);
    }

    @Override
    public void run(Long jobId) {
        SysJob job = selectJobById(jobId);
        if (job != null) {
            // 立即执行任务 - 这里简化处理，实际应该使用线程池执行
            log.info("立即执行任务: {}, invokeTarget: {}", job.getJobName(), job.getInvokeTarget());
            // TODO: 实际调用任务执行
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pause(Long jobId) {
        SysJob job = new SysJob();
        job.setJobId(jobId);
        job.setStatus("1");
        return jobMapper.updateJob(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resume(Long jobId) {
        SysJob job = new SysJob();
        job.setJobId(jobId);
        job.setStatus("0");
        return jobMapper.updateJob(job);
    }

    @Override
    public boolean checkCronExpression(String cronExpression) {
        try {
            // CronUtil.isValid removed in newer Hutool versions
            // Try to validate by attempting to use the pattern
            cn.hutool.cron.pattern.CronPattern pattern = new cn.hutool.cron.pattern.CronPattern(cronExpression);
            return pattern != null;
        } catch (Exception e) {
            return false;
        }
    }
}
