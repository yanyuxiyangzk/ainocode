package com.nocode.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.DiagramLayoutEntity;
import com.nocode.admin.repository.DiagramLayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DiagramLayoutService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class DiagramLayoutServiceTest {

    @Mock
    private DiagramLayoutRepository diagramLayoutRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private DiagramLayoutService diagramLayoutService;

    private DiagramLayoutEntity testLayout;

    @BeforeEach
    void setUp() {
        testLayout = new DiagramLayoutEntity();
        testLayout.setId(1L);
        testLayout.setDatasourceName("test_datasource");
        testLayout.setSchemaName("test_schema");
        testLayout.setLayoutData("{\"nodes\":[],\"edges\":[]}");
        testLayout.setViewConfig("{\"zoom\":1.0}");
        testLayout.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSaveLayout_NewLayout_WithSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.empty());
        when(diagramLayoutRepository.save(any(DiagramLayoutEntity.class))).thenReturn(testLayout);

        // When
        diagramLayoutService.saveLayout("test_datasource", "test_schema",
            "{\"nodes\":[],\"edges\":[]}", "{\"zoom\":1.0}");

        // Then
        verify(diagramLayoutRepository, times(1)).save(any(DiagramLayoutEntity.class));
    }

    @Test
    void testSaveLayout_UpdateExisting_WithSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.of(testLayout));
        when(diagramLayoutRepository.save(any(DiagramLayoutEntity.class))).thenReturn(testLayout);

        // When
        diagramLayoutService.saveLayout("test_datasource", "test_schema",
            "{\"nodes\":[{}],\"edges\":[]}", "{\"zoom\":1.5}");

        // Then
        verify(diagramLayoutRepository, times(1)).save(any(DiagramLayoutEntity.class));
    }

    @Test
    void testSaveLayout_NewLayout_WithoutSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaNameIsNull("test_datasource"))
            .thenReturn(Optional.empty());
        when(diagramLayoutRepository.save(any(DiagramLayoutEntity.class))).thenReturn(testLayout);

        // When
        diagramLayoutService.saveLayout("test_datasource", null,
            "{\"nodes\":[],\"edges\":[]}", "{\"zoom\":1.0}");

        // Then
        verify(diagramLayoutRepository, times(1)).save(any(DiagramLayoutEntity.class));
    }

    @Test
    void testGetLayout_Found_WithSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.of(testLayout));

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("test_datasource", "test_schema");

        // Then
        assertNotNull(result);
        assertNotNull(result.get("layoutData"));
        assertNotNull(result.get("viewConfig"));
        assertNotNull(result.get("updatedAt"));
    }

    @Test
    void testGetLayout_NotFound_WithSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("nonexistent", "schema"))
            .thenReturn(Optional.empty());

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("nonexistent", "schema");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLayout_Found_WithoutSchema() {
        // Given
        testLayout.setSchemaName(null);
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaNameIsNull("test_datasource"))
            .thenReturn(Optional.of(testLayout));

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("test_datasource", null);

        // Then
        assertNotNull(result);
        assertNotNull(result.get("layoutData"));
    }

    @Test
    void testGetLayout_NotFound_WithoutSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaNameIsNull("nonexistent"))
            .thenReturn(Optional.empty());

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("nonexistent", null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLayout_InvalidJsonLayoutData() {
        // Given
        testLayout.setLayoutData("invalid json");
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.of(testLayout));

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("test_datasource", "test_schema");

        // Then
        assertNotNull(result);
        // Invalid JSON should return null for layoutData
        assertNull(result.get("layoutData"));
    }

    @Test
    void testGetLayout_NullLayoutData() {
        // Given
        testLayout.setLayoutData(null);
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.of(testLayout));

        // When
        Map<String, Object> result = diagramLayoutService.getLayout("test_datasource", "test_schema");

        // Then
        assertNotNull(result);
        assertNull(result.get("layoutData"));
    }

    @Test
    void testDeleteLayout_WithSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("test_datasource", "test_schema"))
            .thenReturn(Optional.of(testLayout));
        doNothing().when(diagramLayoutRepository).delete(any(DiagramLayoutEntity.class));

        // When
        diagramLayoutService.deleteLayout("test_datasource", "test_schema");

        // Then
        verify(diagramLayoutRepository, times(1)).delete(any(DiagramLayoutEntity.class));
    }

    @Test
    void testDeleteLayout_WithoutSchema() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaNameIsNull("test_datasource"))
            .thenReturn(Optional.of(testLayout));
        doNothing().when(diagramLayoutRepository).delete(any(DiagramLayoutEntity.class));

        // When
        diagramLayoutService.deleteLayout("test_datasource", null);

        // Then
        verify(diagramLayoutRepository, times(1)).delete(any(DiagramLayoutEntity.class));
    }

    @Test
    void testDeleteLayout_NotFound() {
        // Given
        when(diagramLayoutRepository.findByDatasourceNameAndSchemaName("nonexistent", "schema"))
            .thenReturn(Optional.empty());

        // When
        diagramLayoutService.deleteLayout("nonexistent", "schema");

        // Then
        verify(diagramLayoutRepository, never()).delete(any(DiagramLayoutEntity.class));
    }
}
