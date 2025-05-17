package com.tanus.loan_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tanus.loan_management.entity.User;

import java.util.List;      // <-- Add this import!
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    
}
