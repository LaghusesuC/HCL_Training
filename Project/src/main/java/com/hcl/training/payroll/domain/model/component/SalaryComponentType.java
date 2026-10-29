package com.hcl.training.payroll.domain.model.component;

/**
 * Sealed hierarchy modeling salary component types according to Section 7 Design Concepts.
 */
public sealed interface SalaryComponentType permits EarningType, DeductionType {

    String getCode();
    String getDescription();
    boolean isTaxable();

    enum Category {
        EARNING,
        DEDUCTION
    }

    Category getCategory();
}
