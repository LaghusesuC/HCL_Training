package com.hcl.training.payroll.dto;

import com.hcl.training.payroll.domain.entity.PayrollRun;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollRunResponse(
        Long runId,
        String payrollMonth,
        PayrollRun.Status status,
        BigDecimal totalGross,
        BigDecimal totalDeductions,
        BigDecimal totalNet,
        int employeeCount,
        LocalDateTime createdAt,
        LocalDateTime lockedAt,
        String lockedBy
) {}
