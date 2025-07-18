package com.gaurav.taskmanager2.model.TaskModel;

import lombok.*;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Task")
public class Task {
    @Id
    private String taskId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate taskDeadline;
    private TaskPriority priority;
    private TaskStatus status;
    private String username;
}
