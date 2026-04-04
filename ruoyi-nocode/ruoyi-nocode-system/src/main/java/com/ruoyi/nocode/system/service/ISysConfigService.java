package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysConfig;

import java.util.List;

/**
 * 参数配置Service接口
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 根据配置ID查询配置
     */
    SysConfig selectConfigById(Long configId);

    /**
     * 根据配置键名查询配置
     */
    SysConfig selectConfigByKey(String configKey);

    /**
     * 查询配置列表
     */
    List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 新增配置
     */
    int insertConfig(SysConfig config);

    /**
     * 修改配置
     */
    int updateConfig(SysConfig config);

    /**
     * 删除配置
     */
    int deleteConfigById(Long configId);

    /**
     * 批量删除配置
     */
    int deleteConfigByIds(Long[] configIds);

    /**
     * 校验配置键名是否唯一
     */
    boolean checkConfigKeyUnique(String configKey);
}
