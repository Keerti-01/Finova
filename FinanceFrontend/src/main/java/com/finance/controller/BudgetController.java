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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finance.model.Budget;
import com.finance.model.UserInfo;

@Configuration
@Controller
public class BudgetController {

	@Autowired
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	};
	
	private String backendurl = "http://localhost:8081/api/budgets";
	
	@GetMapping("/budget/{userId}")
	public String getAllBudgets(@PathVariable String userId, Model model) {
	    ResponseEntity<List<Budget>> response = getRestTemplate().exchange(
	        backendurl + "/user/" + userId,
	        HttpMethod.GET,
	        null,
	        new ParameterizedTypeReference<List<Budget>>() {}
	    );

	    List<Budget> budgets = response.getBody();
	    if (budgets == null) {
	        budgets = new ArrayList<>(); // Avoid null pointer issues
	    }
	    //System.out.println(budgets);
	    System.out.println("Received Budgets: " + budgets);
	    
	    // Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    //System.out.println("User ID: " + user.getUserId());
	    
	    model.addAttribute("budgets", budgets); // Passing data to Thymeleaf template
	    model.addAttribute("user", user);
	    
	    return "budget"; 
	}
	
	@GetMapping("/createBudgetForm/{userId}")
	public String createBudgetForm(@PathVariable String userId,Model model) {
		
		// Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
		model.addAttribute("budget",new Budget());
		model.addAttribute("user",user);
		return "createBudget";
	}
	
	@PostMapping("/addBudget/{userId}")
	public String addBudget(@PathVariable String userId, @ModelAttribute Budget budget,Model model) {
		// Fetch user info (Ensure user details are retrieved)
	    ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/users/" + userId, UserInfo.class);
	    UserInfo user = userResponse.getBody();
	    if (user == null) {
	        throw new RuntimeException("User not found!");
	    }
	    model.addAttribute("user",user);
	    
	 // **Validation: Ensure spentAmount is not greater than limitAmount**
	    if (budget.getSpentAmount() > budget.getLimitAmount()) {
	        model.addAttribute("error", "Spent amount cannot be greater than the limit amount.");
	        return "createBudget";  // Return to form with an error message
	    }
	    
	    ResponseEntity<Budget> response = getRestTemplate().postForEntity(backendurl+"/"+userId, budget, Budget.class);
	    if (response.getStatusCode().is2xxSuccessful()) {
	        return "redirect:/budget/" + userId;  // Redirect to list of budgets for the user
	    } else {
	        model.addAttribute("error", "Failed to create budget. Please try again.");
	        return "createBudget"; // Return to the form if creation fails
	    }
	}
	
	@GetMapping("/deleteBudget/{budgetId}/{userId}")
	public String deleteBudget(@PathVariable Long budgetId,@PathVariable String userId, Model model) {
		ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		model.addAttribute("user",user);
	    
	    getRestTemplate().delete(backendurl+"/"+budgetId); // Sends DELETE request to backend

	    return "redirect:/budget/"+userId; // Redirect to updated budget list
	}
	
	@PostMapping("/updateSpentAmount/{budgetId}/{userId}")
	public String updateBudget(@PathVariable Long budgetId,@PathVariable String userId, Model model,
			@RequestParam double additionalAmount, RedirectAttributes redirectAttributes) {
		
		ResponseEntity<UserInfo> userResponse = getRestTemplate().getForEntity(
		        "http://localhost:8081/api/users/" + userId, UserInfo.class);
		    UserInfo user = userResponse.getBody();
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }
		model.addAttribute("user",user);
		
		// Fetch the existing budget
	    ResponseEntity<Budget> budgetResponse = getRestTemplate().getForEntity(
	        "http://localhost:8081/api/budgets/" + budgetId, Budget.class);

	    Budget budget = budgetResponse.getBody();
	    if (budget == null) {
	        throw new RuntimeException("Budget not found!");
	    }
	    
	    double newSpentAmount = budget.getSpentAmount() + additionalAmount;
	    
	    budget.setSpentAmount(newSpentAmount);
	    // Ensure spentAmount does not exceed limitAmount
	    if (newSpentAmount > budget.getLimitAmount()) {
	        model.addAttribute("error", "Spent amount cannot exceed the limit.");
	        redirectAttributes.addFlashAttribute("error", "Spent amount cannot exceed the limit.");
	        return "redirect:/budget/" + userId;
	    }

	    //budget.setUser(user);
	    
	    System.out.println();
	   
		try {
		budget.setUser(null); // Nullify user to prevent serialization issues
		getRestTemplate().put(backendurl+"/"+budgetId,budget);
		
		}catch(HttpClientErrorException e) { 
			String errorMessage = e.getResponseBodyAsString();
	        model.addAttribute("error", errorMessage);
	        System.out.println("Backend error: " + errorMessage);
	        return "redirect:/budget/"+userId; 
		}
		
		return "redirect:/budget/"+userId; 
	}
}
