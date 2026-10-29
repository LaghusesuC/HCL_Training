package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("percentageComponentCalculator")
public class PercentageComponentCalculator implements SalaryComponentCalculator {

    @Override
    public Money calculate(BigDecimal amountOrRate, Money baseSalary) {
        if (amountOrRate == null || baseSalary == null) {
            return Money.ZERO;
        }
        return baseSalary.percentage(amountOrRate);
    }
}
