package com.gaurav.taskmanager2.Controller.UserController;

import com.gaurav.taskmanager2.model.UserModel.UserDTO;
import com.gaurav.taskmanager2.model.UserModel.UserSignup;
import com.gaurav.taskmanager2.service.UserService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private UserService userService;

    //create
    @PostMapping("/sign-up")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserSignup userSignup) {
        UserDTO userDTO = this.userService.createUser(userSignup);
        return ResponseEntity.ok(Map.of("message","User signed up successfully","response",userDTO));
    }
}
