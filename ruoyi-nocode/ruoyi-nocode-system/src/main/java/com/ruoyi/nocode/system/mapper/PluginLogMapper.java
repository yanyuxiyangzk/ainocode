package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.PluginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 插件操作日志Mapper接口
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface PluginLogMapper extends BaseMapper<PluginLog> {

    /**
     * 根据插件ID查询操作日志
     *
     * @param pluginId 插件ID
     * @return 日志列表
     */
    List<PluginLog> selectByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 根据插件编码查询操作日志
     *
     * @param pluginCode 插件编码
     * @return 日志列表
     */
    List<PluginLog> selectByPluginCode(@Param("pluginCode") String pluginCode);

    /**
     * 根据操作类型查询日志
     *
     * @param operationType 操作类型
     * @return 日志列表
     */
    List<PluginLog> selectByOperationType(@Param("operationType") String operationType);
}
