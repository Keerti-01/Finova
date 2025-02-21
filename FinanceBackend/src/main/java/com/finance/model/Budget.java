package com.finance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Budget {
	
	@Override
	public String toString() {
		return "Budget (budgetId=" + budgetId + ", category=" + category + ", limitAmount=" + limitAmount
				+ ", spentAmount=" + spentAmount + ", user=" + user + ")";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long budgetId;
	
	@NotEmpty(message = "category cannot be null")
	private String category;
	
	@NotNull(message = "limit amount cannot be null")
	private Double limitAmount;
	
	@NotNull(message = "spent amount cannot be null")
	private Double spentAmount = 0.0;

	// Many budgets belong to one user
	@ManyToOne
	@JoinColumn(name = "userid", nullable = false)
	@JsonIgnoreProperties("budgets")
	private UserInfo user;
	
	// Getters and Setters
	public Long getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(Long budgetId) {
		this.budgetId = budgetId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {
		this.limitAmount = limitAmount;
	}

	public Double getSpentAmount() {
		return spentAmount;
	}

	public void setSpentAmount(Double spentAmount) {
		this.spentAmount = spentAmount;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}
}
