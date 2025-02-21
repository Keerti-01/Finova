package com.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.exception.InvalidEntityException;
import com.finance.model.Investment;
import com.finance.model.UserInfo;
import com.finance.repository.InvestmentRepository;
import com.finance.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvestmentService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserRepository userInfoRepository;  // Injecting UserInfoRepository to fetch User

    // Add a new investment
    public Investment addInvestment(Investment investment, String userId) throws InvalidEntityException{
        UserInfo user = userInfoRepository.findById(userId).orElseThrow(() -> new InvalidEntityException("User not found"));
        investment.setStartDate(LocalDate.now());
        investment.setUser(user);
        return investmentRepository.save(investment);
    }

    // Get an investment by its ID
    public Investment getInvestmentById(Long id) throws InvalidEntityException{
        return investmentRepository.findById(id).orElseThrow(() -> new InvalidEntityException("Investment not found"));
    }

    // Get all investments for a user
    public List<Investment> getInvestmentsByUserId(String userId) {
        return investmentRepository.findByUser_UserId(userId);
    }

    // Update investment details
    public Investment updateInvestment(Long id, Investment investmentDetails) throws InvalidEntityException{
        Investment existingInvestment = investmentRepository.findById(id).orElseThrow(() -> new InvalidEntityException("Investment not found"));
        
        // Update fields
        if(investmentDetails.getName() !=null) existingInvestment.setName(investmentDetails.getName());
        if(investmentDetails.getAmount() > 0) existingInvestment.setAmount(investmentDetails.getAmount());
        if(investmentDetails.getReturnRate()>0) existingInvestment.setReturnRate(investmentDetails.getReturnRate());
        existingInvestment.setStartDate(LocalDate.now());

        return investmentRepository.save(existingInvestment);
    }

    // Delete an investment by its ID
    public void deleteInvestment(Long id) throws InvalidEntityException{
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvalidEntityException("Investment not found"));
        investmentRepository.delete(investment);
    }
}
