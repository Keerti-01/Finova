package com.finance.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.model.UserInfo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Configuration
@Controller
public class UserController {
	 
	@Autowired
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	};
	
	private String backendurl = "http://localhost:8081/api/users";
	
	@GetMapping("/")
	public String home() {
		return "onboard";
	}
	
	@GetMapping("/loginForm")
	public String loginForm(Model model) {
		model.addAttribute("user",new UserInfo());
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute UserInfo user, Model model, HttpSession session,BindingResult bindingResult) {
		
		// Check for validation errors before calling the backend
	    if (bindingResult.hasErrors()) {
	        System.out.println("Validation errors: " + bindingResult.getAllErrors());
	        model.addAttribute("user", user);
	        return "login"; // Return the login page with errors
	    }

	    try {
	        ResponseEntity<UserInfo> response = getRestTemplate().postForEntity(backendurl + "/login", user, UserInfo.class);

	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	        	model.addAttribute("user",user);
	            // ✅ Store user in session
	            session.setAttribute("loggedInUser", response.getBody());
	            return "redirect:/dashboard"; // ✅ Redirect to dashboard on success
	        }

	    } catch (HttpClientErrorException e) {
	        try {
	            // ✅ Fix: Directly extract the message field instead of treating it as a map
	            JsonNode jsonResponse = new ObjectMapper().readTree(e.getResponseBodyAsString());
	            String errorMessage = jsonResponse.has("message") ? jsonResponse.get("message").asText() : "Invalid username or password.";

	            model.addAttribute("error", errorMessage);

	        } catch (JsonProcessingException jsonEx) {
	            model.addAttribute("error", "Invalid credentials.");
	            jsonEx.printStackTrace();
	        }
	    } catch (Exception e) {
	        model.addAttribute("error", "An unexpected error occurred.");
	        e.printStackTrace();
	    }

	    model.addAttribute("user", user); // Ensure user object is passed back to the view
	    return "login"; // Return login page in case of failure
	}
	
	
	@GetMapping("/signupForm")
	public String signupForm(Model model) {
		model.addAttribute("user",new UserInfo());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute UserInfo user, BindingResult bindingResult, Model model, HttpSession session) {
		// If there are validation errors from frontend
	    if (bindingResult.hasErrors()) {
	        System.out.println("Validation errors: " + bindingResult.getAllErrors());
	        model.addAttribute("user", user); // Keep entered data
	        return "signup"; // Return back to form
	    }

	    try {
	        // Send signup request to backend
	        ResponseEntity<UserInfo> response = getRestTemplate().postForEntity(backendurl + "/register", user, UserInfo.class);

	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	        	model.addAttribute("user",user);
	            session.setAttribute("loggedInUser", response.getBody());
	            return "redirect:/dashboard"; // Redirect on success
	        }

	    } catch (HttpClientErrorException e) {
	        try {
	            String responseBody = e.getResponseBodyAsString();
	            System.out.println("Backend Validation Errors: " + responseBody); // Debugging

	            Map<String, String> errors = new ObjectMapper().readValue(responseBody, new TypeReference<Map<String, String>>() {});

	            for (Map.Entry<String, String> entry : errors.entrySet()) {
	                System.out.println("Field: " + entry.getKey() + " - Error: " + entry.getValue()); // Debugging
	                
	                String field = entry.getKey();
	    			String errorMsg = entry.getValue();		
	    			
	    			if ("message".equals(field)) {
	    	            // If the error is general, add as a global error
	    	            model.addAttribute("error", errorMsg);
	    	        } else {
	    	            // Bind field-specific errors
	    	            bindingResult.rejectValue(field, "", errorMsg);
	    	        }
	    			
	                model.addAttribute("error",errors.get(entry.getKey()));
	    			
	            }
	            
	            model.addAttribute("user", user);
	            return "signup";
	            
	        } catch (JsonProcessingException e1) {
	            e1.printStackTrace();
	        }
	    }

	    model.addAttribute("user", user);
	    return "signup"; // Return to signup page if there are errors
	}
	
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {
		// Retrieve the logged-in user from session
	    UserInfo loggedInUser = (UserInfo) session.getAttribute("loggedInUser");
	    // If no user is logged in, redirect to login page
	    if (loggedInUser == null) {
	        return "redirect:/loginForm";
	    }
	    //System.out.println(loggedInUser);

	    // ✅ Add user info to model for Thymeleaf
	    model.addAttribute("user", loggedInUser);

	    return "dashboard";
	}
	
	@GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
	
	@GetMapping("/profilePage/{userId}")
	public String profilePage(Model model, @PathVariable String userId) {
		ResponseEntity<UserInfo> userInfo = getRestTemplate().getForEntity(backendurl+"/"+userId, UserInfo.class);
		model.addAttribute("user",userInfo.getBody());
		return "profilePage";
	}
	
	@GetMapping("/editProfile/{userId}")
	public String editProfile(Model model, @PathVariable String userId) {
		ResponseEntity<UserInfo> userInfo = getRestTemplate().getForEntity(backendurl+"/"+userId, UserInfo.class);
		model.addAttribute("user",userInfo.getBody());
		return "editProfile";
	}
	
	
	
	@PostMapping("/update/{userId}")
	public String update(@ModelAttribute UserInfo user, @PathVariable String userId, Model model, BindingResult bindingResult,HttpSession session) {
	    // Step 1: Ensure userId matches the object
	    if (!user.getUserId().equals(userId)) {
	        model.addAttribute("error", "User ID mismatch!");
	        return "editProfile";
	    }

	    // Step 2: Check for validation errors
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("user", user);
	        System.out.println("Validation error: " + bindingResult.getAllErrors());
	        return "editProfile";
	    }

	    try {
	        // Step 3: Perform the update
	        getRestTemplate().put(backendurl + "/" + userId, user);
	        session.setAttribute("loggedInUser", user);  // Update session
	        model.addAttribute("success", true);
	        return "redirect:/dashboard"; // Redirect to dashboard after update

	    } catch (HttpClientErrorException e) { 
	        // Step 4: Handle backend validation errors
	        String errorMessage = e.getResponseBodyAsString();
	        model.addAttribute("error", errorMessage);
	        System.out.println("Backend error: " + errorMessage);
	    } catch (Exception e) {
	        // Step 5: Handle unexpected errors
	        model.addAttribute("error", "Failed to update profile. Please try again.");
	        System.out.println("Unexpected error: " + e.getMessage());
	    }

	    model.addAttribute("user", user);
	    return "editProfile"; // Stay on the edit profile page in case of errors
	}
	

}

