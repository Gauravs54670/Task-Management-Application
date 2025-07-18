package com.gaurav.taskmanager2.model.TaskModel;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private String taskTitle;
    private String taskDescription;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private String username;
    private LocalDateTime createdAt;
    private LocalDate taskDeadline;
}
