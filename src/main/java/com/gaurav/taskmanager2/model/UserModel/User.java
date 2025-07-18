package com.gaurav.taskmanager2.model.UserModel;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class User {
    @Id
    private String userId;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private String password;
    private Set<UserRole> userRoles;
    private String resetToken;
    private Instant expiryTime;
}
