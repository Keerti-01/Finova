package com.finance.model;

import java.time.LocalDate;

public class Investment {
	private Long id;
	private String name; 
	private double amount;
	private double returnRate;   // Rate of return (for future calculation)
    private LocalDate startDate; // Start date of the investment
    private UserInfo user;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getReturnRate() {
		return returnRate;
	}
	public void setReturnRate(double returnRate) {
		this.returnRate = returnRate;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public UserInfo getUser() {
		return user;
	}
	public void setUser(UserInfo user) {
		this.user = user;
	}       
}
