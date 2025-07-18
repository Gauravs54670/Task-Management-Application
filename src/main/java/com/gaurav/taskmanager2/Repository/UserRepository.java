package com.gaurav.taskmanager2.Repository;

import com.gaurav.taskmanager2.model.UserModel.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByUsername(String username);
    public void deleteByUsername(String username);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByResetToken(String token);
}
