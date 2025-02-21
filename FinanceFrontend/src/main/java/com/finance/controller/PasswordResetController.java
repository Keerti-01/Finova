package com.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.finance.dto.CodeVerificationRequest;
import com.finance.model.UserInfo;

@Controller
@RequestMapping("/password")
public class PasswordResetController {
	
	@Autowired
	public RestTemplate restTemplate() {
		return new RestTemplate();
	};

    private final String backendUrl = "http://localhost:8081/password"; // Update this based on your backend URL

    @GetMapping("/forgot")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("otpRequest", new CodeVerificationRequest());
        return "forgotPassword"; // Renders forgotPassword.html
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            String response = restTemplate().postForObject(backendUrl + "/forgot?email=" + email, null, String.class);
            CodeVerificationRequest otpRequest = new CodeVerificationRequest();
            otpRequest.setEmail(email); // ✅ Set email in DTO
            
            model.addAttribute("message", response); 
            model.addAttribute("otpRequest", otpRequest);
            return "verifyCode"; // Redirect to the verification code page
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "User not found or email invalid.");
            return "forgotPassword";
        }
    }

    @GetMapping("/verify")
    public String showVerifyCodeForm(@RequestParam String email,Model model) {
    	CodeVerificationRequest otpRequest = new CodeVerificationRequest();
        otpRequest.setEmail(email); // ✅ Set email in DTO
        
        model.addAttribute("otpRequest", otpRequest);
        model.addAttribute("email",email);
        return "verifyCode"; // Renders verifyCode.html
    }

    @PostMapping("/verify")
    public String processVerification(@RequestParam String email, @RequestParam String code, Model model) {
        try {
            String response = restTemplate().postForObject(backendUrl + "/verify?email=" + email + "&code=" + code, null, String.class);
            CodeVerificationRequest otpRequest = new CodeVerificationRequest();
            otpRequest.setEmail(email); 
            otpRequest.setCode(code);
            
            model.addAttribute("message", response);
            model.addAttribute("otpRequest", otpRequest);
            return "resetPassword"; // Redirect to reset password page
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Invalid or expired verification code.");
            return "verifyCode";
        }
    }

    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String email,Model model) {
    	CodeVerificationRequest otpRequest = new CodeVerificationRequest();
        otpRequest.setEmail(email); 
        model.addAttribute("otpRequest", otpRequest);
        return "resetPassword"; // Renders resetPassword.html
    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String email, @RequestParam String newPassword, Model model) {
        try {
            String response = restTemplate().postForObject(backendUrl + "/reset?email=" + email + "&newPassword=" + newPassword, null, String.class);
            model.addAttribute("message", response);
            model.addAttribute("user",new UserInfo());
            return "login"; // Redirect to login page
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Error resetting password. Please try again.");
            return "resetPassword";
        }
    }

}
