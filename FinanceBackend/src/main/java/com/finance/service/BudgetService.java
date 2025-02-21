package com.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.exception.InvalidEntityException;
import com.finance.model.Budget;
import com.finance.model.UserInfo;
import com.finance.repository.BudgetRepository;
import com.finance.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Budget createBudget(Budget budget, String userId) throws InvalidEntityException{
        UserInfo user = userRepository.findById(userId).orElseThrow(() -> new InvalidEntityException("User not found with ID: " + userId));

        budget.setUser(user);  // Set the user before saving
        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsByUser(String userId) {
        return budgetRepository.findByUser_UserId(userId);
    }
    
    //get all budgets
    public List<Budget> getBudgets(){
    	return budgetRepository.findAll();
    }

    // Get budget by ID
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    // Get all budgets for a user
    public List<Budget> getBudgetsByUserId(String userId) {
        return budgetRepository.findByUser_UserId(userId);
    }

    // Update budget details
    /*public Budget updateBudget(Long id, Budget budgetDetails) {
    	Budget budget = budgetRepository.findById(id).orElseThrow(()-> new InvalidEntityException("User not found"));
    	budget.setCategory(budgetDetails.getCategory());
        budget.setLimitAmount(budgetDetails.getLimitAmount());
        budget.setSpentAmount(budgetDetails.getSpentAmount());
        if (budgetDetails.getUser() != null) {
            budget.setUser(budgetDetails.getUser());
        }
        return budgetRepository.save(budget);
    }*/
    
    public Budget updateBudget(Long id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new InvalidEntityException("Budget not found with ID: " + id));

        System.out.println("Updating Budget ID: " + id); // Debugging Log

        budget.setCategory(budgetDetails.getCategory() != null ? budgetDetails.getCategory() : budget.getCategory());
        budget.setLimitAmount(budgetDetails.getLimitAmount() != null ? budgetDetails.getLimitAmount() : budget.getLimitAmount());
        budget.setSpentAmount(budgetDetails.getSpentAmount() != null ? budgetDetails.getSpentAmount() : budget.getSpentAmount());

        return budgetRepository.save(budget);
    }


    // Delete budget
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
    
    
}
