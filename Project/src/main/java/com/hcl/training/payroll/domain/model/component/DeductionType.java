package com.hcl.training.payroll.domain.model.component;

public enum DeductionType implements SalaryComponentType {
    PROVIDENT_FUND("PF", "Employee Provident Fund (12% of Basic)", false),
    PROFESSIONAL_TAX("PT", "Professional Tax (Statutory deduction)", false),
    TAX_DEDUCTED_AT_SOURCE("TDS", "Income Tax Deducted at Source", false),
    LOSS_OF_PAY("LOP", "Attendance Loss of Pay Deduction", false),
    HEALTH_INSURANCE("INSURANCE", "Corporate Health Insurance", false);

    private final String code;
    private final String description;
    private final boolean taxable;

    DeductionType(String code, String description, boolean taxable) {
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
        return Category.DEDUCTION;
    }
}
