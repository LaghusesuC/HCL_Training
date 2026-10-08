package com.hcl.bank.model;

/**
 * Thrown when a deposit or withdrawal amount is invalid (e.g., negative or zero).
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
