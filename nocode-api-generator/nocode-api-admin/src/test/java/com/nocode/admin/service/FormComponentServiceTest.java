package com.nocode.admin.service;

import com.nocode.admin.entity.FormComponentEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.repository.FormComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FormComponentService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class FormComponentServiceTest {

    @Mock
    private FormComponentRepository formComponentRepository;

    @InjectMocks
    private FormComponentService formComponentService;

    private FormComponentEntity testComponent;

    @BeforeEach
    void setUp() {
        testComponent = new FormComponentEntity();
        testComponent.setId(1L);
        testComponent.setFormId(1L);
        testComponent.setComponentType("input");
        testComponent.setLabel("用户名");
        testComponent.setFieldName("username");
        testComponent.setPlaceholder("请输入用户名");
        testComponent.setRequired(true);
        testComponent.setSort(1);
        testComponent.setDelFlag("0");
    }

    @Test
    void testCreate_Success() {
        // Given
        when(formComponentRepository.save(any(FormComponentEntity.class))).thenReturn(testComponent);

        // When
        FormComponentEntity result = formComponentService.create(testComponent);

        // Then
        assertNotNull(result);
        assertEquals("input", result.getComponentType());
        assertEquals("用户名", result.getLabel());
        verify(formComponentRepository, times(1)).save(any(FormComponentEntity.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        FormComponentEntity updateData = new FormComponentEntity();
        updateData.setLabel("新用户名");
        updateData.setFieldName("newUsername");
        updateData.setPlaceholder("请输入新用户名");

        when(formComponentRepository.findById(1L)).thenReturn(Optional.of(testComponent));
        when(formComponentRepository.save(any(FormComponentEntity.class))).thenReturn(testComponent);

        // When
        FormComponentEntity result = formComponentService.update(1L, updateData);

        // Then
        assertNotNull(result);
        verify(formComponentRepository, times(1)).save(any(FormComponentEntity.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        when(formComponentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            formComponentService.update(999L, new FormComponentEntity());
        });
    }

    @Test
    void testGetById_Found() {
        // Given
        when(formComponentRepository.findById(1L)).thenReturn(Optional.of(testComponent));

        // When
        Optional<FormComponentEntity> result = formComponentService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("用户名", result.get().getLabel());
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(formComponentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<FormComponentEntity> result = formComponentService.getById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        FormComponentEntity component2 = new FormComponentEntity();
        component2.setId(2L);
        component2.setLabel("密码");

        when(formComponentRepository.findAll()).thenReturn(Arrays.asList(testComponent, component2));

        // When
        List<FormComponentEntity> result = formComponentService.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void testFindByFormId() {
        // Given
        when(formComponentRepository.findByFormId(1L)).thenReturn(Arrays.asList(testComponent));

        // When
        List<FormComponentEntity> result = formComponentService.findByFormId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFormId());
    }

    @Test
    void testDelete_SoftDelete() {
        // Given
        when(formComponentRepository.findById(1L)).thenReturn(Optional.of(testComponent));
        when(formComponentRepository.save(any(FormComponentEntity.class))).thenReturn(testComponent);

        // When
        formComponentService.delete(1L);

        // Then
        assertEquals("1", testComponent.getDelFlag());
        verify(formComponentRepository, times(1)).save(any(FormComponentEntity.class));
    }

    @Test
    void testHardDelete() {
        // When
        formComponentService.hardDelete(1L);

        // Then
        verify(formComponentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByFormId_SoftDelete() {
        // Given
        FormComponentEntity component2 = new FormComponentEntity();
        component2.setId(2L);
        component2.setFormId(1L);
        component2.setDelFlag("0");

        when(formComponentRepository.findByFormId(1L)).thenReturn(Arrays.asList(testComponent, component2));
        when(formComponentRepository.saveAll(anyList())).thenReturn(Arrays.asList(testComponent, component2));

        // When
        formComponentService.deleteByFormId(1L);

        // Then
        assertEquals("1", testComponent.getDelFlag());
        assertEquals("1", component2.getDelFlag());
        verify(formComponentRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(formComponentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        formComponentService.delete(999L);

        // Then - should not throw exception, just do nothing
        verify(formComponentRepository, never()).save(any(FormComponentEntity.class));
    }
}