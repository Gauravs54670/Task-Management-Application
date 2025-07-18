package com.gaurav.taskmanager2.service.UserService;

import com.gaurav.taskmanager2.Repository.UserRepository;
import com.gaurav.taskmanager2.model.UserModel.User;
import com.gaurav.taskmanager2.model.UserModel.UserDTO;
import com.gaurav.taskmanager2.model.UserModel.UserRole;
import com.gaurav.taskmanager2.model.UserModel.UserSignup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDTO getByUsername(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO createUser(UserSignup user) {
        boolean exist = this.userRepository.findByUsername(user.getUsername()).isPresent();
        if(exist)
            throw new RuntimeException("User is already present");
        boolean exist1 = this.userRepository.findByEmail(user.getEmail()).isPresent();
        if(exist1)
            throw new RuntimeException("Email is already present");
        User newUser = convertToUser(user);
        return convertToUserDTO(this.userRepository.save(newUser));
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return this.userRepository.findAll()
                .stream().map(this::convertToUserDTO).toList();
    }
    @Override
    public void deleteUser(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        this.userRepository.deleteByUsername(user.getUsername());
    }

    @Override
    public UserDTO updateUser(String username, User user) {
        User existUser = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getUsername()!=null && !user.getUsername().isEmpty())
            existUser.setUsername(user.getUsername());
        if(user.getEmail()!=null && !user.getEmail().isEmpty()) {
            if(this.userRepository.findByEmail(user.getEmail()).isPresent())
                throw new RuntimeException("Email is already registered please try again");
            existUser.setEmail(user.getEmail());
        }
        if(user.getPassword()!=null && !user.getPassword().isEmpty() && !
            passwordEncoder.matches(existUser.getPassword(),user.getPassword()))
            existUser.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getUserRoles()!=null && !user.getUserRoles().isEmpty()) {
            existUser.setUserRoles(new HashSet<>(user.getUserRoles()));
        }
        return convertToUserDTO(this.userRepository.save(existUser));
    }
    @Override
    public UserDTO updateRole(String username, UserRole role) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getUserRoles().contains(role))
            throw new IllegalStateException("Role is already assigned");
        user.getUserRoles().add(role);
        return convertToUserDTO(this.userRepository.save(user));
    }
    // helper methods
    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .userRoles(user.getUserRoles())
                .build();
    }
    private User convertToUser(UserSignup userSignup) {
        return User.builder()
                .username(userSignup.getUsername())
                .email(userSignup.getEmail())
                .password(passwordEncoder.encode(userSignup.getPassword()))
                .userRoles(Set.of(UserRole.USER))
                .build();
    }
}
