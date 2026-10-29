package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.model.Money;

/**
 * Strategy Pattern interface for tax calculations (conforms to Section 7 Design Concepts).
 */
public interface TaxCalculationStrategy {
    Money calculateTds(Money monthlyGross);
}
