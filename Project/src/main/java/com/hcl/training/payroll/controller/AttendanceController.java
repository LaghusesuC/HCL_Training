package com.hcl.training.payroll.controller;

import com.hcl.training.payroll.dto.AttendanceRequest;
import com.hcl.training.payroll.dto.AttendanceResponse;
import com.hcl.training.payroll.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "Attendance", description = "Endpoints for employee monthly attendance and loss of pay records")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    @Operation(summary = "Get attendance summaries for a month (YYYY-MM)")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(@RequestParam String month) {
        return ResponseEntity.ok(attendanceService.getAttendanceByMonth(month));
    }

    @PostMapping
    @Operation(summary = "Record or update employee attendance for a month")
    public ResponseEntity<AttendanceResponse> recordAttendance(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.recordAttendance(request));
    }

    @PostMapping("/generate-default")
    @Operation(summary = "Generate default attendance records (30 payable days) for all active employees for a month")
    public ResponseEntity<Map<String, Object>> generateDefaultAttendance(@RequestParam String month) {
        int count = attendanceService.generateDefaultAttendanceForMonth(month);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "payrollMonth", month,
                "recordsGenerated", count
        ));
    }
}
