package com.tanus.loan_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tanus.loan_management.entity.Loan;
import com.tanus.loan_management.entity.User;
import com.tanus.loan_management.repository.LoanRepository;
import com.tanus.loan_management.repository.UserRepository;
import com.tanus.loan_management.service.LoanEligibilityService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanEligibilityService eligibilityService;

    // Unified dashboard handler
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("loans", loanRepository.findByUser(user));
        } else if ("OFFICER".equalsIgnoreCase(user.getRole())) {
            List<Loan> loans = loanRepository.findByOfficer(user);
            model.addAttribute("loans", loans);
        } else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("loans", loanRepository.findAll());
            model.addAttribute("officers", userRepository.findByRole("OFFICER"));
        }
        return "dashboard";
    }

    @GetMapping("/apply-page")
    public String showApplyPage(@AuthenticationPrincipal User user, Model model) {
        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/loans/dashboard";
        }
        model.addAttribute("loanForm", new Loan());
        return "apply-loan";
    }

    @PostMapping("/apply")
    public String applyLoan(
        @AuthenticationPrincipal User user,
        @Valid @ModelAttribute("loanForm") Loan loan,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "apply-loan";
        }
        
        
        loan.setUser(user);
        loan.setApplicationDate(LocalDate.now());
        loan.setStatus(Loan.LoanStatus.PENDING);
        
        // Calculate eligibility score
        double score = eligibilityService.calculateEligibilityScore(loan);
        loan.setEligibilityScore(score);
        
        loanRepository.save(loan);
        return "redirect:/loans/dashboard";
    }

    @PostMapping("/withdraw/{loanId}")
    public String withdrawLoan(
        @PathVariable Long loanId,
        @AuthenticationPrincipal User user,
        RedirectAttributes redirectAttributes
    ) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            if (loan.getUser().getId().equals(user.getId())) {
                loanRepository.delete(loan);
                redirectAttributes.addFlashAttribute("success", "Loan application withdrawn successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Loan not found");
        }
        return "redirect:/loans/dashboard";
    }

    @PostMapping("/allocate/{loanId}/{officerId}")
    public String allocateLoan(
        @PathVariable Long loanId,
        @PathVariable Long officerId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
            User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new IllegalArgumentException("Officer not found"));

            // Calculate eligibility score
            double score = eligibilityService.calculateEligibilityScore(loan);
            loan.setEligibilityScore(score);
            
            loan.setOfficer(officer);
            loan.setStatus(Loan.LoanStatus.UNDER_REVIEW);
            loan.setAllocationDate(LocalDate.now());
            loanRepository.save(loan);
            
            redirectAttributes.addFlashAttribute("success", "Loan allocated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/loans/dashboard";
    }

    @PostMapping("/decide/{loanId}")
    public ResponseEntity<String> decideLoan(
        @PathVariable Long loanId,
        @RequestParam("decision") String decision,
        @AuthenticationPrincipal User officer) {
        
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
        }
        
        Loan loan = optionalLoan.get();
        if (!loan.getOfficer().getId().equals(officer.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        if ("APPROVE".equalsIgnoreCase(decision)) {
            loan.setStatus(Loan.LoanStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            loan.setStatus(Loan.LoanStatus.REJECTED);
        } else {
            return ResponseEntity.badRequest().body("Invalid decision");
        }
        
        loanRepository.save(loan);
        return ResponseEntity.ok("Loan " + decision.toLowerCase());
    }
}
