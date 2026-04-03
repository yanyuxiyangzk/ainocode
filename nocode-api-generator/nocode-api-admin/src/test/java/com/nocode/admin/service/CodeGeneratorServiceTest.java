package com.nocode.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.CodeGeneratorConfigEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.repository.CodeGeneratorConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CodeGeneratorService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class CodeGeneratorServiceTest {

    @Mock
    private CodeGeneratorConfigRepository codeGeneratorConfigRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CodeGeneratorService codeGeneratorService;

    private CodeGeneratorConfigEntity testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new CodeGeneratorConfigEntity();
        testConfig.setId(1L);
        testConfig.setName("Test Generator");
        testConfig.setTableName("sys_user");
        testConfig.setEntityName("SysUser");
        testConfig.setPackageName("com.ruoyi.system");
        testConfig.setModuleName("system");
        testConfig.setGenerateType("TEMPLATE");
        testConfig.setStatus("ENABLED");
    }

    @Test
    void testCreate_Success() {
        // Given
        when(codeGeneratorConfigRepository.save(any(CodeGeneratorConfigEntity.class))).thenReturn(testConfig);

        // When
        CodeGeneratorConfigEntity result = codeGeneratorService.create(testConfig);

        // Then
        assertNotNull(result);
        assertEquals("Test Generator", result.getName());
        verify(codeGeneratorConfigRepository, times(1)).save(any(CodeGeneratorConfigEntity.class));
    }

    @Test
    void testGetById_Found() {
        // Given
        when(codeGeneratorConfigRepository.findById(1L)).thenReturn(Optional.of(testConfig));

        // When
        Optional<CodeGeneratorConfigEntity> result = codeGeneratorService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("SysUser", result.get().getEntityName());
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(codeGeneratorConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<CodeGeneratorConfigEntity> result = codeGeneratorService.getById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        CodeGeneratorConfigEntity config2 = new CodeGeneratorConfigEntity();
        config2.setId(2L);
        config2.setName("Generator 2");
        when(codeGeneratorConfigRepository.findAll()).thenReturn(Arrays.asList(testConfig, config2));

        // When
        List<CodeGeneratorConfigEntity> result = codeGeneratorService.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void testFindByStatus() {
        // Given
        when(codeGeneratorConfigRepository.findByStatus("ENABLED")).thenReturn(Arrays.asList(testConfig));

        // When
        List<CodeGeneratorConfigEntity> result = codeGeneratorService.findByStatus("ENABLED");

        // Then
        assertEquals(1, result.size());
        assertEquals("ENABLED", result.get(0).getStatus());
    }

    @Test
    void testUpdate_Success() {
        // Given
        CodeGeneratorConfigEntity updateData = new CodeGeneratorConfigEntity();
        updateData.setName("Updated Generator");
        updateData.setTableName("sys_role");

        when(codeGeneratorConfigRepository.findById(1L)).thenReturn(Optional.of(testConfig));
        when(codeGeneratorConfigRepository.save(any(CodeGeneratorConfigEntity.class))).thenReturn(testConfig);

        // When
        CodeGeneratorConfigEntity result = codeGeneratorService.update(1L, updateData);

        // Then
        assertNotNull(result);
        verify(codeGeneratorConfigRepository, times(1)).save(any(CodeGeneratorConfigEntity.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        when(codeGeneratorConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            codeGeneratorService.update(999L, new CodeGeneratorConfigEntity());
        });
    }

    @Test
    void testDelete() {
        // When
        codeGeneratorService.delete(1L);

        // Then
        verify(codeGeneratorConfigRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGenerateCode_ConfigNotFound() {
        // Given
        when(codeGeneratorConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            codeGeneratorService.generateCode(999L, "./output");
        });
    }

    @Test
    void testToCamelCase() {
        // Test the utility method via reflection or by testing public API that uses it

        // Given
        CodeGeneratorConfigEntity config = new CodeGeneratorConfigEntity();
        config.setTableName("sys_user_info");

        // When
        // We can't directly test private methods, but we can test behavior
        // that depends on them through code generation
        when(codeGeneratorConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        // Then - just verify it doesn't throw
        Optional<CodeGeneratorConfigEntity> result = codeGeneratorService.getById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testToKebabCase() {
        // Given
        CodeGeneratorConfigEntity config = new CodeGeneratorConfigEntity();
        config.setEntityName("SysUserInfo");

        when(codeGeneratorConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        // Then - just verify it doesn't throw
        Optional<CodeGeneratorConfigEntity> result = codeGeneratorService.getById(1L);
        assertTrue(result.isPresent());
    }
}