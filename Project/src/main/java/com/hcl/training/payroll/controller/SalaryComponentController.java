package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.SalaryComponentDto;
import com.hcl.training.payroll.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-components")
@Tag(name = "Salary Components", description = "Endpoints for defining earning and deduction salary components (FR1)")
public class SalaryComponentController {

    private final SalaryService salaryService;

    public SalaryComponentController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    @Operation(summary = "List all defined salary components")
    public ResponseEntity<List<SalaryComponentDto>> getAllComponents() {
        return ResponseEntity.ok(salaryService.getAllComponents());
    }

    @PostMapping
    @Operation(summary = "Define a new salary component")
    public ResponseEntity<SalaryComponentDto> createComponent(@Valid @RequestBody SalaryComponentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createComponent(dto));
    }
}
