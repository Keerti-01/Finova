package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.model.Learning;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {
    // Custom queries can be added if needed
	List<Learning> findByUser_UserId(String userId);
}
