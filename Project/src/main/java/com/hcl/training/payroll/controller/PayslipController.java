package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.domain.entity.AppUser;
import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.Payslip;
import com.hcl.training.payroll.dto.PayrollEntryResponse;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.AppUserRepository;
import com.hcl.training.payroll.repository.EmployeeRepository;
import com.hcl.training.payroll.repository.PayslipRepository;
import com.hcl.training.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payslips")
@Tag(name = "Payslips", description = "Endpoints for viewing payslips and streaming OpenPDF documents (FR6)")
public class PayslipController {

    private final PayslipRepository payslipRepository;
    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollService payrollService;

    public PayslipController(PayslipRepository payslipRepository,
                             AppUserRepository appUserRepository,
                             EmployeeRepository employeeRepository,
                             PayrollService payrollService) {
        this.payslipRepository = payslipRepository;
        this.appUserRepository = appUserRepository;
        this.employeeRepository = employeeRepository;
        this.payrollService = payrollService;
    }

    @GetMapping("/me")
    @Operation(summary = "View payslips history for current authenticated employee (FR6)")
    public ResponseEntity<List<PayrollEntryResponse>> getMyPayslips(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        AppUser appUser = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authentication.getName()));

        Employee employee = appUser.getEmployee();
        if (employee == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Payslip> payslips = payslipRepository.findByEmployeeId(employee.getEmployeeId());
        List<PayrollEntryResponse> entries = payslips.stream()
                .map(p -> payrollService.mapEntryToResponse(p.getPayrollEntry()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(entries);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "View payslips history for a specific employee ID")
    public ResponseEntity<List<PayrollEntryResponse>> getEmployeePayslips(@PathVariable Long employeeId) {
        List<Payslip> payslips = payslipRepository.findByEmployeeId(employeeId);
        List<PayrollEntryResponse> entries = payslips.stream()
                .map(p -> payrollService.mapEntryToResponse(p.getPayrollEntry()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download or stream the official OpenPDF payslip file")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long id) {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with ID: " + id));

        byte[] pdfBytes = payslip.getPdfContent();
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new BadRequestException("Payslip PDF document content is missing.");
        }

        String filename = payslip.getDocumentReference() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }
}
