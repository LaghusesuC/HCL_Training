package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.SalaryStructureDto;
import com.hcl.training.payroll.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-structures")
@Tag(name = "Salary Structures", description = "Endpoints for defining structured compensation packages (FR1)")
public class SalaryStructureController {

    private final SalaryService salaryService;

    public SalaryStructureController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    @Operation(summary = "List all salary structures")
    public ResponseEntity<List<SalaryStructureDto>> getAllStructures() {
        return ResponseEntity.ok(salaryService.getAllStructures());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get salary structure details with components")
    public ResponseEntity<SalaryStructureDto> getStructureById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.getStructureById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new salary structure with component definitions")
    public ResponseEntity<SalaryStructureDto> createStructure(@Valid @RequestBody SalaryStructureDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createStructure(dto));
    }
}
