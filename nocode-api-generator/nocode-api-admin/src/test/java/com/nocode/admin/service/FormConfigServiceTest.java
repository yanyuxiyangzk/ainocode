package com.nocode.admin.service;

import com.nocode.admin.entity.FormConfigEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.repository.FormConfigRepository;
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
 * FormConfigService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class FormConfigServiceTest {

    @Mock
    private FormConfigRepository formConfigRepository;

    @InjectMocks
    private FormConfigService formConfigService;

    private FormConfigEntity testForm;

    @BeforeEach
    void setUp() {
        testForm = new FormConfigEntity();
        testForm.setId(1L);
        testForm.setName("Test Form");
        testForm.setDescription("Test Description");
        testForm.setFormKey("test_form");
        testForm.setVersion(1);
        testForm.setStatus("DRAFT");
    }

    @Test
    void testCreate_Success() {
        // Given
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenReturn(testForm);

        // When
        FormConfigEntity result = formConfigService.create(testForm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getVersion());
        assertEquals("DRAFT", result.getStatus());
        verify(formConfigRepository, times(1)).save(any(FormConfigEntity.class));
    }

    @Test
    void testGetById_Found() {
        // Given
        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));

        // When
        Optional<FormConfigEntity> result = formConfigService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Form", result.get().getName());
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(formConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<FormConfigEntity> result = formConfigService.getById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        FormConfigEntity form2 = new FormConfigEntity();
        form2.setId(2L);
        form2.setName("Form 2");
        when(formConfigRepository.findAll()).thenReturn(Arrays.asList(testForm, form2));

        // When
        List<FormConfigEntity> result = formConfigService.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void testFindByStatus() {
        // Given
        when(formConfigRepository.findByStatus("DRAFT")).thenReturn(Arrays.asList(testForm));

        // When
        List<FormConfigEntity> result = formConfigService.findByStatus("DRAFT");

        // Then
        assertEquals(1, result.size());
        assertEquals("DRAFT", result.get(0).getStatus());
    }

    @Test
    void testUpdate_Success() {
        // Given
        FormConfigEntity updateData = new FormConfigEntity();
        updateData.setName("Updated Form");
        updateData.setDescription("Updated Description");

        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenReturn(testForm);

        // When
        FormConfigEntity result = formConfigService.update(1L, updateData);

        // Then
        assertNotNull(result);
        verify(formConfigRepository, times(1)).save(any(FormConfigEntity.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        when(formConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            formConfigService.update(999L, new FormConfigEntity());
        });
    }

    @Test
    void testPublish_Success() {
        // Given
        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenReturn(testForm);

        // When
        FormConfigEntity result = formConfigService.publish(1L);

        // Then
        assertNotNull(result);
        assertEquals("PUBLISHED", result.getStatus());
    }

    @Test
    void testPublish_NotFound() {
        // Given
        when(formConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            formConfigService.publish(999L);
        });
    }

    @Test
    void testDelete_SoftDelete() {
        // Given - soft delete sets delFlag to "1" and saves
        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenReturn(testForm);

        // When
        formConfigService.delete(1L);

        // Then - verify soft delete (set delFlag and save) not hard delete
        verify(formConfigRepository, never()).deleteById(1L);
        verify(formConfigRepository, times(1)).findById(1L);
        verify(formConfigRepository, times(1)).save(any(FormConfigEntity.class));
    }

    @Test
    void testSearchByName() {
        // Given
        when(formConfigRepository.findByNameContaining("Test")).thenReturn(Arrays.asList(testForm));

        // When
        List<FormConfigEntity> result = formConfigService.searchByName("Test");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Test"));
    }

    @Test
    void testUnpublish_Success() {
        // Given
        testForm.setStatus("PUBLISHED");
        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenReturn(testForm);

        // When
        FormConfigEntity result = formConfigService.unpublish(1L);

        // Then
        assertNotNull(result);
        assertEquals("DRAFT", result.getStatus());
    }

    @Test
    void testUnpublish_NotFound() {
        // Given
        when(formConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            formConfigService.unpublish(999L);
        });
    }

    @Test
    void testHardDelete() {
        // When
        formConfigService.hardDelete(1L);

        // Then
        verify(formConfigRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByName_Found() {
        // Given
        when(formConfigRepository.findByName("Test Form")).thenReturn(Optional.of(testForm));

        // When
        Optional<FormConfigEntity> result = formConfigService.findByName("Test Form");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Form", result.get().getName());
    }

    @Test
    void testFindByName_NotFound() {
        // Given
        when(formConfigRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // When
        Optional<FormConfigEntity> result = formConfigService.findByName("NonExistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCreateBy() {
        // Given
        testForm.setCreateBy("admin");
        when(formConfigRepository.findByCreateBy("admin")).thenReturn(Arrays.asList(testForm));

        // When
        List<FormConfigEntity> result = formConfigService.findByCreateBy("admin");

        // Then
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getCreateBy());
    }

    @Test
    void testFindPublishedForms() {
        // Given
        testForm.setStatus("PUBLISHED");
        when(formConfigRepository.findPublishedForms()).thenReturn(Arrays.asList(testForm));

        // When
        List<FormConfigEntity> result = formConfigService.findPublishedForms();

        // Then
        assertEquals(1, result.size());
        assertEquals("PUBLISHED", result.get(0).getStatus());
    }

    @Test
    void testCopyForm_Success() {
        // Given
        when(formConfigRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formConfigRepository.save(any(FormConfigEntity.class))).thenAnswer(invocation -> {
            FormConfigEntity entity = invocation.getArgument(0);
            entity.setId(2L);
            return entity;
        });

        // When
        FormConfigEntity result = formConfigService.copyForm(1L, "Copied Form");

        // Then
        assertNotNull(result);
        assertEquals("Copied Form", result.getName());
        assertEquals("DRAFT", result.getStatus());
        assertEquals(1, result.getVersion());
        verify(formConfigRepository, times(1)).save(any(FormConfigEntity.class));
    }

    @Test
    void testCopyForm_NotFound() {
        // Given
        when(formConfigRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            formConfigService.copyForm(999L, "New Form");
        });
    }

    @Test
    void testCountByStatus() {
        // Given
        when(formConfigRepository.countByStatus("DRAFT")).thenReturn(5L);

        // When
        long result = formConfigService.countByStatus("DRAFT");

        // Then
        assertEquals(5L, result);
    }

    @Test
    void testExistsByName_True() {
        // Given
        when(formConfigRepository.findByName("Test Form")).thenReturn(Optional.of(testForm));

        // When
        boolean result = formConfigService.existsByName("Test Form");

        // Then
        assertTrue(result);
    }

    @Test
    void testExistsByName_False() {
        // Given
        when(formConfigRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // When
        boolean result = formConfigService.existsByName("NonExistent");

        // Then
        assertFalse(result);
    }
}