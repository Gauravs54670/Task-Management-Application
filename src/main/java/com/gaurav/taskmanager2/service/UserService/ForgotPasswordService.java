package com.gaurav.taskmanager2.service.UserService;

import com.gaurav.taskmanager2.Repository.UserRepository;
import com.gaurav.taskmanager2.model.UserModel.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class ForgotPasswordService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender javaMailSender;

    //forgot password
    public void forgotPassword(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User does not exist please use right credential"));
        String resetToken = getResetToken();
        Instant expirationTime = Instant.now().plus(15, ChronoUnit.MINUTES);
        user.setResetToken(resetToken);
        user.setExpiryTime(expirationTime);
        this.userRepository.save(user);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Reset Password Request");
        simpleMailMessage.setText(
                "Hi " + user.getUsername() + ",\n\n" +
                        "To reset your password, use this following code:\n" + resetToken +
                        "\n\nIf you did not request this, please ignore this email."
        );
        this.javaMailSender.send(simpleMailMessage);
        System.out.println("Email sent to: " + user.getEmail());
    }
    //reset password component
    @Transactional
    public void resetPassword(String newPassword, String token) {
        User user = this.userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token or expired token please try again"));
        if(user.getResetToken() == null || user.getExpiryTime().isBefore(Instant.now()))
            throw new RuntimeException("Token is invalid or expired please try again");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setExpiryTime(null);
        this.userRepository.save(user);
    }
    //Helper method
    private String getResetToken() {
        int token = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(token);
    }
}
