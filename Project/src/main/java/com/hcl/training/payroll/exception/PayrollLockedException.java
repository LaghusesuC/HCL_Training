package com.hcl.training.payroll.exception;

public class PayrollLockedException extends RuntimeException {
    public PayrollLockedException(String message) {
        super(message);
    }
}
