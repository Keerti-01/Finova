package com.finance.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.model.UserInfo;
import com.finance.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/password")
public class PasswordResetController {
	 @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private JavaMailSender mailSender;

	    private final Map<String, String> verificationCodes = new HashMap<>();

	    @PostMapping("/forgot")
	    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
	        UserInfo user = userService.findByEmail(email);
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	        }

	        // Generate a 6-digit verification code
	        String code = String.format("%06d", new Random().nextInt(999999));

	        // Store the code temporarily
	        verificationCodes.put(email, code);

	        // Send email
	        sendVerificationEmail(email, code);

	        return ResponseEntity.ok("Verification code sent to your email.");
	    }

	    @PostMapping("/verify")
	    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
	        if (verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code)) {
	            return ResponseEntity.ok("Code verified. Proceed to reset password.");
	        }
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired code.");
	    }

	    @PostMapping("/reset")
	    public ResponseEntity<String> resetPassword(@RequestParam String email,@Valid @RequestParam String newPassword) {
	        UserInfo user = userService.findByEmail(email);
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	        }

	        user.setPassword(newPassword);  // Ideally, hash the password before saving
	        userService.save(user);
	        verificationCodes.remove(email); // Remove the used code

	        return ResponseEntity.ok("Password successfully reset.");
	    }

	    private void sendVerificationEmail(String toEmail, String code) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(toEmail);
	        message.setSubject("Password Reset Code");
	        message.setText("Your password reset code is: " + code);
	        mailSender.send(message);
	    }
}
