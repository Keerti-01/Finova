package com.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.model.Investment;
import com.finance.service.InvestmentService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    // Add a new investment
    @PostMapping("/{userId}")
    public ResponseEntity<Investment> addInvestment(@Valid @RequestBody Investment investment, @PathVariable String userId) {
        Investment newInvestment = investmentService.addInvestment(investment, userId);
        return ResponseEntity.ok(newInvestment);
    }

    // Get an investment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Investment> getInvestmentById(@PathVariable Long id) {
        Investment investment = investmentService.getInvestmentById(id);
        return ResponseEntity.ok(investment);
    }

    // Get all investments for a user by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Investment>> getInvestmentsByUserId(@PathVariable String userId) {
        List<Investment> investments = investmentService.getInvestmentsByUserId(userId);
        return ResponseEntity.ok(investments);
    }

    // Update investment details
    @PutMapping("/{id}")
    public ResponseEntity<Investment> updateInvestment(@PathVariable Long id,@Valid @RequestBody Investment investmentDetails) {
        Investment updatedInvestment = investmentService.updateInvestment(id, investmentDetails);
        return ResponseEntity.ok(updatedInvestment);
    }

    // Delete an investment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }
}
