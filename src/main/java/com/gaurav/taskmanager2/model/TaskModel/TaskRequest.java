package com.gaurav.taskmanager2.model.TaskModel;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TaskRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDate taskDeadline;
}
