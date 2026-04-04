package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统访问记录Mapper接口
 */
@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

    /**
     * 根据访问ID查询访问记录
     */
    SysLogininfor selectLogininforById(@Param("infoId") Long infoId);

    /**
     * 查询登录日志列表
     */
    List<SysLogininfor> selectLogininforList(@Param("logininfor") SysLogininfor logininfor,
                                            @Param("beginTime") LocalDateTime beginTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 批量删除登录日志
     */
    int deleteLogininforByIds(@Param("infoIds") Long[] infoIds);

    /**
     * 清空登录日志
     */
    int cleanLogininfor();
}
