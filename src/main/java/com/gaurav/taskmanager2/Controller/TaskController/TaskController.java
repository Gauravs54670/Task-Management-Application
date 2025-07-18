package com.gaurav.taskmanager2.Controller.TaskController;

import com.gaurav.taskmanager2.model.TaskModel.*;
import com.gaurav.taskmanager2.service.TaskService.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    //post a task
    @PostMapping("/create")
    public ResponseEntity<?> postTask(@Valid @RequestBody TaskRequest taskRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskResponse taskResponse = this.taskService.createTask(username,taskRequest);
        return ResponseEntity.ok(Map.of("task",taskResponse,"message","task creation successful"));
    }
    //get a task
    @GetMapping("/get-task")
    public ResponseEntity<?> getTask(@RequestParam(value = "title",required = true) String title) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskResponse taskResponse = this.taskService.getTaskByUsername(title,username);
        return ResponseEntity.ok(Map.of("task",taskResponse,"message","Task fetched successfully"));
    }
    //get all task
    @GetMapping("/get-tasks")
    public ResponseEntity<?> getAllTasksByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskResponse> taskResponseList = this.taskService.getAllTasksByUsername(username);
        return ResponseEntity.ok(Map.of("Task list",taskResponseList,"message","Tasks fetched successfully"));
    }
    //get task by status
    @GetMapping("/get-task/status")
    public ResponseEntity<?> getTaskByStatus(@RequestParam(value = "status",required = true) String taskStatus) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskStatus status = TaskStatus.valueOf(taskStatus.toUpperCase());
            List<TaskResponse> taskResponseList = this.taskService.getByStatus(username,status);
            return new ResponseEntity<>(Map.of("Tasks list",taskResponseList,"message","Tasks fetched successfully"),HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(Map.of("error","Invalid value","Allowed values are",TaskStatus.values()),HttpStatus.CONFLICT);
        }
    }

    //get task by priority
    @GetMapping("/get-task/priority")
    public ResponseEntity<?> getTaskByPriority(@RequestParam(value = "priority") String priority) {
        if(priority == null || priority.isEmpty())
            throw new IllegalArgumentException("Task priority is required");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
            List<TaskResponse> taskResponseList = this.taskService.getByPriority(username,taskPriority);
            return new ResponseEntity<>(Map.of("Tasks list",taskResponseList,"message","Task fetched successfully"),HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException){
            return new ResponseEntity<>(Map.of("error","Invalid value","Allowed values are",TaskPriority.values()),HttpStatus.CONFLICT);
        }
    }
    //delete a task
    @DeleteMapping("/delete-task")
    public ResponseEntity<?> deleteTask(@RequestParam(value = "title") String title) {
        if(title == null || title.isEmpty())
            throw new IllegalArgumentException("Task title is required");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        this.taskService.deleteTask(username,title);
        return ResponseEntity.ok(Map.of("message","Task deleted successfully"));
    }
    //update the task
    @PutMapping("/update-task")
    public ResponseEntity<?> updateTask(@RequestParam(value = "title") String title,
                                       @Valid @RequestBody TaskRequest taskRequest) {
        if(title == null || title.isEmpty())
            throw new IllegalArgumentException("Task title is required");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskResponse taskResponse = this.taskService.updateTask(username,title,taskRequest);
        return new ResponseEntity<>(Map.of("message","Task updated successfully","response",taskResponse),HttpStatus.OK);
    }
    //get task by created at in ascending order
    @GetMapping("/get-task-asc")
    public ResponseEntity<?> getTaskInAscending(@RequestParam(defaultValue = "asc") String order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskResponse> taskResponseList = this.taskService.getSortedTaskByCreatedAt(username,order);
        return ResponseEntity.ok(Map.of("message", "Tasks sorted by createdAt (" + order + ")","response",taskResponseList));
    }
    //get task by created at in descending order
    public ResponseEntity<?> getTaskDescending(@RequestParam(defaultValue = "desc") String order) {
        if(order == null || order.isEmpty())
            throw new IllegalArgumentException("Order value is mandatory");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskResponse> taskResponseList = this.taskService.getSortedTaskByCreatedAt(username,order);
        return ResponseEntity.ok(Map.of("message", "Tasks sorted by createdAt (" + order + ")","response",taskResponseList));
    }
}