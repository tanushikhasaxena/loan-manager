package com.tanus.loan_management.service;

import com.tanus.loan_management.entity.Loan;
import org.springframework.stereotype.Service;

@Service
public class LoanEligibilityService {

    public double calculateEligibilityScore(Loan loan) {
        double score = 0.0;

        // CIBIL Score (30%)
        if (loan.getCibilScore() != null) {
            score += 0.3 * getCibilScorePercent(loan.getCibilScore());
        } else {
            score += 0.3 * 0.3; // Fallback to worst-case
        }

        // Annual Income vs Loan Amount (25%)
        double incomeRatio = 0.0;
        if (loan.getAnnualIncome() != null && loan.getLoanAmount() != null && loan.getLoanAmount() > 0) {
            incomeRatio = loan.getAnnualIncome() / loan.getLoanAmount();
            score += 0.25 * getIncomePercent(incomeRatio);
        }

        // Job Tenure (15%)
        score += 0.15 * getJobTenurePercent(loan.getYearsInCurrentJob());

        // Existing Loans (10%)
        Boolean hasLoans = loan.getHasExistingLoans();
        if (hasLoans != null && hasLoans) {
            score += 0.1 * 0.4;
        } else {
            score += 0.1 * 1.0;
        }

        // Loan Amount weight (10%)
        score += 0.1 * getLoanAmountPercent(incomeRatio);

        // Co-Applicant (10%)
        score += 0.1 * (loan.getCoApplicantIncome() != null ? 0.7 : 0.4);

        // Cap the score between 0.0 and 1.0
        score = Math.max(0.0, Math.min(1.0, score));
        return Math.round(score * 100.0) / 100.0;
    }

    private double getCibilScorePercent(int cibilScore) {
        if (cibilScore >= 750) return 1.0;
        if (cibilScore >= 700) return 0.8;
        if (cibilScore >= 650) return 0.6;
        return 0.3;
    }

    private double getIncomePercent(double incomeRatio) {
        if (incomeRatio >= 2) return 1.0;
        if (incomeRatio >= 1.5) return 0.8;
        return 0.5;
    }

    private double getJobTenurePercent(Integer years) {
        if (years == null) return 0.4;
        if (years > 3) return 1.0;
        if (years >= 1) return 0.7;
        return 0.4;
    }

    private double getLoanAmountPercent(double incomeRatio) {
        if (incomeRatio >= 2) return 1.0;
        if (incomeRatio >= 1.33) return 0.8;
        return 0.5;
    }
}
