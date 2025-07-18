package com.gaurav.taskmanager2.Repository;

import com.gaurav.taskmanager2.model.TaskModel.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {
    Optional<Task> findByTitleAndUsername(String title, String username);
    List<Task> findAllByUsername(String username);
    void deleteAllByUsername(String username);
}
