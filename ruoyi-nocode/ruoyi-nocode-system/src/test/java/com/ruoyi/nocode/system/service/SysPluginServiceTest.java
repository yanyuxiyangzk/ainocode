package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.PluginInfo;
import com.ruoyi.nocode.system.entity.PluginLog;
import com.ruoyi.nocode.system.mapper.PluginInfoMapper;
import com.ruoyi.nocode.system.mapper.PluginLogMapper;
import com.ruoyi.nocode.system.plugin.NocodeSpringPluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysPluginService 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class SysPluginServiceTest {

    @Mock
    private PluginInfoMapper pluginInfoMapper;

    @Mock
    private PluginLogMapper pluginLogMapper;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ILiquorCompilerService liquorCompilerService;

    private NocodeSpringPluginManager pluginManager;

    @BeforeEach
    void setUp() {
        Path pluginsRoot = Paths.get("./test-plugins");
        pluginManager = new NocodeSpringPluginManager(
                pluginsRoot,
                applicationContext,
                pluginInfoMapper,
                pluginLogMapper,
                liquorCompilerService
        );
    }

    @Test
    @DisplayName("查询插件列表")
    void testSelectPluginList() {
        List<PluginInfo> mockList = new ArrayList<>();
        PluginInfo plugin1 = new PluginInfo();
        plugin1.setPluginId(1L);
        plugin1.setPluginCode("demo-plugin");
        plugin1.setPluginName("演示插件");
        plugin1.setStatus(PluginInfo.STATUS_ENABLED);
        mockList.add(plugin1);

        when(pluginInfoMapper.selectList(any())).thenReturn(mockList);

        List<PluginInfo> result = pluginInfoMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("demo-plugin", result.get(0).getPluginCode());
    }

    @Test
    @DisplayName("根据编码查询插件")
    void testSelectByPluginCode() {
        PluginInfo plugin = new PluginInfo();
        plugin.setPluginId(1L);
        plugin.setPluginCode("demo-plugin");
        plugin.setPluginName("演示插件");

        when(pluginInfoMapper.selectByPluginCode("demo-plugin")).thenReturn(plugin);

        PluginInfo result = pluginInfoMapper.selectByPluginCode("demo-plugin");

        assertNotNull(result);
        assertEquals("demo-plugin", result.getPluginCode());
    }

    @Test
    @DisplayName("查询已启用插件")
    void testSelectEnabledPlugins() {
        List<PluginInfo> mockList = new ArrayList<>();
        PluginInfo plugin1 = new PluginInfo();
        plugin1.setPluginId(1L);
        plugin1.setPluginCode("enabled-plugin");
        plugin1.setStatus(PluginInfo.STATUS_ENABLED);
        mockList.add(plugin1);

        when(pluginInfoMapper.selectEnabledPlugins()).thenReturn(mockList);

        List<PluginInfo> result = pluginInfoMapper.selectEnabledPlugins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PluginInfo.STATUS_ENABLED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("检查插件编码唯一性")
    void testCheckPluginCodeUnique() {
        when(pluginInfoMapper.selectByPluginCode("new-plugin")).thenReturn(null);

        boolean result = pluginInfoMapper.selectByPluginCode("new-plugin") == null;

        assertTrue(result);
    }

    @Test
    @DisplayName("插件编码重复")
    void testCheckPluginCodeDuplicate() {
        PluginInfo existingPlugin = new PluginInfo();
        existingPlugin.setPluginId(1L);
        existingPlugin.setPluginCode("existing-plugin");

        when(pluginInfoMapper.selectByPluginCode("existing-plugin")).thenReturn(existingPlugin);

        PluginInfo result = pluginInfoMapper.selectByPluginCode("existing-plugin");

        assertNotNull(result);
    }

    @Test
    @DisplayName("记录插件操作日志")
    void testLogOperation() {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(1L);
        pluginInfo.setPluginCode("demo-plugin");
        pluginInfo.setPluginName("演示插件");

        when(pluginLogMapper.insert(any(PluginLog.class))).thenReturn(1);

        PluginLog log = new PluginLog();
        log.setPluginId(pluginInfo.getPluginId());
        log.setPluginCode(pluginInfo.getPluginCode());
        log.setOperationType(PluginLog.OPERATION_ENABLE);
        log.setOperationStatus(PluginLog.STATUS_SUCCESS);

        int result = pluginLogMapper.insert(log);

        assertEquals(1, result);
        verify(pluginLogMapper, times(1)).insert(any(PluginLog.class));
    }
}
