package com.hcl.training.payroll.dto;

import com.hcl.training.payroll.domain.entity.SalaryComponent;
import java.math.BigDecimal;

public record StructureComponentItem(
        Long componentId,
        String componentCode,
        String componentName,
        SalaryComponent.Type componentType,
        SalaryComponent.CalculationMode calculationMode,
        BigDecimal amountOrRate
) {}
