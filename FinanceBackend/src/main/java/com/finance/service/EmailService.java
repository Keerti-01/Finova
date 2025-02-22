package com.finance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Email from the application properties
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Method to send an email
    @Async
    public void sendEmail(String to, String subject, String message) throws MessagingException {
        jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        
        try {
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true); // true means HTML content
        }catch(jakarta.mail.MessagingException e) { e.printStackTrace();}
        
        mailSender.send(mimeMessage);
    }

    // Method to send emails to both user and admin
    public void sendRegistrationEmailToUserAndAdmin(String userEmail, String userName) throws MessagingException {
        String subject = "Welcome to the Finova Finance App!";
        String userMessage = "Dear " + userName + ",\n\nWelcome to the Finova. Your account has been successfully created. "
        		+ "Track your budget, investments and expenses seamlessly!!!\n\nBest regards, Finova App Team.";
        String adminMessage = "A new user has registered: " + userName + " (" + userEmail + ").";

        // Send email to user
        sendEmail(userEmail, subject, userMessage);

        // Send email to admin
        sendEmail("serlichinnu2507@gmail.com", "New User Registration", adminMessage); // Replace with actual admin email
    }
}
