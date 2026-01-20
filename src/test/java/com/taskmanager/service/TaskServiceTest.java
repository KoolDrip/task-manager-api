package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task("Test Task", "Test Description");
        testTask.setId(1L);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(testTask, new Task("Task 2", "Description 2"));
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTitle());
    }

    @Test
    void getTaskById_WhenNotExists_ShouldReturnEmpty() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(testTask);

        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).save(testTask);
    }

    @Test
    void deleteTask_WhenExists_ShouldReturnTrue() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        boolean result = taskService.deleteTask(1L);

        assertTrue(result);
        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_WhenNotExists_ShouldReturnFalse() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = taskService.deleteTask(99L);

        assertFalse(result);
    }

    @Test
    void toggleTaskStatus_ShouldToggleCompleted() {
        testTask.setCompleted(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        Optional<Task> result = taskService.toggleTaskStatus(1L);

        assertTrue(result.isPresent());
        assertTrue(result.get().isCompleted());
    }

    @Test
    void getCompletedTasks_ShouldReturnOnlyCompleted() {
        Task completedTask = new Task("Completed", "Done");
        completedTask.setCompleted(true);
        when(taskRepository.findByCompleted(true)).thenReturn(Arrays.asList(completedTask));

        List<Task> result = taskService.getCompletedTasks();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyPending() {
        when(taskRepository.findByCompleted(false)).thenReturn(Arrays.asList(testTask));

        List<Task> result = taskService.getPendingTasks();

        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
    }
}
