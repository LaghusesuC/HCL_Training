package com.hcl.training.payroll.dto;

import com.hcl.training.payroll.domain.entity.SalaryComponent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalaryComponentDto(
        Long componentId,

        @NotBlank(message = "Component code is required")
        String componentCode,

        @NotBlank(message = "Component name is required")
        String componentName,

        @NotNull(message = "Component type is required")
        SalaryComponent.Type componentType,

        @NotNull(message = "Calculation mode is required")
        SalaryComponent.CalculationMode calculationMode,

        @NotNull(message = "Default value is required")
        BigDecimal defaultValue
) {}
