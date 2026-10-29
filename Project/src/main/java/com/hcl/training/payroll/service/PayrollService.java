package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.*;
import com.hcl.training.payroll.domain.model.Money;
import com.hcl.training.payroll.dto.PayrollEntryResponse;
import com.hcl.training.payroll.dto.PayrollRunResponse;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.PayrollLockedException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.*;
import com.hcl.training.payroll.service.calc.ComponentCalculatorFactory;
import com.hcl.training.payroll.service.calc.SalaryComponentCalculator;
import com.hcl.training.payroll.service.calc.TaxCalculationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayrollEntryRepository payrollEntryRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceSummaryRepository attendanceRepository;
    private final PayslipRepository payslipRepository;
    private final ComponentCalculatorFactory calculatorFactory;
    private final TaxCalculationStrategy taxCalculationStrategy;
    private final PayslipPdfService payslipPdfService;

    public PayrollService(PayrollRunRepository payrollRunRepository,
                          PayrollEntryRepository payrollEntryRepository,
                          EmployeeRepository employeeRepository,
                          AttendanceSummaryRepository attendanceRepository,
                          PayslipRepository payslipRepository,
                          ComponentCalculatorFactory calculatorFactory,
                          TaxCalculationStrategy taxCalculationStrategy,
                          PayslipPdfService payslipPdfService) {
        this.payrollRunRepository = payrollRunRepository;
        this.payrollEntryRepository = payrollEntryRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.payslipRepository = payslipRepository;
        this.calculatorFactory = calculatorFactory;
        this.taxCalculationStrategy = taxCalculationStrategy;
        this.payslipPdfService = payslipPdfService;
    }

    @Transactional(readOnly = true)
    public List<PayrollRunResponse> getAllPayrollRuns() {
        return payrollRunRepository.findAll().stream()
                .sorted(Comparator.comparing(PayrollRun::getPayrollMonth).reversed())
                .map(this::mapRunToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayrollRunResponse getPayrollRunById(Long runId) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found with ID: " + runId));
        return mapRunToResponse(run);
    }

    /**
     * Executes or recalculates payroll for a given month (conforms to NFR-P4 Idempotency).
     */
    public PayrollRunResponse executePayrollRun(String payrollMonth) {
        Optional<PayrollRun> existingRunOpt = payrollRunRepository.findByPayrollMonth(payrollMonth);
        PayrollRun payrollRun;

        if (existingRunOpt.isPresent()) {
            payrollRun = existingRunOpt.get();
            if (payrollRun.isLocked()) {
                throw new PayrollLockedException("Payroll run for month " + payrollMonth + " is LOCKED and cannot be recalculated (FR7).");
            }
            // Clear existing entries to prevent duplication (NFR-P4)
            List<PayrollEntry> existingEntries = payrollEntryRepository.findByPayrollRunRunId(payrollRun.getRunId());
            for (PayrollEntry entry : existingEntries) {
                payslipRepository.findByPayrollEntry(entry).ifPresent(payslipRepository::delete);
            }
            payrollEntryRepository.deleteAll(existingEntries);
            payrollRun.getEntries().clear();
        } else {
            payrollRun = new PayrollRun(payrollMonth);
            payrollRun = payrollRunRepository.save(payrollRun);
        }

        List<Employee> activeEmployees = employeeRepository.findByActiveTrue();
        if (activeEmployees.isEmpty()) {
            throw new BadRequestException("No active employees found to process payroll.");
        }

        Money totalRunGross = Money.ZERO;
        Money totalRunDeductions = Money.ZERO;
        Money totalRunNet = Money.ZERO;

        for (Employee employee : activeEmployees) {
            SalaryStructure structure = employee.getSalaryStructure();
            if (structure == null || structure.getComponents().isEmpty()) {
                // Skip or handle employees without salary structure assigned
                continue;
            }

            // 1. Attendance retrieval (default 30 days if not explicitly entered)
            AttendanceSummary attendance = attendanceRepository.findByEmployeeAndPayrollMonth(employee, payrollMonth)
                    .orElse(new AttendanceSummary(employee, payrollMonth, 30, 30, 0));

            int totalWorkingDays = Math.max(1, attendance.getTotalWorkingDays());
            int payableDays = attendance.getPayableDays();
            int lossOfPayDays = attendance.getLossOfPayDays();

            // 2. Identify Basic Pay Component
            Money basicPay = Money.ZERO;
            for (StructureComponent sc : structure.getComponents()) {
                if ("BASIC".equalsIgnoreCase(sc.getSalaryComponent().getComponentCode())) {
                    basicPay = Money.of(sc.getAmountOrRate());
                    break;
                }
            }

            // If basic wasn't explicitly coded as BASIC, pick the first earning component
            if (basicPay.isZero()) {
                for (StructureComponent sc : structure.getComponents()) {
                    if (sc.getSalaryComponent().getComponentType() == SalaryComponent.Type.EARNING) {
                        basicPay = Money.of(sc.getAmountOrRate());
                        break;
                    }
                }
            }

            // 3. Calculate Earnings
            Money grossSalary = Money.ZERO;
            for (StructureComponent sc : structure.getComponents()) {
                if (sc.getSalaryComponent().getComponentType() == SalaryComponent.Type.EARNING) {
                    SalaryComponentCalculator calc = calculatorFactory.getCalculator(sc.getSalaryComponent().getCalculationMode());
                    Money componentAmt = calc.calculate(sc.getAmountOrRate(), basicPay);
                    grossSalary = grossSalary.add(componentAmt);
                }
            }

            // 4. Loss of Pay Calculation
            Money perDayRate = basicPay.divide(BigDecimal.valueOf(totalWorkingDays));
            Money lossOfPayAmount = perDayRate.multiply(BigDecimal.valueOf(lossOfPayDays));

            // 5. Calculate Deductions
            // Statutory PF: 12% of basic
            Money pfAmount = basicPay.percentage(12.0);

            // Statutory Professional Tax (PT): flat 200 standard
            Money ptAmount = Money.of(200.00);

            // TDS via Strategy Pattern
            Money tdsAmount = taxCalculationStrategy.calculateTds(grossSalary);

            // Custom structure deductions (e.g. Health Insurance)
            Map<String, Money> customDeductions = new HashMap<>();
            for (StructureComponent sc : structure.getComponents()) {
                if (sc.getSalaryComponent().getComponentType() == SalaryComponent.Type.DEDUCTION) {
                    SalaryComponentCalculator calc = calculatorFactory.getCalculator(sc.getSalaryComponent().getCalculationMode());
                    Money componentAmt = calc.calculate(sc.getAmountOrRate(), basicPay);
                    customDeductions.put(sc.getSalaryComponent().getComponentName(), componentAmt);
                }
            }

            Money totalDeductions = pfAmount.add(ptAmount).add(tdsAmount);
            for (Money cDed : customDeductions.values()) {
                totalDeductions = totalDeductions.add(cDed);
            }

            // 6. Net Salary = Gross - Total Deductions - LOP
            Money netSalary = grossSalary.subtract(totalDeductions).subtract(lossOfPayAmount);
            if (netSalary.compareTo(Money.ZERO) < 0) {
                netSalary = Money.ZERO;
            }

            // 7. Persist Payroll Entry
            PayrollEntry entry = new PayrollEntry(
                    payrollRun,
                    employee,
                    payableDays,
                    lossOfPayDays,
                    lossOfPayAmount.amount(),
                    grossSalary.amount(),
                    totalDeductions.amount(),
                    netSalary.amount()
            );

            entry.addDeduction("Provident Fund (PF 12%)", pfAmount.amount());
            entry.addDeduction("Professional Tax (PT)", ptAmount.amount());
            if (tdsAmount.isPositive()) {
                entry.addDeduction("Tax Deducted at Source (TDS)", tdsAmount.amount());
            }
            for (Map.Entry<String, Money> cd : customDeductions.entrySet()) {
                entry.addDeduction(cd.getKey(), cd.getValue().amount());
            }

            PayrollEntry savedEntry = payrollEntryRepository.save(entry);
            payrollRun.addEntry(savedEntry);

            // 8. Generate and persist OpenPDF Payslip
            String docRef = "PAY-" + payrollMonth.replace("-", "") + "-" + employee.getEmployeeNumber();
            byte[] pdfBytes = payslipPdfService.generatePayslipPdf(savedEntry, docRef);
            Payslip payslip = new Payslip(savedEntry, docRef, pdfBytes);
            payslipRepository.save(payslip);

            totalRunGross = totalRunGross.add(grossSalary);
            totalRunDeductions = totalRunDeductions.add(totalDeductions.add(lossOfPayAmount));
            totalRunNet = totalRunNet.add(netSalary);
        }

        payrollRun.setTotalGross(totalRunGross.amount());
        payrollRun.setTotalDeductions(totalRunDeductions.amount());
        payrollRun.setTotalNet(totalRunNet.amount());

        PayrollRun savedRun = payrollRunRepository.save(payrollRun);
        return mapRunToResponse(savedRun);
    }

    /**
     * Locks a payroll run to prevent further modifications (FR5, FR7).
     */
    public PayrollRunResponse lockPayrollRun(Long runId, String username) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found with ID: " + runId));

        if (run.isLocked()) {
            throw new PayrollLockedException("Payroll run for month " + run.getPayrollMonth() + " is already locked!");
        }

        run.lock(username != null ? username : "System");
        PayrollRun saved = payrollRunRepository.save(run);
        return mapRunToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PayrollEntryResponse> getEntriesForRun(Long runId) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found with ID: " + runId));

        return payrollEntryRepository.findByPayrollRunRunId(run.getRunId()).stream()
                .map(this::mapEntryToResponse)
                .collect(Collectors.toList());
    }

    private PayrollRunResponse mapRunToResponse(PayrollRun run) {
        int empCount = run.getEntries() != null ? run.getEntries().size() :
                payrollEntryRepository.findByPayrollRunRunId(run.getRunId()).size();

        return new PayrollRunResponse(
                run.getRunId(),
                run.getPayrollMonth(),
                run.getStatus(),
                run.getTotalGross(),
                run.getTotalDeductions(),
                run.getTotalNet(),
                empCount,
                run.getCreatedAt(),
                run.getLockedAt(),
                run.getLockedBy()
        );
    }

    public PayrollEntryResponse mapEntryToResponse(PayrollEntry e) {
        Map<String, BigDecimal> deductionsMap = new LinkedHashMap<>();
        if (e.getDeductions() != null) {
            for (PayrollDeduction d : e.getDeductions()) {
                deductionsMap.put(d.getDeductionType(), d.getAmount());
            }
        }

        Optional<Payslip> payslipOpt = payslipRepository.findByPayrollEntry(e);
        Long payslipId = payslipOpt.map(Payslip::getPayslipId).orElse(null);
        String docRef = payslipOpt.map(Payslip::getDocumentReference).orElse(null);

        Employee emp = e.getEmployee();

        return new PayrollEntryResponse(
                e.getEntryId(),
                e.getPayrollRun() != null ? e.getPayrollRun().getRunId() : null,
                e.getPayrollRun() != null ? e.getPayrollRun().getPayrollMonth() : null,
                emp.getEmployeeId(),
                emp.getEmployeeNumber(),
                emp.getFullName(),
                emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "N/A",
                emp.getDesignation(),
                emp.getPanNumber(),
                emp.getBankAccountNumber(),
                e.getPayableDays(),
                e.getLossOfPayDays(),
                e.getLossOfPayAmount(),
                e.getGrossSalary(),
                e.getTotalDeductions(),
                e.getNetSalary(),
                deductionsMap,
                payslipId,
                docRef
        );
    }
}
