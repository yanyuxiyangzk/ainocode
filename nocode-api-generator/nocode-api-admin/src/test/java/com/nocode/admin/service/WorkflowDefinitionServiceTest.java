package com.nocode.admin.service;

import com.nocode.admin.entity.WorkflowDefinitionEntity;
import com.nocode.admin.entity.WorkflowInstanceEntity;
import com.nocode.admin.entity.WorkflowTaskEntity;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.exception.WorkflowException;
import com.nocode.admin.repository.WorkflowDefinitionRepository;
import com.nocode.admin.repository.WorkflowInstanceRepository;
import com.nocode.admin.repository.WorkflowTaskRepository;
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
 * WorkflowDefinitionService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class WorkflowDefinitionServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private WorkflowTaskRepository workflowTaskRepository;

    @InjectMocks
    private WorkflowDefinitionService workflowDefinitionService;

    private WorkflowDefinitionEntity testWorkflow;

    @BeforeEach
    void setUp() {
        testWorkflow = new WorkflowDefinitionEntity();
        testWorkflow.setId(1L);
        testWorkflow.setName("Test Workflow");
        testWorkflow.setProcessKey("test_workflow");
        testWorkflow.setDescription("Test Workflow Description");
        testWorkflow.setVersion(1);
        testWorkflow.setStatus("DRAFT");
        testWorkflow.setSuspended(false);
    }

    @Test
    void testCreate_Success() {
        // Given
        when(workflowDefinitionRepository.save(any(WorkflowDefinitionEntity.class))).thenReturn(testWorkflow);

        // When
        WorkflowDefinitionEntity result = workflowDefinitionService.create(testWorkflow);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getVersion());
        assertEquals("DRAFT", result.getStatus());
        verify(workflowDefinitionRepository, times(1)).save(any(WorkflowDefinitionEntity.class));
    }

    @Test
    void testGetById_Found() {
        // Given
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));

        // When
        Optional<WorkflowDefinitionEntity> result = workflowDefinitionService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Workflow", result.get().getName());
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(workflowDefinitionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<WorkflowDefinitionEntity> result = workflowDefinitionService.getById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        WorkflowDefinitionEntity workflow2 = new WorkflowDefinitionEntity();
        workflow2.setId(2L);
        workflow2.setName("Workflow 2");
        when(workflowDefinitionRepository.findAll()).thenReturn(Arrays.asList(testWorkflow, workflow2));

        // When
        List<WorkflowDefinitionEntity> result = workflowDefinitionService.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void testUpdate_Success() {
        // Given
        WorkflowDefinitionEntity updateData = new WorkflowDefinitionEntity();
        updateData.setName("Updated Workflow");
        updateData.setDescription("Updated Description");

        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(workflowDefinitionRepository.save(any(WorkflowDefinitionEntity.class))).thenReturn(testWorkflow);

        // When
        WorkflowDefinitionEntity result = workflowDefinitionService.update(1L, updateData);

        // Then
        assertNotNull(result);
        assertEquals(2, testWorkflow.getVersion()); // Version should increment
        verify(workflowDefinitionRepository, times(1)).save(any(WorkflowDefinitionEntity.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        when(workflowDefinitionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowDefinitionService.update(999L, new WorkflowDefinitionEntity());
        });
    }

    @Test
    void testDeploy_Success() {
        // Given
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(workflowDefinitionRepository.save(any(WorkflowDefinitionEntity.class))).thenReturn(testWorkflow);

        // When
        WorkflowDefinitionEntity result = workflowDefinitionService.deploy(1L);

        // Then
        assertNotNull(result);
        assertEquals("DEPLOYED", result.getStatus());
        assertFalse(result.getSuspended());
    }

    @Test
    void testDeploy_NotFound() {
        // Given
        when(workflowDefinitionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowDefinitionService.deploy(999L);
        });
    }

    @Test
    void testSuspend_Success() {
        // Given
        testWorkflow.setStatus("DEPLOYED");
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(workflowDefinitionRepository.save(any(WorkflowDefinitionEntity.class))).thenReturn(testWorkflow);

        // When
        WorkflowDefinitionEntity result = workflowDefinitionService.suspend(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuspended());
        assertEquals("SUSPENDED", result.getStatus());
    }

    @Test
    void testStartProcess_Success() {
        // Given
        testWorkflow.setStatus("DEPLOYED");
        testWorkflow.setSuspended(false);

        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setApplicant("tester");
        instance.setInstanceStatus("RUNNING");

        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        WorkflowInstanceEntity result = workflowDefinitionService.startProcess(1L, "tester", "BK001");

        // Then
        assertNotNull(result);
        assertEquals("RUNNING", result.getInstanceStatus());
        assertEquals("start", result.getCurrentNodeId());
    }

    @Test
    void testStartProcess_WorkflowNotFound() {
        // Given
        when(workflowDefinitionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowDefinitionService.startProcess(999L, "tester", "BK001");
        });
    }

    @Test
    void testStartProcess_WorkflowSuspended() {
        // Given
        testWorkflow.setSuspended(true);
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));

        // When & Then
        assertThrows(WorkflowException.class, () -> {
            workflowDefinitionService.startProcess(1L, "tester", "BK001");
        });
    }

    @Test
    void testCompleteTask_Success() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setInstanceStatus("RUNNING");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        WorkflowInstanceEntity result = workflowDefinitionService.completeTask(1L, "admin");

        // Then
        assertNotNull(result);
        assertEquals("COMPLETED", result.getInstanceStatus());
        assertEquals("end", result.getCurrentNodeId());
        assertNotNull(result.getEndTime());
    }

    @Test
    void testCompleteTask_InstanceNotFound() {
        // Given
        when(workflowInstanceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowDefinitionService.completeTask(999L, "admin");
        });
    }

    @Test
    void testCompleteTask_InstanceNotRunning() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setInstanceStatus("COMPLETED");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // When & Then
        assertThrows(WorkflowException.class, () -> {
            workflowDefinitionService.completeTask(1L, "admin", null);
        });
    }

    @Test
    void testCompleteTask_InstanceNotFound() {
        // Given
        when(workflowInstanceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowDefinitionService.completeTask(999L, "admin");
        });
    }

    @Test
    void testCounterSign_Success() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setInstanceStatus("RUNNING");

        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setId(1L);
        task.setInstanceId(1L);
        task.setAssignee("user1");
        task.setTaskStatus("CLAIMED");
        task.setNodeId("task1");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(task));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(task);
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        Map<String, Object> result = workflowDefinitionService.counterSign(1L, "user1", true, "同意");

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("counterSignComplete"));
        assertEquals("AGREE", result.get("outcome"));
    }

    @Test
    void testCounterSign_Reject() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setInstanceStatus("RUNNING");

        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setId(1L);
        task.setInstanceId(1L);
        task.setAssignee("user1");
        task.setTaskStatus("CLAIMED");
        task.setNodeId("task1");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(task));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(task);
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        Map<String, Object> result = workflowDefinitionService.counterSign(1L, "user1", false, "驳回");

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("counterSignComplete"));
        assertEquals("REJECT", result.get("outcome"));
    }

    @Test
    void testRejectToPreviousNode_Success() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setInstanceStatus("RUNNING");
        instance.setCurrentNodeId("task2");

        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setId(1L);
        task.setInstanceId(1L);
        task.setAssignee("user1");
        task.setTaskStatus("CLAIMED");
        task.setNodeId("task2");

        WorkflowDefinitionEntity definition = new WorkflowDefinitionEntity();
        definition.setId(1L);

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(task));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(task);
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(task);
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        WorkflowInstanceEntity result = workflowDefinitionService.rejectToPreviousNode(1L, "user1", "驳回原因");

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getInstanceStatus());
    }

    @Test
    void testTransferTask_Success() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setInstanceStatus("RUNNING");

        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setId(1L);
        task.setInstanceId(1L);
        task.setAssignee("user1");
        task.setTaskStatus("CLAIMED");
        task.setNodeId("task1");
        task.setNodeName("审批节点");
        task.setPriority("NORMAL");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(task));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        WorkflowTaskEntity result = workflowDefinitionService.transferTask(1L, "user1", "user2", "因出差转交");

        // Then
        assertNotNull(result);
        assertEquals("user2", result.getAssignee());
        assertEquals("PENDING", result.getTaskStatus());
    }

    @Test
    void testCancelProcess_Success() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(1L);
        instance.setApplicant("applicant1");
        instance.setInstanceStatus("RUNNING");

        WorkflowTaskEntity task = new WorkflowTaskEntity();
        task.setId(1L);
        task.setInstanceId(1L);
        task.setTaskStatus("CLAIMED");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(task));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(task);
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(instance);

        // When
        WorkflowInstanceEntity result = workflowDefinitionService.cancelProcess(1L, "applicant1", "不需要审批了");

        // Then
        assertNotNull(result);
        assertEquals("CANCELLED", result.getInstanceStatus());
    }

    @Test
    void testCancelProcess_NotApplicant() {
        // Given
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setId(1L);
        instance.setApplicant("applicant1");
        instance.setInstanceStatus("RUNNING");

        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // When & Then
        assertThrows(WorkflowException.class, () -> {
            workflowDefinitionService.cancelProcess(1L, "otherUser", "原因");
        });
    }

    @Test
    void testActivate_Success() {
        // Given
        testWorkflow.setStatus("SUSPENDED");
        testWorkflow.setSuspended(true);
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testWorkflow));
        when(workflowDefinitionRepository.save(any(WorkflowDefinitionEntity.class))).thenReturn(testWorkflow);

        // When
        WorkflowDefinitionEntity result = workflowDefinitionService.activate(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuspended());
        assertEquals("DEPLOYED", result.getStatus());
    }

    @Test
    void testFindByProcessKey() {
        // Given
        when(workflowDefinitionRepository.findByProcessKey("test_workflow")).thenReturn(Optional.of(testWorkflow));

        // When
        Optional<WorkflowDefinitionEntity> result = workflowDefinitionService.findByProcessKey("test_workflow");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Workflow", result.get().getName());
    }

    @Test
    void testFindByStatus() {
        // Given
        when(workflowDefinitionRepository.findByStatus("DEPLOYED")).thenReturn(Arrays.asList(testWorkflow));

        // When
        List<WorkflowDefinitionEntity> result = workflowDefinitionService.findByStatus("DEPLOYED");

        // Then
        assertEquals(1, result.size());
        assertEquals("DEPLOYED", result.get(0).getStatus());
    }

    @Test
    void testDelete() {
        // When
        workflowDefinitionService.delete(1L);

        // Then
        verify(workflowDefinitionRepository, times(1)).deleteById(1L);
    }
}