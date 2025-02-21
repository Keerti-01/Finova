package com.finance.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.finance.*")
public class FinanceFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceFrontendApplication.class, args);
	}

}
