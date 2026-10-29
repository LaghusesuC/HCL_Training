package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Standard progressive tax calculation strategy for monthly TDS calculation.
 */
@Component
public class SimplifiedTaxCalculationStrategy implements TaxCalculationStrategy {

    private static final Money SLAB_1 = Money.of(25000.00); // Up to 25k/mo exempt
    private static final Money SLAB_2 = Money.of(50000.00); // 25k - 50k at 5%
    private static final Money SLAB_3 = Money.of(100000.00); // 50k - 100k at 10%

    @Override
    public Money calculateTds(Money monthlyGross) {
        if (monthlyGross == null || !monthlyGross.isGreaterThan(SLAB_1)) {
            return Money.ZERO;
        }

        if (!monthlyGross.isGreaterThan(SLAB_2)) {
            Money taxable = monthlyGross.subtract(SLAB_1);
            return taxable.percentage(5.0);
        }

        if (!monthlyGross.isGreaterThan(SLAB_3)) {
            Money taxFromSlab2 = SLAB_2.subtract(SLAB_1).percentage(5.0);
            Money taxableInSlab3 = monthlyGross.subtract(SLAB_2);
            return taxFromSlab2.add(taxableInSlab3.percentage(10.0));
        }

        // Exceeds 100k
        Money taxFromSlab2 = SLAB_2.subtract(SLAB_1).percentage(5.0);
        Money taxFromSlab3 = SLAB_3.subtract(SLAB_2).percentage(10.0);
        Money taxableAboveSlab3 = monthlyGross.subtract(SLAB_3);
        return taxFromSlab2.add(taxFromSlab3).add(taxableAboveSlab3.percentage(15.0));
    }
}
