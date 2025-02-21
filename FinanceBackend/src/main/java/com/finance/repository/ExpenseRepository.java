package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.model.Expense;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all expenses for a user
	List<Expense> findByUser_UserId(String userId);
    
}
