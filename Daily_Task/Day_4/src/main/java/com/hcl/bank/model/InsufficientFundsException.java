package com.hcl.bank.model;

/**
 * Thrown when an account withdrawal exceeds available balance.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
