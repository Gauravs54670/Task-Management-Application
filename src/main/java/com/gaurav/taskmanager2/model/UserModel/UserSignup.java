package com.gaurav.taskmanager2.model.UserModel;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class UserSignup {
    private String username;
    private String email;
    private String password;
}
