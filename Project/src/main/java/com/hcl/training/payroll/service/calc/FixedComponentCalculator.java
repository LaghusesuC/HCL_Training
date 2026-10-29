package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("fixedComponentCalculator")
public class FixedComponentCalculator implements SalaryComponentCalculator {

    @Override
    public Money calculate(BigDecimal amountOrRate, Money baseSalary) {
        if (amountOrRate == null) {
            return Money.ZERO;
        }
        return Money.of(amountOrRate);
    }
}
