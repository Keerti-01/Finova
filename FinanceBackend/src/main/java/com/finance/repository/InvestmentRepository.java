package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.model.Investment;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    // Find all investments by userId
    List<Investment> findByUser_UserId(String userId);

    // You can also define methods to fetch specific investments by userId and investment ID if needed
}
