package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysJob;

import java.util.List;

/**
 * 定时任务Service接口
 */
public interface ISysJobService extends IService<SysJob> {

    /**
     * 根据任务ID查询任务
     */
    SysJob selectJobById(Long jobId);

    /**
     * 查询任务列表
     */
    List<SysJob> selectJobList(SysJob job);

    /**
     * 新增任务
     */
    int insertJob(SysJob job);

    /**
     * 更新任务
     */
    int updateJob(SysJob job);

    /**
     * 删除任务
     */
    int deleteJobById(Long jobId);

    /**
     * 批量删除任务
     */
    int deleteJobByIds(Long[] jobIds);

    /**
     * 立即执行任务
     */
    void run(Long jobId);

    /**
     * 暂停任务
     */
    int pause(Long jobId);

    /**
     * 恢复任务
     */
    int resume(Long jobId);

    /**
     * 校验cron表达式
     */
    boolean checkCronExpression(String cronExpression);
}
