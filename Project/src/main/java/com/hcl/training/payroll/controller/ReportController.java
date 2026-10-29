package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.DashboardStatsDto;
import com.hcl.training.payroll.dto.DepartmentReportDto;
import com.hcl.training.payroll.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Endpoints for department payroll cost summaries and dashboard analytics (FR8)")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/department-costs")
    @Operation(summary = "Get department-wise payroll cost summaries for a month (FR8)")
    public ResponseEntity<List<DepartmentReportDto>> getDepartmentCosts(
            @RequestParam(required = false) String month) {
        return ResponseEntity.ok(reportService.getDepartmentCosts(month));
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get overall dashboard summary KPIs and recent statistics")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }
}
