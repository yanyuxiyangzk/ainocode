package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务日志Mapper接口
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 根据任务日志ID查询任务日志
     */
    SysJobLog selectJobLogById(@Param("jobLogId") Long jobLogId);

    /**
     * 查询任务日志列表
     */
    List<SysJobLog> selectJobLogList(SysJobLog jobLog,
                                      @Param("beginTime") LocalDateTime beginTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 新增任务日志
     */
    int insertJobLog(SysJobLog jobLog);

    /**
     * 删除任务日志
     */
    int deleteJobLogById(@Param("jobLogId") Long jobLogId);

    /**
     * 批量删除任务日志
     */
    int deleteJobLogByIds(@Param("jobLogIds") Long[] jobLogIds);

    /**
     * 清空任务日志
     */
    int cleanJobLog();
}
