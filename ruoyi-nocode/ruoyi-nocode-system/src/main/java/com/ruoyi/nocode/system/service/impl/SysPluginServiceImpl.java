package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.PluginInfo;
import com.ruoyi.nocode.system.entity.PluginLog;
import com.ruoyi.nocode.system.mapper.PluginInfoMapper;
import com.ruoyi.nocode.system.mapper.PluginLogMapper;
import com.ruoyi.nocode.system.plugin.NocodeSpringPluginManager;
import com.ruoyi.nocode.system.service.ISysPluginService;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 插件管理Service实现
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
public class SysPluginServiceImpl extends ServiceImpl<PluginInfoMapper, PluginInfo> implements ISysPluginService {

    @Autowired
    private PluginInfoMapper pluginInfoMapper;

    @Autowired
    private PluginLogMapper pluginLogMapper;

    @Autowired
    private NocodeSpringPluginManager pluginManager;

    @Value("${pf4j.plugins-root:./plugins}")
    private String pluginsRoot;

    @Override
    public List<PluginInfo> selectPluginList(PluginInfo pluginInfo) {
        LambdaQueryWrapper<PluginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PluginInfo::getDelFlag, "0");
        
        if (pluginInfo.getPluginCode() != null && !pluginInfo.getPluginCode().isEmpty()) {
            wrapper.like(PluginInfo::getPluginCode, pluginInfo.getPluginCode());
        }
        if (pluginInfo.getPluginName() != null && !pluginInfo.getPluginName().isEmpty()) {
            wrapper.like(PluginInfo::getPluginName, pluginInfo.getPluginName());
        }
        if (pluginInfo.getStatus() != null && !pluginInfo.getStatus().isEmpty()) {
            wrapper.eq(PluginInfo::getStatus, pluginInfo.getStatus());
        }
        if (pluginInfo.getInstallType() != null && !pluginInfo.getInstallType().isEmpty()) {
            wrapper.eq(PluginInfo::getInstallType, pluginInfo.getInstallType());
        }
        
        wrapper.orderByAsc(PluginInfo::getStartOrder);
        wrapper.orderByDesc(PluginInfo::getCreateTime);
        
        return list(wrapper);
    }

    @Override
    public PluginInfo selectByPluginCode(String pluginCode) {
        return pluginInfoMapper.selectByPluginCode(pluginCode);
    }

    @Override
    public List<PluginInfo> selectEnabledPlugins() {
        return pluginInfoMapper.selectEnabledPlugins();
    }

    @Override
    public List<PluginInfo> selectAutoStartPlugins() {
        return pluginInfoMapper.selectAutoStartPlugins();
    }

    @Override
    public boolean checkPluginCodeUnique(PluginInfo pluginInfo) {
        Long pluginId = pluginInfo.getPluginId();
        PluginInfo existing = pluginInfoMapper.checkPluginCodeUnique(
                pluginInfo.getPluginCode(), pluginId);
        return existing == null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPlugin(InputStream inputStream, String fileName, String operator, String operationIp) {
        try {
            // 确保插件目录存在
            Path pluginsPath = Paths.get(pluginsRoot);
            if (!Files.exists(pluginsPath)) {
                Files.createDirectories(pluginsPath);
            }
            
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path pluginPath = pluginsPath.resolve(uniqueFileName);
            
            // 复制文件到插件目录
            Files.copy(inputStream, pluginPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 计算文件哈希
            String fileHash = calculateFileHash(pluginPath);
            
            // 检查是否已存在相同文件
            PluginInfo existingPlugin = pluginInfoMapper.selectByFileHash(fileHash);
            if (existingPlugin != null) {
                Files.delete(pluginPath);
                throw new RuntimeException("已存在相同的插件: " + existingPlugin.getPluginCode());
            }
            
            // 调用PF4J管理器安装
            String pluginId = pluginManager.installPlugin(pluginPath, operator, operationIp);
            
            // 更新文件哈希
            PluginInfo installedPlugin = pluginInfoMapper.selectByPluginCode(pluginId);
            if (installedPlugin != null) {
                installedPlugin.setFileHash(fileHash);
                pluginInfoMapper.updateById(installedPlugin);
            }
            
            return pluginId;
            
        } catch (IOException e) {
            log.error("安装插件失败: {}", fileName, e);
            throw new RuntimeException("安装插件失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPluginFromMaven(String groupId, String artifactId, String version, String operator, String operationIp) {
        // Maven安装暂未实现，需要集成Maven Resolver
        throw new UnsupportedOperationException("Maven方式安装暂未实现");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uninstallPlugin(Long pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = getById(pluginId);
        if (pluginInfo == null) {
            throw new RuntimeException("插件不存在: " + pluginId);
        }
        
        return pluginManager.uninstallPlugin(pluginInfo.getPluginCode(), operator, operationIp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enablePlugin(Long pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = getById(pluginId);
        if (pluginInfo == null) {
            throw new RuntimeException("插件不存在: " + pluginId);
        }
        
        return pluginManager.enablePlugin(pluginInfo.getPluginCode(), operator, operationIp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disablePlugin(Long pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = getById(pluginId);
        if (pluginInfo == null) {
            throw new RuntimeException("插件不存在: " + pluginId);
        }
        
        return pluginManager.disablePlugin(pluginInfo.getPluginCode(), operator, operationIp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reloadPlugin(Long pluginId, String operator, String operationIp) {
        PluginInfo pluginInfo = getById(pluginId);
        if (pluginInfo == null) {
            throw new RuntimeException("插件不存在: " + pluginId);
        }
        
        return pluginManager.reloadPlugin(pluginInfo.getPluginCode(), operator, operationIp);
    }

    @Override
    public List<PluginLog> selectPluginLogs(Long pluginId) {
        return pluginLogMapper.selectByPluginId(pluginId);
    }

    @Override
    public void startAutoStartPlugins() {
        List<PluginInfo> autoStartPlugins = selectAutoStartPlugins();
        
        for (PluginInfo pluginInfo : autoStartPlugins) {
            try {
                enablePlugin(pluginInfo.getPluginId(), "system", "localhost");
                log.info("自动启动插件: {}", pluginInfo.getPluginCode());
            } catch (Exception e) {
                log.error("自动启动插件失败: {}", pluginInfo.getPluginCode(), e);
            }
        }
    }

    @Override
    public String getPluginRuntimeStatus(Long pluginId) {
        PluginInfo pluginInfo = getById(pluginId);
        if (pluginInfo == null) {
            return "NOT_FOUND";
        }
        
        PluginState state = pluginManager.getPluginRuntimeState(pluginInfo.getPluginCode());
        return state != null ? state.name() : "NOT_LOADED";
    }

    /**
     * 计算文件MD5哈希
     */
    private String calculateFileHash(Path path) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException("获取MD5算法失败", e);
        }
        
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        
        byte[] hash = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
