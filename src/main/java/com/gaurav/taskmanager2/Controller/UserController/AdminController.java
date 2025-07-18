package com.gaurav.taskmanager2.Controller.UserController;

import com.gaurav.taskmanager2.model.TaskModel.TaskResponse;
import com.gaurav.taskmanager2.model.UserModel.RoleUpdateRequest;
import com.gaurav.taskmanager2.model.UserModel.User;
import com.gaurav.taskmanager2.model.UserModel.UserDTO;
import com.gaurav.taskmanager2.model.UserModel.UserRole;
import com.gaurav.taskmanager2.service.TaskService.TaskService;
import com.gaurav.taskmanager2.service.UserService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    //Get all users
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> usersList = this.userService.getAllUsers();
        return ResponseEntity.ok(Map.of("message","Users list fetched successful","response",usersList));
    }
    //Get User
    @GetMapping("/get-user")
    public ResponseEntity<?> getUser (@RequestParam(value = "username") String username) {
        if(username == null || username.trim().isEmpty())
            throw new IllegalStateException("username is required");
        UserDTO user = this.userService.getByUsername(username);
        return ResponseEntity.ok(Map.of("message","User fetched successfully","response",user));
    }
    //deleting user
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "username") String username) {
        if(username == null || username.trim().isEmpty())
            throw new IllegalStateException("username is required");
        Set<UserRole> role = this.userService.getByUsername(username).getUserRoles();
        if(role.contains(UserRole.ADMIN))
            throw new RuntimeException("Admin can't be deleted");
        this.taskService.deleteAllTaskByUsername(username);
        this.userService.deleteUser(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message","user successfully deleted"));
    }
    //update user
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestParam(value = "username",required = true) String username,@Valid @RequestBody User user) {
        UserDTO updatedUser = this.userService.updateUser(username,user);
        return ResponseEntity.ok(Map.of("message","user updated successfully","response",updatedUser));
    }
    //update the role
    @PutMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestParam(value = "username",required = true) String username, @RequestBody RoleUpdateRequest role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toString().toUpperCase());
            UserDTO user = this.userService.updateRole(username,userRole);
            return ResponseEntity.ok(Map.of("message","role updated successfully","response",user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "invalid value", "Allowed value", UserRole.values()));
        }
    }
    //get all tasks
    @GetMapping("/get-all-tasks")
    public ResponseEntity<?> getAllTasks() {
        List<TaskResponse> taskResponseList = this.taskService.getAllTasks();
        return ResponseEntity.ok(Map.of("message","task fetched successfully","response",taskResponseList));
    }
}
