package com.hcl.training.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PayrollRunRequest(
        @NotBlank(message = "Payroll month is required")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Payroll month must be in YYYY-MM format")
        String payrollMonth
) {}
