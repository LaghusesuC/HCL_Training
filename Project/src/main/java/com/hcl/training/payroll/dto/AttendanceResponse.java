package com.hcl.training.payroll.dto;

public record AttendanceResponse(
        Long attendanceId,
        Long employeeId,
        String employeeNumber,
        String employeeName,
        String departmentName,
        String payrollMonth,
        int totalWorkingDays,
        int payableDays,
        int lossOfPayDays
) {}
