package com.gaurav.taskmanager2.service.TaskService;

import com.gaurav.taskmanager2.model.TaskModel.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {
    public TaskResponse createTask(String username,TaskRequest taskRequest);
    public TaskResponse getTaskByUsername(String title, String username);
    public List<TaskResponse> getAllTasksByUsername(String username);
    public void deleteTask(String username, String title);
    public TaskResponse updateTask(String username, String title, TaskRequest taskRequest);
    public List<TaskResponse> getByStatus(String username, TaskStatus taskStatus);
    public List<TaskResponse> getByPriority(String username, TaskPriority taskPriority);
    public List<TaskResponse> getAllTasks();
    public List<TaskResponse> getSortedTaskByCreatedAt(String username,String order);
    public void deleteAllTaskByUsername(String username);
}
