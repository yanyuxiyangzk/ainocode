package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 参数配置Mapper接口
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置ID查询配置
     */
    SysConfig selectConfigById(@Param("configId") Long configId);

    /**
     * 根据配置键名查询配置
     */
    SysConfig selectConfigByKey(@Param("configKey") String configKey);

    /**
     * 查询配置列表
     */
    java.util.List<SysConfig> selectConfigList(SysConfig config);

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
    int deleteConfigById(@Param("configId") Long configId);

    /**
     * 批量删除配置
     */
    int deleteConfigByIds(@Param("configIds") Long[] configIds);
}
