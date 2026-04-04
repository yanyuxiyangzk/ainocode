package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysLogininfor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志Service接口
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /**
     * 查询登录日志
     */
    SysLogininfor selectLogininforById(Long infoId);

    /**
     * 查询登录日志列表
     */
    List<SysLogininfor> selectLogininforList(SysLogininfor logininfor, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 新增登录日志
     */
    int insertLogininfor(SysLogininfor logininfor);

    /**
     * 删除登录日志
     */
    int deleteLogininforById(Long infoId);

    /**
     * 批量删除登录日志
     */
    int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空登录日志
     */
    void cleanLogininfor();
}
