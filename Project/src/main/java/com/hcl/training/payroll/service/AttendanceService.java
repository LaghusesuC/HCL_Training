package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.AttendanceSummary;
import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.dto.AttendanceRequest;
import com.hcl.training.payroll.dto.AttendanceResponse;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.AttendanceSummaryRepository;
import com.hcl.training.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceSummaryRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceSummaryRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByMonth(String payrollMonth) {
        return attendanceRepository.findByPayrollMonth(payrollMonth).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.employeeId()));

        if (request.payableDays() + request.lossOfPayDays() > request.totalWorkingDays()) {
            throw new BadRequestException("Payable days + LOP days (" + (request.payableDays() + request.lossOfPayDays())
                    + ") cannot exceed total working days (" + request.totalWorkingDays() + ")");
        }

        Optional<AttendanceSummary> existing = attendanceRepository.findByEmployeeAndPayrollMonth(employee, request.payrollMonth());
        AttendanceSummary summary;
        if (existing.isPresent()) {
            summary = existing.get();
            summary.setTotalWorkingDays(request.totalWorkingDays());
            summary.setPayableDays(request.payableDays());
            summary.setLossOfPayDays(request.lossOfPayDays());
        } else {
            summary = new AttendanceSummary(
                    employee,
                    request.payrollMonth(),
                    request.totalWorkingDays(),
                    request.payableDays(),
                    request.lossOfPayDays()
            );
        }

        return mapToResponse(attendanceRepository.save(summary));
    }

    /**
     * Helper to populate standard default attendance (30 days total, 30 payable, 0 LOP)
     * for all active employees for the month if not already present.
     */
    public int generateDefaultAttendanceForMonth(String payrollMonth) {
        List<Employee> activeEmployees = employeeRepository.findByActiveTrue();
        int createdCount = 0;
        for (Employee emp : activeEmployees) {
            Optional<AttendanceSummary> existing = attendanceRepository.findByEmployeeAndPayrollMonth(emp, payrollMonth);
            if (existing.isEmpty()) {
                AttendanceSummary summary = new AttendanceSummary(emp, payrollMonth, 30, 30, 0);
                attendanceRepository.save(summary);
                createdCount++;
            }
        }
        return createdCount;
    }

    private AttendanceResponse mapToResponse(AttendanceSummary a) {
        return new AttendanceResponse(
                a.getAttendanceId(),
                a.getEmployee().getEmployeeId(),
                a.getEmployee().getEmployeeNumber(),
                a.getEmployee().getFullName(),
                a.getEmployee().getDepartment() != null ? a.getEmployee().getDepartment().getDepartmentName() : "N/A",
                a.getPayrollMonth(),
                a.getTotalWorkingDays(),
                a.getPayableDays(),
                a.getLossOfPayDays()
        );
    }
}
