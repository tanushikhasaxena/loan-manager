package com.tanus.loan_management.controller;

import com.tanus.loan_management.entity.User;
import com.tanus.loan_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register-page")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register-form")
    public String handleRegistration(
            @ModelAttribute("user") User user,
            Model model
    ) {
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null ||
                user.getName().isBlank() || user.getEmail().isBlank() || user.getPassword().isBlank()) {
            model.addAttribute("error", "All fields are required!");
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        user.setRole(user.getRole() != null ? user.getRole() : "CUSTOMER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        model.addAttribute("success", "Account with TrustBank successfully created! Login to proceed.");
        return "register";
    }

    @GetMapping("/login-page")
    public String showLoginPage() {
        return "login";
    }
}
