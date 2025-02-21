package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.model.UserInfo;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String>{

	UserInfo findByEmail(String email);
}
