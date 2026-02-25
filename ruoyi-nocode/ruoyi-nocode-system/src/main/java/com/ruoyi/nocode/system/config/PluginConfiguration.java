package com.ruoyi.nocode.system.config;

import com.ruoyi.nocode.system.mapper.PluginInfoMapper;
import com.ruoyi.nocode.system.mapper.PluginLogMapper;
import com.ruoyi.nocode.system.plugin.NocodeSpringPluginManager;
import com.ruoyi.nocode.system.service.ILiquorCompilerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PF4J 插件配置类
 * 
 * @author ruoyi-nocode
 */
@Configuration
public class PluginConfiguration {

    /**
     * 插件根目录
     */
    @Value("${pf4j.plugins-root:./plugins}")
    private String pluginsRoot;

    /**
     * 创建PF4J Spring插件管理器
     * <p>
     * 注意：由于NocodeSpringPluginManager需要依赖注入其他Bean，
     * 这里使用ApplicationContext手动构建，避免循环依赖问题
     *
     * @param applicationContext   Spring应用上下文
     * @param pluginInfoMapper     插件信息Mapper
     * @param pluginLogMapper      插件日志Mapper
     * @param liquorCompilerService Liquor编译服务
     * @return NocodeSpringPluginManager实例
     */
    @Bean
    public NocodeSpringPluginManager pluginManager(
            ApplicationContext applicationContext,
            PluginInfoMapper pluginInfoMapper,
            PluginLogMapper pluginLogMapper,
            ILiquorCompilerService liquorCompilerService) {
        
        Path pluginsRootPath = Paths.get(pluginsRoot);
        
        return new NocodeSpringPluginManager(
                pluginsRootPath,
                applicationContext,
                pluginInfoMapper,
                pluginLogMapper,
                liquorCompilerService
        );
    }
}
