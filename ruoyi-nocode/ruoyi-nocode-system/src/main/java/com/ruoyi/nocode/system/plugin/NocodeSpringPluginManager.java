package com.ruoyi.nocode.system.plugin;

import com.ruoyi.nocode.system.entity.PluginInfo;
import com.ruoyi.nocode.system.entity.PluginLog;
import com.ruoyi.nocode.system.mapper.PluginInfoMapper;
import com.ruoyi.nocode.system.mapper.PluginLogMapper;
import com.ruoyi.nocode.system.service.ILiquorCompilerService;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.*;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 零代码平台PF4J插件管理器
 * 扩展SpringPluginManager，集成数据库持久化和Liquor动态编译
 * 
 * @author ruoyi-nocode
 */
@Slf4j
public class NocodeSpringPluginManager extends SpringPluginManager {

    private final PluginInfoMapper pluginInfoMapper;
    private final PluginLogMapper pluginLogMapper;
    private final ILiquorCompilerService liquorCompilerService;
    
    /**
     * Spring应用上下文（用于扩展点Bean注册）
     */
    private ApplicationContext applicationContext;

    /**
     * 构造函数
     *
     * @param pluginsRoot 插件根目录
     */
    public NocodeSpringPluginManager(Path pluginsRoot) {
        super(pluginsRoot);
        this.pluginInfoMapper = null;
        this.pluginLogMapper = null;
        this.liquorCompilerService = null;
    }

    /**
     * 构造函数 - Spring注入版本
     *
     * @param pluginsRoot          插件根目录
     * @param applicationContext   Spring应用上下文
     * @param pluginInfoMapper     插件信息Mapper
     * @param pluginLogMapper      插件日志Mapper
     * @param liquorCompilerService Liquor编译服务
     */
    public NocodeSpringPluginManager(
            Path pluginsRoot,
            ApplicationContext applicationContext,
            PluginInfoMapper pluginInfoMapper,
            PluginLogMapper pluginLogMapper,
            @Lazy ILiquorCompilerService liquorCompilerService) {
        super(pluginsRoot);
        this.pluginInfoMapper = pluginInfoMapper;
        this.pluginLogMapper = pluginLogMapper;
        this.liquorCompilerService = liquorCompilerService;
        this.applicationContext = applicationContext;
    }
    
