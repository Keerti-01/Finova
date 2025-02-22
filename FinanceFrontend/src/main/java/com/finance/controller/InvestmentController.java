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

import com.finance.model.Investment;
import com.finance.model.UserInfo;

@Configuration
@Controller
public class InvestmentController {
	@Autowired
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	};
	
	private String backendurl = "http://localhost:8081/api/investments";
	
	@GetMapping("/investment/{userId}")
	public String getAllInvestments(@PathVariable String userId, Model model) {
	    ResponseEntity<List<Investment>> response = getRestTemplate().exchange(
	        backendurl + "/user/" + userId,
	        HttpMethod.GET,
	        null,
	        new ParameterizedTypeReference<List<Investment>>() {}
	    );

	    List<Investment> investment = response.getBody();
	    if (investment == null) {
	        investment = new ArrayList<>(); // Avoid null pointer issues
	    }
	    System.out.println(investment);
	    System.out.println("Received investments: " + investment);
	    
	    // Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    System.out.println("User ID: " + user.getUserId());
	    
	    model.addAttribute("investment", investment); // Passing data to Thymeleaf template
	    model.addAttribute("user", user);
	    
	    return "investment"; 
	}
	
	@GetMapping("/createInvestmentForm/{userId}")
	public String createInvestmentForm(@PathVariable String userId,Model model) {
		
		// Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
		model.addAttribute("investment",new Investment());
		model.addAttribute("user",user);
		return "addInvestment";
	}
	
	@PostMapping("/addInvestment/{userId}")
	public String addExpense(@PathVariable String userId, @ModelAttribute Investment investment,Model model) {
		// Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    model.addAttribute("user",user);
	    
	    ResponseEntity<Investment> response = getRestTemplate().postForEntity(backendurl+"/"+userId, investment, Investment.class);
	    if (response.getStatusCode().is2xxSuccessful()) {
	        return "redirect:/investment/" + userId;  // Redirect to list of budgets for the user
	    } else {
	        model.addAttribute("error", "Failed to add expense. Please try again.");
	        return "addInvestment"; // Return to the form if creation fails
	    }
	}
	
	//delete investment
		@GetMapping("/deleteInvestment/{investmentId}/{userId}")
		public String deleteBudget(@PathVariable Long investmentId,@PathVariable String userId, Model model) {
			ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
			        "http://localhost:8081/api/users/" + userId, UserInfo.class);
			    UserInfo user = userResponse.getBody();
			    if (user == null) {
			        throw new RuntimeException("User not found!");
			    }
			model.addAttribute("user",user);
		    
		    getRestTemplate().delete(backendurl+"/"+investmentId); // Sends DELETE request to backend

		    return "redirect:/investment/"+userId; // Redirect to updated budget list
		}
		
		//update
		@GetMapping("/updateInvestmentForm/{investmentId}/{userId}")
		public String updateExpenseForm(@PathVariable String userId,@PathVariable Long investmentId,Model model) {
			
			// Fetch user info (Ensure user details are retrieved)
		    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		    
		    ResponseEntity<Investment> response = getRestTemplate().getForEntity(backendurl+"/"+investmentId, Investment.class);
		    if (response == null) {
		        model.addAttribute("error", "Investment not found!");
		        return "updateInvestment";
		    }

			model.addAttribute("investment",response.getBody());
			model.addAttribute("user",user);
			return "updateInvestment";
		}
		
		@PostMapping("/updateInvestment/{investmentId}/{userId}")
		public String updateInvestment(@PathVariable String userId, @PathVariable Long investmentId 
				,@ModelAttribute Investment investment,Model model) {
			// Fetch user info (Ensure user details are retrieved)
		    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		    model.addAttribute("user",user);
		    
		    try {
		    getRestTemplate().put(backendurl+"/"+investmentId, investment);
		    }catch(HttpClientErrorException e) {
		    	String errorMessage = e.getResponseBodyAsString();
		        model.addAttribute("error", errorMessage);
		        System.out.println("Backend error: " + errorMessage);
		        return "updateInvestment";
		    }
		    		
		    return "redirect:/investment/" + userId;  // Redirect to list of budgets for the user
		}
	
}
