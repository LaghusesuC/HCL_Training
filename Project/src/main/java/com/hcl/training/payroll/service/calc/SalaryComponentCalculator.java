package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.model.Money;
import java.math.BigDecimal;

public interface SalaryComponentCalculator {
    Money calculate(BigDecimal amountOrRate, Money baseSalary);
}
