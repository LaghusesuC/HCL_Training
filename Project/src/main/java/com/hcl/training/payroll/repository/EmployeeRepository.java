package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentDepartmentId(Long departmentId);
    List<Employee> findByActiveTrue();
    boolean existsByEmployeeNumber(String employeeNumber);
    boolean existsByEmail(String email);
}