    /**
     * 设置Spring应用上下文
     *
     * @param applicationContext Spring应用上下文
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 安装插件（从JAR文件）
     *
     * @param pluginPath 插件JAR文件路径
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 插件ID
     */
    public String installPlugin(Path pluginPath, String operator, String operationIp) {
        String pluginId = null;
        PluginInfo pluginInfo = null;
        String oldStatus = null;

        try {
            // 加载插件
            pluginId = loadPlugin(pluginPath);
            PluginWrapper pluginWrapper = getPlugin(pluginId);
            
            // 获取插件描述
            PluginDescriptor descriptor = pluginWrapper.getDescriptor();
            
            // 检查是否已存在
            pluginInfo = pluginInfoMapper.selectByPluginCode(pluginId);
            boolean isNew = (pluginInfo == null);
            
            if (isNew) {
                pluginInfo = new PluginInfo();
                pluginInfo.setPluginCode(pluginId);
            } else {
                oldStatus = pluginInfo.getStatus();
            }
            
            // 更新插件信息
            pluginInfo.setPluginName(descriptor.getPluginId());
            pluginInfo.setPluginVersion(descriptor.getVersion());
            pluginInfo.setPluginDesc(getPluginDescription(descriptor));
            pluginInfo.setPluginClass(descriptor.getPluginClass());
            pluginInfo.setPluginProvider(descriptor.getProvider());
            pluginInfo.setPluginPath(pluginPath.toString());
            pluginInfo.setPluginSize(pluginPath.toFile().length());
            pluginInfo.setInstallType(PluginInfo.INSTALL_TYPE_JAR);
            pluginInfo.setStatus(PluginInfo.STATUS_DISABLED);
            pluginInfo.setRunMode(PluginInfo.RUN_MODE_STATIC);
            pluginInfo.setLastLoadTime(LocalDateTime.now());
            pluginInfo.setCreateBy(operator);
            pluginInfo.setUpdateBy(operator);
            
            if (isNew) {
                pluginInfoMapper.insert(pluginInfo);
            } else {
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 卸载插件（安装后默认不启动）
            unloadPlugin(pluginId);
            
            // 记录日志
            logOperation(pluginInfo, PluginLog.OPERATION_INSTALL, PluginLog.STATUS_SUCCESS,
                    "插件安装成功", oldStatus, PluginInfo.STATUS_DISABLED, null, operator, operationIp);
            
            log.info("插件安装成功: {} ({})", descriptor.getPluginId(), descriptor.getVersion());
            return pluginId;
            
        } catch (Exception e) {
            log.error("插件安装失败: {}", pluginPath, e);
            
            // 记录失败日志
            if (pluginInfo != null) {
                pluginInfo.setStatus(PluginInfo.STATUS_ERROR);
                pluginInfo.setErrorMsg(e.getMessage());
                pluginInfoMapper.updateById(pluginInfo);
                
                logOperation(pluginInfo, PluginLog.OPERATION_INSTALL, PluginLog.STATUS_FAIL,
                        "插件安装失败", oldStatus, PluginInfo.STATUS_ERROR, e.getMessage(), operator, operationIp);
            }
            
            throw new RuntimeException("插件安装失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取插件描述信息
     * 兼容不同版本的PF4J PluginDescriptor接口
     *
     * @param descriptor 插件描述器
     * @return 描述信息
     */
    private String getPluginDescription(PluginDescriptor descriptor) {
        // PF4J 3.x 的 PluginDescriptor 使用 getPluginDescription() 方法
        // 如果返回 null 或方法不存在，返回空字符串
        String description = descriptor.getPluginDescription();
        return description != null ? description : "";
    }

    /**
     * 卸载插件
     *
     * @param pluginId   插件ID
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    public boolean uninstallPlugin(String pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = pluginInfoMapper.selectByPluginCode(pluginId);
        String oldStatus = null;
        
        try {
            if (pluginInfo != null) {
                oldStatus = pluginInfo.getStatus();
                
                // 如果插件正在运行，先停止
                if (PluginInfo.STATUS_ENABLED.equals(oldStatus)) {
                    stopPlugin(pluginId);
                }
            }
            
            // 删除插件文件
            if (pluginInfo != null && pluginInfo.getPluginPath() != null) {
                Path pluginPath = Path.of(pluginInfo.getPluginPath());
                if (pluginPath.toFile().exists()) {
                    try {
                        java.nio.file.Files.delete(pluginPath);
                    } catch (IOException e) {
                        log.warn("删除插件文件失败: {}", pluginPath, e);
                    }
                }
            }
            
            // 软删除数据库记录
            if (pluginInfo != null) {
                pluginInfo.setDelFlag("2");
                pluginInfo.setLastUnloadTime(LocalDateTime.now());
                pluginInfo.setUpdateBy(operator);
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 从内存中卸载
            if (getPlugins().stream().anyMatch(p -> p.getPluginId().equals(pluginId))) {
                unloadPlugin(pluginId);
            }
            
            // 记录日志
            logOperation(pluginInfo, PluginLog.OPERATION_UNINSTALL, PluginLog.STATUS_SUCCESS,
                    "插件卸载成功", oldStatus, null, null, operator, operationIp);
            
            log.info("插件卸载成功: {}", pluginId);
            return true;
            
        } catch (Exception e) {
            log.error("插件卸载失败: {}", pluginId, e);
            
            // 更新状态
            if (pluginInfo != null) {
                pluginInfo.setStatus(PluginInfo.STATUS_ERROR);
                pluginInfo.setErrorMsg(e.getMessage());
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 记录失败日志
            logOperation(pluginInfo, PluginLog.OPERATION_UNINSTALL, PluginLog.STATUS_FAIL,
                    "插件卸载失败", oldStatus, PluginInfo.STATUS_ERROR, e.getMessage(), operator, operationIp);
            
            throw new RuntimeException("插件卸载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 启用插件
     *
     * @param pluginId   插件ID
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    public boolean enablePlugin(String pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = pluginInfoMapper.selectByPluginCode(pluginId);
        String oldStatus = null;
        
        try {
            if (pluginInfo == null) {
                throw new RuntimeException("插件不存在: " + pluginId);
            }
            
            oldStatus = pluginInfo.getStatus();
            
            // 检查插件文件是否存在
            Path pluginPath = Path.of(pluginInfo.getPluginPath());
            if (!pluginPath.toFile().exists()) {
                throw new RuntimeException("插件文件不存在: " + pluginInfo.getPluginPath());
            }
            
            // 加载并启动插件
            String loadedPluginId = loadPlugin(pluginPath);
            startPlugin(loadedPluginId);
            
            // 更新数据库状态
            pluginInfo.setStatus(PluginInfo.STATUS_ENABLED);
            pluginInfo.setLastLoadTime(LocalDateTime.now());
            pluginInfo.setErrorMsg(null);
            pluginInfo.setUpdateBy(operator);
            pluginInfoMapper.updateById(pluginInfo);
            
            // 记录日志
            logOperation(pluginInfo, PluginLog.OPERATION_ENABLE, PluginLog.STATUS_SUCCESS,
                    "插件启用成功", oldStatus, PluginInfo.STATUS_ENABLED, null, operator, operationIp);
            
            log.info("插件启用成功: {}", pluginId);
            return true;
            
        } catch (Exception e) {
            log.error("插件启用失败: {}", pluginId, e);
            
            // 更新状态
            if (pluginInfo != null) {
                pluginInfo.setStatus(PluginInfo.STATUS_ERROR);
                pluginInfo.setErrorMsg(e.getMessage());
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 记录失败日志
            logOperation(pluginInfo, PluginLog.OPERATION_ENABLE, PluginLog.STATUS_FAIL,
                    "插件启用失败", oldStatus, PluginInfo.STATUS_ERROR, e.getMessage(), operator, operationIp);
            
            throw new RuntimeException("插件启用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 停用插件
     *
     * @param pluginId   插件ID
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    public boolean disablePlugin(String pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = pluginInfoMapper.selectByPluginCode(pluginId);
        String oldStatus = null;
        
        try {
            if (pluginInfo == null) {
                throw new RuntimeException("插件不存在: " + pluginId);
            }
            
            oldStatus = pluginInfo.getStatus();
            
            // 停止并卸载插件
            stopPlugin(pluginId);
            unloadPlugin(pluginId);
            
            // 更新数据库状态
            pluginInfo.setStatus(PluginInfo.STATUS_DISABLED);
            pluginInfo.setLastUnloadTime(LocalDateTime.now());
            pluginInfo.setErrorMsg(null);
            pluginInfo.setUpdateBy(operator);
            pluginInfoMapper.updateById(pluginInfo);
            
            // 记录日志
            logOperation(pluginInfo, PluginLog.OPERATION_DISABLE, PluginLog.STATUS_SUCCESS,
                    "插件停用成功", oldStatus, PluginInfo.STATUS_DISABLED, null, operator, operationIp);
            
            log.info("插件停用成功: {}", pluginId);
            return true;
            
        } catch (Exception e) {
            log.error("插件停用失败: {}", pluginId, e);
            
            // 更新状态
            if (pluginInfo != null) {
                pluginInfo.setStatus(PluginInfo.STATUS_ERROR);
                pluginInfo.setErrorMsg(e.getMessage());
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 记录失败日志
            logOperation(pluginInfo, PluginLog.OPERATION_DISABLE, PluginLog.STATUS_FAIL,
                    "插件停用失败", oldStatus, PluginInfo.STATUS_ERROR, e.getMessage(), operator, operationIp);
            
            throw new RuntimeException("插件停用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重载插件
     *
     * @param pluginId   插件ID
     * @param operator   操作人
     * @param operationIp 操作IP
     * @return 是否成功
     */
    public boolean reloadPlugin(String pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = pluginInfoMapper.selectByPluginCode(pluginId);
        String oldStatus = null;
        
        try {
            if (pluginInfo == null) {
                throw new RuntimeException("插件不存在: " + pluginId);
            }
            
            oldStatus = pluginInfo.getStatus();
            
            // 如果正在运行，先停止
            if (PluginInfo.STATUS_ENABLED.equals(oldStatus)) {
                stopPlugin(pluginId);
                unloadPlugin(pluginId);
            }
            
            // 重新加载
            Path pluginPath = Path.of(pluginInfo.getPluginPath());
            String loadedPluginId = loadPlugin(pluginPath);
            startPlugin(loadedPluginId);
            
            // 更新数据库状态
            pluginInfo.setStatus(PluginInfo.STATUS_ENABLED);
            pluginInfo.setLastLoadTime(LocalDateTime.now());
            pluginInfo.setErrorMsg(null);
            pluginInfo.setUpdateBy(operator);
            pluginInfoMapper.updateById(pluginInfo);
            
            // 记录日志
            logOperation(pluginInfo, PluginLog.OPERATION_RELOAD, PluginLog.STATUS_SUCCESS,
                    "插件重载成功", oldStatus, PluginInfo.STATUS_ENABLED, null, operator, operationIp);
            
            log.info("插件重载成功: {}", pluginId);
            return true;
            
        } catch (Exception e) {
            log.error("插件重载失败: {}", pluginId, e);
            
            // 更新状态
            if (pluginInfo != null) {
                pluginInfo.setStatus(PluginInfo.STATUS_ERROR);
                pluginInfo.setErrorMsg(e.getMessage());
                pluginInfoMapper.updateById(pluginInfo);
            }
            
            // 记录失败日志
            logOperation(pluginInfo, PluginLog.OPERATION_RELOAD, PluginLog.STATUS_FAIL,
                    "插件重载失败", oldStatus, PluginInfo.STATUS_ERROR, e.getMessage(), operator, operationIp);
            
            throw new RuntimeException("插件重载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取插件运行时状态
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState getPluginRuntimeState(String pluginId) {
        PluginWrapper plugin = getPlugin(pluginId);
        return plugin != null ? plugin.getPluginState() : null;
    }

    /**
     * 获取所有已加载的插件ID列表
     *
     * @return 插件ID列表
     */
    public List<String> getLoadedPluginIds() {
        return getPlugins().stream()
                .map(PluginWrapper::getPluginId)
                .toList();
    }

    /**
     * 记录操作日志
     */
    private void logOperation(PluginInfo pluginInfo, String operationType, String operationStatus,
                              String operationDesc, String oldStatus, String newStatus,
                              String errorMsg, String operator, String operationIp) {
        if (pluginLogMapper == null) {
            return;
        }
        
        PluginLog log = new PluginLog();
        log.setPluginId(pluginInfo != null ? pluginInfo.getPluginId() : null);
        log.setPluginCode(pluginInfo != null ? pluginInfo.getPluginCode() : null);
        log.setPluginName(pluginInfo != null ? pluginInfo.getPluginName() : null);
        log.setOperationType(operationType);
        log.setOperationStatus(operationStatus);
        log.setOperationDesc(operationDesc);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setErrorMsg(errorMsg);
        log.setOperationBy(operator);
        log.setOperationIp(operationIp);
        log.setOperationTime(LocalDateTime.now());
        
        pluginLogMapper.insert(log);
    }
}
