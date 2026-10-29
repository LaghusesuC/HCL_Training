package com.hcl.training.payroll.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role,
        Long employeeId,
        String fullName
) {
    public AuthResponse(String token, String username, String role, Long employeeId, String fullName) {
        this(token, "Bearer", username, role, employeeId, fullName);
    }
}
