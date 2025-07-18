package com.gaurav.taskmanager2.scheduler;

import com.gaurav.taskmanager2.Repository.TaskRepository;
import com.gaurav.taskmanager2.Repository.UserRepository;
import com.gaurav.taskmanager2.model.TaskModel.Task;
import com.gaurav.taskmanager2.model.UserModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TaskDeadLineScheduler {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    //check alert every 1 minute
    @Scheduled(fixedRate = 60000 * 5)
    public void checkUpcomingDeadLine() {
        List<Task> taskList = this.taskRepository.findAll();
        LocalDate today = LocalDate.now();
        for(Task task: taskList) {
            LocalDate deadLine = task.getTaskDeadline();
            if(deadLine != null && !deadLine.isBefore(today)) {
                long daysLeft = ChronoUnit.DAYS.between(today,deadLine);
                if(daysLeft <= 2) {
                    String email = getEmail(task.getUsername());
                    if(email != null)
                        sendEmail(email,task.getTitle(),deadLine,daysLeft);
                }
            }
        }
    }
    //Helper methods
    private void sendEmail(String email,String title,LocalDate deadLine,long daysLeft) {
        String subject = "⚠️ Upcoming Task Deadline Alert";
        String body = "Dear user,\n\nYour task \"" + title +
                "\" is due in " + daysLeft + " day(s) (Deadline: " + deadLine + ").\n" +
                "Please make sure to complete it on time.\n\nRegards,\nTask Manager";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        this.javaMailSender.send(simpleMailMessage);
        System.out.println("Alert sent to "+email);
    }
    private String getEmail(String username) {
        return this.userRepository.findByUsername(username)
                .map(User::getEmail).orElse(null);
    }
}
