package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.Department;
import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.PayrollEntry;
import com.hcl.training.payroll.domain.entity.PayrollRun;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PayslipPdfServiceTest {

    @Test
    @DisplayName("Should generate valid non-empty PDF bytes with PDF magic header %PDF")
    void testGeneratePayslipPdf() {
        PayslipPdfService pdfService = new PayslipPdfService();

        Department dept = new Department("Engineering", "ENG");
        Employee emp = new Employee("EMP001", "Rajesh Sharma", "rajesh@hcl.com", "Architect", dept, null);
        PayrollRun run = new PayrollRun("2026-10");

        PayrollEntry entry = new PayrollEntry(
                run, emp, 30, 0, BigDecimal.ZERO,
                BigDecimal.valueOf(80000.00),
                BigDecimal.valueOf(12000.00),
                BigDecimal.valueOf(68000.00)
        );
        entry.addDeduction("PF", BigDecimal.valueOf(6000.00));
        entry.addDeduction("PT", BigDecimal.valueOf(200.00));
        entry.addDeduction("TDS", BigDecimal.valueOf(5800.00));

        byte[] pdfBytes = pdfService.generatePayslipPdf(entry, "PAY-202610-EMP001");

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF byte array should be non-trivial in size");

        // Verify PDF Magic Header "%PDF"
        String header = new String(pdfBytes, 0, 4);
        assertEquals("%PDF", header);
    }
}
