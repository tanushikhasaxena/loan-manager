package com.tanus.loan_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Eligibility score field
    @Column(name = "eligibility_score")
    private Double eligibilityScore;

    // Personal Information
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    // Employment & Income

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @Size(max = 100, message = "Employer name must be less than 100 characters")
    private String currentEmployer;

    @Size(max = 50, message = "Job title must be less than 50 characters")
    private String jobTitle;

    // ** Add @Column for DB mapping with snake_case **
    @Column(name = "years_in_current_job")
    @Min(value = 0, message = "Years in job cannot be negative")
    @Max(value = 50, message = "Years in job cannot exceed 50")
    private Integer yearsInCurrentJob;

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be positive")
    private Double monthlyIncome;

    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual income must be positive")
    private Double annualIncome;

    private Boolean hasExistingLoans = false;

    @Column(name = "cibil_score")
    @Min(value = 300, message = "CIBIL score must be at least 300")
    @Max(value = 900, message = "CIBIL score cannot exceed 900")
    private Integer cibilScore;

    @Size(min = 9, max = 18, message = "Account number must be 9-18 digits")
    private String bankAccountNumber;

    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
             message = "Invalid PAN format (e.g., ABCDE1234F)")
    private String panCardNumber;

    // Loan Details
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private Double loanAmount;

    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months (30 years)")
    private Integer loanTenureMonths;

    @Future(message = "EMI start date must be in the future")
    private LocalDate emiStartDate;

    // Co-Applicant
    @Size(max = 100, message = "Co-applicant name too long")
    private String coApplicantName;

    @Positive(message = "Co-applicant income must be positive")
    private Double coApplicantIncome;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id")
    private User officer;

    // Status & Dates
    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.PENDING;

    private LocalDate applicationDate = LocalDate.now();

    private LocalDate allocationDate;

    // Enums
    public enum EmploymentStatus {
        SALARIED, SELF_EMPLOYED, UNEMPLOYED
    }

    public enum LoanStatus {
        PENDING, UNDER_REVIEW, APPROVED, REJECTED, DISBURSED, CLOSED
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getEligibilityScore() { return eligibilityScore; }
    public void setEligibilityScore(Double eligibilityScore) { this.eligibilityScore = eligibilityScore; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getCurrentEmployer() { return currentEmployer; }
    public void setCurrentEmployer(String currentEmployer) { this.currentEmployer = currentEmployer; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Integer getYearsInCurrentJob() { return yearsInCurrentJob; }
    public void setYearsInCurrentJob(Integer yearsInCurrentJob) { this.yearsInCurrentJob = yearsInCurrentJob; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Double getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(Double annualIncome) { this.annualIncome = annualIncome; }

    public Boolean getHasExistingLoans() { return hasExistingLoans; }
    public void setHasExistingLoans(Boolean hasExistingLoans) { this.hasExistingLoans = hasExistingLoans; }

    public Integer getCibilScore() { return cibilScore; }
    public void setCibilScore(Integer cibilScore) { this.cibilScore = cibilScore; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getPanCardNumber() { return panCardNumber; }
    public void setPanCardNumber(String panCardNumber) { this.panCardNumber = panCardNumber; }

    public Double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(Double loanAmount) { this.loanAmount = loanAmount; }

    public String getLoanPurpose() { return loanPurpose; }
    public void setLoanPurpose(String loanPurpose) { this.loanPurpose = loanPurpose; }

    public Integer getLoanTenureMonths() { return loanTenureMonths; }
    public void setLoanTenureMonths(Integer loanTenureMonths) { this.loanTenureMonths = loanTenureMonths; }

    public LocalDate getEmiStartDate() { return emiStartDate; }
    public void setEmiStartDate(LocalDate emiStartDate) { this.emiStartDate = emiStartDate; }

    public String getCoApplicantName() { return coApplicantName; }
    public void setCoApplicantName(String coApplicantName) { this.coApplicantName = coApplicantName; }

    public Double getCoApplicantIncome() { return coApplicantIncome; }
    public void setCoApplicantIncome(Double coApplicantIncome) { this.coApplicantIncome = coApplicantIncome; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getOfficer() { return officer; }
    public void setOfficer(User officer) { this.officer = officer; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }
}
