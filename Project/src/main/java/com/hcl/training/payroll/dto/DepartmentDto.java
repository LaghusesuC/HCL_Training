package com.hcl.training.payroll.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentDto(
        Long departmentId,

        @NotBlank(message = "Department name is required")
        String departmentName,

        @NotBlank(message = "Department code is required")
        String departmentCode,

        Long employeeCount
) {
    public DepartmentDto(Long departmentId, String departmentName, String departmentCode) {
        this(departmentId, departmentName, departmentCode, 0L);
    }
}
