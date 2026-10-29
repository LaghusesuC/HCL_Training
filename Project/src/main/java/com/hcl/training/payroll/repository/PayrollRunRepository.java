package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    Optional<PayrollRun> findByPayrollMonth(String payrollMonth);
    boolean existsByPayrollMonth(String payrollMonth);
}
