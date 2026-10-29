package com.hcl.training.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SalaryStructureDto(
        Long structureId,

        @NotBlank(message = "Structure name is required")
        String structureName,

        String description,

        List<StructureComponentItem> components
) {}
