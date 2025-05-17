package com.tanus.loan_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tanus.loan_management.entity.Loan;
import com.tanus.loan_management.entity.User;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    List<Loan> findByOfficer(User officer);
}
