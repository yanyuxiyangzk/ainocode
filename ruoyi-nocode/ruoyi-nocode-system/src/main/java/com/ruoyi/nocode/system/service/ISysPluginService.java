package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.PluginInfo;
import com.ruoyi.nocode.system.entity.PluginLog;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * 插件管理Service接口
 * 
 * @author ruoyi-nocode
 */
public interface ISysPluginService extends IService<PluginInfo> {

    /**
     * 查询所有插件列表
     *
     * @param pluginInfo 查询条件
     * @return 插件列表
     */
    List<PluginInfo> selectPluginList(PluginInfo pluginInfo);

    /**
     * 根据插件编码查询
     *
     * @param pluginCode 插件编码
     * @return 插件信息
     */
    PluginInfo selectByPluginCode(String pluginCode);

    /**
     * 查询已启用的插件
     *
     * @return 插件列表
     */
    List<PluginInfo> selectEnabledPlugins();

    /**
     * 查询自动启动的插件
     *
     * @return 插件列表
     */
    List<PluginInfo> selectAutoStartPlugins();

    /**
     * 校验插件编码是否唯一
     *
     * @param pluginInfo 插件信息
     * @return 结果
     */
    boolean checkPluginCodeUnique(PluginInfo pluginInfo);

    /**
     * 安装插件（上传JAR文件）
     *
     * @param inputStream JAR文件输入流
     * @param fileName    文件名
     * @param operator    操作人
     * @param operationIp 操作IP
     * @return 插件ID
     */
    String installPlugin(InputStream inputStream, String fileName, String operator, String operationIp);

    /**
     * 安装插件（从Maven坐标）
     *
     * @param groupId    Maven GroupId
     * @param artifactId Maven ArtifactId
     * @param version    版本号
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 插件ID
     */
    String installPluginFromMaven(String groupId, String artifactId, String version, String operator, String operationIp);

    /**
     * 卸载插件
     *
     * @param pluginId   插件ID（数据库主键）
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean uninstallPlugin(Long pluginId, String operator, String operationIp);

    /**
     * 启用插件
     *
     * @param pluginId   插件ID（数据库主键）
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean enablePlugin(Long pluginId, String operator, String operationIp);

    /**
     * 停用插件
     *
     * @param pluginId   插件ID（数据库主键）
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean disablePlugin(Long pluginId, String operator, String operationIp);

    /**
     * 重载插件
     *
     * @param pluginId   插件ID（数据库主键）
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean reloadPlugin(Long pluginId, String operator, String operationIp);

    /**
     * 查询插件操作日志
     *
     * @param pluginId 插件ID
     * @return 日志列表
     */
    List<PluginLog> selectPluginLogs(Long pluginId);

    /**
     * 启动所有自动启动的插件
     */
    void startAutoStartPlugins();

    /**
     * 获取插件运行时状态
     *
     * @param pluginId 插件ID
     * @return 运行时状态描述
     */
    String getPluginRuntimeStatus(Long pluginId);
}
