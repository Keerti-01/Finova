package com.finance.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.model.UserInfo;
import com.finance.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/home")
	public String home() {
		return "welcome to finance application";
	}
	
	// Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserInfo> register(@Valid @RequestBody UserInfo user) {
        UserInfo registeredUser = userService.register(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // Login an existing user
    @PostMapping("/login")
    public ResponseEntity<UserInfo> login(@RequestBody UserInfo user) {
        UserInfo loggedInUser = userService.login(user);
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }
    
    //get user by id
    @GetMapping("/{id}")
    public Optional<UserInfo> getUserById(@PathVariable String id) {
    	return userService.getUserById(id);
    }
    
    //get all users
    @GetMapping("/all")
    public List<UserInfo> getAllUsers(){
    	return userService.getAllUsers();
    }
    
    //update user details
    @PutMapping("/{id}")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo user, @PathVariable String id) {
    	UserInfo updateuser = userService.updateUser(user, id);
    	return new ResponseEntity<>(updateuser, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<UserInfo> deleteUser(@PathVariable String id){
    	return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
    
    // Endpoint to change password
    @PutMapping("/change-password")
    public String changePassword(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            return userService.changePassword(email, oldPassword, newPassword);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
