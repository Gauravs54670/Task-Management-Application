package com.gaurav.taskmanager2.model.UserModel;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String token;
}
