package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.PluginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 插件信息Mapper接口
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface PluginInfoMapper extends BaseMapper<PluginInfo> {

    /**
     * 根据插件编码查询插件信息
     *
     * @param pluginCode 插件编码
     * @return 插件信息
     */
    PluginInfo selectByPluginCode(@Param("pluginCode") String pluginCode);

    /**
     * 查询所有已启用的插件
     *
     * @return 插件列表
     */
    List<PluginInfo> selectEnabledPlugins();

    /**
     * 查询所有自动启动的插件
     *
     * @return 插件列表
     */
    List<PluginInfo> selectAutoStartPlugins();

    /**
     * 根据状态查询插件列表
     *
     * @param status 插件状态
     * @return 插件列表
     */
    List<PluginInfo> selectByStatus(@Param("status") String status);

    /**
     * 校验插件编码是否唯一
     *
     * @param pluginCode 插件编码
     * @param pluginId   插件ID（编辑时排除自身）
     * @return 插件信息
     */
    PluginInfo checkPluginCodeUnique(@Param("pluginCode") String pluginCode, @Param("pluginId") Long pluginId);

    /**
     * 根据文件哈希查询插件
     *
     * @param fileHash 文件哈希值
     * @return 插件信息
     */
    PluginInfo selectByFileHash(@Param("fileHash") String fileHash);
}
