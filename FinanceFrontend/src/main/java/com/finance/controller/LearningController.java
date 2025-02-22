package com.finance.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.finance.model.Learning;
import com.finance.model.UserInfo;

@Configuration
@Controller
public class LearningController {

	@Autowired
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	};
	
	private String backendurl = "http://localhost:8081/api/learning";
	
	@GetMapping("/learning/{userId}")
	public String getAllLearning(@PathVariable String userId, Model model) {
	    ResponseEntity<List<Learning>> response = getRestTemplate().exchange(
	        backendurl,
	        HttpMethod.GET,
	        null,
	        new ParameterizedTypeReference<List<Learning>>() {}
	    );

	    List<Learning> learning = response.getBody();
	    if (learning == null) {
	        learning = new ArrayList<>(); // Avoid null pointer issues
	    }
	    System.out.println(learning);
	    System.out.println("Received Learnings: " + learning);
	    
	    // Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    System.out.println("User ID: " + user.getUserId());
	    model.addAttribute("learning", learning); // Passing data to Thymeleaf template
	    model.addAttribute("user", user);
	    
	    return "learning"; 
	}
	
	@GetMapping("/add-learning-form/{userId}")
	public String addLearningForm(@PathVariable String userId, Model model) {
		ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
			model.addAttribute("learning",new Learning());
			model.addAttribute("user",user);
			return "addLearning";
	}
	
	@PostMapping("/addLearning/{userId}")
	public String addLearning(@PathVariable String userId, @ModelAttribute Learning learning,Model model) {
		// Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    model.addAttribute("user",user);
	    
	    ResponseEntity<Learning> response = getRestTemplate().postForEntity(backendurl+"/"+userId, learning, Learning.class);
	    if (response.getStatusCode().is2xxSuccessful()) {
	        return "redirect:/learning/" + userId;  // Redirect to list of budgets for the user
	    } else {
	        model.addAttribute("error", "Failed to add expense. Please try again.");
	        return "addLearning"; // Return to the form if creation fails
	    }
	}
	
	@GetMapping("/myLearnings/{userId}")
	public String myLearnings(@PathVariable String userId, Model model) {
	    List<Learning> learning = new ArrayList<>();

	    // Fetch learning modules with error handling
	    try {
	        ResponseEntity<List<Learning>> response = getRestTemplate().exchange(
	            backendurl + "/user/" + userId,
	            HttpMethod.GET,
	            null,
	            new ParameterizedTypeReference<List<Learning>>() {}
	        );
	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            learning = response.getBody();
	        }
	    } catch (HttpClientErrorException.NotFound e) {
	        System.out.println("No learnings found for user ID: " + userId);
	    } catch (Exception e) {
	        System.out.println("Error fetching learnings: " + e.getMessage());
	    }

	    // Fetch user info safely
	    UserInfo user = null;
	    try {
	        ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	            "http://localhost:8081/api/users/" + userId, UserInfo.class);
	        user = userResponse.getBody();
	        if (user == null) {
	            throw new RuntimeException("User not found!");
	        }
	    } catch (Exception e) {
	        System.out.println("Error fetching user info: " + e.getMessage());
	        throw new RuntimeException("User not found!");
	    }

	    // Add attributes to model
	    model.addAttribute("learning", learning);
	    model.addAttribute("user", user);

	    return "myLearnings";
	}

	
		//delete learning
		@GetMapping("/deleteLearning/{learningId}/{userId}")
		public String deleteLearning(@PathVariable Long learningId,@PathVariable String userId, Model model) {
			ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
			        "http://localhost:8081/api/users/" + userId, UserInfo.class);
			    UserInfo user = userResponse.getBody();
			    if (user == null) {
			        throw new RuntimeException("User not found!");
			    }
			model.addAttribute("user",user);
		    
		    getRestTemplate().delete(backendurl+"/"+learningId); // Sends DELETE request to backend

		    return "redirect:/myLearnings/"+userId; 
		}
		
		//update
		@GetMapping("/updateLearningForm/{learningId}/{userId}")
		public String updateLearningForm(@PathVariable String userId,@PathVariable Long learningId,Model model) {
			
			// Fetch user info (Ensure user details are retrieved)
		    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		    
		    ResponseEntity<Learning> response = getRestTemplate().getForEntity(backendurl+"/"+learningId, Learning.class);
		    if (response == null) {
		        model.addAttribute("error", "Learning not found!");
		        return "updateLearning";
		    }

			model.addAttribute("learning",response.getBody());
			model.addAttribute("user",user);
			return "updateLearning";
		}
		
		@PostMapping("/updateLearning/{learningId}/{userId}")
		public String updateLearning(@PathVariable String userId, @PathVariable Long learningId ,
				@ModelAttribute Learning learning,Model model) {
			
		    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		    model.addAttribute("user",user);
		    
		    try {
		    getRestTemplate().put(backendurl+"/"+learningId, learning);
		    }catch(HttpClientErrorException e) {
		    	String errorMessage = e.getResponseBodyAsString();
		        model.addAttribute("error", errorMessage);
		        System.out.println("Backend error: " + errorMessage);
		        return "updateLearning";
		    }
		    		
		    return "redirect:/myLearnings/" + userId;  
		}
}

