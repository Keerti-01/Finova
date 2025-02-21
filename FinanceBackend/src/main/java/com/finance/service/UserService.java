package com.finance.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.exception.InvalidEntityException;
import com.finance.model.UserInfo;
import com.finance.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private EmailService emailService;
	
	public UserInfo register(UserInfo user) throws InvalidEntityException{
		 // Check if the email already exists in the database
        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new InvalidEntityException("User with email " + user.getEmail() + " already exists");
        }
        
		String custID = UUID.randomUUID().toString().substring(0, 7);  
		 // Parse it as a base-16 (hexadecimal) number
	    custID = "CU" + Integer.toString(Integer.parseInt(custID, 16) % 10000000);  // Use base-16 to handle hexadecimal string
	    
		user.setUserId(custID);
		
        UserInfo registeredUser = userRepo.save(user);

        // Send email to both user and admin
        try {
            emailService.sendRegistrationEmailToUserAndAdmin(user.getEmail(), user.getName());
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

        return registeredUser;

	}
	
	// Login user
    public UserInfo login(UserInfo user) throws InvalidEntityException{
        // Check if the user exists by email
        UserInfo prev = userRepo.findByEmail(user.getEmail());

        if (prev != null) {
            // Verify password
            if (prev.getPassword().equals(user.getPassword())) {
                return prev;  // Return user details if login is successful
            } else {
                throw new InvalidEntityException("Invalid password");
            }
        } else {
            throw new InvalidEntityException("User with email " + user.getEmail() + " not found");
        }
    }
    
    public Optional<UserInfo> getUserById(String id) {
    	return userRepo.findById(id);
    }
    
    public List<UserInfo> getAllUsers() {
    	return userRepo.findAll();
    }
    
    //update user
    public UserInfo updateUser(UserInfo user, String id) throws InvalidEntityException{
    	UserInfo prev = userRepo.findById(id).orElseThrow(()-> new InvalidEntityException("User not found"));
    	if(user.getName() != null) prev.setName(user.getName());
    	if(user.getEmail() != null) prev.setEmail(user.getEmail());
    	if(user.getPassword() != null) prev.setPassword(user.getPassword());
    	if(user.getIncome() > 0) prev.setIncome(user.getIncome());
    	if(user.getGoals() != null) prev.setGoals(user.getGoals());
    	return userRepo.save(prev);
    }
    
    //delete user
    public UserInfo deleteUser(String id) {
    	UserInfo prev = userRepo.findById(id).orElseThrow(()-> new InvalidEntityException("User not found"));
    	userRepo.delete(prev);
    	return prev;
    }
    
    // Method to change password
    public String changePassword(String email, String oldPassword, String newPassword) throws Exception {
        // 1. Check if the user exists by email
        UserInfo user = userRepo.findByEmail(email);
        
        if (user == null) {
            throw new InvalidEntityException("User not found with the provided email");
        }

        // 2. Verify the old password if needed (optional, depending on your use case)
        if (!oldPassword.equals(user.getPassword())) {
            throw new InvalidEntityException("Old password is incorrect");
        }

        // 3. Validate new password (min 6 characters, at least one uppercase, one special character)
        if (!newPassword.matches(".*[A-Z].*") || !newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*") || newPassword.length() < 6) {
            throw new InvalidEntityException("Password must be at least 6 characters long, contain at least one uppercase letter and one special character.");
        }

        // 4. Update the password (save it as plain text)
        user.setPassword(newPassword);  // No encoding applied here, using plain text
        userRepo.save(user);

        // 5. Send an email confirming the password change
        String subject = "Your Password Has Been Changed";
        String message = "Hello " + user.getName() + ",\n\nYour password has been successfully changed.\nIf you did not make this change, please contact support immediately.";
        
        emailService.sendEmail(user.getEmail(), subject, message);

        return "Password changed successfully.";
    }

	public UserInfo findByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepo.findByEmail(email);
	}

	public void save(UserInfo user) {
		// TODO Auto-generated method stub
		userRepo.save(user);
	}
}
