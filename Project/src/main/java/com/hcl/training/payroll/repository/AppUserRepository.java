package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.AppUser;
import com.hcl.training.payroll.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmployee(Employee employee);
    boolean existsByUsername(String username);
}
