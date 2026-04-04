package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysConfig;
import com.ruoyi.nocode.system.mapper.SysConfigMapper;
import com.ruoyi.nocode.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 参数配置Service实现
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectConfigById(configId);
    }

    @Override
    public SysConfig selectConfigByKey(String configKey) {
        return configMapper.selectConfigByKey(configKey);
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConfig(SysConfig config) {
        return configMapper.insertConfig(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConfig(SysConfig config) {
        return configMapper.updateConfig(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConfigById(Long configId) {
        return configMapper.deleteConfigById(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConfigByIds(Long[] configIds) {
        return configMapper.deleteConfigByIds(configIds);
    }

    @Override
    public boolean checkConfigKeyUnique(String configKey) {
        SysConfig config = configMapper.selectConfigByKey(configKey);
        return config == null;
    }
}
