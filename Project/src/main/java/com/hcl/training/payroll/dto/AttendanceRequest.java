package com.hcl.training.payroll.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotBlank(message = "Payroll month is required (YYYY-MM)")
        String payrollMonth,

        @Min(value = 1, message = "Total working days must be at least 1")
        @Max(value = 31, message = "Total working days cannot exceed 31")
        int totalWorkingDays,

        @Min(value = 0, message = "Payable days must be positive")
        @Max(value = 31, message = "Payable days cannot exceed 31")
        int payableDays,

        @Min(value = 0, message = "Loss of pay days must be positive")
        @Max(value = 31, message = "Loss of pay days cannot exceed 31")
        int lossOfPayDays
) {}
