package com.hcl.training.payroll.dto;

import java.time.LocalDate;

public record EmployeeResponse(
        Long employeeId,
        String employeeNumber,
        String fullName,
        String email,
        String designation,
        LocalDate dateOfJoining,
        String bankAccountNumber,
        String panNumber,
        Long departmentId,
        String departmentName,
        Long salaryStructureId,
        String salaryStructureName,
        boolean active
) {}
