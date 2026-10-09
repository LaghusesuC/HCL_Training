package com.hcl.training.payment;

/**
 * Interface for payments that support refunds.
 */
public interface Refundable {
    void refund(double amount);
}
