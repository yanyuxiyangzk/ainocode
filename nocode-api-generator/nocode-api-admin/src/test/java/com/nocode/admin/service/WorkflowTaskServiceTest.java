package com.nocode.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WorkflowTaskService 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class WorkflowTaskServiceTest {

    @Mock
    private WorkflowTaskRepository workflowTaskRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private WorkflowTaskService workflowTaskService;

    private WorkflowTaskEntity testTask;
    private WorkflowInstanceEntity testInstance;
    private WorkflowDefinitionEntity testDefinition;

    @BeforeEach
    void setUp() {
        testTask = new WorkflowTaskEntity();
        testTask.setId(1L);
        testTask.setInstanceId(1L);
        testTask.setNodeId("task1");
        testTask.setNodeName("审批节点");
        testTask.setAssignee("user1");
        testTask.setTaskStatus("PENDING");
        testTask.setPriority("NORMAL");

        testInstance = new WorkflowInstanceEntity();
        testInstance.setId(1L);
        testInstance.setDefinitionId(1L);
        testInstance.setApplicant("applicant1");
        testInstance.setInstanceStatus("RUNNING");
        testInstance.setCurrentNodeId("task1");
        testInstance.setCurrentNodeName("审批节点");

        testDefinition = new WorkflowDefinitionEntity();
        testDefinition.setId(1L);
        testDefinition.setName("测试流程");
        testDefinition.setProcessKey("test_process");
        testDefinition.setStatus("DEPLOYED");
        testDefinition.setNodeDefinition("[]");
        testDefinition.setSequenceFlow("[]");
    }

    @Test
    void testCreateTask_Success() {
        // Given
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);

        // When
        WorkflowTaskEntity result = workflowTaskService.createTask(1L, "task1", "审批节点", "user1");

        // Then
        assertNotNull(result);
        assertEquals("PENDING", result.getTaskStatus());
        assertEquals("NORMAL", result.getPriority());
        verify(workflowTaskRepository, times(1)).save(any(WorkflowTaskEntity.class));
    }

    @Test
    void testClaimTask_Success() {
        // Given
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);

        // When
        WorkflowTaskEntity result = workflowTaskService.claimTask(1L, "user1");

        // Then
        assertNotNull(result);
        assertEquals("CLAIMED", result.getTaskStatus());
        assertEquals("user1", result.getAssignee());
        assertNotNull(result.getClaimTime());
    }

    @Test
    void testClaimTask_NotPending() {
        // Given
        testTask.setTaskStatus("CLAIMED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThrows(WorkflowException.class, () -> {
            workflowTaskService.claimTask(1L, "user1");
        });
    }

    @Test
    void testClaimTask_NotFound() {
        // Given
        when(workflowTaskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            workflowTaskService.claimTask(999L, "user1");
        });
    }

    @Test
    void testCompleteTask_Success() {
        // Given
        testTask.setTaskStatus("CLAIMED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);
        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(testInstance));
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testDefinition));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(testInstance);

        // When
        WorkflowTaskEntity result = workflowTaskService.completeTask(1L, "同意");

        // Then
        assertNotNull(result);
        assertEquals("COMPLETED", result.getTaskStatus());
        assertEquals("同意", result.getComment());
        assertNotNull(result.getCompleteTime());
    }

    @Test
    void testRejectTask_Success() {
        // Given
        testTask.setTaskStatus("CLAIMED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);
        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(testInstance));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(testInstance);

        // When
        WorkflowTaskEntity result = workflowTaskService.rejectTask(1L, "驳回原因");

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getTaskStatus());
        assertEquals("驳回原因", result.getComment());
    }

    @Test
    void testTransferTask_Success() {
        // Given
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);

        // When
        WorkflowTaskEntity result = workflowTaskService.transferTask(1L, "user2");

        // Then
        assertNotNull(result);
        assertEquals("user2", result.getAssignee());
    }

    @Test
    void testCountersignTask_Success() {
        // Given
        testTask.setIsCounterSign(true);
        testTask.setTaskStatus("CLAIMED");
        testTask.setCountersignCount(3);
        testTask.setCountersignedCount(1);

        WorkflowTaskEntity task2 = new WorkflowTaskEntity();
        task2.setId(2L);
        task2.setInstanceId(1L);
        task2.setNodeId("task1");
        task2.setTaskStatus("COMPLETED");
        task2.setCountersignResult("AGREE");

        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);
        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(testInstance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(testTask, task2));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(testInstance);
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testDefinition));

        // When
        Map<String, Object> result = workflowTaskService.countersignTask(1L, "user1", "同意", true);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("counterSignComplete"));
        assertEquals("AGREE", result.get("outcome"));
    }

    @Test
    void testCountersignTask_AllCountersigned_Approved() {
        // Given
        testTask.setIsCounterSign(true);
        testTask.setTaskStatus("CLAIMED");
        testTask.setCountersignCount(2);
        testTask.setCountersignedCount(1);

        WorkflowTaskEntity task2 = new WorkflowTaskEntity();
        task2.setId(2L);
        task2.setInstanceId(1L);
        task2.setNodeId("task1");
        task2.setTaskStatus("COMPLETED");
        task2.setCountersignResult("AGREE");

        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);
        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(testInstance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(testTask, task2));
        when(workflowDefinitionRepository.findById(1L)).thenReturn(Optional.of(testDefinition));
        when(workflowInstanceRepository.save(any(WorkflowInstanceEntity.class))).thenReturn(testInstance);

        // When
        Map<String, Object> result = workflowTaskService.countersignTask(1L, "user1", "同意", true);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("counterSignComplete"));
        assertEquals("AGREE", result.get("outcome"));
    }

    @Test
    void testCountersignTask_AllCountersigned_NotApproved() {
        // Given
        testTask.setIsCounterSign(true);
        testTask.setTaskStatus("CLAIMED");
        testTask.setCountersignCount(2);
        testTask.setCountersignedCount(1);

        WorkflowTaskEntity task2 = new WorkflowTaskEntity();
        task2.setId(2L);
        task2.setInstanceId(1L);
        task2.setNodeId("task1");
        task2.setTaskStatus("COMPLETED");
        task2.setCountersignResult("REJECT");

        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);
        when(workflowInstanceRepository.findById(1L)).thenReturn(Optional.of(testInstance));
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(testTask, task2));
        when(workflowTaskRepository.save(any(WorkflowTaskEntity.class))).thenReturn(testTask);

        // When
        Map<String, Object> result = workflowTaskService.countersignTask(1L, "user1", "不同意", false);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("counterSignComplete"));
        assertEquals("REJECT", result.get("outcome"));
    }

    @Test
    void testGetById_Found() {
        // Given
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        Optional<WorkflowTaskEntity> result = workflowTaskService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindByInstanceId() {
        // Given
        when(workflowTaskRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(testTask));

        // When
        List<WorkflowTaskEntity> result = workflowTaskService.findByInstanceId(1L);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFindTodoTasks() {
        // Given
        testTask.setTaskStatus("CLAIMED");
        when(workflowTaskRepository.findByAssigneeAndTaskStatus("user1", "CLAIMED")).thenReturn(Arrays.asList(testTask));

        // When
        List<WorkflowTaskEntity> result = workflowTaskService.findTodoTasks("user1");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFindPendingTasks() {
        // Given
        when(workflowTaskRepository.findByAssigneeAndTaskStatus("user1", "PENDING")).thenReturn(Arrays.asList(testTask));

        // When
        List<WorkflowTaskEntity> result = workflowTaskService.findPendingTasks("user1");

        // Then
        assertEquals(1, result.size());
    }
}