package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.PayrollEntry;
import com.hcl.training.payroll.domain.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    Optional<Payslip> findByDocumentReference(String documentReference);
    Optional<Payslip> findByPayrollEntry(PayrollEntry payrollEntry);

    @Query("SELECT p FROM Payslip p WHERE p.payrollEntry.employee.employeeId = :employeeId ORDER BY p.generatedAt DESC")
    List<Payslip> findByEmployeeId(@Param("employeeId") Long employeeId);
}
