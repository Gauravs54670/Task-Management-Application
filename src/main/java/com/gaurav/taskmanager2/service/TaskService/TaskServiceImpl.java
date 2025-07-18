package com.gaurav.taskmanager2.service.TaskService;

import com.gaurav.taskmanager2.Repository.TaskRepository;
import com.gaurav.taskmanager2.model.TaskModel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    //get task by username
    @Override
    public TaskResponse getTaskByUsername(String title, String username) {
        Task task = this.taskRepository.findByTitleAndUsername(title,username)
                .orElseThrow(() -> new RuntimeException("Task with title '" + title + "' not found for user '" + username + "'"));
        return convertToResponse(task);
    }
    //get all tasks by username
    @Override
    public List<TaskResponse> getAllTasksByUsername(String username) {
        List<Task> taskResponseList = this.taskRepository.findAllByUsername(username);
        if(taskResponseList.isEmpty())
            throw new RuntimeException("Task list is empty");
        return taskResponseList.stream().map(this::convertToResponse).toList();
    }
    //task creation
    @Override
    public TaskResponse createTask(String username, TaskRequest taskRequest) {
        boolean exist = this.taskRepository.findByTitleAndUsername(taskRequest.getTitle(),username).isPresent();
        if(exist)
            throw new RuntimeException("Task is already present for the user");
        Task newTask = convertToTask(taskRequest);
        newTask.setUsername(username);
        if(taskRequest.getStatus() != null && !taskRequest.getStatus().isEmpty()) {
            try {
                TaskStatus status = TaskStatus.valueOf(taskRequest.getStatus().toUpperCase());
                newTask.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status attribute"+ taskRequest.getStatus() +
                        "/nAllowed values are : " + Arrays.toString(TaskStatus.values()));
            }
        } else newTask.setStatus(TaskStatus.PENDING);
        if(taskRequest.getPriority() != null && !taskRequest.getPriority().isEmpty()) {
            try {
                TaskPriority priority = TaskPriority.valueOf(taskRequest.getPriority().toUpperCase());
                newTask.setPriority(priority);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid priority attribute "+ taskRequest.getPriority() +
                        "/nAllowed values are : "+ Arrays.toString(TaskPriority.values()));
            }
        } else newTask.setPriority(TaskPriority.MEDIUM);
        return convertToResponse(this.taskRepository.save(newTask));
    }
    //delete task
    @Override
    public void deleteTask(String username, String title) {
        Task task = this.taskRepository.findByTitleAndUsername(title,username)
                .orElseThrow(()-> new RuntimeException("Task not found"));
        this.taskRepository.delete(task);
    }
    @Override
    public void deleteAllTaskByUsername(String username) {
        this.taskRepository.deleteAllByUsername(username);
    }
    //update task
    @Override
    public TaskResponse updateTask(String username, String title, TaskRequest taskRequest) {
        Task task = this.taskRepository.findByTitleAndUsername(title,username)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if(taskRequest.getStatus() != null && !taskRequest.getStatus().isEmpty()) {
            try {
                TaskStatus status = TaskStatus.valueOf(taskRequest.getStatus().toUpperCase());
                if(status == TaskStatus.CANCELLED) {
                    this.taskRepository.delete(task);
                    return TaskResponse.builder()
                            .taskTitle(taskRequest.getTitle())
                            .taskDescription("Task is canceled and deleted")
                            .taskStatus(TaskStatus.CANCELLED)
                            .build();
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status attribute "+ taskRequest.getStatus() +
                        "/nAllowed values are : "+ Arrays.toString(TaskStatus.values()));
            }
        }
        if(taskRequest.getTitle() != null && !taskRequest.getTitle().isEmpty())
            task.setTitle(taskRequest.getTitle());
        if(taskRequest.getDescription() != null && !taskRequest.getDescription().isEmpty())
            task.setDescription(taskRequest.getDescription());
        if(taskRequest.getStatus() != null && !taskRequest.getStatus().isEmpty()) {
            try {
                TaskStatus status = TaskStatus.valueOf(taskRequest.getStatus().toUpperCase());
                validateStatus(task.getStatus(),status);
                task.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status attribute "+taskRequest.getStatus()+
                        "/Allowed values are "+Arrays.toString(TaskStatus.values()));
            }
        }
        if(taskRequest.getPriority() != null && !taskRequest.getPriority().isEmpty()) {
            try {
                TaskPriority priority = TaskPriority.valueOf(taskRequest.getPriority().toUpperCase());
                task.setPriority(priority);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid priority attribute "+ taskRequest.getPriority() +
                        "/Allowed values are "+ Arrays.toString(TaskPriority.values()));
            }
        }
        Task updatedTask = this.taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    //get task list by status
    @Override
    public List<TaskResponse> getByStatus(String username, TaskStatus taskStatus) {
        List<Task> taskList = this.taskRepository.findAllByUsername(username);
        if(taskList.isEmpty())
            throw new RuntimeException("Task list is empty");
        return taskList.stream().filter(task -> taskStatus.equals(task.getStatus()))
                .map(this::convertToResponse).toList();
    }
    //get task list by priority
    @Override
    public List<TaskResponse> getByPriority(String username, TaskPriority taskPriority) {
        List<Task> taskList = this.taskRepository.findAllByUsername(username);
        if(taskList.isEmpty())
            throw new RuntimeException("Task list is empty");
        return taskList.stream().filter(t -> taskPriority.equals(t.getPriority()))
                .map(this::convertToResponse).toList();
    }

    @Override
    public List<TaskResponse> getAllTasks() {
       List<Task> taskResponseList = this.taskRepository.findAll();
       return taskResponseList.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<TaskResponse> getSortedTaskByCreatedAt(String username, String order) {
        List<Task> taskList = this.taskRepository.findAllByUsername(username);
        if(taskList.isEmpty())
            throw new RuntimeException("No task found for the user");
        if("asc".equalsIgnoreCase(order))
            taskList.sort(Comparator.comparing(Task::getCreatedAt));
        else if("desc".equalsIgnoreCase(order))
            taskList.sort(Comparator.comparing(Task::getCreatedAt).reversed());
        else
            throw new IllegalArgumentException("""
                    Invalid order value. Allowed values are
                    Ascending -> asc
                    Descending -> desc""");
        return taskList.stream().map(this::convertToResponse).toList();
    }

    //helper methods
    private void validateStatus(TaskStatus oldStatus, TaskStatus newStatus) {
        if(oldStatus == TaskStatus.TODO && newStatus == TaskStatus.COMPLETED)
            throw new RuntimeException("Can not directly update status from 'To Do' to 'Completed'");
    }
    private TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .taskTitle(task.getTitle())
                .taskDescription(task.getDescription())
                .taskStatus(task.getStatus() != null ? task.getStatus():TaskStatus.PENDING)
                .taskPriority(task.getPriority() !=null ? task.getPriority():TaskPriority.MEDIUM)
                .username(task.getUsername())
                .createdAt(task.getCreatedAt())
                .taskDeadline(task.getTaskDeadline())
                .build();

    }
    private Task convertToTask(TaskRequest taskRequest) {
        return Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .taskDeadline(taskRequest.getTaskDeadline())
                .build();
    }
}
