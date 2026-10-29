package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.Department;
import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.PayrollEntry;
import com.hcl.training.payroll.domain.entity.PayrollRun;
import com.hcl.training.payroll.dto.DashboardStatsDto;
import com.hcl.training.payroll.dto.DepartmentReportDto;
import com.hcl.training.payroll.repository.DepartmentRepository;
import com.hcl.training.payroll.repository.EmployeeRepository;
import com.hcl.training.payroll.repository.PayrollEntryRepository;
import com.hcl.training.payroll.repository.PayrollRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final PayrollEntryRepository payrollEntryRepository;

    public ReportService(DepartmentRepository departmentRepository,
                         EmployeeRepository employeeRepository,
                         PayrollRunRepository payrollRunRepository,
                         PayrollEntryRepository payrollEntryRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.payrollEntryRepository = payrollEntryRepository;
    }

    /**
     * Aggregates department-wise payroll costs for a given month using Java Streams (Section 7).
     */
    public List<DepartmentReportDto> getDepartmentCosts(String payrollMonth) {
        Optional<PayrollRun> runOpt;
        if (payrollMonth != null && !payrollMonth.isBlank()) {
            runOpt = payrollRunRepository.findByPayrollMonth(payrollMonth);
        } else {
            // Find latest payroll run
            runOpt = payrollRunRepository.findAll().stream()
                    .max(Comparator.comparing(PayrollRun::getPayrollMonth));
        }

        if (runOpt.isEmpty()) {
            return Collections.emptyList();
        }

        PayrollRun run = runOpt.get();
        List<PayrollEntry> entries = payrollEntryRepository.findByPayrollRunRunId(run.getRunId());

        // Group entries by department using Java Streams
        Map<Department, List<PayrollEntry>> entriesByDept = entries.stream()
                .filter(e -> e.getEmployee() != null && e.getEmployee().getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getEmployee().getDepartment()));

        return entriesByDept.entrySet().stream()
                .map(entry -> {
                    Department dept = entry.getKey();
                    List<PayrollEntry> deptEntries = entry.getValue();

                    long headcount = deptEntries.size();

                    BigDecimal totalGross = deptEntries.stream()
                            .map(PayrollEntry::getGrossSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalLOP = deptEntries.stream()
                            .map(PayrollEntry::getLossOfPayAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalDeductions = deptEntries.stream()
                            .map(PayrollEntry::getTotalDeductions)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalNet = deptEntries.stream()
                            .map(PayrollEntry::getNetSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal avgNet = headcount > 0
                            ? totalNet.divide(BigDecimal.valueOf(headcount), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new DepartmentReportDto(
                            dept.getDepartmentId(),
                            dept.getDepartmentName(),
                            dept.getDepartmentCode(),
                            headcount,
                            totalGross,
                            totalLOP,
                            totalDeductions,
                            totalNet,
                            avgNet
                    );
                })
                .sorted(Comparator.comparing(DepartmentReportDto::totalNet).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Dashboard KPI summary across all entities and recent payroll runs.
     */
    public DashboardStatsDto getDashboardStats() {
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.findByActiveTrue().size();
        long totalDepartments = departmentRepository.count();
        List<PayrollRun> allRuns = payrollRunRepository.findAll();
        long totalRuns = allRuns.size();
        long lockedRuns = allRuns.stream().filter(PayrollRun::isLocked).count();

        Optional<PayrollRun> latestRun = allRuns.stream()
                .max(Comparator.comparing(PayrollRun::getPayrollMonth));

        String latestMonth = latestRun.map(PayrollRun::getPayrollMonth).orElse("N/A");
        BigDecimal lastMonthGross = latestRun.map(PayrollRun::getTotalGross).orElse(BigDecimal.ZERO);
        BigDecimal lastMonthNet = latestRun.map(PayrollRun::getTotalNet).orElse(BigDecimal.ZERO);

        BigDecimal ytdPayrollCost = allRuns.stream()
                .map(PayrollRun::getTotalNet)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DepartmentReportDto> deptBreakdown = getDepartmentCosts(latestMonth);

        return new DashboardStatsDto(
                totalEmployees,
                activeEmployees,
                totalDepartments,
                totalRuns,
                lockedRuns,
                lastMonthGross,
                lastMonthNet,
                ytdPayrollCost,
                latestMonth,
                deptBreakdown
        );
    }
}
