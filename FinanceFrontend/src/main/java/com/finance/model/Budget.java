package com.finance.model;


public class Budget {
	
	private Long budgetId;
	
	private String category;

	private Double limitAmount;

	private Double spentAmount = 0.0;

	private UserInfo user;

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

	@Override
	public String toString() {
		return "Budget (budgetId=" + budgetId + ", category=" + category + ", limitAmount=" + limitAmount
				+ ", spentAmount=" + spentAmount + ", user=" + user + ")";
	}
	
}
