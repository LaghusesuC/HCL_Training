package com.hcl.training.payroll.domain;

import com.hcl.training.payroll.domain.entity.*;
import com.hcl.training.payroll.domain.model.component.DeductionType;
import com.hcl.training.payroll.domain.model.component.EarningType;
import com.hcl.training.payroll.domain.model.component.SalaryComponentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DomainEntitiesTest {

    @Test
    @DisplayName("SalaryComponentType sealed hierarchy categorizes correctly")
    void testSealedComponentHierarchy() {
        SalaryComponentType basic = EarningType.BASIC;
        assertEquals(SalaryComponentType.Category.EARNING, basic.getCategory());
        assertTrue(basic.isTaxable());

        SalaryComponentType pf = DeductionType.PROVIDENT_FUND;
        assertEquals(SalaryComponentType.Category.DEDUCTION, pf.getCategory());
        assertFalse(pf.isTaxable());
    }

    @Test
    @DisplayName("SalaryStructure and StructureComponent creation and linkage")
    void testSalaryStructureLinkage() {
        Department it = new Department("Engineering", "ENG");
        assertEquals("ENG", it.getDepartmentCode());

        SalaryComponent basicComp = new SalaryComponent("BASIC", "Basic Pay",
                SalaryComponent.Type.EARNING, SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(50000));

        SalaryComponent hraComp = new SalaryComponent("HRA", "House Rent Allowance",
                SalaryComponent.Type.EARNING, SalaryComponent.CalculationMode.PERCENTAGE_OF_BASIC, BigDecimal.valueOf(40.0));

        SalaryStructure structure = new SalaryStructure("Standard Software Engineer", "L2 Grade");
        structure.addComponent(basicComp, BigDecimal.valueOf(50000.00));
        structure.addComponent(hraComp, BigDecimal.valueOf(40.00));

        assertEquals(2, structure.getComponents().size());
        assertEquals("Standard Software Engineer", structure.getStructureName());

        Employee emp = new Employee("EMP-101", "John Doe", "john@example.com", "Software Engineer", it, structure);
        assertEquals("EMP-101", emp.getEmployeeNumber());
        assertEquals(structure, emp.getSalaryStructure());
        assertEquals(it, emp.getDepartment());
    }

    @Test
    @DisplayName("PayrollRun locking behavior")
    void testPayrollRunLocking() {
        PayrollRun run = new PayrollRun("2026-03");
        assertEquals(PayrollRun.Status.DRAFT, run.getStatus());
        assertFalse(run.isLocked());

        run.lock("adminUser");
        assertEquals(PayrollRun.Status.LOCKED, run.getStatus());
        assertTrue(run.isLocked());
        assertEquals("adminUser", run.getLockedBy());
        assertNotNull(run.getLockedAt());

        // Locking an already locked run should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> run.lock("officerUser"));
    }
}
