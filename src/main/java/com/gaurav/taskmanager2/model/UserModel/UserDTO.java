package com.gaurav.taskmanager2.model.UserModel;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UserDTO {
    private String username;
    private String email;
    private Set<UserRole> userRoles;
}
