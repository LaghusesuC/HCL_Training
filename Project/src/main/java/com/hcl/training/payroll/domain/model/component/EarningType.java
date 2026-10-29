package com.hcl.training.payroll.domain.model.component;

public enum EarningType implements SalaryComponentType {
    BASIC("BASIC", "Basic Salary", true),
    HRA("HRA", "House Rent Allowance", true),
    SPECIAL_ALLOWANCE("SPECIAL_ALLOWANCE", "Special Allowance", true),
    CONVEYANCE("CONVEYANCE", "Conveyance Allowance", false),
    PERFORMANCE_BONUS("PERFORMANCE_BONUS", "Performance Bonus", true);

    private final String code;
    private final String description;
    private final boolean taxable;

    EarningType(String code, String description, boolean taxable) {
        this.code = code;
        this.description = description;
        this.taxable = taxable;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isTaxable() {
        return taxable;
    }

    @Override
    public Category getCategory() {
        return Category.EARNING;
    }
}
