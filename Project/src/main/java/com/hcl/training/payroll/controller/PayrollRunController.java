package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.PayrollEntryResponse;
import com.hcl.training.payroll.dto.PayrollRunRequest;
import com.hcl.training.payroll.dto.PayrollRunResponse;
import com.hcl.training.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll-runs")
@Tag(name = "Payroll Runs", description = "Endpoints for monthly payroll calculations, entry reviews, and locking (FR3, FR4, FR5)")
public class PayrollRunController {

    private final PayrollService payrollService;

    public PayrollRunController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    @Operation(summary = "List all payroll runs")
    public ResponseEntity<List<PayrollRunResponse>> getAllPayrollRuns() {
        return ResponseEntity.ok(payrollService.getAllPayrollRuns());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payroll run by ID")
    public ResponseEntity<PayrollRunResponse> getPayrollRunById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollRunById(id));
    }

    @PostMapping
    @Operation(summary = "Execute or recalculate monthly payroll (FR3, FR4, NFR-P4)")
    public ResponseEntity<PayrollRunResponse> executePayrollRun(@Valid @RequestBody PayrollRunRequest request) {
        PayrollRunResponse response = payrollService.executePayrollRun(request.payrollMonth());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/entries")
    @Operation(summary = "View employee payroll entries for a payroll run")
    public ResponseEntity<List<PayrollEntryResponse>> getEntriesForRun(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getEntriesForRun(id));
    }

    @PostMapping("/{id}/lock")
    @Operation(summary = "Approve and lock a payroll run (FR5, FR7)")
    public ResponseEntity<PayrollRunResponse> lockPayrollRun(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "PayrollOfficer";
        return ResponseEntity.ok(payrollService.lockPayrollRun(id, username));
    }
}
