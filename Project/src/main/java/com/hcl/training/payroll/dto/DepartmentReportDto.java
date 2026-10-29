package com.hcl.training.payroll.dto;

import java.math.BigDecimal;

public record DepartmentReportDto(
        Long departmentId,
        String departmentName,
        String departmentCode,
        long employeeCount,
        BigDecimal totalGross,
        BigDecimal totalLossOfPay,
        BigDecimal totalDeductions,
        BigDecimal totalNet,
        BigDecimal averageNetSalary
) {}
