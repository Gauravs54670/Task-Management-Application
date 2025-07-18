package com.gaurav.taskmanager2.Controller.UserController;

import com.gaurav.taskmanager2.JWT.JwtUtil;
import com.gaurav.taskmanager2.model.UserModel.ForgotPasswordRequest;
import com.gaurav.taskmanager2.model.UserModel.ResetPasswordRequest;
import com.gaurav.taskmanager2.model.UserModel.UserLogin;
import com.gaurav.taskmanager2.service.UserService.ForgotPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @PostMapping
    private ResponseEntity<?> generateToken(@RequestBody UserLogin loginRequest) {
        try {
            //Authenticate the user by username and password
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),loginRequest.getPassword()
            ));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("Authentication complete");
            //If authentication successful then load the full details of the user
            //Generate the token through username
            String token = jwtUtils.generateToken(userDetails);
            log.info("Token generated");
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            map.put("message","User logged in successfully");
            return new ResponseEntity<>(map,HttpStatus.OK);
        } catch (Exception e) {
            //If authentication failed
            Map<String,String> map = new HashMap<>();
            map.put("message","Token generation failed please try again");
            map.put("response",e.getMessage());
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordRequest(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        this.forgotPasswordService.forgotPassword(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(Map.of("message","password reset code sent to email"));
    }
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        this.forgotPasswordService.resetPassword(resetPasswordRequest.getNewPassword(), resetPasswordRequest.getToken());
        return ResponseEntity.ok(Map.of("message","Password updated successfully"));
    }
}
