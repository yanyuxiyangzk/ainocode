package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 根据操作日志ID查询操作日志
     */
    SysOperLog selectOperLogById(@Param("operId") Long operId);

    /**
     * 查询操作日志列表
     */
    List<SysOperLog> selectOperLogList(@Param("operLog") SysOperLog operLog,
                                       @Param("beginTime") LocalDateTime beginTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 批量删除操作日志
     */
    int deleteOperLogByIds(@Param("operIds") Long[] operIds);

    /**
     * 清空操作日志
     */
    int cleanOperLog();
}
