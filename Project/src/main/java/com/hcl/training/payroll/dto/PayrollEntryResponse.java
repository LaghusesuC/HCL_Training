package com.hcl.training.payroll.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PayrollEntryResponse(
        Long entryId,
        Long runId,
        String payrollMonth,
        Long employeeId,
        String employeeNumber,
        String employeeName,
        String departmentName,
        String designation,
        String panNumber,
        String bankAccountNumber,
        int payableDays,
        int lossOfPayDays,
        BigDecimal lossOfPayAmount,
        BigDecimal grossSalary,
        BigDecimal totalDeductions,
        BigDecimal netSalary,
        Map<String, BigDecimal> deductionsBreakdown,
        Long payslipId,
        String payslipDocRef
) {}
