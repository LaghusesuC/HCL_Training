package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.EmployeeRequest;
import com.hcl.training.payroll.dto.EmployeeResponse;
import com.hcl.training.payroll.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Endpoints for employee directory and salary structure assignments")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "List employees with optional department and active status filter")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(employeeService.getAllEmployees(departmentId, activeOnly));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee details by ID")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee information")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PutMapping("/{id}/salary-structure")
    @Operation(summary = "Assign a salary structure to an employee (FR2)")
    public ResponseEntity<EmployeeResponse> assignSalaryStructure(
            @PathVariable Long id,
            @RequestBody Map<String, Long> payload) {
        Long structureId = payload.get("structureId");
        if (structureId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employeeService.assignSalaryStructure(id, structureId));
    }
}
