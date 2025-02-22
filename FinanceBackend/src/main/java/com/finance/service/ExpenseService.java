package com.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.exception.InvalidEntityException;
import com.finance.model.Expense;
import com.finance.model.UserInfo;
import com.finance.repository.ExpenseRepository;
import com.finance.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userInfoRepository; 

    // Add a new expense
    public Expense addExpense(Expense expense, String userId) {
    	UserInfo user = userInfoRepository.findById(userId).orElseThrow(() -> new InvalidEntityException("User not found"));
    	expense.setDate(LocalDate.now());
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    // Get expense by ID
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // Get all expenses for a user
    public List<Expense> getExpensesByUserId(String userId) {
        return expenseRepository.findByUser_UserId(userId);
    }

    // Update expense details
    public Expense updateExpense(Long id, Expense expenseDetails) {
    	Expense expense = expenseRepository.findById(id).orElseThrow(()-> new InvalidEntityException("User not found"));
    	expense.setDate(LocalDate.now());
    	if(expenseDetails.getCategory()!=null) expense.setCategory(expenseDetails.getCategory());
        if(expenseDetails.getAmount() > 0) expense.setAmount(expenseDetails.getAmount());
        return expenseRepository.save(expense);
    }

    // Delete expense by ID
    public boolean deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false; // Or throw an exception
    }
}
