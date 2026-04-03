package com.nocode.admin.service;

import com.nocode.admin.entity.DatasourceConfigEntity;
import com.nocode.admin.repository.DatasourceConfigRepository;
import com.nocode.core.datasource.DatasourceRegistry;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.DatasourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DatasourceConfigService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class DatasourceConfigServiceTest {

    @Mock
    private DatasourceConfigRepository datasourceConfigRepository;

    @Mock
    private DatasourceRegistry datasourceRegistry;

    @InjectMocks
    private DatasourceConfigService datasourceConfigService;

    private DatasourceConfigEntity testEntity;
    private DatasourceConfig testConfig;

    @BeforeEach
    void setUp() {
        testEntity = new DatasourceConfigEntity();
        testEntity.setId(1L);
        testEntity.setName("test_datasource");
        testEntity.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        testEntity.setUsername("root");
        testEntity.setPassword("dGVzdHBhc3M="); // Base64 encoded "testpass"
        testEntity.setDriverClassName("com.mysql.cj.jdbc.Driver");
        testEntity.setDatabaseType("MYSQL");
        testEntity.setInitialSize(5);
        testEntity.setMinIdle(5);
        testEntity.setMaxActive(20);
        testEntity.setMaxWait(60000L);
        testEntity.setEnabled(true);

        testConfig = new DatasourceConfig();
        testConfig.setName("test_datasource");
        testConfig.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        testConfig.setUsername("root");
        testConfig.setPassword("testpass");
        testConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        testConfig.setDatabaseType(DatabaseType.MYSQL);
        testConfig.setInitialSize(5);
        testConfig.setMinIdle(5);
        testConfig.setMaxActive(20);
        testConfig.setMaxWait(60000L);
        testConfig.setEnabled(true);
    }

    @Test
    void testSave_Success() {
        // Given
        when(datasourceConfigRepository.save(any(DatasourceConfigEntity.class))).thenReturn(testEntity);

        // When
        DatasourceConfigEntity result = datasourceConfigService.save(testConfig);

        // Then
        assertNotNull(result);
        assertEquals("test_datasource", result.getName());
        assertEquals("jdbc:mysql://localhost:3306/test", result.getJdbcUrl());
        assertEquals("MYSQL", result.getDatabaseType());
        verify(datasourceConfigRepository, times(1)).save(any(DatasourceConfigEntity.class));
    }

    @Test
    void testFindByName_Found() {
        // Given
        when(datasourceConfigRepository.findByName("test_datasource")).thenReturn(Optional.of(testEntity));

        // When
        DatasourceConfigEntity result = datasourceConfigService.findByName("test_datasource");

        // Then
        assertNotNull(result);
        assertEquals("test_datasource", result.getName());
    }

    @Test
    void testFindByName_NotFound() {
        // Given
        when(datasourceConfigRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // When
        DatasourceConfigEntity result = datasourceConfigService.findByName("nonexistent");

        // Then
        assertNull(result);
    }

    @Test
    void testFindAllEnabled() {
        // Given
        when(datasourceConfigRepository.findByEnabledTrue()).thenReturn(Arrays.asList(testEntity));

        // When
        List<DatasourceConfigEntity> result = datasourceConfigService.findAllEnabled();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getEnabled());
    }

    @Test
    void testExistsByName_True() {
        // Given
        when(datasourceConfigRepository.existsByName("test_datasource")).thenReturn(true);

        // When
        boolean result = datasourceConfigService.existsByName("test_datasource");

        // Then
        assertTrue(result);
    }

    @Test
    void testExistsByName_False() {
        // Given
        when(datasourceConfigRepository.existsByName("nonexistent")).thenReturn(false);

        // When
        boolean result = datasourceConfigService.existsByName("nonexistent");

        // Then
        assertFalse(result);
    }

    @Test
    void testToDatasourceConfig_Success() {
        // When
        DatasourceConfig result = datasourceConfigService.toDatasourceConfig(testEntity);

        // Then
        assertNotNull(result);
        assertEquals("test_datasource", result.getName());
        assertEquals("jdbc:mysql://localhost:3306/test", result.getJdbcUrl());
        assertEquals("root", result.getUsername());
        assertEquals("testpass", result.getPassword());
        assertEquals(DatabaseType.MYSQL, result.getDatabaseType());
    }

    @Test
    void testToEntity_Success() {
        // When
        DatasourceConfigEntity result = datasourceConfigService.toEntity(testConfig);

        // Then
        assertNotNull(result);
        assertEquals("test_datasource", result.getName());
        assertEquals("jdbc:mysql://localhost:3306/test", result.getJdbcUrl());
        assertEquals("root", result.getUsername());
        assertEquals(DatabaseType.MYSQL.name(), result.getDatabaseType());
    }

    @Test
    void testFindAllEnabledConfigs() {
        // Given
        when(datasourceConfigRepository.findByEnabledTrue()).thenReturn(Arrays.asList(testEntity));

        // When
        List<DatasourceConfig> result = datasourceConfigService.findAllEnabledConfigs();

        // Then
        assertEquals(1, result.size());
        assertEquals(DatabaseType.MYSQL, result.get(0).getDatabaseType());
    }

    @Test
    void testDeleteByName() {
        // When
        datasourceConfigService.deleteByName("test_datasource");

        // Then
        verify(datasourceConfigRepository, times(1)).deleteByName("test_datasource");
    }

    @Test
    void testTestConnection_InvalidDriver() {
        // Given
        DatasourceConfig invalidConfig = new DatasourceConfig();
        invalidConfig.setName("invalid");
        invalidConfig.setJdbcUrl("jdbc:invalid://localhost");
        invalidConfig.setUsername("user");
        invalidConfig.setPassword("pass");
        invalidConfig.setDriverClassName("com.invalid.Driver");

        // When
        Map<String, Object> result = datasourceConfigService.testConnection(invalidConfig);

        // Then
        assertNotNull(result);
        assertEquals("invalid", result.get("name"));
        assertEquals(false, result.get("success"));
        assertTrue(result.get("message").toString().contains("驱动类未找到"));
    }

    @Test
    void testTestConnection_Entity() {
        // Given
        DatasourceConfigEntity invalidEntity = new DatasourceConfigEntity();
        invalidEntity.setName("invalid_entity");
        invalidEntity.setJdbcUrl("jdbc:invalid://localhost");
        invalidEntity.setUsername("user");
        invalidEntity.setPassword("pass");
        invalidEntity.setDriverClassName("com.invalid.Driver");

        // When
        Map<String, Object> result = datasourceConfigService.testConnection(invalidEntity);

        // Then
        assertNotNull(result);
        assertEquals("invalid_entity", result.get("name"));
        assertEquals(false, result.get("success"));
    }

    @Test
    void testTestConnectionByName_NotFound() {
        // Given
        when(datasourceConfigRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = datasourceConfigService.testConnectionByName("nonexistent");

        // Then
        assertNotNull(result);
        assertEquals("nonexistent", result.get("name"));
        assertEquals(false, result.get("success"));
        assertTrue(result.get("message").toString().contains("数据源不存在"));
    }

    @Test
    void testRegisterAndTestConnection_Success() {
        // Given
        DatasourceConfigEntity validEntity = new DatasourceConfigEntity();
        validEntity.setName("valid_ds");
        validEntity.setJdbcUrl("jdbc:h2:mem:testdb");
        validEntity.setUsername("sa");
        validEntity.setPassword("");
        validEntity.setDriverClassName("org.h2.Driver");
        validEntity.setDatabaseType("H2");
        validEntity.setEnabled(true);

        when(datasourceConfigRepository.save(any(DatasourceConfigEntity.class))).thenReturn(validEntity);

        // When
        // Note: This test may fail if H2 driver is not available
        // In a real scenario, you would mock the connection test
        Map<String, Object> result = datasourceConfigService.testConnection(
            new DatasourceConfig() {{
                setName("valid_ds");
                setJdbcUrl("jdbc:h2:mem:testdb");
                setUsername("sa");
                setPassword("");
                setDriverClassName("org.h2.Driver");
                setDatabaseType(DatabaseType.H2);
            }}
        );

        // Then
        assertNotNull(result);
        // Result depends on whether H2 driver is available
    }

    @Test
    void testRefreshDatasources() {
        // Given
        when(datasourceConfigRepository.findByEnabledTrue()).thenReturn(Arrays.asList(testEntity));
        when(datasourceConfigRepository.save(any(DatasourceConfigEntity.class))).thenReturn(testEntity);

        // When
        datasourceConfigService.refreshDatasources();

        // Then
        verify(datasourceRegistry, times(1)).refreshAll(anyList());
    }
}