package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.PayrollEntry;
import com.hcl.training.payroll.domain.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, Long> {
    List<PayrollEntry> findByPayrollRunRunId(Long runId);
    Optional<PayrollEntry> findByPayrollRunAndEmployee(PayrollRun payrollRun, Employee employee);
    List<PayrollEntry> findByEmployeeEmployeeId(Long employeeId);
}
