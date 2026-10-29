package com.hcl.training.payroll.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDto(
        long totalEmployees,
        long activeEmployees,
        long totalDepartments,
        long totalPayrollRuns,
        long lockedRuns,
        BigDecimal lastMonthGross,
        BigDecimal lastMonthNet,
        BigDecimal ytdPayrollCost,
        String latestMonth,
        List<DepartmentReportDto> departmentBreakdown
) {}
