package com.finance.model;

import java.util.List;

public class UserInfo {
	private String userId;
    private String name;
    private String email;
    private String password;
    private long income;
    private String goals;
    private List<Budget> budgets;
    private List<Expense> expenses; // One user can have many expenses
    private List<Investment> investments; // One user can have many investments
    private List<Learning> learning;

    public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}

	public List<Investment> getInvestments() {
		return investments;
	}

	public void setInvestments(List<Investment> investments) {
		this.investments = investments;
	}

	public List<Budget> getBudgets() {
		return budgets;
	}

	public void setBudgets(List<Budget> budgets) {
		this.budgets = budgets;
	}

	// Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getIncome() {
        return income;
    }

    public List<Learning> getLearning() {
		return learning;
	}

	public void setLearning(List<Learning> learning) {
		this.learning = learning;
	}

	public void setIncome(long income) {
        this.income = income;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }
}
