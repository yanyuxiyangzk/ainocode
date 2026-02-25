package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.PluginDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 插件依赖关系Mapper接口
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface PluginDependencyMapper extends BaseMapper<PluginDependency> {

    /**
     * 根据插件ID查询依赖列表
     *
     * @param pluginId 插件ID
     * @return 依赖列表
     */
    List<PluginDependency> selectByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 查询依赖于指定插件的所有插件
     *
     * @param dependPluginCode 被依赖的插件编码
     * @return 依赖关系列表
     */
    List<PluginDependency> selectDependentPlugins(@Param("dependPluginCode") String dependPluginCode);

    /**
     * 删除插件的所有依赖关系
     *
     * @param pluginId 插件ID
     * @return 影响行数
     */
    int deleteByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 批量插入依赖关系
     *
     * @param dependencies 依赖列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<PluginDependency> dependencies);
}
