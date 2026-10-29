package com.hcl.training.payroll.service.calc;

import com.hcl.training.payroll.domain.entity.SalaryComponent;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern for obtaining the appropriate calculator based on calculation mode.
 */
@Component
public class ComponentCalculatorFactory {

    private final FixedComponentCalculator fixedCalculator;
    private final PercentageComponentCalculator percentageCalculator;

    public ComponentCalculatorFactory(FixedComponentCalculator fixedCalculator,
                                      PercentageComponentCalculator percentageCalculator) {
        this.fixedCalculator = fixedCalculator;
        this.percentageCalculator = percentageCalculator;
    }

    public SalaryComponentCalculator getCalculator(SalaryComponent.CalculationMode mode) {
        if (mode == SalaryComponent.CalculationMode.PERCENTAGE_OF_BASIC) {
            return percentageCalculator;
        }
        return fixedCalculator;
    }
}
