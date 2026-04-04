package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务Mapper接口
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

    /**
     * 根据任务ID查询任务
     */
    SysJob selectJobById(@Param("jobId") Long jobId);

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
    int deleteJobById(@Param("jobId") Long jobId);

    /**
     * 批量删除任务
     */
    int deleteJobByIds(@Param("jobIds") Long[] jobIds);
}
