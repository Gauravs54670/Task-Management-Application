package com.gaurav.taskmanager2.service.UserService;

import com.gaurav.taskmanager2.model.UserModel.User;
import com.gaurav.taskmanager2.model.UserModel.UserDTO;
import com.gaurav.taskmanager2.model.UserModel.UserRole;
import com.gaurav.taskmanager2.model.UserModel.UserSignup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    public UserDTO getByUsername(String username);
    public UserDTO createUser(UserSignup user);
    public UserDTO updateUser(String username, User user);
    public List<UserDTO> getAllUsers();
    public void deleteUser(String username);
    public UserDTO updateRole(String username, UserRole role);
}
