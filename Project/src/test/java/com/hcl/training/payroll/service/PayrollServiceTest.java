package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.*;
import com.hcl.training.payroll.domain.model.Money;
import com.hcl.training.payroll.dto.PayrollRunResponse;
import com.hcl.training.payroll.exception.PayrollLockedException;
import com.hcl.training.payroll.repository.*;
import com.hcl.training.payroll.service.calc.ComponentCalculatorFactory;
import com.hcl.training.payroll.service.calc.FixedComponentCalculator;
import com.hcl.training.payroll.service.calc.PercentageComponentCalculator;
import com.hcl.training.payroll.service.calc.SimplifiedTaxCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRunRepository payrollRunRepository;
    @Mock
    private PayrollEntryRepository payrollEntryRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AttendanceSummaryRepository attendanceRepository;
    @Mock
    private PayslipRepository payslipRepository;
    @Mock
    private PayslipPdfService payslipPdfService;

    private PayrollService payrollService;

    private Employee testEmployee;
    private SalaryStructure testStructure;

    @BeforeEach
    void setUp() {
        ComponentCalculatorFactory calculatorFactory = new ComponentCalculatorFactory(
                new FixedComponentCalculator(),
                new PercentageComponentCalculator()
        );
        SimplifiedTaxCalculationStrategy taxStrategy = new SimplifiedTaxCalculationStrategy();

        payrollService = new PayrollService(
                payrollRunRepository,
                payrollEntryRepository,
                employeeRepository,
                attendanceRepository,
                payslipRepository,
                calculatorFactory,
                taxStrategy,
                payslipPdfService
        );

        Department dept = new Department("Engineering", "ENG");
        testStructure = new SalaryStructure("Standard Band", "Standard");

        SalaryComponent basicComp = new SalaryComponent("BASIC", "Basic", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(50000));
        SalaryComponent hraComp = new SalaryComponent("HRA", "HRA", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.PERCENTAGE_OF_BASIC, BigDecimal.valueOf(40));

        testStructure.addComponent(basicComp, BigDecimal.valueOf(50000));
        testStructure.addComponent(hraComp, BigDecimal.valueOf(40));

        testEmployee = new Employee("EMP100", "John Doe", "john@example.com", "Dev", dept, testStructure);
    }

    @Test
    @DisplayName("Should execute payroll run and calculate gross, deductions, and net correctly")
    void testExecutePayrollRun() {
        String month = "2026-11";
        when(payrollRunRepository.findByPayrollMonth(month)).thenReturn(Optional.empty());
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(testEmployee));
        when(attendanceRepository.findByEmployeeAndPayrollMonth(testEmployee, month))
                .thenReturn(Optional.of(new AttendanceSummary(testEmployee, month, 30, 30, 0)));

        when(payrollRunRepository.save(any(PayrollRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(payrollEntryRepository.save(any(PayrollEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(payslipPdfService.generatePayslipPdf(any(), any())).thenReturn(new byte[]{1, 2, 3});

        PayrollRunResponse response = payrollService.executePayrollRun(month);

        assertNotNull(response);
        assertEquals(month, response.payrollMonth());
        assertEquals(PayrollRun.Status.DRAFT, response.status());

        // Basic: 50,000 + HRA (40% of 50k): 20,000 = Gross 70,000
        assertEquals(0, BigDecimal.valueOf(70000.00).compareTo(response.totalGross()));

        // Deductions: PF (12% of 50,000 = 6,000) + PT (200) + TDS (tax on 70k: 1250 + 10% of 20k = 3250) = 9,450
        // Net: 70,000 - 9,450 = 60,550
        assertTrue(response.totalNet().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.totalNet().compareTo(response.totalGross()) < 0);
    }

    @Test
    @DisplayName("Should throw PayrollLockedException when trying to re-run locked payroll (FR7)")
    void testCannotReRunLockedPayroll() {
        String month = "2026-10";
        PayrollRun lockedRun = new PayrollRun(month);
        lockedRun.lock("admin");

        when(payrollRunRepository.findByPayrollMonth(month)).thenReturn(Optional.of(lockedRun));

        assertThrows(PayrollLockedException.class, () -> payrollService.executePayrollRun(month));
    }
}
