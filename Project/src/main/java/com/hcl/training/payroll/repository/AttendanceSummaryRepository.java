package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.AttendanceSummary;
import com.hcl.training.payroll.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {
    Optional<AttendanceSummary> findByEmployeeAndPayrollMonth(Employee employee, String payrollMonth);
    Optional<AttendanceSummary> findByEmployeeEmployeeIdAndPayrollMonth(Long employeeId, String payrollMonth);
    List<AttendanceSummary> findByPayrollMonth(String payrollMonth);
}
