package com.hcl.training.payroll.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "Employee number is required")
        String employeeNumber,

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String designation,

        LocalDate dateOfJoining,

        String bankAccountNumber,

        String panNumber,

        @NotNull(message = "Department ID is required")
        Long departmentId,

        Long salaryStructureId,

        Boolean active
) {}
