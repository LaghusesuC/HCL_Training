package com.hcl.training.payment;

/**
 * CashPayment extending Payment (does not implement Refundable).
 */
public class CashPayment extends Payment {

    public CashPayment(double amount) {
        super(amount);
    }

    @Override
    public void pay() {
        System.out.println("Processing Cash Payment of Rs." + amount + " at the counter.");
    }
}
