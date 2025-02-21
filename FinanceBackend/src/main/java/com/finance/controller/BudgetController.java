package com.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.model.Budget;
import com.finance.service.BudgetService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*") // Allow frontend access
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/{userId}")
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget, @PathVariable String userId) {
        Budget savedBudget = budgetService.createBudget(budget, userId);
        return ResponseEntity.ok(savedBudget);
    }


    @GetMapping("/")
    public ResponseEntity<List<Budget>> getBudgets() {
        List<Budget> budgets = budgetService.getBudgets();
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUser(@PathVariable String userId) {
        List<Budget> budgets = budgetService.getBudgetsByUser(userId);
        return ResponseEntity.ok().body(budgets);
    }

    // Update budget details
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        try {
            return ResponseEntity.ok(budgetService.updateBudget(id, budgetDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete budget
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
    
    //get budget by id
    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long budgetId){
    	Optional<Budget> budget = budgetService.getBudgetById(budgetId);
    	return budget.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    	
    }
    
    
}
